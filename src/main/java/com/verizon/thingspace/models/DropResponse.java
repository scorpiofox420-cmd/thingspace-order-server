package com.verizon.thingspace.models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Response of dropCredentials. Raw parsed body.
 */
public class DropResponse {

    private final JsonNode body;

    public DropResponse(JsonNode body) {
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