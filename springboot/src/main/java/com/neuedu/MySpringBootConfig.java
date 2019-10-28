package com.neuedu;

import com.neuedu.interceptors.AdminAuthroityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class MySpringBootConfig implements WebMvcConfigurer {

    @Autowired
    AdminAuthroityInterceptor adminAuthroityInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthroityInterceptor).addPathPatterns("/manage/**");
    }
}
