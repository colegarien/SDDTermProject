package edu.uco.schambers.classmate.Adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.AdapterModels.StudentAbsenceByClass;

public class EnrollmentAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/";

    public void getSchools(final Callback<ArrayList<String>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<String> schools = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    String school = array.getString(i);
                    schools.add(school);
                }

                callback.onComplete(schools);
            }
        });

        call.execute(new ServiceCall(Url + "schools", "GET", ""));
    }

    public void getYears(String school, final Callback<ArrayList<String>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<String> list = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    String school = Integer.toString(array.getInt(i));
                    list.add(school);
                }

                callback.onComplete(list);
            }
        });

        call.execute(new ServiceCall(Url + "years/" + school, "GET", ""));
    }

    public void getSemesters(String school, String year, final Callback<ArrayList<String>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<String> list = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    String school = array.getString(i);
                    list.add(school);
                }

                callback.onComplete(list);
            }
        });

        call.execute(new ServiceCall(Url + "semesters/" + school + "/" + year, "GET", ""));
    }

    public void getClasses(String school, final String year, final String semester, int studentId, final Callback<ArrayList<Class>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<Class> list = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    Class classItem = new Class();
                    classItem.setId(jsonObject.getInt("Id"));
                    classItem.setClass_name(jsonObject.getString("Class_Name"));
                    classItem.setProfessor_name(jsonObject.getString("Professor_Name"));
                    classItem.setEnrolled(jsonObject.getBoolean("IsEnrolled"));
                    classItem.setYear(Integer.valueOf(year));
                    classItem.setSemester(semester);
                    list.add(classItem);
                }

                callback.onComplete(list);
            }
        });

        call.execute(new ServiceCall(Url + "classes/" + school + "/" + year + "/" + semester + "/" + studentId, "GET", ""));
    }

    public void classEnroll(int user_id, int class_id, final Callback<HttpResponse> callback) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Class_Id", class_id);
        jsonObject.put("User_Id", user_id);
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {
                callback.onComplete(result);
            }
        });

        call.execute(new ServiceCall(Url + "Enrollments/", "POST", jsonObject.toString()));
    }

}
