package edu.uco.schambers.classmate.Adapter;


import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServiceHandlerAsync extends AsyncTask<ServiceCall, Integer, HttpResponse> {

    private Callback<HttpResponse> callback;

    public ServiceHandlerAsync(Callback<HttpResponse> callback) {
        this.callback = callback;
    }

    @Override
    protected HttpResponse doInBackground(ServiceCall... serviceCalls) {
        ServiceCall serviceCall = serviceCalls[0];
        try {
            return makeServiceCall(serviceCall.getUrl(), serviceCall.getMethod(), serviceCall.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        try {
            callback.onComplete(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpResponse makeServiceCall(String serviceUrl, String method, String body) throws IOException {
        URL url = new URL(serviceUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        if (method == "GET" || method == "DELETE")
            conn.setRequestProperty("Accept", "application/json");

        // Send post request
        if (method == "POST" || method == "PUT") {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
        }

        if (conn.getResponseCode() >= 300)
        {
            HttpResponse response = new HttpResponse("", conn.getResponseCode());
            conn.disconnect();
            return response;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String responseText = "";
        String output;

        while ((output = br.readLine()) != null) {
            responseText += output;
        }

        HttpResponse response = new HttpResponse(responseText, conn.getResponseCode());

        conn.disconnect();

        System.out.println("Response from Server .... \n");
        System.out.println(responseText);

        return response;
    }
}