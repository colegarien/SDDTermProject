package edu.uco.schambers.classmate.Fragments;

import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.ClassAdapter;
import edu.uco.schambers.classmate.Adapter.EnrollmentAdapter;
import edu.uco.schambers.classmate.Adapter.HttpResponse;
import edu.uco.schambers.classmate.AdapterModels.Class;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.uco.schambers.classmate.AdapterModels.StudentAbsenceByClass;
import edu.uco.schambers.classmate.AdapterModels.TokenUtility;
import edu.uco.schambers.classmate.AdapterModels.User;
import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeacherClassManagement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherClassManagement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentEnrollment extends Fragment {
    private OnFragmentInteractionListener mListener;

    ArrayList<Class> classList;

    Spinner school;
    ArrayAdapter<String> schoolAdapter;
    ArrayList<String> schoolList;

    Spinner semester;
    ArrayAdapter<String> semesterAdapter;
    ArrayList<String> semesterList;

    Spinner year;
    ArrayAdapter<String> yearAdapter;
    ArrayList<String> yearList;
    TableLayout classTable;
    Context context;
    TableRow classTableHeader;

    EnrollmentAdapter enrollmentAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TeacherClassManagement.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentEnrollment newInstance() {
        StudentEnrollment fragment = new StudentEnrollment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StudentEnrollment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_student_enrollment, container, false);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void initUI(final View rootView) throws JSONException {
        school = (Spinner)rootView.findViewById(R.id.sp_school);
        semester = (Spinner)rootView.findViewById(R.id.sp_semester);
        year = (Spinner)rootView.findViewById(R.id.sp_year);
        //Create a table layout object and link it to the fragment's table layout
        classTable = (TableLayout) rootView.findViewById(R.id.classTable);
        context = rootView.getContext();
        resetTable();
        // disable spinners on launching activity
        year.setEnabled(false);
        semester.setEnabled(false);

        // instantiate arraylists
        classList = new ArrayList<>();
        schoolList = new ArrayList<>();
        yearList = new ArrayList<>();
        semesterList = new ArrayList<>();

        // instantiate array adapters for each object
        schoolAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, schoolList);
        yearAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, yearList);
        semesterAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, semesterList);

        // bind lists to controls
        school.setAdapter(schoolAdapter);
        year.setAdapter(yearAdapter);
        semester.setAdapter(semesterAdapter);

        final User user = TokenUtility.parseUserToken(getActivity());
        enrollmentAdapter = new EnrollmentAdapter();

        enrollmentAdapter.getSchools(new Callback<ArrayList<String>>() {
            @Override
            public void onComplete(ArrayList<String> result) throws Exception {
                //schoolList = result;
                schoolList.clear();
                schoolList.add("Select school...");
                for (String school : result) {
                    schoolList.add(school);
                }
                schoolAdapter.notifyDataSetChanged();
            }
        });

        school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSchool = school.getSelectedItem().toString();
                enrollmentAdapter.getYears(selectedSchool, new Callback<ArrayList<String>>() {
                    @Override
                    public void onComplete(ArrayList<String> result) throws Exception {
                        yearList.clear();
                        yearList.add("Select Year");
                        for (String year : result) {
                            yearList.add(year);
                        }
                        yearAdapter.notifyDataSetChanged();
                        year.setEnabled(true);
                        year.setSelection(0);
                        semesterList.clear();
                        semesterAdapter.notifyDataSetChanged();
                        semester.setEnabled(false);
                    }
                });

                resetTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(year.getSelectedItemPosition() == 0){
                    return;
                }
                String selectedSchool = school.getSelectedItem().toString();
                String selectedYear = year.getSelectedItem().toString();
                enrollmentAdapter.getSemesters(selectedSchool, selectedYear, new Callback<ArrayList<String>>() {
                    @Override
                    public void onComplete(ArrayList<String> result) throws Exception {
                        semesterList.clear();
                        semesterList.add("Select Semester");
                        for (String semester : result) {
                            semesterList.add(semester);
                        }
                        semesterAdapter.notifyDataSetChanged();
                        semester.setEnabled(true);
                        semester.setSelection(0);
                    }
                });

                resetTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSchool = school.getSelectedItem().toString();
                String selectedYear = year.getSelectedItem().toString();
                String selectedSemester = semester.getSelectedItem().toString();

                try {
                    enrollmentAdapter.getClasses(selectedSchool, selectedYear, selectedSemester, TokenUtility.parseUserToken(getActivity()).getId(), new Callback<ArrayList<Class>>() {
                        @Override
                        public void onComplete(ArrayList<Class> result) throws Exception {
                            classList.clear();
                            for (Class c : result) {
                                classList.add(c);
                            }

                            buildTable();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button clickedButton = (Button) view;

            }
        };

        //refreshList();
        /*((EditText) rootView.findViewById(R.id.classname_et)).setText("");
        rootView.findViewById(R.id.classname_et).requestFocus();
        ((EditText) rootView.findViewById(R.id.schoolname_et)).setText("");*/

    }

    /*public void refreshList() throws JSONException {
        final User user = TokenUtility.parseUserToken(getActivity());
        final ClassAdapter classAdapter = new ClassAdapter();

        classAdapter.professorClasses(user.getpKey(), new Callback<ArrayList<Class>>() {
            @Override
            public void onComplete(ArrayList<Class> result) throws Exception {
                listItems.clear();
                for (Class classItem : result) {
                    listItems.add(
                            classItem.getClass_name() + "-" +
                                    classItem.getSemester() + "-" +
                                    Integer.toString(classItem.getYear()) + "\n" +
                                    classItem.getSchool());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }*/

    public void buildTable(){
        resetTable();

        //Loops through the entire list of students
        for (final Class classItem : classList) {
            //Create a new table row
            TableRow tr = new TableRow(context);

            //Set each column's data in the row
            TextView c0 = new TextView(context);
            c0.setText(classItem.getClass_name());
            TextView c1 = new TextView(context);
            c1.setText(classItem.getProfessor_name());
            final Button c2 = new Button(context);
            c2.setText("Enroll");
            //c2.setEnabled(false);
            if(classItem.isEnrolled()){
                c2.setEnabled(false);
                c2.setText("Enrolled");
            }

            if (!isSemesterAvailable(classItem.getYear(), classItem.getSemester())){
                c2.setEnabled(false);
                c2.setText("View Only");
            }

            c2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        enrollmentAdapter.classEnroll(TokenUtility.parseUserToken(getActivity()).getId(), classItem.getId(), new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse result) throws Exception {
                                Log.d("EnrollDebug", "Enroll result" + result);
                                if(result.getHttpCode() == 409){
                                    Toast.makeText(getActivity(), "Cant Enroll in the same class twice", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getActivity(), "Enrollment was Successful ", Toast.LENGTH_LONG).show();
                                    c2.setEnabled(false);
                                    c2.setText("Enrolled");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //Add the data to the row
            tr.addView(c0, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr.addView(c1, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr.addView(c2, new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            //Add the row to the table
            classTable.addView(tr);
        }
    }

    private void resetTable(){
        if (classTable == null){
            return;
        }

        classTable.bringToFront();
        classTable.removeAllViews();

        if (classTableHeader == null) {
            //Create a new table row
            classTableHeader = new TableRow(context);
            //Set up the clickable functionality for the table row
            classTableHeader.setClickable(false);

            TextView hClassName = new TextView(context);
            hClassName.setText("Class");
            TextView hProf = new TextView(context);
            hProf.setText("Professor");
            TextView hEnrollStatus = new TextView(context);
            hEnrollStatus.setText("Enroll");

            Drawable cellBackground = ContextCompat.getDrawable(context, R.drawable.cell_shape);
            hClassName.setBackground(cellBackground);
            hProf.setBackground(cellBackground);
            hEnrollStatus.setBackground(cellBackground);

            hClassName.setTypeface(Typeface.DEFAULT_BOLD);
            hProf.setTypeface(Typeface.DEFAULT_BOLD);
            hEnrollStatus.setTypeface(Typeface.DEFAULT_BOLD);

            hClassName.setPadding(10, 0, 0, 0);
            hProf.setPadding(10, 0, 0, 0);
            hEnrollStatus.setPadding(10, 0, 0, 0);

            hClassName.setTextSize(18);
            hProf.setTextSize(18);
            hEnrollStatus.setTextSize(18);

            classTableHeader.addView(hClassName, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            classTableHeader.addView(hProf, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            classTableHeader.addView(hEnrollStatus, new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        }

        classTable.addView(classTableHeader);
    }

    private boolean isSemesterAvailable(int year, String semester){
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);

        // For example: class year 2014 < current year 2015
        // Semester is no longer available
        if (year< currentYear){
            return false;
        }

        if (year == currentYear){

            int monthInt = cal.get(Calendar.MONTH) + 1;

            // For example: semester spring && month March > month Jan.
            // Semester is no longer available
            if (semester.equalsIgnoreCase("spring") && monthInt > 1) {
                return false;
            }

            if (semester.equalsIgnoreCase("summer") && monthInt > 6) {
                return false;
            }

            if (semester.equalsIgnoreCase("fall") && monthInt > 8) {
                return false;
            }
        }

        return true;
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
