/* Team 9Lives
 *
 * Author: Rayan Al-Hammami (Modified version of Student Attendance Module
 *         with permission of Mossi Al-Obaid)
 * Purpose:
 *   UI backend for teacher attendance and class management
 *   module.
 * Edits:
 *      11/25/2015 11:31 PM Rayan Al-Hammami*   Code Cleanup and Verification of Teacher Attendance Piechart Pulling Actual Data
 *      11/1/2015 9:36 PM	Matt McHughes	clean up code
        11/1/2015 9:30 PM	Matt McHughes	teacher class management only shows current schools teacher has added
        10/27/2015 3:59 PM	Steven	make table layout correct for all devices
        10/27/2015 3:36 PM	Rayan Al-Hammami*	Corrected Formatting in Class Management Table and Corrected Redundant Classes...
        10/27/2015 1:40 PM	Steven	fix crash on entering class management
        10/27/2015 1:24 PM	Rayan Al-Hammami*	Swapped Function Arguments for createClass in ClassAdapter and Tested...
        10/27/2015 1:23 PM	Rayan Al-Hammami*	Added CSV Import for Schools in Class Management...
        10/27/2015 10:18 AM	Matt McHughes	starting styles for 21+
        10/25/2015 4:07 PM	nelalison	renamed my Database package to AdapterModels
        10/12/2015 4:24 PM	nelalison	create class and update classList done
        10/11/2015 8:39 PM	Rayan Al-Hammami*	Added Simple Input Validation to Class Management Interface...
        10/11/2015 8:39 PM	ralhamami	Created Teacher Class Management Fragment and Edited TeacherInterface Launch Config...
        10/11/2015 8:39 PM	Rayan Al-Hammami*	Created Teacher Class Management Fragment and Edited TeacherInterface Launch Config...
        9/26/2015 3:51 PM	Steven	Refactor broadcast receiver into a background service....
        9/14/2015 8:41 PM	Connor Archer	Connor Archer's Teacher Question Module up to date.Completes my Sprint 1 Requirements.
        9/12/2015 2:52 PM	Steven	implement launching StudentResponseFragment through notifications...
*/
package edu.uco.schambers.classmate.Fragments;

import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.ClassAdapter;
import edu.uco.schambers.classmate.AdapterModels.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.uco.schambers.classmate.AdapterModels.TokenUtility;
import edu.uco.schambers.classmate.AdapterModels.User;
import edu.uco.schambers.classmate.R;

public class TeacherClassManagement extends Fragment {
    //Will hold incoming arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //Will hold added classes
    private ArrayList<String> listItems;

    //Will act as adapter to the above list
    private ArrayAdapter<String> adapter;

    //Spinner to hold schools
    private List<String> spinnerSchool;

    //Will reference button and spinner controls
    private Button addButton;
    private Spinner semester;
    private Spinner year;
    private Spinner schoolName;

    //Animation
    private Animation errorBlink;

    //Will reference shared preferences
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public static final String MyPREFS = "MyPREFS";
    public static final String MySCHOOL = "MySCHOOL";

    public static TeacherClassManagement newInstance(String param1, String param2) {
        TeacherClassManagement fragment = new TeacherClassManagement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherClassManagement() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_teacher_class_management, container, false);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void initUI(final View rootView) throws JSONException {
        //Set up button and spinner references
        addButton = (Button)rootView.findViewById(R.id.add_class_btn);
        semester = (Spinner)rootView.findViewById(R.id.semester_sp);
        year = (Spinner)rootView.findViewById(R.id.year_sp);

        //Initialize listItems
        listItems = new ArrayList<String>();
        String[] schoolArray = new String[25];
        spinnerSchool =  new ArrayList<String>();
        sp = getActivity().getSharedPreferences(MySCHOOL, Context.MODE_PRIVATE);

        //Get the schools from shared preferences and store them within the schoolArray
        for(int i = 0; i < sp.getInt("SCHOOL_COUNT", 1); i++){
            schoolArray[i] = sp.getString("SCHOOL_ARRAY_" + i, null);
            spinnerSchool.add(schoolArray[i]);
        }

        //Configure the school adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerSchool);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolName = (Spinner) rootView.findViewById(R.id.schoolname_sp);
        schoolName.setAdapter(adapter);

        //Get the current user's information
        final User user = TokenUtility.parseUserToken(getActivity());

        //Set up a new ClassAdapter object to retrieve class info
        final ClassAdapter classAdapter = new ClassAdapter();

        //Refresh the list
        refreshList();

        //Set the classname edittext to "" and return focus to it
        ((EditText) rootView.findViewById(R.id.classname_et)).setText("");
        rootView.findViewById(R.id.classname_et).requestFocus();

        //Add an onclicklistener to the add button
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean valid = true;

                //Make all necessary references
                EditText classNameTemp = ((EditText) rootView.findViewById(R.id.classname_et));
                Spinner semester = (Spinner) rootView.findViewById((R.id.semester_sp));
                Spinner year = (Spinner) rootView.findViewById((R.id.year_sp));

                //Validate the user input data
                if (classNameTemp.getText().toString().length() == 0) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    classNameTemp.startAnimation(errorBlink);
                    classNameTemp.setError("Class Name is Required!");
                    valid = false;
                }

                if (semester.getSelectedItem().toString().contains("..") ||
                        year.getSelectedItem().toString().contains("..") ||
                        schoolName.getSelectedItem().toString().contains("..")) {
                    Toast.makeText(getActivity(), "Select State/School/Semester/Year!", Toast.LENGTH_LONG)
                            .show();
                    valid = false;
                }

                //If the information is valid however, create the class in the DB through the
                //class adapter object, and refresh the on-screen list
                if (valid) {
                    try {
                        classAdapter.createClass(user.getpKey(), classNameTemp.getText().toString(),
                                schoolName.getSelectedItem().toString(), semester.getSelectedItem().toString(),
                                Integer.parseInt(year.getSelectedItem().toString()), new Callback<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean isAdded) throws Exception {
                                        if (isAdded) {
                                            Toast.makeText(getActivity(), "Class Added!", Toast.LENGTH_LONG).show();

                                            refreshList();
                                            ((EditText) rootView.findViewById(R.id.classname_et)).setText("");
                                            rootView.findViewById(R.id.classname_et).requestFocus();
                                        }
                                    }
                                }
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error creating new class", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    //Method for refreshing the list
    public void refreshList() throws JSONException {
        //Get user info and set up a new ClassAdapter object
        final User user = TokenUtility.parseUserToken(getActivity());
        final ClassAdapter classAdapter = new ClassAdapter();

        //Get this teacher's class information and populate
        //the on-screen list
        classAdapter.professorClasses(user.getpKey(), new Callback<ArrayList<Class>>() {
            @Override
            public void onComplete(ArrayList<Class> result) throws Exception {
                listItems.clear();
                TableLayout classTable = (TableLayout) getView().findViewById(R.id.classMgmtTable);
                classTable.removeAllViews();
                for (Class classItem : result) {
                    TableRow tr = new TableRow(getView().getContext());
                    String schoolnames[] = classItem.getSchool().split(" ");
                    String schoolAbbrev = "";
                    if (classItem.getSchool().length()>4) {
                        for (int i = 0; i < schoolnames.length; i++) {
                            if (!schoolnames[i].contains("of") || !schoolnames[i].contains("the")) {
                                schoolAbbrev += schoolnames[i].charAt(0);
                            }
                        }
                    }
                    else{
                        schoolAbbrev = classItem.getSchool();
                    }
                    //Set each column's data in the row
                    TextView c0 = new TextView(getView().getContext());
                    c0.setText(classItem.getClass_name());
                    TextView c2 = new TextView(getView().getContext());
                    c2.setText(String.valueOf(schoolAbbrev));
                    TextView c3 = new TextView(getView().getContext());
                    c3.setText(String.valueOf(classItem.getSemester() + "/" + classItem.getYear()));
                    tr.addView(c0, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                    tr.addView(c2, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, .5f));
                    tr.addView(c3, new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f));

                    //Add the row to the table
                    classTable.addView(tr);
                    listItems.add(
                            classItem.getClass_name() + "-" +
                                    classItem.getSemester() + "-" +
                                    Integer.toString(classItem.getYear()) + "\n" +
                                    classItem.getSchool());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
