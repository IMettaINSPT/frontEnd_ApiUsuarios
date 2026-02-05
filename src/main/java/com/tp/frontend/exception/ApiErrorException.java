package com.tp.frontend.exception;

import com.tp.frontend.dto.Error.ApiError;

public class ApiErrorException extends RuntimeException {

    private final ApiError apiError;
    private final int httpStatus;

    public ApiErrorException(ApiError apiError, int httpStatus) {
        super(apiError != null && apiError.getMessage() != null ? apiError.getMessage() : "Error del servidor");
        this.apiError = apiError;
        this.httpStatus = httpStatus;
    }

    public ApiError getApiError() { return apiError; }
    public int getHttpStatus() { return httpStatus; }
}
