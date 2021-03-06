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
import java.util.Set;

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
    private TextView schoolLbl;
    private EditText currentPass;
    private EditText newPass;
    private EditText confirmNewPass;
    private Button cancel;
    private Button confirm;
    private Button add;
    private CheckBox changePass;
    private boolean toChangePass;
    private Animation errorBlink;
    private List<String> spinnerArray;
    private Spinner spinner;
    private Spinner state;
    private Spinner school;
    ArrayAdapter<String> schooladapter;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    boolean addSchoolActive;
    


    public SharedPreferences sp;
    public SharedPreferences sp2;
    public SharedPreferences.Editor editor;
    public static final String MyPREFS = "MyPREFS";
    public static final String MySCHOOL = "MySCHOOL";
    public String token;
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
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initUI(final View rootView) throws JSONException {

        name  = (TextView)rootView.findViewById(R.id.stored_name_lbl);
        email = (TextView)rootView.findViewById(R.id.stored_email_lbl);
        schoolLbl = (TextView)rootView.findViewById(R.id.ui_school_lbl);
        id = (TextView)rootView.findViewById(R.id.stored_id_lbl);
        idLbl = (TextView)rootView.findViewById(R.id.ui_id_lbl);
        currentPass = (EditText)rootView.findViewById(R.id.old_pass);
        newPass = (EditText)rootView.findViewById(R.id.new_pass);
        spinner = (Spinner)rootView.findViewById((R.id.spinner));
        confirmNewPass = (EditText)rootView.findViewById(R.id.confirm_new_pass);
        confirm = (Button)rootView.findViewById(R.id.confirm_btn);
        cancel = (Button)rootView.findViewById(R.id.cancel_btn);
        changePass = (CheckBox)rootView.findViewById(R.id.change_pw_cb);
        state = (Spinner)rootView.findViewById(R.id.state_sp);
        school = (Spinner)rootView.findViewById(R.id.school_sp);
        spinner = (Spinner)rootView.findViewById(R.id.spinner);
        add = (Button)rootView.findViewById(R.id.addClass_btn);

        String[] schoolArray = new String[25];
        spinnerArray =  new ArrayList<String>();
        addSchoolActive = false;


        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        sp2 = getActivity().getSharedPreferences(MySCHOOL, Context.MODE_PRIVATE);
        token = sp.getString("AUTH_TOKEN", null);
        user = TokenUtility.parseUserToken(token);
        name.setText(user.getName().toString());
        email.setText(user.getEmail().toString());
        for(int i = 0; i < sp2.getInt("SCHOOL_COUNT", 1); i++){
            schoolArray[i] = sp2.getString("SCHOOL_ARRAY_" + i, null);
            spinnerArray.add(schoolArray[i]);
            Log.i("school array", sp2.getString("SCHOOL_ARRAY_" + i, "not found"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) rootView.findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
        id.setText(Integer.toString(user.getId()));
        toChangePass = false;
        ChangePasswordVisibility(toChangePass);

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
        add.setVisibility(View.GONE);
        schoolLbl.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);


        if (!user.isStudent()) {
            id.setVisibility(View.GONE);
            idLbl.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
            schoolLbl.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        }

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toChangePass = true;
                ChangePasswordVisibility(toChangePass);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addSchoolActive == false){
                    state.setVisibility(View.VISIBLE);
                    school.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    changePass.setVisibility(View.GONE);
                    addSchoolActive = true;
                } else {
                    sp = getActivity().getSharedPreferences(MySCHOOL, Context.MODE_PRIVATE);
                    editor = sp.edit();
                    int count = sp.getInt("SCHOOL_COUNT", 1);
                    editor.putInt("SCHOOL_COUNT", count + 1);
                    editor.putString("SCHOOL_ARRAY_" + count, school.getSelectedItem().toString());
                    editor.commit();
                    Fragment user = UserInformation.newInstance("test", "test");
                    launchFragment(user);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(toChangePass == true){
                    toChangePass = false;
                    ChangePasswordVisibility(toChangePass);
                    currentPass.setText("");
                    newPass.setText("");
                    confirmNewPass.setText("");
                    changePass.setChecked(false);
                }else if (addSchoolActive == true){
                    state.setVisibility(View.GONE);
                    school.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    changePass.setVisibility(View.VISIBLE);
                    addSchoolActive = false;
                }


            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAdapter userAdapter = new UserAdapter();
                if (!user.isValidPassword(newPass.getText().toString())) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    newPass.startAnimation(errorBlink);
                    newPass.requestFocus();
                    newPass.setError("Password must be at least eight characters");
                } else if (!newPass.getText().toString().equals(confirmNewPass.getText().toString())) {
                    errorBlink = AnimationUtils.loadAnimation(getActivity(), R.anim.errorblink);
                    newPass.startAnimation(errorBlink);
                    newPass.requestFocus();
                    newPass.setError("New passwords do not match.");
                } else {
                    try {
                        userAdapter.changePass(user.getEmail(), currentPass.getText().toString(), newPass.getText().toString(), new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse response) throws Exception {
                                if (response.getHttpCode() == 204) {
                                    Toast.makeText(getActivity(), "Your Password has been Updated", Toast.LENGTH_LONG).show();
                                } else if (response.getHttpCode() == 401) {
                                    currentPass.requestFocus();
                                    currentPass.setError("Incorrect Current Password");
                                } else {
                                    Toast.makeText(getActivity(), "Error communicating with server. Status code: " + response.getHttpCode(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


                /*if (!dr.validateUser(user.getEmail().toString(), currentPass.getText().toString())) {
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

                      }*/
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
            changePass.setVisibility(View.GONE);
            add.setVisibility(View.GONE);

        }else{
            currentPass.setVisibility(View.GONE);
            newPass.setVisibility(View.GONE);
            confirmNewPass.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            changePass.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
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
    

    private void launchFragment(Fragment f) {
        if (f != null) {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.fragment_container, f).addToBackStack(null);
            trans.commit();
        }
    }

}
