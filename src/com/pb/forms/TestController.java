package com.pb.forms;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class TestController {
    protected Log logger = LogFactory.getLog(getClass());
    private static Font FONT_COURIER_BOLD_LG = FontFactory.getFont(FontFactory.COURIER_BOLD, 512.0f);
    private static Font FONT_HELVETICA_BOLD_LG = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 512.0f);
    private static Font FONT_HELVETICA_BOLD_SM = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    private static Font FONT_HELVETICA_SM = FontFactory.getFont(FontFactory.HELVETICA);
    
    @Autowired(required=true)
    private FontLibrary fontLibrary;
        
    @RequestMapping(value="/banner", method=RequestMethod.GET)
    public void testPost(ServletRequest req, OutputStream os) throws IOException, DocumentException {
        final String message = ServletRequestUtils.getStringParameter(req, "message", "Eat at Moe's");
        final String trimmedMessage = StringUtils.trimAllWhitespace(message);
        logger.info("Trimmed message = " + trimmedMessage);
        final String requestedFont = ServletRequestUtils.getStringParameter(req, "font", "courier");
        final Font font = fontLibrary.getFont(requestedFont, 512.0f);

        Document doc = new Document(PageSize.LETTER);        
        PdfWriter pdfw = PdfWriter.getInstance(doc, os);
        pdfw.setInitialLeading(font.getSize() + 20.0f);
        doc.open();
        final String license = fontLibrary.getLicense(requestedFont);
        doc.addHeader("Font License", license);
        for ( int i = 0; i < trimmedMessage.length(); i++ ) {
            doc.newPage();
            Chunk chunk = new Chunk(trimmedMessage.charAt(i), font);
            Paragraph letterParagraph = new Paragraph(chunk);
            letterParagraph.setAlignment(Paragraph.ALIGN_CENTER);            
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100f);
            PdfPCell cell = new PdfPCell();
            cell.addElement(letterParagraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(0);
            cell.setPaddingTop(150f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setExtendLastRow(true);            

            doc.add(table);
        }
        doc.close();
    }
}
