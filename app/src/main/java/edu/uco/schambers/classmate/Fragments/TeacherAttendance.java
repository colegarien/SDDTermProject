/* Team 9Lives
 *
 * Author: Rayan Al-Hammami
 * Purpose:
 *   UI backend for teacher attendance and class management
 *   module.
 *      Edit 10/09/2015
 *      Added selectable functionality for student table rows
 */
package edu.uco.schambers.classmate.Fragments;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.ClassAdapter;
import edu.uco.schambers.classmate.AdapterModels.*;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.R;

public class TeacherAttendance extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Spinner sSemester;
    private Spinner sYear;
    User user;
    ClassAdapter classAdapter = new ClassAdapter();

    //UI Components
    ArrayList<AbsenceByClass> students = new ArrayList<>();

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

        /*//Set up 30 Student Dummy Records
        students[0] = new Student("Rayan","Al-Hammami",5, "Data Structures");
        students[1] = new Student("Rajiv","Mancharamo",3, "Data Structures");
        students[2] = new Student("Mossi","Alobaid",4, "Data Structures");
        students[3] = new Student("Sana","Mumallah",7,"Programming I");
        students[4] = new Student("Zakria","Hamdi",2,"Programming I");
        students[5] = new Student("Josh","Johnson",5,"Programming I");
        students[6] = new Student("Eric","Miller",3, "Programming I");
        students[7] = new Student("Tim","Thompson",4,"Programming I");
        students[8] = new Student("Tom","Timson",7,"Programming II");
        students[9] = new Student("Ralph","Matheson",2,"Programming II");
        students[10] = new Student("Pete","Peterson",5,"Programming II");
        students[11] = new Student("Frank","Wilson",3,"Programming II");
        students[12] = new Student("James","Taylor",4,"Computer Organization");
        students[13] = new Student("Jimmy","Page",7,"Computer Organization");
        students[14] = new Student("Robert","Plant",2,"Computer Organization");
        students[15] = new Student("John","Bonham",5,"Computer Organization");
        students[16] = new Student("John-Paul","Jones",3,"Computer Organization");
        students[17] = new Student("Ozzy","Smith",4,"Data Structures");
        students[18] = new Student("Randy","Teller",7,"Data Structures");
        students[19] = new Student("Sam","Smith",2,"Data Structures");
        students[20] = new Student("Dean","Howard",5,"Data Structures");
        students[21] = new Student("Paul","Mithcell",3,"Data Structures");
        students[22] = new Student("Will","Black",4,"Programming I");
        students[23] = new Student("Isaac","Turner",7,"Programming I");
        students[24] = new Student("Abdualaziz","Ahmed",2,"Programming I");
        students[25] = new Student("Zachary","Lee",5,"Programming I");
        students[26] = new Student("Tom","Hanks",3,"Programming II");
        students[27] = new Student("Albert","Albertson",4,"Programming II");
        students[28] = new Student("Raymond","Butler",7,"Programming II");
        students[29] = new Student("Howard","Howardson",2,"Programming II");
        //End dummy record insertion*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);
        initUI(rootView);

        return rootView;
    }


    private void initUI(final View rootView) {
        try {
            user = TokenUtility.parseUserToken(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sSemester = (Spinner) rootView.findViewById(R.id.semester_sp);
        sYear = (Spinner) rootView.findViewById(R.id.year_sp);

        //ArrayList set up to hold values of courses
        final List<SpinnerItem> spinnerArray =  new ArrayList<>();
        //Hard-coded Course Selection temporarily for testing purposes
        spinnerArray.add(new SpinnerItem("Select Course..", "-1"));
        //spinnerArray.add("All"); Retained just in case functionality is returned
        /*spinnerArray.add("Data Structures");
        spinnerArray.add("Programming I");
        spinnerArray.add("Programming II");
        spinnerArray.add("Computer Organization");*/
        //Link Spinner control to course ArrayList from above
        final ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                rootView.getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) rootView.findViewById(R.id.spinnerClassSelect);
        sItems.setAdapter(adapter);

        //ArrayList set up to hold values of sorting options
        final List<String> spinnerArraySort =  new ArrayList<String>();
        //Hard-coded sorting options temporarily for testing purposes
        spinnerArraySort.add("Sort..");
        spinnerArraySort.add("By Name A-Z");
        spinnerArraySort.add("By Name Z-A");
        spinnerArraySort.add("By Absences Low-High");
        spinnerArraySort.add("By Absences High-Low");
        //Link Spinner control to sort ArrayList from above
        ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(
                rootView.getContext(), android.R.layout.simple_spinner_item, spinnerArraySort);

        adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItemsSort = (Spinner) rootView.findViewById(R.id.spinnerSort);
        sItemsSort.setAdapter(adapterSort);
        sItems.setSelection(0);

        sSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = sYear.getSelectedItemPosition();
                if (pos == 0)
                    return;
                int year = Integer.parseInt(sYear.getSelectedItem().toString());
                String semester = adapterView.getSelectedItem().toString();
                classAdapter.professorClasses(user.getpKey(), semester, year, new Callback<ArrayList<edu.uco.schambers.classmate.AdapterModels.Class>>() {
                    @Override
                    public void onComplete(ArrayList<Class> result) throws Exception {
                        spinnerArray.clear();
                        spinnerArray.add(new SpinnerItem("Select Course..", "-1"));
                        for (Class classItem : result) {
                            spinnerArray.add(new SpinnerItem(classItem.getClass_name(), Integer.toString(classItem.getId())));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = sSemester.getSelectedItemPosition();
                if (pos == 0)
                    return;
                int year = Integer.parseInt(adapterView.getSelectedItem().toString());
                String semester = sSemester.getSelectedItem().toString();
                classAdapter.professorClasses(user.getpKey(), semester, year, new Callback<ArrayList<edu.uco.schambers.classmate.AdapterModels.Class>>() {
                    @Override
                    public void onComplete(ArrayList<Class> result) throws Exception {
                        spinnerArray.clear();
                        spinnerArray.add(new SpinnerItem("Select Course..", "-1"));
                        for (Class classItem : result) {
                            spinnerArray.add(new SpinnerItem(classItem.getClass_name(), Integer.toString(classItem.getId())));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Add onItemSelectedListener to Course Selection spinner
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                SpinnerItem item = (SpinnerItem)parent.getSelectedItem();
                classAdapter.studentAbsences(Integer.parseInt(item.getValue()), new Callback<ArrayList<AbsenceByClass>>() {
                    @Override
                    public void onComplete(ArrayList<AbsenceByClass> result) throws Exception {
                        for (int i=0; i<result.size(); i++){
                            Log.d("RAYAN TEST", result.get(i).getName());
                        }
                        students.clear();
                        for (AbsenceByClass studentAbsense : result) {
                            students.add(studentAbsense);
                        }

                        //Call buildTable method to build student attendance table dynamically
                        buildTable(rootView, pos);
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Add onItemSelectedListener to Sort Selection spinner
        sItemsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*//If selected item is XXX sort by name in ascending order
                if (parent.getSelectedItem().equals("By Name A-Z")) {
                    sortByName("asc");
                }
                //Otherwise, if selected item is XXX sort by name descending order
                else if (parent.getSelectedItem().equals("By Name Z-A")){
                    sortByName("des");
                }
                //Otherwise, if selected item is XXX sort by # of absences ascending order
                else if (parent.getSelectedItem().equals("By Absences Low-High")){
                    sortByAbsences("asc");
                }
                //Otherwise, if selected item is XXX sort by # of absences descending order
                else if (parent.getSelectedItem().equals("By Absences High-Low")){
                    sortByAbsences("des");
                }
                //Call buildTable method to build student attendance table dynamically
                //based on the new sort pattern
                buildTable(rootView,spinnerArray,sItems.getSelectedItemPosition());*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //buildTable method to build the table dynamically, and reduce complications above
    public void buildTable(View view, int position){
        //Create a table layout object and link it to the fragment's table layout
        TableLayout teacherAttendanceTable = (TableLayout) view.findViewById(R.id.teacherAttendanceTable);
        teacherAttendanceTable.setStretchAllColumns(true);
        teacherAttendanceTable.bringToFront();
        teacherAttendanceTable.removeAllViews();
        //Loops through the entire list of students
        for (final AbsenceByClass student : students) {
            //If the current student's course is equal to the course chosen

            //Store the number of absences
            final int absences = student.getAbsences();
            //Create a new table row
            TableRow tr = new TableRow(view.getContext());
            //Set up the clickable functionality for the table row
            tr.setClickable(true);
            tr.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //If the row is touched
                    if (event.getAction() == MotionEvent.ACTION_DOWN ||
                            event.getAction() == MotionEvent.ACTION_CANCEL) {
                        //Momentarily change background color to indicate the touch
                        v.setBackgroundColor(Color.GRAY);
                        //Set up a new fragment object
                        Fragment teacherAttendanceItem = TeacherAttendanceItem.newInstance(absences + "", student.getName());
                        //Pass the attendance info, replace the current fragment, and place old
                        //fragment on the backstack
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, teacherAttendanceItem);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        //Once touch is lifted, return the row color
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setBackgroundColor(Color.LTGRAY);
                    }
                    return false;
                }
            });
            //Set each column's data in the row
            TextView c0 = new TextView(view.getContext());
            c0.setText(student.getName());
            TextView c2 = new TextView(view.getContext());
            c2.setText(String.valueOf(student.getAbsences())); //student.getAbsences())
            //Add the data to the row
            tr.addView(c0);
            tr.addView(c2);
            //Add the row to the table
            teacherAttendanceTable.addView(tr);
        }
    }

    /*//sortByName method that takes asc for ascending or des for descending order
    public void sortByName(String sortType){
        int n = students.size();
        int k;
        for (int m = n; m >= 0; m--) {
            for (int i = 0; i < n - 1; i++) {
                k = i + 1;
                int comp = students.get(i).getStudentName().compareTo(students.get(k).getStudentName());
                if (sortType.equals("asc")) {
                    if (comp > 0) {
                        swapNumbers(i, k, students);
                    }
                }
                else{
                    if (comp < 0) {
                        swapNumbers(i, k, students);
                    }
                }
            }
        }

    }

    //sortByAbsences method that takes asc for ascending or des for descending order
    public void sortByAbsences(String sortType){
        int n = students.length;
        int k;
        for (int m = n; m >= 0; m--) {
            for (int i = 0; i < n - 1; i++) {
                k = i + 1;
                if (sortType.equals("asc")) {
                    if (students[i].numberAbsences > students[k].numberAbsences) {
                        swapNumbers(i, k, students);
                    }
                }
                else{
                    if (students[i].numberAbsences < students[k].numberAbsences) {
                        swapNumbers(i, k, students);
                    }
                }
            }
        }
    }

    //Utility method to aid in swapping numbers for the sorting methods
    private static void swapNumbers(int i, int j, ArrayList<Student> array) {

        Student temp;
        temp = array.;
        array[i] = array[j];
        array[j] = temp;
    }*/

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
