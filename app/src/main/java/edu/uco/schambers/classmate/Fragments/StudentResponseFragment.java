package edu.uco.schambers.classmate.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentQuestionService;

public class StudentResponseFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_QUESTION = "edu.uco.schambers.classmate.arq_question";

    // TODO: Rename and change types of parameters
    private IQuestion question;

    //UI Components
    private RadioGroup radioGroup;
    private Button sendBtn;
    private TextView questionText;
    private View questionCardView;

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
        if (question != null)
        {
            questionCardView = inflater.inflate(R.layout.question_response_card, (ViewGroup) rootView);
            initUI(questionCardView);
            populateQuestionCardFromQuestion();

        }
        //inserting a question for testing
        else
        {
            question = new DefaultMultiChoiceQuestion();
            questionCardView = inflater.inflate(R.layout.question_response_card, (ViewGroup) rootView);
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
                    sendResponse(question);
                }
            }
        });
    }

    private void sendResponse(IQuestion question)
    {
        //TODO implement send method
        Intent questionResponseIntent = StudentQuestionService.getNewSendResponseIntent(getActivity(),question);
        getActivity().startService(questionResponseIntent);
        dismissCardAnimation();
    }

    private void dismissCardAnimation()
    {
        Animation slideOutRight = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
        slideOutRight.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                questionCardView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        questionCardView.startAnimation(slideOutRight);
    }


    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void populateQuestionCardFromQuestion()
    {
        if (question != null)
        {
            questionText.setText(question.getQuestionText());
            for (String s : question.getQuestionChoices())
            {
                RadioButton rb = generateRadioButtonForResponse(s);
                radioGroup.addView(rb);
            }
        }
    }

    private RadioButton generateRadioButtonForResponse(String s)
    {
        RadioButton radioButton = new RadioButton(getActivity());
        radioButton.setId(View.generateViewId());
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        radioButton.setGravity(Gravity.CENTER_VERTICAL);
        radioButton.setLayoutParams(params);
        radioButton.setText(s);
        return radioButton;
    }
}
