package edu.uco.schambers.classmate.Fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.uco.schambers.classmate.R;
//this fragment will be acting as our debug interface.
//Please use fragment transactions to launch your modules (which should be fragments, hint hint)
//Do this within the onClickListeners for the appropriate buttons
public class Debug extends Fragment
{

    Button mattBtn;
    Button stevenBtn;
    Button wenxiBtn;
    Button nelsonBtn;
    Button thomasBtn;
    Button mosaBtn;
    Button connorBtn;
    Button coleBtn;
    Button rayanBtn;

    public Debug()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_debug, container, false);
        initUI(rootView);
        setOnClickListeners();
        return rootView;
    }

    public void initUI(View rootView)
    {
        mattBtn = (Button) rootView.findViewById(R.id.matt_btn);
        stevenBtn = (Button) rootView.findViewById(R.id.steven_btn);
        wenxiBtn = (Button) rootView.findViewById(R.id.wenxi_btn);
        nelsonBtn = (Button) rootView.findViewById(R.id.nelson_btn);
        thomasBtn = (Button) rootView.findViewById(R.id.thomas_btn);
        mosaBtn = (Button) rootView.findViewById(R.id.mosa_btn);
        connorBtn = (Button) rootView.findViewById(R.id.connor_btn);
        coleBtn = (Button) rootView.findViewById(R.id.cole_btn);
        rayanBtn = (Button) rootView.findViewById(R.id.rayan_btn);

    }

    //region OnClickListeners

    private void setOnClickListeners()
    {
        mattBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showRequestToast();
                //create an instance of your fragment here and pass it to the next function
                launchFragment(null);
            }
        });

        stevenBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment studentResponse = StudentResponse.newInstance("test","test");
                launchFragment(studentResponse);
            }
        });

        wenxiBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment studentRollCall = StudentRollCall.newInstance("test","test");
                //create an instance of your fragment here and pass it to the next function
                launchFragment(studentRollCall);
            }
        });

        nelsonBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                showRequestToast();
                //create an instance of your fragment here and pass it to the next function
                launchFragment(null);
            }
        });

        thomasBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showRequestToast();
                //create an instance of your fragment here and pass it to the next function
                launchFragment(null);
            }
        });

        mosaBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showRequestToast();
                //create an instance of your fragment here and pass it to the next function
                launchFragment(null);
            }
        });

        connorBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showRequestToast();
                //create an instance of your fragment here and pass it to the next function
                launchFragment(null);
            }
        });

        coleBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment teacherRollCall = TeacherRollCall.newInstance("test","test");
                launchFragment(teacherRollCall);
            }
        });

        rayanBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showRequestToast();
                //create an instance of your fragment here and pass it to the next function
                launchFragment(null);
            }
        });
    }


    private void showRequestToast()
    {
        Toast.makeText(getActivity(),getResources().getString(R.string.set_listener), Toast.LENGTH_SHORT).show();
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
    //endregion

}
