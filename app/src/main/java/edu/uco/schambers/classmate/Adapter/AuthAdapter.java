package edu.uco.schambers.classmate.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Nelson on 9/22/2015.
 */
public class AuthAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/authenticate";

    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public static final String MyPREFS = "MyPREFS";
    private Activity activity;

    public AuthAdapter(Activity activity) {

        this.activity = activity;
    }

    //validate user then generate token
    public static void authenticate(String email, String password, final Callback<HttpResponse> callback) throws JSONException, IOException {


        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                callback.onComplete(response);
            }
        });

        call.execute(new ServiceCall(Url + "?email=" + email + "&password=" + password, "POST", ""));
    }
}
