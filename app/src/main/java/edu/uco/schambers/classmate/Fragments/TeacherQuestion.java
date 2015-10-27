package edu.uco.schambers.classmate.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.DefaultUnanswerdQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentQuestionService;
import edu.uco.schambers.classmate.Services.TeacherQuestionService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeacherQuestion.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherQuestion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherQuestion extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_QUESTION = "edu.uco.schambers.classmate.arq_question";

    // TODO: Rename and change types of parameters
    private IQuestion question;

    private ListView listView;
    private ArrayList<String> myAnsArray;
    private ArrayAdapter<String> adapter;

    private Button toggleBtn;

    private boolean toggle;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TeacherQuestion.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherQuestion newInstance(IQuestion question) {
        TeacherQuestion fragment = new TeacherQuestion();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherQuestion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (IQuestion) getArguments().getSerializable(ARG_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_question, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(final View rootView){
        toggleBtn = (Button) rootView.findViewById(R.id.btn_send_question_propose);
        myAnsArray = new ArrayList<>();
        listView = (ListView) rootView.findViewById(R.id.answer_list_view);
        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, myAnsArray);
        listView.setAdapter(adapter);
        listView.setVisibility(View.INVISIBLE);

        toggle = true;

        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle) {
                    toggleBtn.setText(R.string.btn_teacher_call_time);
                    sendQuestion();
                    toggle = false;
                } else {
                    toggleBtn.setText(R.string.btn_teacher_send);
                    callTime();
                    toggle = true;
                }
            }
        });

    }


    private void sendQuestion(){
        IQuestion question = new DefaultMultiChoiceQuestion();
        Intent intent = TeacherQuestionService.getNewSendResponseIntent(getActivity(), question);
        listView.setVisibility(View.INVISIBLE);
        myAnsArray.clear();
        getActivity().startService(intent);


        //stub toast
    }

    private void callTime() {
        //TODO implement sendCollection method
        IQuestion question = new DefaultUnanswerdQuestion(); //Redundent
        Intent intent = TeacherQuestionService.getNewCallTimeIntent(getActivity(), question);

        getActivity().startService(intent);

        for (IQuestion q :
                TeacherQuestionService.answerList) {
            adapter.add("Student answered: " + q.getAnswer());
        }
        /* for testing solo
        adapter.add("Sttuddy: AAA");
        adapter.add("Sttuddy: BBB");
        adapter.add("Sttuddy: CCC");
        adapter.add("Sttuddy: AAA");
        adapter.add("Sttuddy: EEE");*/
        listView.setVisibility(View.VISIBLE);

        Toast.makeText(getActivity(), "Answers collected from class!", Toast.LENGTH_SHORT).show();
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
