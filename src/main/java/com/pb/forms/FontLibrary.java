package com.pb.forms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
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

    private Map<String, Font> fontCacheMap = new ConcurrentHashMap<String, Font>();
    private Map<String, String> licenseCacheMap = new ConcurrentHashMap<String, String>();
    
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
        if ( fontCacheMap.containsKey(fontName) ) {
            return fontCacheMap.get(fontName);
        }
        final String fontResource = fontMap.get(fontName);
        ServletContextResource scr = new ServletContextResource(servletContext, fontResource);
        final File fontFile = scr.getFile();
        Font font = new Font(BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED), size);
        fontCacheMap.put(fontName, font);
        return font;       
    }

    String getLicense(final String fontName) throws IOException {
        if ( licenseCacheMap.containsKey(fontName) ) {
            return licenseCacheMap.get(fontName);
        }
        
        final String licenseResource = licenseMap.get(fontName);
        ServletContextResource scr = new ServletContextResource(servletContext, licenseResource);
        String license = toString(scr.getInputStream());
        licenseCacheMap.put(fontName, license);
        return license;
    }

    private String toString(final InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream);
    }
}
