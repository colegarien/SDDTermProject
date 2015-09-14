package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentInterface.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentInterface#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentInterface extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button rollcall;
    private Button attendanceRecords;
    private Button inClassResponse;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentInterface.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentInterface newInstance(String param1, String param2) {
        StudentInterface fragment = new StudentInterface();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StudentInterface() {
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
        View rootView = inflater.inflate(R.layout.fragment_student_interface, container, false);
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

    private void initUI(final View rootView) {

        rollcall = (Button) rootView.findViewById(R.id.stu_roll_btn);
        attendanceRecords = (Button)rootView.findViewById(R.id.stu_attendance_btn);
        inClassResponse = (Button)rootView.findViewById(R.id.stu_response_btn);


        rollcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment roll = StudentRollCall.newInstance("test", "test");
                launchFragment(roll);

            }
        });

        attendanceRecords.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


            }
        });

        inClassResponse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Fragment response = StudentResponseFragment.newInstance("test", "test");
                launchFragment(response);

            }
        });



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