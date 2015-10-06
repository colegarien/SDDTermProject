package edu.uco.schambers.classmate.Adapter;

public class HttpResponse {
    private String response;
    private int httpCode;

    public HttpResponse(String response, int httpCode) {
        this.response = response;
        this.httpCode = httpCode;
    }

    public String getResponse() {
        return response;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
