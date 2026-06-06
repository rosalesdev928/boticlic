package com.boticlic.boticlic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Este archivo le dice a Spring:
// "cuando alguien pida /uploads/productos/foto.jpg,
//  búscala en la carpeta externa app.upload.dir"
// Así las imágenes sobreviven rebuilds y deploys.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/productos}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Convierte la ruta a formato file:/// que Spring entiende
        // Ej: C:/boticlic-uploads/productos → file:///C:/boticlic-uploads/productos/
        String rutaAbsoluta = java.nio.file.Paths.get(uploadDir)
                .toAbsolutePath()
                .toString()
                .replace("\\", "/");

        if (!rutaAbsoluta.endsWith("/")) {
            rutaAbsoluta += "/";
        }

        // Mapea la URL /uploads/productos/** a la carpeta externa
        registry.addResourceHandler("/uploads/productos/**")
                .addResourceLocations("file:///" + rutaAbsoluta);
    }
}