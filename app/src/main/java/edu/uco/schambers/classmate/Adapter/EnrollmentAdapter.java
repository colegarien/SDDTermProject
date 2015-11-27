package edu.uco.schambers.classmate.Adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.AdapterModels.StudentByClass;

/**
 * Created by Nelson.
 */

public class EnrollmentAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/";

    //Getting a list of schools
    public static void getSchools(final Callback<ArrayList<String>> callback) {
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

    //Getting a list of years based on a particular school
    public static void getYears(String school, final Callback<ArrayList<String>> callback) throws UnsupportedEncodingException {
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

        String url = Url + "years/" + URLEncoder.encode(school, "UTF-8").replace("+", "%20");

        call.execute(new ServiceCall(url, "GET", ""));
    }

    //Getting a list of Semesters based on a particular Year
    public static void getSemesters(String school, String year, final Callback<ArrayList<String>> callback) throws UnsupportedEncodingException {
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

        call.execute(new ServiceCall(Url + "semesters/" + URLEncoder.encode(school, "UTF-8").replace("+", "%20") + "/" + year, "GET", ""));
    }

    //Get current student enrolled classes
    public static void getEnrolledClasses(int userId, boolean showOld, final Callback<ArrayList<Class>> callback) throws UnsupportedEncodingException {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                String json = response.getResponse();
                JSONArray array = new JSONArray(json);

                ArrayList<Class> list = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    Class classItem = new Class();
                    classItem.setId(jsonObject.getInt("Id"));
                    classItem.setClass_name(jsonObject.getString("Class_Name"));
                    classItem.setProfessor_name(jsonObject.getString("Professor_Name"));
                    list.add(classItem);
                }

                callback.onComplete(list);
            }
        });

        call.execute(new ServiceCall(Url + "Enrollments/" + userId + "/" + (showOld ? "1" : "0"), "GET", ""));
    }

    //Get all classes based on school, year, semester, and user_Id to check if student IsEnrolled
    public static void getClasses(String school, final String year, final String semester, int userId, final Callback<ArrayList<Class>> callback) throws UnsupportedEncodingException {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                String json = response.getResponse();
                JSONArray array = new JSONArray(json);

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

        call.execute(new ServiceCall(Url + "classes/" + URLEncoder.encode(school, "UTF-8").replace("+", "%20") + "/" + year + "/" + semester + "/" + userId, "GET", ""));
    }

    //Enroll student in a class
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

    //delete a class enrollment entry
    public void dropClass(int class_id, int user_id, final Callback<HttpResponse> callback) throws JSONException {

        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {
                callback.onComplete(result);
            }
        });

        call.execute(new ServiceCall(Url + "Dropclass/" + class_id + "/" + user_id + "/", "DELETE", ""));
    }

    //Get every student in a particular class
    public void getStudentsByClass(int class_id, final Callback<ArrayList<StudentByClass>> callback) throws JSONException {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {

                JSONArray array = new JSONArray(result.getResponse());

                ArrayList<StudentByClass> list = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    StudentByClass student = new StudentByClass();
                    student.setId(jsonObject.getInt("User_Id"));
                    student.setName(jsonObject.getString("Name"));
                    student.setEnrollmentId(jsonObject.getInt("Enrollment_Id"));
                    list.add(student);
                }

                callback.onComplete(list);
            }
        });

        call.execute(new ServiceCall(Url + "studentsbyclass/" + class_id, "GET", ""));
    }

}
