package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import edu.uco.schambers.classmate.Models.Questions.Choice;
import edu.uco.schambers.classmate.Models.Questions.TestIQuestion;
import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherQuestionResults#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherQuestionResults extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Button displayResultsButton;
    TestIQuestion testQuestion;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherQuestionResults.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherQuestionResults newInstance(String param1, String param2) {
        TeacherQuestionResults fragment = new TeacherQuestionResults();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherQuestionResults() {
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
        View root = inflater.inflate(R.layout.fragment_teacher_question_results, container, false);
        InitUI(root);
        return root;
    }

    private int getHighestAnswer(List<Choice> l) {
        int highest = l.get(0).getChoiceAnswers();
        for (Choice c : l) {
            if (c.getChoiceAnswers() > highest){
                highest=c.getChoiceAnswers();
            }
        }
        System.out.println("highest: " + highest);
        return highest;
    }

    private int getHeightInPixels(int answers, int totalAnswers,int highest) {
        WindowManager wm = (WindowManager) getView().getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        double height = size.y * 2 / 5;
        double dblHighest = highest;
        double dblAnswers = answers;
        double dblTotalAnswers = totalAnswers;
        double percentage = dblAnswers/dblTotalAnswers;
        double percentageOfHighest = dblHighest / dblTotalAnswers;
       // System.out.println("percentageOfHighest: " + percentageOfHighest);
        double numberToMultiplyByAtEnd = height / percentageOfHighest;
//        System.out.println("numbertobemultipliedatend:" + numberToMultiplyByAtEnd);
//        System.out.println("Percentage: " + percentage);
//        System.out.println("Height: " + height);
//        System.out.println("Percentage * height: " + (int) (percentage * numberToMultiplyByAtEnd));
        return (int) (percentage * numberToMultiplyByAtEnd);
    }

    private void InitUI(View root) {
        displayResultsButton = (Button) root.findViewById(R.id.displayResultsButton);

        displayResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestIQuestion testIQuestion = new TestIQuestion();
                v = getView();
                displayResultsButton.setVisibility(View.GONE);
                //todo programmatically add textViews for any number of choices
                List<Choice> c = testIQuestion.getQuestionChoicesChoiceClass();

                TextView question = (TextView) v.findViewById(R.id.question);

                TextView c1 = (TextView) v.findViewById(R.id.choice01);
                TextView c2 = (TextView) v.findViewById(R.id.choice02);
                TextView c3 = (TextView) v.findViewById(R.id.choice03);
                TextView c4 = (TextView) v.findViewById(R.id.choice04);

                c1.setText("A: " + c.get(0).getChoiceText());
                c2.setText("B: " + c.get(1).getChoiceText());
                c3.setText("C: " + c.get(2).getChoiceText());
                c4.setText("D: " + c.get(3).getChoiceText());

                TextView p1 = (TextView) v.findViewById(R.id.colortext01Text);
                TextView p2 = (TextView) v.findViewById(R.id.colortext02Text);
                TextView p3 = (TextView) v.findViewById(R.id.colortext03Text);
                TextView p4 = (TextView) v.findViewById(R.id.colortext04Text);

                TextView bottomText1 = (TextView) v.findViewById(R.id.bottomText01);
                TextView bottomText2 = (TextView) v.findViewById(R.id.bottomText02);
                TextView bottomText3 = (TextView) v.findViewById(R.id.bottomText03);
                TextView bottomText4 = (TextView) v.findViewById(R.id.bottomText04);

                bottomText1.setText("A");
                bottomText2.setText("B");
                bottomText3.setText("C");
                bottomText4.setText("D");



                question.setText(testIQuestion.getQuestionText());

                int totalAnswers = testIQuestion.getTotalAnswers();
                int highest = getHighestAnswer(testIQuestion.getQuestionChoicesChoiceClass());

                TextView bar1 = (TextView) v.findViewById(R.id.colortext01);
                bar1.setHeight(getHeightInPixels(c.get(0).getChoiceAnswers(), totalAnswers, highest));
                p1.setText(String.format("%.1f",((double)c.get(0).getChoiceAnswers() / (double) totalAnswers * 100.0)) + "%");

                TextView bar2 = (TextView) v.findViewById(R.id.colortext02);
                bar2.setHeight(getHeightInPixels(c.get(1).getChoiceAnswers(), totalAnswers,highest));
                p2.setText(String.format("%.1f", ((double) c.get(1).getChoiceAnswers() / (double) totalAnswers * 100.0)) + "%");

                TextView bar3 = (TextView) v.findViewById(R.id.colortext03);
                bar3.setHeight(getHeightInPixels(c.get(2).getChoiceAnswers(), totalAnswers,highest));
                p3.setText(String.format("%.1f", ((double) c.get(2).getChoiceAnswers() / (double) totalAnswers * 100.0)) + "%");

                TextView bar4 = (TextView) v.findViewById(R.id.colortext04);
                bar4.setHeight(getHeightInPixels(c.get(3).getChoiceAnswers(),totalAnswers, highest));
                p4.setText(String.format("%.1f", ((double) c.get(3).getChoiceAnswers() / (double) totalAnswers * 100.0)) + "%");



            }
        });
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

}
