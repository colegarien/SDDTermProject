package edu.uco.schambers.classmate.Database;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;

public class TokenUtility {

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
