/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.controller.buscalendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nusino.microservices.exception.AuthorizationException;
import com.nusino.microservices.exception.FeedbackableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(value = FeedbackableException.class)
    public @ResponseBody
    ApiError exception(FeedbackableException exception) {
        ApiError apiError = new ApiError(HttpStatus.EXPECTATION_FAILED, exception.code != null ? exception.code.toString() : null, exception.getMessage());
        if (exception.fields != null) {
            apiError.setSubErrors(Arrays.asList(exception.fields));
        }

        return apiError;
    }

    @ExceptionHandler(value = AuthorizationException.class)
    public @ResponseBody
    ApiError exception(AuthorizationException exception) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, exception.code != null ? exception.code.toString() : null, exception.getMessage());
        return apiError;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public @ResponseBody
    ApiError exception(RuntimeException exception) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, null, "Unknown service exception");
        return apiError;
    }

}

class ApiError {
    private HttpStatus errStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String errCode;
    private String errMessage;
    private String debugMessage;
    private List<String> subErrors;


    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.errCode = errCode;
    }

    public ApiError(HttpStatus status, String code, String msg) {
        this();
        this.errStatus = status;
        this.errCode = code;
        this.errMessage = msg;
    }


    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.errStatus = status;
        this.errMessage = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message) {
        this();
        this.errStatus = status;
        this.errMessage = message;
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.errStatus = status;
        this.errMessage = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    public HttpStatus getErrStatus() {
        return errStatus;
    }

    public void setErrStatus(HttpStatus errStatus) {
        this.errStatus = errStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public List<String> getSubErrors() {
        return subErrors;
    }

    public void setSubErrors(List<String> subErrors) {
        this.subErrors = subErrors;
    }
}

abstract class ApiSubError {

}