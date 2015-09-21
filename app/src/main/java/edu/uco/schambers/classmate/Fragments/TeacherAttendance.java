/* Team 9Lives
 *
 * Author: Rayan Al-Hammami
 * Purpose:
 *   UI backend for teacher attendance and class management
 *   module.
 */
package edu.uco.schambers.classmate.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentRollCall.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherAttendance#newInstance} factory method to
 * create an instance of this fragment.
 */
//TESTING CLASS - Student objects are created and fill an array that populates
//the main table
class Student{
    String studentName;
    int numberAbsences;
    String course;
    public String getStudentName(){return studentName;}
    public int getNumberAbsences(){return numberAbsences;}
    public String getCourse(){return course;}
    Student(String studentName, int numberAbsences,String course){
        this.studentName = studentName;
        this.numberAbsences=numberAbsences;
        this.course = course;
    }
}


public class TeacherAttendance extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI Components
    private Spinner spinClassSelect;
    Student [] students = new Student[30];

    private SharedPreferences prefs;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentRollCall.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherAttendance newInstance(String param1, String param2) {
        TeacherAttendance fragment = new TeacherAttendance();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherAttendance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        students[0] = new Student("Rayan Al-Hammami",5, "Data Structures");
        students[1] = new Student("Rajiv Mancharamo",3, "Data Structures");
        students[2] = new Student("Mossi Alobaid",4, "Data Structures");
        students[3] = new Student("Sana Mumallah",7,"Programming I");
        students[4] = new Student("Zakria Hamdi",2,"Programming I");
        students[5] = new Student("Josh Johnson",5,"Programming I");
        students[6] = new Student("Eric Miller",3, "Programming I");
        students[7] = new Student("Tim Thompson",4,"Programming I");
        students[8] = new Student("Tom Timson",7,"Programming II");
        students[9] = new Student("Ralph Matheson",2,"Programming II");
        students[10] = new Student("Pete Peterson",5,"Programming II");
        students[11] = new Student("Frank Wilson",3,"Programming II");
        students[12] = new Student("James Taylor",4,"Computer Organization");
        students[13] = new Student("Jimmy Page",7,"Computer Organization");
        students[14] = new Student("Robert Plant",2,"Computer Organization");
        students[15] = new Student("John Bonham",5,"Computer Organization");
        students[16] = new Student("John-Paul Jones",3,"Computer Organization");
        students[17] = new Student("Ozzy Smith",4,"Data Structures");
        students[18] = new Student("Randy Teller",7,"Data Structures");
        students[19] = new Student("Sam Smith",2,"Data Structures");
        students[20] = new Student("Dean Howard",5,"Data Structures");
        students[21] = new Student("Paul Mithcell",3,"Data Structures");
        students[22] = new Student("Will Black",4,"Programming I");
        students[23] = new Student("Isaac Turner",7,"Programming I");
        students[24] = new Student("Abdualaziz Ahmed",2,"Programming I");
        students[25] = new Student("Zachary Lee",5,"Programming I");
        students[26] = new Student("Tom Hanks",3,"Programming II");
        students[27] = new Student("Albert Albertson",4,"Programming II");
        students[28] = new Student("Raymond Butler",7,"Programming II");
        students[29] = new Student("Howard Howardson",2,"Programming II");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);
        initUI(rootView);

        return rootView;
    }


    private void initUI(final View rootView)
    {
        final List<String> spinnerArray =  new ArrayList<String>();
        //Hard-coded temporatily for testing purposes
        spinnerArray.add("All");
        spinnerArray.add("Data Structures");
        spinnerArray.add("Programming I");
        spinnerArray.add("Programming II");
        spinnerArray.add("Computer Organization");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                rootView.getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) rootView.findViewById(R.id.spinnerClassSelect);
        sItems.setAdapter(adapter);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TableLayout teacherAttendanceTable = (TableLayout) rootView.findViewById(R.id.teacherAttendanceTable);
                teacherAttendanceTable.setStretchAllColumns(true);
                teacherAttendanceTable.bringToFront();
                teacherAttendanceTable.removeAllViews();
                for (int i = 0; i < students.length; i++) {
                    if (students[i].getCourse().equals(spinnerArray.get(position))||
                            spinnerArray.get(position).equals("All")) {
                        TableRow tr = new TableRow(view.getContext());
                        TextView c1 = new TextView(view.getContext());
                        c1.setText(students[i].getStudentName());
                        TextView c2 = new TextView(view.getContext());
                        c2.setText(String.valueOf(students[i].getNumberAbsences()));
                        tr.addView(c1);
                        tr.addView(c2);
                        teacherAttendanceTable.addView(tr);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //for (int i = 0; i < students.length; i++){

        //}
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
