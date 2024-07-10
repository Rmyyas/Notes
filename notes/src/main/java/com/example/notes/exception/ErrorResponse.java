package com.example.notes.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private int errorCode;
    private String errorMsg;
    private HttpStatus status;

    public ErrorResponse(int errorCode, String errorMsg, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
