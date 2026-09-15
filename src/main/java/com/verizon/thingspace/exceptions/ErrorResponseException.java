package com.verizon.thingspace.exceptions;

/**
 * Raised when Verizon returns a non-2xx response (e.g. 400 Bad Request).
 */
public class ErrorResponseException extends RuntimeException {

    private final int statusCode;
    private final String rawBody;

    public ErrorResponseException(int statusCode, String rawBody) {
        super("Verizon API error: HTTP " + statusCode + " — " + rawBody);
        this.statusCode = statusCode;
        this.rawBody = rawBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getRawBody() {
        return rawBody;
    }
}