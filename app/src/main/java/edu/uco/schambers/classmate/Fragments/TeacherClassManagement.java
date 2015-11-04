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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
public class TeacherClassManagement extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ListView addedClasses;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> schooladapter;
    private List<String> spinnerArray;
    Button addButton;
    Spinner semester;
    Spinner year;
    private Spinner schoolName;
    ProgressBar pb;
    private Animation errorBlink;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public static final String MyPREFS = "MyPREFS";
    public static final String MySCHOOL = "MySCHOOL";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherClassManagement.
     */
    // TODO: Rename and change types and number of parameters
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
        //addedClasses = (ListView)rootView.findViewById(R.id.add_class_lst);
        addButton = (Button)rootView.findViewById(R.id.add_class_btn);
        semester = (Spinner)rootView.findViewById(R.id.semester_sp);
        year = (Spinner)rootView.findViewById(R.id.year_sp);
        listItems = new ArrayList<String>();
        String[] schoolArray = new String[25];
        spinnerArray =  new ArrayList<String>();
        sp = getActivity().getSharedPreferences(MySCHOOL, Context.MODE_PRIVATE);

        for(int i = 0; i < sp.getInt("SCHOOL_COUNT", 1); i++){
            schoolArray[i] = sp.getString("SCHOOL_ARRAY_" + i, null);
            spinnerArray.add(schoolArray[i]);
            Log.i("school array", sp.getString("SCHOOL_ARRAY_" + i, "not found"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolName = (Spinner) rootView.findViewById(R.id.schoolname_sp);
        schoolName.setAdapter(adapter);


//        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, listItems);
//        //addedClasses.setAdapter(adapter);
//        Spinner stateName = (Spinner)rootView.findViewById(R.id.state_sp);
//        stateName.setSelection(0);
//        stateName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Scanner scanner = new Scanner(getResources().openRawResource(R.raw.schools));
//                List<String> list = new ArrayList<String>();
//                while (scanner.hasNextLine()) {
//                    String data = scanner.nextLine();
//                    String[] values = data.split(",");
//                    if (values[1].equals(parent.getSelectedItem().toString()))
//                        list.add(values[0]);
//                }
//                schooladapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
//                Spinner schoolName = (Spinner) rootView.findViewById(R.id.schoolname_sp);
//                schoolName.setAdapter(schooladapter);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        final Spinner schoolName = ((Spinner) rootView.findViewById(R.id.schoolname_sp));
        final User user = TokenUtility.parseUserToken(getActivity());
        final ClassAdapter classAdapter = new ClassAdapter();

        refreshList();
        ((EditText) rootView.findViewById(R.id.classname_et)).setText("");
        rootView.findViewById(R.id.classname_et).requestFocus();

        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean valid = true;
                EditText classNameTemp = ((EditText) rootView.findViewById(R.id.classname_et));

                Spinner semester = (Spinner) rootView.findViewById((R.id.semester_sp));
                Spinner year = (Spinner) rootView.findViewById((R.id.year_sp));

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
                if (valid) {

                    try {

                        classAdapter.createClass(user.getpKey(), classNameTemp.getText().toString(), schoolName.getSelectedItem().toString(), semester.getSelectedItem().toString(), Integer.parseInt(year.getSelectedItem().toString()), new Callback<Boolean>() {
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

                    /*listItems.add(classNameTemp.getText() + "-" +
                            semester.getSelectedItem() + "-" + year.getSelectedItem() + "\n" +
                            schoolName.getText());
                    adapter.notifyDataSetChanged();
                    ((EditText) rootView.findViewById(R.id.classname_et)).setText("");
                    rootView.findViewById(R.id.classname_et).requestFocus();
                    ((EditText) rootView.findViewById(R.id.schoolname_et)).setText("");
                    Toast.makeText(getActivity(), "Class Added!", Toast.LENGTH_LONG)
                            .show();*/
                }


            }
        });


    }

    public void refreshList() throws JSONException {
        final User user = TokenUtility.parseUserToken(getActivity());
        final ClassAdapter classAdapter = new ClassAdapter();

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
