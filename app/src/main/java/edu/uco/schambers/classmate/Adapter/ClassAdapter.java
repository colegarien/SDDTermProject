package edu.uco.schambers.classmate.Adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.uco.schambers.classmate.AdapterModels.*;
import edu.uco.schambers.classmate.AdapterModels.Class;

/**
 * Created by Nelson.
 */

public class ClassAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/";

    //create professor class
    public static void createClass(int professorId, String className, String school, String semester, int year, final Callback<Boolean> callback) throws JSONException {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {
                if (result.getHttpCode() == 204)
                    callback.onComplete(true);
                else
                    throw new Exception("Failed with HTTP status code " + result.getHttpCode());
            }
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("School", school);
        jsonObject.put("Class_Name", className);
        jsonObject.put("Professor", professorId);
        jsonObject.put("Semester", semester);
        jsonObject.put("Year", year);

        call.execute(new ServiceCall(Url + "classes/", "POST", jsonObject.toString()));
    }

    //Get all professor classes based on id
    public static void professorClasses(int professorId, final Callback<ArrayList<Class>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<Class> classes = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    Class newClass = new Class();
                    newClass.setId(jsonObject.getInt("Id"));
                    newClass.setSchool(jsonObject.getString("School"));
                    newClass.setClass_name(jsonObject.getString("Class_Name"));
                    newClass.setSemester(jsonObject.getString("Semester"));
                    newClass.setYear(jsonObject.getInt("Year"));
                    classes.add(newClass);
                }

                callback.onComplete(classes);
            }
        });

        call.execute(new ServiceCall(Url + "professorclasses/" + professorId, "GET", ""));
    }

    //Get a professor class based on his Id, Semester, and Year
    public static void professorClasses(int professorId, String semester, int year, final Callback<ArrayList<Class>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<Class> classes = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    Class newClass = new Class();
                    newClass.setId(jsonObject.getInt("Id"));
                    newClass.setSchool(jsonObject.getString("School"));
                    newClass.setClass_name(jsonObject.getString("Class_Name"));
                    newClass.setSemester(jsonObject.getString("Semester"));
                    newClass.setYear(jsonObject.getInt("Year"));
                    classes.add(newClass);
                }

                callback.onComplete(classes);
            }
        });

        call.execute(new ServiceCall(Url + "professorclasses/" + professorId + "/" + semester + "/" + year, "GET", ""));
    }

    //Get all student absences for a particular class
    public static void studentAbsences(int classId, final Callback<ArrayList<AbsenceByClass>> callback) {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {
            @Override
            public void onComplete(HttpResponse response) throws Exception {
                Log.d("WS", response.getResponse());

                JSONArray array = new JSONArray(response.getResponse());

                ArrayList<AbsenceByClass> students = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    AbsenceByClass student = new AbsenceByClass();
                    student.setEnrollmentId(jsonObject.getInt("Enrollment_Id"));
                    student.setName(jsonObject.getString("Name"));
                    student.setStudentId(jsonObject.getInt("Student_Id"));
                    student.setUserId(jsonObject.getInt("User_Id"));
                    student.setAbsences(jsonObject.getInt("Absences"));

                    students.add(student);
                }

                callback.onComplete(students);
            }
        });

        call.execute(new ServiceCall(Url + "absencesbyclass/" + classId, "GET", ""));
    }


}
