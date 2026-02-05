package com.tp.frontend.support;

import com.tp.frontend.exception.ApiErrorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class ErrorBinder {

    public void bind(ApiErrorException ex, BindingResult br) {
        var apiError = ex.getApiError();

        if (apiError != null && apiError.getFieldErrors() != null && !apiError.getFieldErrors().isEmpty()) {
            apiError.getFieldErrors().forEach((field, message) -> {
                if (field != null && !field.isBlank()) {
                    br.rejectValue(field, "backend.validation", message);
                }
            });
        } else {
            br.reject("backend.error",
                    apiError != null && apiError.getMessage() != null
                            ? apiError.getMessage()
                            : ex.getMessage());
        }

    }
}