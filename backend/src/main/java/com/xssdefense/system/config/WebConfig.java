package com.xssdefense.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Web配置类
 * 处理跨域请求等Web相关配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 全局CORS配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:8081", 
                    "http://localhost:8082", 
                    "http://localhost:8083", 
                    "http://localhost:8084", 
                    "http://localhost:8085", 
                    "http://localhost:8086", 
                    "http://localhost:8087", 
                    "http://localhost:8088", 
                    "http://localhost:8089"
                ) // 允许多个端口的前端应用源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 允许携带凭证
                .maxAge(3600); // 预检请求的有效期，单位为秒
    }

    /**
     * CORS过滤器
     * 提供更细粒度的CORS控制
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:8081", 
            "http://localhost:8082", 
            "http://localhost:8083", 
            "http://localhost:8084", 
            "http://localhost:8085", 
            "http://localhost:8086", 
            "http://localhost:8087", 
            "http://localhost:8088", 
            "http://localhost:8089"
        ));
        // 允许的HTTP方法
        config.addAllowedMethod("*");
        // 允许的请求头
        config.addAllowedHeader("*");
        // 允许携带凭证
        config.setAllowCredentials(true);
        // 预检请求的有效期
        config.setMaxAge(3600L);
        
        // 应用CORS配置到所有路径
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 