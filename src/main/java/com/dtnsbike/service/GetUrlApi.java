package com.dtnsbike.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class GetUrlApi {
    public static String url(String subdirectory) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            Resource resource = new ClassPathResource("/config/ConfigURI.properties");
            inputStream = new FileInputStream(resource.getFile());
            // load properties from file
            properties.load(inputStream);
            // get property by name
            return properties.getProperty("scheme") + properties.getProperty("subdomain") + subdirectory;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
