package com.midasdev.mybg.global.util.default_value_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class DefaultedRequestBodyResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public DefaultedRequestBodyResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DefaultedRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String json = servletRequest.getReader().lines().collect(Collectors.joining());

        // TODO: @Default를 사용할 때, empty body 일 경우 어떻게 되는지 확인하고, 적절한 처리

        Object dto = objectMapper.readValue(json, parameter.getParameterType());
        DefaultValueResolver.applyDefaults(dto);
        return dto;
    }
}
