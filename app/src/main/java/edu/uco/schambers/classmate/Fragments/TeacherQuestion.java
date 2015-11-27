/**
 * Team 9Lives
 * <p/>
 * Author: Connor Archer
 * Purpose:
 * UI front end for the Teacher side of the question/answer feature. Controls sending questions
 * to connected student devices and facilitates communication with the TeacherQuestionService
 * which is the backend of the feature.
 * <p/>
 * Edit: 11/25/2015
 * Cleaned up code, removing some redundant sections and adding relevant missing comments.
 */

package edu.uco.schambers.classmate.Fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;

import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.DefaultUnanswerdQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestionWoodchuck5choices;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.TeacherQuestionService;

/**
 * TeacherQuestion class a boundary object for handling contact between the user on the visual
 * interface and the business objects related to it, like the TeacherQuestionService.
 */
public class TeacherQuestion extends Fragment {
    public static final String ARG_QUESTION = "edu.uco.schambers.classmate.arq_question";
    private IQuestion question; //Leftover, redundant but not worth refactoring to remove ATM
    private ArrayList<String> myAnsArray; //array of answers received by students
    private Button toggleBtn; //Wired to the UI fragment widget
    private boolean toggle; //To keep track of sending/receiving the question/answer

    /**
     * Default Constructor for TeacherQuestion
     *
     * @param question IQuestion generic object
     * @return fragment TeacherQuestion fragment
     */
    public static TeacherQuestion newInstance(IQuestion question) {
        TeacherQuestion fragment = new TeacherQuestion();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    //Default
    public TeacherQuestion() {
        // Required empty public constructor
    }

    @Override   //Default onCreate with appropriate initializations
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (IQuestion) getArguments().getSerializable(ARG_QUESTION);
        }
    }

    @Override   //Default onCreateView with appropriate initializations
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_question, container, false);
        initUI(rootView); // Primary initialization
        return rootView;
    }

    /**
     * Initializes the primary fragment functinality and wires appropriate widgets.
     * @param rootView View from parent calling, standard setup
     */
    private void initUI(final View rootView) {
        //For switching between sending and receiving
        toggleBtn = (Button) rootView.findViewById(R.id.btn_send_question_propose);
        toggle = true;
        //For storing the answers after students send them back
        myAnsArray = new ArrayList<>();

        //Primary functinality of fragment rests here, activates relevant Service features
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Assumes you pressed "Send Question", First click will be here
                if (toggle) {
                    toggleBtn.setText(R.string.btn_teacher_call_time);
                    sendQuestion();
                    toggle = false;
                } else { //Assumes you pressed "Call Time"
                    toggleBtn.setText(R.string.btn_teacher_send);
                    callTime();
                    toggle = true;
                }
            }
        });

    }

    /**
     * Creates a new question object of the type of the teachers choosing to send to students
     */
    private void sendQuestion() {
        //Creation of question, could be any of the various types
        IQuestion question = new DefaultMultiChoiceQuestion();
        //TeacherQuestionService Factory returns a new product: an intent of the appropriate type
        Intent intent = TeacherQuestionService.getNewSendResponseIntent(getActivity(), question);
        //Reset AnswerList if asking new question
        myAnsArray.clear();
        //Activate the service with the given intent
        getActivity().startService(intent);
    }

    /**
     * Collects answers from the students and stops the recording any late answers
     */
    private void callTime() {
        //Redundant, since no answer is being expected, serves to fit the format of the intent
        IQuestion question = new DefaultUnanswerdQuestion();
        //TeacherQuestionService Factory returns a new product: an intent of the appropriate type
        Intent intent = TeacherQuestionService.getNewCallTimeIntent(getActivity(), question);
        //Activate the service with the given intent
        getActivity().startService(intent);
        //Temporary storage for incoming answers collected from service
        ArrayList<IQuestion> answerList = new ArrayList<>();

        //Begin Packaging the answerlist into a bundle to launch the TeacherQuestionResults fragment
        Bundle bundle = new Bundle();

        //If no real answers returned, generate mock answer list
        if (TeacherQuestionService.answerList.isEmpty()) {
            bundle.putSerializable("inputList", getMockData());
        } else {
            //Real answers returned, compile them into a local list from TeacherQuestionService
            for (IQuestion q : TeacherQuestionService.answerList) {
                answerList.add(q); //Portion for sending
            }
            bundle.putSerializable("inputList",answerList);
        }

        //Finish packaging into bundle
        Fragment newFragment = new TeacherQuestionResults();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //For visual reflection&confirmation of answers collected
        Toast.makeText(getActivity(), "Answers collected from class!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates MockData for testing purposes, generates answers randomly for 25 students
     * @return inputList ArrayList of IQuestion's generated for testing purposes
     */
    private ArrayList<IQuestion> getMockData() {
        ArrayList<IQuestion> inputList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            IQuestion mockQuestion = new DefaultMultiChoiceQuestion();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(0));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 1; i++) {
            IQuestion mockQuestion = new DefaultMultiChoiceQuestion();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(1));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 16; i++) {
            IQuestion mockQuestion = new DefaultMultiChoiceQuestion();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(2));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 3; i++) {
            IQuestion mockQuestion = new DefaultMultiChoiceQuestion();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(3));
            inputList.add(mockQuestion);
        }

        return inputList;
    }

    //Default
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}