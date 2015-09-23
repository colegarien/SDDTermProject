package edu.uco.schambers.classmate.Adapter;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServiceHandlerAsync extends AsyncTask<ServiceCall, Integer, String> {

    private Callback callback;

    public ServiceHandlerAsync(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(ServiceCall... serviceCalls) {
        ServiceCall serviceCall = serviceCalls[0];
        try {
            return makeServiceCall(serviceCall.getUrl(), serviceCall.getMethod(), serviceCall.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        callback.onComplete(json);
    }

    private static String makeServiceCall(String serviceUrl, String method, String body) throws IOException {
        URL url = new URL(serviceUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        if (method == "GET")
            conn.setRequestProperty("Accept", "application/json");

        // Send post request
        if (method == "POST") {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
        }

        if (conn.getResponseCode() >= 300) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String response = "";
        String output;

        while ((output = br.readLine()) != null) {
            response += output;
        }

        conn.disconnect();

        System.out.println("Response from Server .... \n");
        System.out.println(response);
        return response;
    }
}
