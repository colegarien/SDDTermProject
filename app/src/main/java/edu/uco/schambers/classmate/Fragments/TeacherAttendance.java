/* Team 9Lives
 *
 * Author: Rayan Al-Hammami
 * Purpose:
 *   UI backend for teacher attendance and class management
 *   module
 * Edits:
 *      11/25/2015 11:31 PM Rayan Al-Hammami*   Code Cleanup and Verification of Teacher Attendance Piechart Pulling Actual Data
 *      11/23/2015 2:54 AM	Rayan Al-Hammami*	Corrected (From what is apparent) Piechart Data Issue...
        11/22/2015 7:32 PM	nelalison	fixed TeacherAttendance
        11/12/2015 7:13 PM	nelalison	studentAttendance spinner populating from cloud DB
        11/10/2015 12:15 PM	Rayan Al-Hammami*	Added Note Functionality for Individual Students...
        10/27/2015 1:23 PM	Rayan Al-Hammami*	Added CSV Import for Schools in Class Management...
        10/25/2015 4:44 PM	nelalison	Added Enrollment button to student Interface
        10/11/2015 8:39 PM	Rayan Al-Hammami*	Added Simple Input Validation to Class Management Interface...
        10/11/2015 8:39 PM	Rayan Al-Hammami*	Cleaned Up Teacher Attendance Table and Added Semester/Year Spinners...
        10/11/2015 8:39 PM	ralhamami	Created Teacher Class Management Fragment and Edited TeacherInterface Launch Config...
        10/9/2015 3:44 AM	Rayan Al-Hammami*	Updated Comments, Author Blocks, and Cleaned Up...
        10/9/2015 3:44 AM	Rayan Al-Hammami*	Added Selectable Table Rows with Student Attendance Data Pie Chart Fragment...
        9/29/2015 6:42 AM	Steven	Merge branch 'ra_sortable_table'
        9/29/2015 6:37 AM	Rayan Al-Hammami*	Split Last Name,First Name and Additional Improvements...
        9/29/2015 6:37 AM	Rayan Al-Hammami*	Added Sorting Functionality for Attendance Table...
        9/29/2015 6:00 AM	Rayan Al-Hammami*	Split Last Name,First Name and Additional Improvements...
        9/29/2015 6:00 AM	Rayan Al-Hammami*	Added Sorting Functionality for Attendance Table...
        9/21/2015 6:48 PM	Rayan Al-Hammami*	Added Author Blocks to All of My Files...
        9/21/2015 6:20 PM	ralhamami	Added "Sort" Button and Button Borders...
        9/21/2015 6:20 PM	Rayan Al-Hammami*	Added "All" Option On Class Selection Spinner...
        9/17/2015 4:41 PM	Rayan Al-Hammami*	Added Scrollable Table With Test Values Populated by Listener...
        9/14/2015 11:39 PM	Rayan Al-Hammami*	Spinner class selection box added
        9/8/2015 1:31 PM	colegarien*	Filled Out TeacherRollCall UI...
        9/7/2015 4:47 PM	OzzyGreyman	Added TeacherRollCall Fragment...
        9/7/2015 3:16 PM	Steven	finish implementing fragment handling of UI elements
        9/4/2015 11:02 AM	Steven	Add debug fragment and implement fragment launch system...
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

import edu.uco.schambers.classmate.Adapter.AttendanceAdapter;
import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.ClassAdapter;
import edu.uco.schambers.classmate.AdapterModels.*;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.R;

public class TeacherAttendance extends Fragment {
    //Used for incoming fragment arguments, if any
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1,mParam2;

    //Spinner linked to semester selection
    private Spinner sSemester;

    //Spinner linked to year selection
    private Spinner sYear;

    //Will store the current user (Teacher) to pull related class information
    private User user;

    //Will store attendances, absences,
    //class id, and student id on specific student
    //to display attendance info, and pie chart in next fragment
    private int attendances=0,absences=0,classId=0,studentId=0;

    //Adapter type object to access and retrieve class information
    ClassAdapter classAdapter = new ClassAdapter();

    //ArrayList object to hold all students added to table of specific class
    ArrayList<AbsenceByClass> students = new ArrayList<>();

    //New instance method
    public static TeacherAttendance newInstance(String param1, String param2) {
        TeacherAttendance fragment = new TeacherAttendance();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //Default constructor
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
    }


    //Inflate the layout for this fragment, initialize, and return the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);
        initUI(rootView);

        return rootView;
    }

    //Initialization method
    private void initUI(final View rootView) {
        //Retrieve the user (Teacher) information from the TokenUtility
        try {
            user = TokenUtility.parseUserToken(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Link the semester and year spinners to their UI controls
        sSemester = (Spinner) rootView.findViewById(R.id.semester_sp);
        sYear = (Spinner) rootView.findViewById(R.id.year_sp);

        //ArrayList set up to hold values of courses
        final List<SpinnerItem> spinnerArray =  new ArrayList<>();

        //Using this as a "hint" or "label" for the spinner
        spinnerArray.add(new SpinnerItem("Select Course..", "-1"));

        //Link Spinner control to course ArrayList from above
        final ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                rootView.getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) rootView.findViewById(R.id.spinnerClassSelect);
        sItems.setAdapter(adapter);
        sItems.setSelection(0);

        //Add an OnItemSelectedListener to the semester spinner to populate class spinner
        //once a selection has been made
        sSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Get the year selection and pull this teacher's classes from the class adapter
                //Populate the spinner with this information
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

        //Similarly, add an OnItemSelectedListener to the year spinner to populate class spinner
        //once a selection has been made
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
                            classId = classItem.getId();
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
        //to populate table of students in that class
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;

                //Get the selected item, in order to pull the student absences information
                SpinnerItem item = (SpinnerItem)parent.getSelectedItem();
                classAdapter.studentAbsences(Integer.parseInt(item.getValue()), new Callback<ArrayList<AbsenceByClass>>() {
                    @Override
                    public void onComplete(ArrayList<AbsenceByClass> result) throws Exception {
                        students.clear();
                        for (AbsenceByClass studentAbsence : result) {
                            students.add(studentAbsence);
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
    }

    //buildTable method to build the table dynamically, and reduce complications above
    public void buildTable(View view, int position){

        //Create a table layout object and link it to the fragment's table layout
        //Also remove all previous views
        TableLayout teacherAttendanceTable = (TableLayout) view.findViewById(R.id.teacherAttendanceTable);
        teacherAttendanceTable.setStretchAllColumns(true);
        teacherAttendanceTable.bringToFront();
        teacherAttendanceTable.removeAllViews();

        //Reset attendances and absences
        attendances=0;
        absences=0;

        //Loop through the entire list of students in the class
        for (final AbsenceByClass student : students) {

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

                        //If the current student's course is equal to the course chosen
                        studentId = student.getStudentId();

                        //Get Attendance Info
                        try {
                            AttendanceAdapter.getStudentAbsencesByClass(classId, studentId, new Callback<ArrayList<StudentAbsenceByClass>>() {
                                //Reset absences and attendances
                                //and tally up the numbers of each for later use
                                @Override
                                public void onComplete(ArrayList<StudentAbsenceByClass> result) throws Exception {
                                    attendances = 0;
                                    absences = 0;
                                    for (StudentAbsenceByClass sac: result){
                                        if (sac.isPresent())
                                            attendances++;
                                        else
                                            absences++;
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                                //Set up a new fragment object
                                Fragment teacherAttendanceItem = TeacherAttendanceItem.newInstance(absences + "", attendances + "", student.getName());
                                //Pass the attendance info, replace the current fragment, and place old
                                //fragment on the backstack
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container,teacherAttendanceItem);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }

                            //Once touch is lifted, return the row color
                            else if (event.getAction() == MotionEvent.ACTION_UP){
                                v.setBackgroundColor(Color.LTGRAY);
                            }

                            return false;
                        }
                    });

                    //Set each column's data in the row
                    TextView c0 = new TextView(view.getContext());
                    c0.setText(student.getName());
                    TextView c2 = new TextView(view.getContext());
                    c2.setText(String.valueOf(absences));

                    //Add the data to the row
                    tr.addView(c0);
                    tr.addView(c2);

                    //Add the row to the existing tablelayout
                    teacherAttendanceTable.addView(tr);
                }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
