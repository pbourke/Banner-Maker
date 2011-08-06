package com.pb.forms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.util.UriUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

public class FontLibrary implements ServletContextAware {
    private final Log logger = LogFactory.getLog(getClass());
    
    private Map<String, String> fontMap;
    private Map<String, String> licenseMap;
    private ServletContext servletContext;
    
    public void setFontMap(final Map<String, String> map) {
        fontMap = map;
    }

    public void setLicenseMap(final Map<String, String> map) {
        licenseMap = map;
    }

    @Override
    public void setServletContext(final ServletContext sc) {
        servletContext = sc;
    }

    Font getFont(final String fontName, final float size) throws DocumentException, IOException {
        logger.info(fontMap.toString());
        final String fontResource = fontMap.get(fontName);
        ServletContextResource scr = new ServletContextResource(servletContext, fontResource);
        final File fontFile = scr.getFile();
        return new Font(BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED), size);       
    }
    
    String getLicense(final String fontName) throws IOException {
        logger.info(licenseMap.toString());
        final String licenseResource = licenseMap.get(fontName);
        ServletContextResource scr = new ServletContextResource(servletContext, licenseResource);
        return toString(scr.getInputStream());
    }

    private String toString(final InputStream inputStream) throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(inputStream);
        final StringBuffer sb = new StringBuffer();
        byte[] buf = new byte[1024];
        int num;
        while ( (num = bis.read(buf)) > 0 ) {
            sb.append(new String(buf, 0, num - 1));
        }
        return sb.toString();
    }
}
