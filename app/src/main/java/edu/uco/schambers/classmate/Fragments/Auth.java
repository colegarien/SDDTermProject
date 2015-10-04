package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.uco.schambers.classmate.Database.DataRepo;
import edu.uco.schambers.classmate.Database.User;
import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Auth.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Auth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Auth extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText email;
    private EditText pass;
    private Button signin;
    private TextView signup;
    private TextView resetLink;

    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public static final String MyPREFS = "MyPREFS";
    private DataRepo dr;
    public User user;
    Fragment context = this;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Auth.
     */
    // TODO: Rename and change types and number of parameters
    public static Auth newInstance(String param1, String param2) {
        Auth fragment = new Auth();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Auth() {
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
        View rootView = inflater.inflate(R.layout.fragment_auth, container, false);
        initUI(rootView);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initUI(final View rootView) {

        email = (EditText) rootView.findViewById(R.id.email_et);
        pass = (EditText) rootView.findViewById(R.id.pass_et);
        signin = (Button) rootView.findViewById(R.id.sign_in_btn);
        signup = (TextView) rootView.findViewById(R.id.signup_lbl);
        resetLink = (TextView) rootView.findViewById(R.id.reset_pw_lbl);
        dr = new DataRepo(getActivity());

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment signup = Login.newInstance("test", "test");
                launchFragment(signup);
            }
        });

        resetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment reset = ResetPassword.newInstance("test", "test");
                launchFragment(reset);
            }
        });

        signin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!dr.validateUser(email.getText().toString(), pass.getText().toString())) {
                    pass.setError("Incorrect E-Mail or Password");
                }else{
                    dr = new DataRepo(getActivity());
                    user = dr.getUser(email.getText().toString());
                    sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("USER_KEY", user.getEmail());
                    editor.commit();
                    ChooseInterface(user);
                }
            }
        });


    }

    public void ChooseInterface(User u) {
        if (!u.isStudent()) {
            Fragment teacher = TeacherInterface.newInstance("test", "test");
            launchFragment(teacher);
        }else{
            Fragment student = StudentInterface.newInstance("test", "test");
            launchFragment(student);
        }
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
        public void onFragmentInteraction(Uri uri);
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