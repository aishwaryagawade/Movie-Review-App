package com.example.trainingproject.models;

public class ErrorResponse {
    private String invalidResponse;

    public ErrorResponse(String invalidResponse) {
        this.invalidResponse = invalidResponse;
    }

    public String getInvalidResponse() {
        return invalidResponse;
    }
}
