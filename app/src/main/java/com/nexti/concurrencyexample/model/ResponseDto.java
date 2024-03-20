package com.nexti.concurrencyexample.model;

public class ResponseDto {
    private String responseBody;
    private Long requestTime;

    public ResponseDto(String responseBody, Long requestTime) {
        this.responseBody = responseBody;
        this.requestTime = requestTime;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Long getRequestTime() {
        return requestTime;
    }
}
