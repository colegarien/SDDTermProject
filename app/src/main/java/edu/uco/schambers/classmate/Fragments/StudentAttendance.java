package edu.uco.schambers.classmate.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import edu.uco.schambers.classmate.R;


public class StudentAttendance extends Fragment {
    private Spinner spinner;

    private String[] arraySpinner;

    TextView missing, attendance;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static StudentAttendance newInstance(String param1, String param2) {
        StudentAttendance fragment = new StudentAttendance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StudentAttendance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  missing=(TextView) missing.findViewById(R.id.textView3);
    }

    

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem mute = menu.add("Mute when checked-in");
        mute.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        final MenuItem vibrate = menu.add("Vibrate when checked-in");
        vibrate.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        vibrate.setVisible(!mute.isVisible());

        mute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(true);
                mute.setVisible(false);

                return true;
            }
        });

        vibrate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(false);
                mute.setVisible(true);

                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_attendance, container, false);
        initUI(rootView);
        missing=(TextView) rootView.findViewById(R.id.textView3);
        attendance=(TextView) rootView.findViewById(R.id.textView2);
        return rootView;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initUI(final View rootView)
    {
        this.arraySpinner = new String[] {
                "Software Design and Development", "Web Server Programming", "Mobile Application Programming", "Programming II", "Programming Languages"
        };
        Spinner s = (Spinner) rootView.findViewById(R.id.classlist);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
