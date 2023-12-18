package com.adira.contact.pojo;

public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private String source;
    private T result;


    public ApiResponse(int statusCode, String message, String source, T result) {
        this.statusCode = statusCode;
        this.message = message;
        this.source = source;
        this.result = result;
    }


    public ApiResponse() {
    }


    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }
    
}
