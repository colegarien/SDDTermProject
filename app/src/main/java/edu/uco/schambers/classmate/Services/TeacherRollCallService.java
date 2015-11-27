/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   Background Service for managing the classroom session
 *   Main function is to monitor Students attendance during class
 *
 * Edit: 11/19/2015
 *   Refactoring so the service holds student attendance information
 *
 */

package edu.uco.schambers.classmate.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import edu.uco.schambers.classmate.AdapterModels.Attendance;
import edu.uco.schambers.classmate.AdapterModels.StudentByClass;
import edu.uco.schambers.classmate.AdapterModels.User;
import edu.uco.schambers.classmate.Fragments.TeacherRollCall;
import edu.uco.schambers.classmate.ListenerInterfaces.OnStudentConnectListener;
import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;
import edu.uco.schambers.classmate.ObservableManagers.StudentAttendanceObservable;
import edu.uco.schambers.classmate.SocketActions.SocketAction;
import edu.uco.schambers.classmate.SocketActions.StudentReceiveQuestionsAction;
import edu.uco.schambers.classmate.SocketActions.TeacherRollCallAction;

public class TeacherRollCallService extends Service implements OnStudentConnectListener{
    public static final String ACTION_END_ROLL_CALL_SESSION = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_END_ROLL_CALL_SESSION";
    public static final String ACTION_START_ROLL_CALL_SESSION = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_START_ROLL_CALL_SESSION";

    private IBinder locBinder = new LocalBinder();

    private SocketAction listenForStudents;

    Handler handler;

    String currentTeacher = "";
    String student_pk = "";

    // keeps track of student primary keys
    private ArrayList<String> studentInfo = new ArrayList<String>();
    // attendance records for each student
    private ArrayList<Attendance> studentAttendance = new ArrayList<Attendance>();
    // student records gotten from database
    private ArrayList<StudentByClass> studentByClass = new ArrayList<StudentByClass>();

    public TeacherRollCallService() {
    }

    public static Intent getNewStartSessionIntent(Context context, String teacherName){
        Intent startSessionIntent = getBaseIntent(context);
        startSessionIntent.setAction(ACTION_START_ROLL_CALL_SESSION);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeacherRollCall.ARG_TEACHER, teacherName);
        startSessionIntent.putExtras(bundle);
        return startSessionIntent;
    }
    public static Intent getNewEndSessionIntent(Context context){
        Intent endSessionIntent = getBaseIntent(context);
        endSessionIntent.setAction(ACTION_END_ROLL_CALL_SESSION);
        return endSessionIntent;
    }

    private static Intent getBaseIntent(Context context){
        Intent baseRollCallIntent= new Intent(context, TeacherRollCallService.class);
        return baseRollCallIntent;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        listenForStudents = new TeacherRollCallAction(this);
        listenForStudents.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_END_ROLL_CALL_SESSION:
                    ((TeacherRollCallAction) listenForStudents).stopListening();
                    Toast.makeText(getApplicationContext(), "Class Session Closed", Toast.LENGTH_LONG).show();
                    stopSelf();
                    break;
                case ACTION_START_ROLL_CALL_SESSION:
                    currentTeacher = (String) intent.getExtras().getSerializable(TeacherRollCall.ARG_TEACHER);
                    Toast.makeText(getApplicationContext(), "Class Session Started", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return locBinder;
    }

    // Functionality available to bound fragments
    public ArrayList<String> getStudentInfo(){
        return studentInfo;
    }
    public ArrayList<Attendance> getStudentAttendance(){
        return studentAttendance;
    }
    public ArrayList<StudentByClass> getStudentByClass(){
        return studentByClass;
    }
    public void addAttendance(Attendance a){
        studentAttendance.add(a);
        Log.d("TeacherRollCallService", "Enroll ID: "+a.getEnrollmentId());
    }
    public void addStudent(StudentByClass s){
        // verify that the student has not connected to the class session
        boolean inClassAlready = false;
        for (StudentByClass stu : studentByClass) {
            if (stu.getId() == s.getId()){
                inClassAlready = true;
                break;
            }
        }
        // if the student has not checked in yet
        if(!inClassAlready) {
            studentByClass.add(s);
            Log.d("TeacherRollCallService","Student Added: "+s.getName());

            Attendance newStu = new Attendance();
            newStu.setEnrollmentId(s.getEnrollmentId());
            newStu.setDate(new Date());

            this.addAttendance(newStu);
        }
    }

    @Override
    public void onStudentConnect(String pk, InetAddress ip) {
        Log.d("StudentConnect", "Connected PK: " + pk);
        student_pk = pk;

        // verify that the student has not connected already
        boolean studentInAlready = false;
        for(String cur_pk : studentInfo){
            if(cur_pk==pk){
                studentInAlready= true;
                break;
            }
        }
        // if the student has yet to connect
        if(!studentInAlready) {
            studentInfo.add(student_pk);

            // notify student attendance observers (i.e. TeacherRollCall fragment)
            StudentAttendanceObservable.getInstance().directNotifyObservers(studentInfo);
            if (ip != null) {
                IPAddressManager.getInstance().addStudentAddress(ip);
                Log.d("StudentConnect", "IP Added: " + ip.toString());
            }
        }
    }

    public class LocalBinder extends Binder {
        public TeacherRollCallService getService(){
            // return this instance of the for calling of public methods
            return TeacherRollCallService.this;
        }
    }
}
