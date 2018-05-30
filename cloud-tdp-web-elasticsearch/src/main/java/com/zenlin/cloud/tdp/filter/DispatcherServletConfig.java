package com.zenlin.cloud.tdp.filter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 浙江卓锐科技有限公司
 * Date:2018-01-18
 * Description:配置跨域
 * Project:
 *
 * @author ZENLIN
 */
@Configuration
@ComponentScan(basePackages = {"com.zenlin"}, includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)})
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
