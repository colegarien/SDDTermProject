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
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;

public class StudentResponseFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_QUESTION= "edu.uco.schambers.classmate.arq_question";

    // TODO: Rename and change types of parameters
    private IQuestion question;

    //UI Components
    private RadioGroup radioGroup;
    private Button sendBtn;
    private TextView questionText;

    private OnFragmentInteractionListener mListener;

    public static StudentResponseFragment newInstance(IQuestion question)
    {
        StudentResponseFragment fragment = new StudentResponseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
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
            question = (IQuestion) getArguments().getSerializable(ARG_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_response, container, false);
        if(question != null)
        {
            View questionCardView = inflater.inflate(R.layout.question_response_card,(ViewGroup)rootView);
            initUI(questionCardView);
            populateQuestionCardFromQuestion();

        }
        return rootView;
    }

    private void initUI(final View rootView)
    {
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_response_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checked = (RadioButton) rootView.findViewById(checkedId);
                question.answerQuestion(checked.getText().toString());
            }
        });
        sendBtn = (Button) rootView.findViewById(R.id.btn_send_question_response);
        questionText = (TextView) rootView.findViewById(R.id.response_card_question_text);
        sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (question.questionIsAnswered())
                {
                    sendResponse(question.getAnswer());
                }
            }
        });
    }

    private void sendResponse(CharSequence text)
    {
        //TODO implement send method

        //testing notifications
        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.response_sent),text), Toast.LENGTH_SHORT).show();
    }


    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void populateQuestionCardFromQuestion()
    {
        if(question != null)
        {
            questionText.setText(question.getQuestionText());
            int index = 0;
            for(String s : question.getQuestionChoices())
            {
                RadioButton radioButton =(RadioButton) radioGroup.getChildAt(index);
                radioButton.setText(s);
                index++;
            }
        }
    }
}
