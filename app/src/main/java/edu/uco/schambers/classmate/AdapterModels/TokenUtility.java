package edu.uco.schambers.classmate.AdapterModels;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenUtility {

    public static String getSharedPreferences(Activity activity) {
        SharedPreferences sp;
        final String MyPREFS = "MyPREFS";
        sp = activity.getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        return sp.getString("AUTH_TOKEN", null);
    }

    public static User parseUserToken(Activity activity) throws JSONException {
        return parseUserToken(getSharedPreferences(activity));
    }

    public static User parseUserToken(String token) throws JSONException {
        String[] split = token.substring(1, token.length() - 1).replace("\\","").split("\\|");


        String json = split[1];
        //String workingJson = "{\"Id\":44,\"Name\":\"d\",\"Email\":\"d@d.com\",\"Student_Id\":0,\"Role_Name\":\"faculty\"}";
        JSONObject jsonObject = new JSONObject(json);

        User user = new User();
        user.setpKey(jsonObject.getInt("Id"));
        user.setName(jsonObject.getString("Name"));
        user.setEmail(jsonObject.getString("Email"));
        user.setId(jsonObject.getInt("Student_Id"));
        user.setIsStudent(jsonObject.getString("Role_Name").equals("student"));

        return user;
    }
}
