package com.zenlin.cloud.tdp.filter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 描述:
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/22  9:24.
 */
@Configuration
@ComponentScan(basePackages = {"com.zenlin.cloud.tdp"}, includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)})
public class DispatcherServletConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                .maxAge(3600);
    }
}
