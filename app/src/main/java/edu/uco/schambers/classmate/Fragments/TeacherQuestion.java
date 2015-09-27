package edu.uco.schambers.classmate.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentQuestionService;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button toggleBtn;

    private boolean toggle;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherQuestion.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherQuestion newInstance(String param1, String param2) {
        TeacherQuestion fragment = new TeacherQuestion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        toggle = true;

        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle) {
                    toggleBtn.setText(R.string.btn_teacher_collect);
                    sendQuestion();
                    toggle = false;
                } else {
                    toggleBtn.setText(R.string.btn_teacher_send);
                    sendCollection();
                    toggle = true;
                }
            }
        });

    }

    private void sendQuestion(){
        //TODO implement sendQuestion method
        IQuestion question = new DefaultMultiChoiceQuestion();
        Intent intent = StudentQuestionService.getNewSendQuestionIntent(getActivity(), question);
        getActivity().startService(intent);
        //stub toast
        Toast.makeText(getActivity(), "Question sent to class!", Toast.LENGTH_SHORT).show();
    }

    private void sendCollection(){
        //TODO implement sendCollection method

        //stub toast
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
        public void onFragmentInteraction(Uri uri);
    }

}
