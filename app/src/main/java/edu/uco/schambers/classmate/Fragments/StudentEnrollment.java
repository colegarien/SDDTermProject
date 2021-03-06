package edu.uco.schambers.classmate.Fragments;

import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.EnrollmentAdapter;
import edu.uco.schambers.classmate.Adapter.HttpResponse;
import edu.uco.schambers.classmate.AdapterModels.Class;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import edu.uco.schambers.classmate.AdapterModels.TokenUtility;
import edu.uco.schambers.classmate.AdapterModels.User;
import edu.uco.schambers.classmate.R;

/**
 * Created by Nelson.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentEnrollment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentEnrollment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentEnrollment extends Fragment {
    /*variables declaration*/
    private OnFragmentInteractionListener mListener;

    ArrayList<Class> classList;//declaring arraylist of type class

    Spinner school;//declaring spinner name school
    ArrayAdapter<String> schoolAdapter; //declaring adapter array name schooladapter
    ArrayList<String> schoolList;//declaring arraylist for schoollist

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
     * @return A new instance of fragment StudentEnrollment.
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

    /* view creation for above record */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_enrollment, container, false);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    /*     GUI    setting    */
    private void initUI(final View rootView) throws JSONException {
        school = (Spinner) rootView.findViewById(R.id.sp_school);
        semester = (Spinner) rootView.findViewById(R.id.sp_semester);
        year = (Spinner) rootView.findViewById(R.id.sp_year);
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
        schoolAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, schoolList);
        yearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, yearList);
        semesterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, semesterList);

        // bind lists to controls
        school.setAdapter(schoolAdapter);
        year.setAdapter(yearAdapter);
        semester.setAdapter(semesterAdapter);

        final User user = TokenUtility.parseUserToken(getActivity());
        enrollmentAdapter = new EnrollmentAdapter();
/*  getting school lists    */
        enrollmentAdapter.getSchools(new Callback<ArrayList<String>>() {
            @Override
            public void onComplete(ArrayList<String> result) throws Exception {
                schoolList.clear();
                schoolList.add("Select school...");
                for (String school : result) {
                    schoolList.add(school);
                }
                schoolAdapter.notifyDataSetChanged();
            }
        });
/*  adding listener for school list and getting all record about school enrollment like year,smester etc   */
        school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSchool = school.getSelectedItem().toString();
                try {
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                resetTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
/* checking which school is selected and inserting selected schoolinto record and enabled there features   */
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (year.getSelectedItemPosition() == 0) {
                    return;
                }
                String selectedSchool = school.getSelectedItem().toString();
                String selectedYear = year.getSelectedItem().toString();
                try {
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                resetTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
 /*    again inserting selected record to list by convering them into repective types as list,
          first get record with the help of methdods above defined  then store into list  */

        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSchool = school.getSelectedItem().toString();
                String selectedYear = year.getSelectedItem().toString();
                String selectedSemester = semester.getSelectedItem().toString();

                try {
                    enrollmentAdapter.getClasses(selectedSchool, selectedYear, selectedSemester, TokenUtility.parseUserToken(getActivity()).getpKey(), new Callback<ArrayList<Class>>() {
                        @Override
                        public void onComplete(ArrayList<Class> result) throws Exception {
                            classList.clear();
                            for (Class c : result) {
                                classList.add(c);
                            }

                            buildTable();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
/* button making and adding event on button click   */
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button clickedButton = (Button) view;

            }
        };

    }

    /* displaying above all recorded data in the form of table */
    public void buildTable() {
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
            if (classItem.isEnrolled()) {
                c2.setEnabled(false);
                c2.setText("Enrolled");
            }

            if (!isSemesterAvailable(classItem.getYear(), classItem.getSemester())) {
                c2.setEnabled(false);
                c2.setText("View Only");
            }
/*   adding events and listener on button click with the help of http codes and displaying error messages if there is any error  */
            c2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        enrollmentAdapter.classEnroll(TokenUtility.parseUserToken(getActivity()).getpKey(), classItem.getId(), new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse result) throws Exception {
                                Log.d("EnrollDebug", "Enroll result" + result);
                                if (result.getHttpCode() == 409) {
                                    Toast.makeText(getActivity(), "Cant Enroll in the same class twice", Toast.LENGTH_LONG).show();
                                } else if (result.getHttpCode() >= 400) {
                                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_LONG).show();
                                } else {
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

    private void resetTable() {
        if (classTable == null) {
            return;
        }

        classTable.bringToFront();
        classTable.removeAllViews();

        if (classTableHeader == null) {
            //Create a new table row
            classTableHeader = new TableRow(context);
            //Set up the clickable functionality for the table row
            classTableHeader.setClickable(false);
/*    designing of table settting attributes of table for userinterface . like paddding,background,foreground,textsize,textcolor etc   */
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
/*   setting header of the table */
            classTableHeader.addView(hClassName, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            classTableHeader.addView(hProf, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            classTableHeader.addView(hEnrollStatus, new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        }

        classTable.addView(classTableHeader);
    }

    private boolean isSemesterAvailable(int year, String semester) {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);

        // For example: class year 2014 < current year 2015
        // Semester is no longer available
        if (year < currentYear) {
            return false;
        }

        if (year == currentYear) {

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

    private void launchFragment(Fragment f) {
        if (f != null) {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.fragment_container, f).addToBackStack(null);
            trans.commit();
        }
    }
}
