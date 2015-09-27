package edu.uco.schambers.classmate.Adapter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;


public class ServiceHandler {

    public static String makeServiceCall(String serviceUrl, String method) throws IOException {
        URL url = new URL(serviceUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
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
