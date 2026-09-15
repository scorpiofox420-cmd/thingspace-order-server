package com.verizon.thingspace.http.response;

/**
 * Minimal ApiResponse wrapper matching the shape the server code uses:
 * getResult() returns the parsed response body.
 */
public class ApiResponse<T> {

    private final T result;
    private final int statusCode;

    public ApiResponse(T result, int statusCode) {
        this.result = result;
        this.statusCode = statusCode;
    }

    public T getResult() {
        return result;
    }

    public int getStatusCode() {
        return statusCode;
    }
}