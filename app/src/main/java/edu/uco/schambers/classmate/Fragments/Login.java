package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.uco.schambers.classmate.Adapter.AuthAdapter;
import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.ClassAdapter;
import edu.uco.schambers.classmate.Adapter.HttpResponse;
import edu.uco.schambers.classmate.Adapter.UserAdapter;
import edu.uco.schambers.classmate.AdapterModels.*;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public static final String MyPREFS = "MyPREFS";
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    AuthAdapter aa;

    //ui components
    private CheckBox cb;
    private EditText idET;
    private Button confirm;
    private EditText pass;
    private EditText confirmPass;
    private EditText name;
    private EditText email;
    private Spinner state;
    private Spinner school;
    private Animation errorBlink;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> schooladapter;

    //user class
    public User user;
    private DataRepo dr;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Login() {
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        Logout();
        initUI(rootView);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initUI(final View rootView) {
        cb = (CheckBox) rootView.findViewById(R.id.student_cb);
        idET = (EditText) rootView.findViewById(R.id.student_id_et);
        confirm = (Button) rootView.findViewById(R.id.signup_btn);
        pass = (EditText) rootView.findViewById(R.id.pass_et);
        confirmPass = (EditText) rootView.findViewById(R.id.confirm_pass_et);
        name = (EditText) rootView.findViewById(R.id.username_et);
        email = (EditText) rootView.findViewById(R.id.email_et);
        state = (Spinner) rootView.findViewById(R.id.state_sp);
        school = (Spinner) rootView.findViewById(R.id.school_sp);
        listItems = new ArrayList<String>();

        user = new User();
        dr = new DataRepo(getActivity());
        aa= new AuthAdapter(getActivity());

        cbVisibility();

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbVisibility();
            }
        });
        //removes error warning when editing name
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    name.setError(null);
                }
            }
        });
        //removes error warning when editing password
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pass.setError(null);
                }
            }
        });
        //removes error warning when editing confirm password
        confirmPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    confirmPass.setError(null);
                }
            }
        });
        //removes error warning when editing email
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    email.setError(null);
                }
            }
        });
        //removes error warning when editing Student ID
        idET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    idET.setError(null);
                }
            }
        });

        state.setSelection(0);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Scanner scanner = new Scanner(getResources().openRawResource(R.raw.schools));
                List<String> list = new ArrayList<String>();
                while (scanner.hasNextLine()) {
                    String data = scanner.nextLine();
                    String[] values = data.split(",");
                    if (values[1].equals(parent.getSelectedItem().toString()))
                        list.add(values[0]);
                }
                schooladapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                Spinner school = (Spinner) rootView.findViewById(R.id.school_sp);
                school.setAdapter(schooladapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check for all appropriate information and toast if missing anything
                if (!user.isValidName(name.getText().toString())) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    name.startAnimation(errorBlink);
                    name.requestFocus();
                    name.setError("Invalid Name");
                } else if (!user.isValidPassword(pass.getText().toString())) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    pass.startAnimation(errorBlink);
                    pass.requestFocus();
                    pass.setError("password must be at least 8 characters");
                } else if (!pass.getText().toString().equals(confirmPass.getText().toString())) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    confirmPass.startAnimation(errorBlink);
                    confirmPass.requestFocus();
                    confirmPass.setError("password do not match");
                } else if (!user.isValidEmail(email.getText().toString())) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    email.startAnimation(errorBlink);
                    email.requestFocus();
                    email.setError("Invalid email");
                } else if (cb.isChecked() && idET.getText().toString().matches("")) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    idET.startAnimation(errorBlink);
                    idET.requestFocus();
                    idET.setError("please fill out all appropriate information");
                } /*else if (cb.isChecked() && dr.userExist(Integer.parseInt(idET.getText().toString()))) {
                    idET.setError("User already exist");
                }*/ else {
                    sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
                    editor = sp.edit();

                    //set username
                    user.setName(name.getText().toString());

                    //set email to user.
                    user.setEmail(email.getText().toString());
                    user.setPassword(pass.getText().toString());

                    if(cb.isChecked()){
                        user.setIsStudent(true);
                        user.setId(Integer.parseInt(idET.getText().toString()));
                    }else if(!cb.isChecked()){
                        user.setIsStudent(false);
                        user.setId(user.getpKey());
                    }

                    try {
                        new UserAdapter().createUser(user, new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse result) {

                                if (result.getHttpCode() == 409){
                                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                                    email.startAnimation(errorBlink);
                                    email.requestFocus();
                                    email.setError("User already exist");
                                }

                                else if (result.getHttpCode() >= 300)
                                    Toast.makeText(null,"Error creating user", Toast.LENGTH_LONG);
                                else{
                                    ChooseInterface(user.isStudent());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        aa.authenticate(email.getText().toString(), pass.getText().toString(), new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse result) {
                                if (result.getHttpCode() == 401) {
                                    pass.requestFocus();
                                    pass.setError("Incorrect E-Mail or Password");
                                } else if (result.getHttpCode() >= 300)
                                    Toast.makeText(null, "Error logging in", Toast.LENGTH_LONG);
                                else {
                                    sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
                                    editor = sp.edit();
                                    editor.putString("AUTH_TOKEN", result.getResponse());
                                    editor.commit();

                                    try {
                                        User user = TokenUtility.parseUserToken(result.getResponse());
                                        ChooseInterface(user.isStudent());
                                    } catch (JSONException e) {
                                        pass.setError("Incorrect E-Mail or Password");
                                        Log.d("DEBUG", e.toString());
                                        Toast.makeText(null, "Error parsing token response", Toast.LENGTH_LONG);
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        pass.requestFocus();
                        pass.setError("Incorrect E-Mail or Password");
                    }

                    //store username in Shared Preferences
                    /*sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("USER_KEY", user.getEmail());
                    editor.commit();*/
                }
            }
        });

    }

    public void cbVisibility() {

        if (!cb.isChecked()) {
            idET.setText("");
            idET.setVisibility(View.INVISIBLE);
            idET.setText("");
            school.setVisibility(View.VISIBLE);
            state.setVisibility(View.VISIBLE);
        } else {
            idET.setVisibility(View.VISIBLE);
            school.setVisibility(View.INVISIBLE);
            state.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        cbVisibility();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            trans.replace(R.id.fragment_container, f).addToBackStack("debug");
            trans.commit();
        }
    }

    public void ChooseInterface(boolean isStudent) {
        if (!isStudent) {
            Fragment teacher = TeacherInterface.newInstance("test", "test");
            launchFragment(teacher);
        }else{
            Fragment student = StudentInterface.newInstance("test", "test");
            launchFragment(student);
        }
    }

    public void Logout(){
        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.remove("AUTH_TOKEN");
        editor.clear();
        editor.putString("AUTH_TOKEN", "");
        editor.commit();

    }

    public void refreshList() throws JSONException {
        final User user = TokenUtility.parseUserToken(getActivity());
        final ClassAdapter classAdapter = new ClassAdapter();

        classAdapter.professorClasses(user.getpKey(), new Callback<ArrayList<edu.uco.schambers.classmate.AdapterModels.Class>>() {
            @Override
            public void onComplete(ArrayList<Class> result) throws Exception {
                listItems.clear();
                TableLayout classTable = (TableLayout) getView().findViewById(R.id.classMgmtTable);
                classTable.removeAllViews();
                for (Class classItem : result) {
                    TableRow tr = new TableRow(getView().getContext());
                    String schoolnames[] = classItem.getSchool().split(" ");
                    String schoolAbbrev = "";
                    if (classItem.getSchool().length() > 4) {
                        for (int i = 0; i < schoolnames.length; i++) {
                            if (!schoolnames[i].contains("of") || !schoolnames[i].contains("the")) {
                                schoolAbbrev += schoolnames[i].charAt(0);
                            }
                        }
                    } else {
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

}