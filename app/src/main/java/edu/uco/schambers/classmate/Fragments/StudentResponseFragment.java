package edu.uco.schambers.classmate.Fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.uco.schambers.classmate.BroadcastReceivers.CallForStudentQuestionResponseReceiver;
import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentResponseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentResponseFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI Components
    private RadioGroup radioGroup;
    private Button sendBtn;
    private TextView questionText;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentResponseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentResponseFragment newInstance(String param1, String param2)
    {
        StudentResponseFragment fragment = new StudentResponseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StudentResponseFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_response, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(final View rootView)
    {
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_response_group);
        sendBtn = (Button) rootView.findViewById(R.id.btn_send_question_response);
        questionText = (TextView) rootView.findViewById(R.id.response_card_question_text);
        sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedButton = (RadioButton) rootView.findViewById(selectedId);
                if (selectedButton != null)
                {
                    sendResponse(selectedButton.getText());
                }
            }
        });
    }

    private void sendResponse(CharSequence text)
    {
        //TODO implement send method

        //testing notifications
        sendTestCallToResponseBroadcast();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void sendTestCallToResponseBroadcast()
    {
        Intent questionResponseBroadcastIntent = CallForStudentQuestionResponseReceiver.getStartIntent(getActivity());
        getActivity().sendBroadcast(questionResponseBroadcastIntent);
    }

}
