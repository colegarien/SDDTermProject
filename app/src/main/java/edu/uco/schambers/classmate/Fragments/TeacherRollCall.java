/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   UI backend for teacher roll call module, used for starting and
 *   stopping the Roll Call Wifi P2P service
 *
 * Edit: 9/21/2015
 *   added button code for adding local wifi P2P service
 *
 */

package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeacherRollCall.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherRollCall#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherRollCall extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI Components
    private Button startBtn;
    private EditText teacherText;
    private EditText classText;
    private ListView connectedList;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherRollCall.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherRollCall newInstance(String param1, String param2) {
        TeacherRollCall fragment = new TeacherRollCall();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherRollCall() {
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
        View rootView = inflater.inflate(R.layout.fragment_teacher_roll_call, container, false);
        initUI(rootView);
        return rootView;
    }


    private void initUI(final View rootView)
    {
        startBtn = (Button) rootView.findViewById(R.id.btn_start_roll_call);
        teacherText = (EditText) rootView.findViewById(R.id.txt_rc_teacher);
        classText = (EditText) rootView.findViewById(R.id.txt_rc_class);
        connectedList = (ListView) rootView.findViewById(R.id.list_connected);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();

                /// TODO: Intialize Roll Call Service
                if(activity instanceof MainActivity) {
                    ((MainActivity) activity).addLocalService(8081, teacherText.getText().toString(), classText.getText().toString(), true);
                    //Toast.makeText(getActivity(), String.format("Teacher, %s, started Class, %s", teacherText.getText(), classText.getText()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

}
