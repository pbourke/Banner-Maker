package com.pb.forms;

import static java.lang.String.format;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class BannerController {
    protected Log logger = LogFactory.getLog(getClass());
    
    @Autowired(required=true)
    private FontLibrary fontLibrary;
        
    @RequestMapping(value="/banner", method=RequestMethod.GET)
    public void banner(ServletRequest req, HttpServletResponse resp, OutputStream os) throws IOException, DocumentException {
        final String message = ServletRequestUtils.getStringParameter(req, "message", "Eat at Moe's");
        final String trimmedMessage = StringUtils.trimAllWhitespace(message);
        final String requestedFont = ServletRequestUtils.getStringParameter(req, "font", "courier");
        final Font font = fontLibrary.getFont(requestedFont, 512.0f);
        logger.info(format("message='%s', font='%s'", trimmedMessage, requestedFont));        

        if ( trimmedMessage.length() > 50 ) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=banner.pdf");
        
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
