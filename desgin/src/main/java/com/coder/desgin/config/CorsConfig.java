package com.coder.desgin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 解决跨域获取资源问题
 * @Author coder
 * @Date 2022/11/3 14:38
 * @Description
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer myCorsConfig(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .maxAge(3600) // 允许跨域时间
                        .allowedMethods("GET", "POST", "DELETE", "PUT")
                        .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")
                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                        .allowedHeaders("*");
            }
        };
    }
}
