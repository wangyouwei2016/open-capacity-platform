package com.open.capacity.ext.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

public class DecEncWebMvcConfigurationSupport extends WebMvcConfigurationSupport {
    @Override
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        return new DecRequestMappingHandlerAdapter();
    }
}
