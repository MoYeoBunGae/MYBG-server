package com.midasdev.mybg.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midasdev.mybg.global.util.default_value_mapper.DefaultedRequestBodyResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new DefaultedRequestBodyResolver(objectMapper));
    }
}

