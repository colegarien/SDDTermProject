package edu.uco.schambers.classmate.Adapter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uco.schambers.classmate.Database.User;

/**
 * Created by calitova on 9/22/2015.
 */
public class UserAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/Users";

    public void createUser(User user) throws JSONException, IOException {
        JSONObject json = new JSONObject();

        json.put("Name", user.getName());
        json.put("Password", user.getPassword());
        json.put("Email", user.getEmail());
        if (user.isStudent())
            json.put("Student_Id", user.getId());

        json.put("Role_Name", user.isStudent() ? "student" : "faculty");

        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<String>() {
            @Override
            public void onComplete(String json) {
                Log.d("WS", json);
            }
        });

        call.execute(new ServiceCall(Url, "POST", json.toString()));

        //ServiceHandler.makeServiceCall(Url, "POST", json.toString());
    }
}
