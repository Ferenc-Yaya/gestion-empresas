package com.dataservices.ssoma.gestion_empresas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde /static/frontend
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("classpath:/static/frontend/");

        // Servir archivos estáticos directamente desde la raíz también
        registry.addResourceHandler("/*.html", "/*.js", "/*.css")
                .addResourceLocations("classpath:/static/frontend/");

        // Servir archivos subidos (solo para desarrollo - en producción usar nginx/apache)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600); // Cache por 1 hora
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir la raíz al index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
