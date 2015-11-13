package edu.uco.schambers.classmate.Adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import edu.uco.schambers.classmate.AdapterModels.Attendance;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.AdapterModels.StudentAbsenceByClass;
import edu.uco.schambers.classmate.AdapterModels.StudentByClass;

public class AttendanceAdapter {
    private final static String Url = "http://classmateapi.azurewebsites.net/api/";

    // Save the list of student attendances.
    public static void saveAttendance(ArrayList<Attendance> attendanceList, final Callback<HttpResponse> callback) throws JSONException {
        JSONArray array = new JSONArray();

        for (Attendance attendance : attendanceList) {
            array.put(attendance);
        }

        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {
                callback.onComplete(result);
            }
        });

        call.execute(new ServiceCall(Url + "saveattendance", "POST", array.toString()));
    }

    // takes professor roll call for each class once roll call has started.
    public static void takeRollCall(int classId, Date date, final Callback<HttpResponse> callback) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Class_Id", classId);
        jsonObject.put("Date", date);

        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {
                callback.onComplete(result);
            }
        });

        call.execute(new ServiceCall(Url + "takerollcall", "POST", jsonObject.toString()));
    }

    // gets attendance and absence dates for a student in a class.
    public static void getStudentAbsencesByClass(int classId, int userId, final Callback<ArrayList<StudentAbsenceByClass>> callback) throws JSONException {
        ServiceHandlerAsync call = new ServiceHandlerAsync(new Callback<HttpResponse>() {

            @Override
            public void onComplete(HttpResponse result) throws Exception {
                JSONArray array = new JSONArray(result.getResponse());

                ArrayList<StudentAbsenceByClass> list = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);

                    StudentAbsenceByClass student = new StudentAbsenceByClass();
                    student.setRollCall(jsonObject.getString("RollCall"));
                    student.setIsPresent(jsonObject.getBoolean("IsPresent"));
                    list.add(student);
                }

                callback.onComplete(list);
            }
        });

        call.execute(new ServiceCall(Url + "studentabsencesbyclass/" + classId + "/" + userId, "GET", ""));
    }
}
