package com.skodin.exceptions.handling;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        int status = (int) errorAttributes.get("status");
        String error = (String) errorAttributes.get("error");

        errorAttributes.put("error", status);
        errorAttributes.put("message", error);

        return errorAttributes;
    }
}