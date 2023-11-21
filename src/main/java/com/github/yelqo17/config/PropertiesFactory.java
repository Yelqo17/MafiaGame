package com.github.yelqo17.config;

import java.io.IOException;
import java.io.InputStream;

public final class PropertiesFactory {
    private static DatabaseProperties properties;
    private PropertiesFactory() {

    }
    public synchronized static DatabaseProperties getProperties() {
        if (properties == null) {
            init();
        }
        return properties;
    }
    private static void init() {
        String filePropertiesName = "application.properties";

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        properties = new DatabaseProperties();
        try (InputStream stream = classLoader.getResourceAsStream(filePropertiesName)) {
            properties.load(stream);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(-1);
        }

        System.out.println();
    }
}
