package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.uco.schambers.classmate.Database.DataRepo;
import edu.uco.schambers.classmate.Database.User;
import edu.uco.schambers.classmate.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInformation.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInformation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInformation extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView name;
    private TextView email;
    private TextView id;
    private TextView idLbl;
    private EditText currentPass;
    private EditText newPass;
    private EditText confirmNewPass;
    private Button cancel;
    private Button confirm;
    private CheckBox changePass;
    private boolean toChangePass;


    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public static final String MyPREFS = "MyPREFS";
    public String user_key;
    private DataRepo dr;
    public User user;
    Fragment context = this;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInformation.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInformation newInstance(String param1, String param2) {
        UserInformation fragment = new UserInformation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserInformation() {
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
        View rootView = inflater.inflate(R.layout.fragment_user_information, container, false);
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

        name  = (TextView)rootView.findViewById(R.id.stored_name_lbl);
        email = (TextView)rootView.findViewById(R.id.stored_email_lbl);
        id = (TextView)rootView.findViewById(R.id.stored_id_lbl);
        idLbl = (TextView)rootView.findViewById(R.id.ui_id_lbl);
        currentPass = (EditText)rootView.findViewById(R.id.old_pass);
        newPass = (EditText)rootView.findViewById(R.id.new_pass);
        confirmNewPass = (EditText)rootView.findViewById(R.id.confirm_new_pass);
        confirm = (Button)rootView.findViewById(R.id.confirm_btn);
        cancel = (Button)rootView.findViewById(R.id.cancel_btn);
        changePass = (CheckBox)rootView.findViewById(R.id.change_pw_cb);


        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        user_key = sp.getString("USER_KEY", null);
        dr = new DataRepo(getActivity());
        user = dr.getUser(user_key);

        name.setText(user.getName().toString());
        email.setText(user.getEmail().toString());
        id.setText(Integer.toString(user.getId()));
        toChangePass = false;
        ChangePasswordVisibility(toChangePass);

        if (!user.isStudent()) {
            id.setVisibility(View.INVISIBLE);
            idLbl.setVisibility(View.INVISIBLE);
        }

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toChangePass = true;
                ChangePasswordVisibility(toChangePass);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toChangePass = false;
                ChangePasswordVisibility(toChangePass);
                currentPass.setText("");
                newPass.setText("");
                confirmNewPass.setText("");
                changePass.setChecked(false);

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dr.validateUser(user.getEmail().toString(), currentPass.getText().toString())) {
                    currentPass.setError("Incorrect Current Password");
                } else if (!user.isValidPassword(newPass.getText().toString())) {
                    newPass.setError("Password must be less than four characters");
                } else if (!newPass.getText().toString().equals(confirmNewPass.getText().toString())) {
                    newPass.setError("New passwords do not match.");
                } else {
                    user.setPassword(newPass.getText().toString());
                    toChangePass = false;
                    ChangePasswordVisibility(toChangePass);
                    Toast.makeText(getActivity(), "Your Password has been Updated", Toast.LENGTH_LONG).show();

                      }
            }
        });



    }

    private void ChangePasswordVisibility(boolean cp){
        if(cp == true){
            currentPass.setVisibility(View.VISIBLE);
            newPass.setVisibility(View.VISIBLE);
            confirmNewPass.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            changePass.setVisibility(View.INVISIBLE);

        }else{
            currentPass.setVisibility(View.INVISIBLE);
            newPass.setVisibility(View.INVISIBLE);
            confirmNewPass.setVisibility(View.INVISIBLE);
            confirm.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
            changePass.setVisibility(View.VISIBLE);
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

}
