package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView password;
    private TextView email;
    private TextView id;

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

        name         = (TextView)rootView.findViewById(R.id.stored_name_lbl);
        password     = (TextView)rootView.findViewById(R.id.stored_pass_lbl);
        email        = (TextView)rootView.findViewById(R.id.stored_email_lbl);
        id        = (TextView)rootView.findViewById(R.id.stored_id_lbl);

        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        user_key = sp.getString("USER_KEY", null);
        dr = new DataRepo(getActivity());
        user = dr.getUser(user_key);


        name.setText(user.getUsername());
        password.setText(user.getPassword());
        email.setText(user.getEmail());
        id.setText(Integer.toString(user.getId()));




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