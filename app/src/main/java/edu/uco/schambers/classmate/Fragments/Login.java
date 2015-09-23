package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import edu.uco.schambers.classmate.Adapter.UserAdapter;
import edu.uco.schambers.classmate.Database.DataRepo;
import edu.uco.schambers.classmate.Database.User;
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



    //ui components
    private CheckBox cb;
    private EditText idET;
    private TextView idTV;
    private Button   confirm;
    private EditText pass;
    private EditText confirmPass;
    private EditText name;
    private EditText email;

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
        idTV = (TextView) rootView.findViewById(R.id.student_id_lbl);
        confirm = (Button)rootView.findViewById(R.id.signup_btn);
        pass = (EditText) rootView.findViewById(R.id.pass_et);
        confirmPass = (EditText)rootView.findViewById(R.id.confirm_pass_et);
        name = (EditText)rootView.findViewById(R.id.username_et);
        email = (EditText)rootView.findViewById(R.id.email_et);

        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        editor = sp.edit();
        user = new User();
        dr   = new DataRepo(getActivity());

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
                if(hasFocus){
                    name.setError(null);
                }
            }
        });
        //removes error warning when editing password
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check for all appropriate information and toast if missing anything
                     if(!user.isValidName(name.getText().toString())) {
                        name.setError("Invalid Name");}
                else if(!user.isValidPassword(pass.getText().toString())){
                        pass.setError("password must be less than 4 characters");}
                else if (!pass.getText().toString().equals(confirmPass.getText().toString())){
                        confirmPass.setError("password do not match");}
                else if(!user.isValidEmail(email.getText().toString())){
                        email.setError("Invalid email");}
                else if (cb.isChecked() && idET.getText().toString().matches("")){

                    Toast warning = Toast.makeText(getActivity(), "please fill out all appropriate information", Toast.LENGTH_LONG);
                    warning.show();
                }
                else {
                    //dr = new DataRepo(getActivity());
                    //check passwords match and save encrypted pass to user
                    if (pass.getText().toString().equals(confirmPass.getText().toString())) {
                        user.setPassword(pass.getText().toString());

                        boolean userExists = true;
                        try
                        {
                            userExists = dr.userExist(Integer.parseInt(idET.getText().toString()));
                        }
                        catch (NumberFormatException e)
                        {
                            //if an integer cant be parsed from the idET, user must be a teacher, allowing to continue on
                        }
                        if(userExists) {
                            //set username
                            user.setName(name.getText().toString());

                            //set email to user.
                            user.setEmail(email.getText().toString());


                            //check passwords match and save encrypted pass to user
                           // if (pass.getText().toString().equals(confirmPass.getText().toString())) {
                           //     user.setPassword(pass.getText().toString());
                           // } else {
                           //     Toast toast = Toast.makeText(getActivity(), "passwords do not match", Toast.LENGTH_LONG);
                           //     toast.show();
                           // }


                            //add role & id, send to appropriate fragment
                            if (!cb.isChecked()) {
                                user.setId(0);
                                user.setIsStudent(false);
                                Fragment teacher = TeacherInterface.newInstance("test", "test");
                                launchFragment(teacher);
                            } else {
                                user.setId(Integer.parseInt(idET.getText().toString()));
                                user.setIsStudent(true);
                                Fragment student = StudentInterface.newInstance("test", "test");
                                launchFragment(student);
                            }

                            //store user in dataRepo
                            dr.createUser(user);
                            //send to web api

                            try {
                                new UserAdapter().createUser(user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //store username in Shared Preferences
                            sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            editor.putString("USER_KEY", user.getName());
                            editor.commit();
                        }
                        else {
                            Toast warning = Toast.makeText(getActivity(), "This Student ID already exist", Toast.LENGTH_LONG);
                            warning.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "passwords do not match", Toast.LENGTH_LONG);
                        toast.show();

                    }
                }
            }
        });

    }

    public void cbVisibility(){

        if (!cb.isChecked())
        {
            idET.setVisibility(View.INVISIBLE);
            idTV.setVisibility(View.INVISIBLE);
            idET.setText("");
        }else{
            idET.setVisibility(View.VISIBLE);
            idTV.setVisibility(View.VISIBLE);
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

    private void launchFragment(Fragment f)
    {
        if(f != null)
        {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.fragment_container, f).addToBackStack("debug");
            trans.commit();
        }
    }

}