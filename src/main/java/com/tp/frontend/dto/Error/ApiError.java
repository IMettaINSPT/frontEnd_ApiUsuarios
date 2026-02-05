package com.tp.frontend.dto.Error;

import java.util.List;
import java.util.Map;

public class ApiError {
    private String message;
    private String status;      // si en backend es string. Si es int, cambialo a Integer
    private String timestamp;   // si viene como ISO string, está ok
    private String trace;
    private String code;
    private Map<String,String> fieldErrors;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTrace() { return trace; }
    public void setTrace(String trace) { this.trace = trace; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Map<String,String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String,String> fieldErrors) { this.fieldErrors = fieldErrors; }
}
