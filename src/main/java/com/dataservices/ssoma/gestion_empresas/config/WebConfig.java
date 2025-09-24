package com.dataservices.ssoma.gestion_empresas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde /static/frontend
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("classpath:/static/frontend/");

        // Servir archivos estáticos directamente desde la raíz también
        registry.addResourceHandler("/*.html", "/*.js", "/*.css")
                .addResourceLocations("classpath:/static/frontend/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir la raíz al index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
