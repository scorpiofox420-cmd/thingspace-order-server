package com.verizon.thingspace.models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Response of retrieveCredentials. Raw parsed body — supported fields will be
 * surfaced as accessors once a real payload shape is confirmed.
 */
public class RetrieveResponse {

    private final JsonNode body;

    public RetrieveResponse(JsonNode body) {
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