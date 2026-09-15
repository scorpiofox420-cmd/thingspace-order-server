package com.verizon.thingspace.models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Response of generateCredentials / resetCredentials. Raw parsed body.
 */
public class GenerateResponse {

    private final JsonNode body;

    public GenerateResponse(JsonNode body) {
        this.body = body;
    }

    public JsonNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return body == null ? "null" : body.toString();
    }
}