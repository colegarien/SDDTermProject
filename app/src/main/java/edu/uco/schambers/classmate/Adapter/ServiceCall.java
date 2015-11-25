package edu.uco.schambers.classmate.Adapter;

/**
 * Created by Nelson on 9/22/2015.
 */
public class ServiceCall {
    private String url;
    private String method;
    private String body;

    public ServiceCall(String url, String method, String body) {

        this.url = url;
        this.method = method;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }
}
