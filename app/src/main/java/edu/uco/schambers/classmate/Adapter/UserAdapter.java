package edu.uco.schambers.classmate.Adapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uco.schambers.classmate.AdapterModels.User;

/**
 * Created by Nelson.
 */

public class UserAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/";

    //create user account for student or professor
    public void createUser(User user, final Callback<HttpResponse> callback) throws JSONException, IOException {
        JSONObject json = new JSONObject();

        json.put("Name", user.getName());
        json.put("Password", user.getPassword());
        json.put("Email", user.getEmail());
        if (user.isStudent())
            json.put("Student_Id", user.getId());

        json.put("Role_Name", user.isStudent() ? "student" : "faculty");

        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                callback.onComplete(response);
            }
        });

        call.execute(new ServiceCall(Url + "Users", "POST", json.toString()));
    }

    //change user password when only when logged in
    public void changePass(String email, String oldPass, String newPass, final Callback<HttpResponse> callback) throws JSONException, IOException {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                callback.onComplete(response);
            }
        });

        call.execute(new ServiceCall(Url + "changePass" + "?email=" + email + "&oldPass=" + oldPass + "&newPass=" + newPass, "POST", ""));
    }
}
