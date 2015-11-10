/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   Background Service for managing the classroom session
 *   Main function is to monitor Students attendance during class
 *
 * Edit: 10/12/2015
 *   Updated onStudentConnect method to gather student IP
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

    // TODO: change to Teacher object
    String currentTeacher = "";
    String student_pk = "";

    // TODO: change to special Student Adapter
    private ArrayList<String> studentInfo = new ArrayList<String>();

    public TeacherRollCallService() {
    }

    // TODO: use Teacher object (check for person responsible, may do myself)
    public static Intent getNewStartSessionIntent(Context context, String teacherName){
        Intent startSessionIntent = getBaseIntent(context);
        startSessionIntent.setAction(ACTION_START_ROLL_CALL_SESSION);
        Bundle bundle = new Bundle();
        // TODO: as mentioned above, this will be changed to teacher object
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
        if (intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_END_ROLL_CALL_SESSION:
                    ((TeacherRollCallAction) listenForStudents).stopListening();
                    // TODO: submit attendance to database
                    Toast.makeText(getApplicationContext(), "Class Session Closed", Toast.LENGTH_LONG).show();
                    stopSelf();
                    break;
                case ACTION_START_ROLL_CALL_SESSION:
                    // TODO: change to Teacher object
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

    // for bound fragments
    public ArrayList<String> getStudentInfo(){
        return studentInfo;
    }

    @Override
    public void onStudentConnect(String pk, InetAddress ip) {
        // TODO: add student ID to ArrayList (possibly query from DB)
        Log.d("StudentConnect", "Connected PK: " + pk);
        student_pk = pk;
        studentInfo.add(student_pk);

        // notify student attendance observers
        StudentAttendanceObservable.getInstance().directNotifyObservers(studentInfo);

        if (ip!=null){
            IPAddressManager.getInstance().addStudentAddress(ip);
            Log.d("StudentConnect", "IP Added: " + ip.toString());
        }
    }

    public class LocalBinder extends Binder {
        public TeacherRollCallService getService(){
            // return this instance of the for calling of public methods
            return TeacherRollCallService.this;
        }
    }
}
