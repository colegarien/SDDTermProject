package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion3Choices;
import edu.uco.schambers.classmate.Models.Questions.IQuestionWoodchuck5choices;
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
    private Animation barGrow;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



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

    private int getHighestAnswer(List<IQuestion> l) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (IQuestion tq : l) {
            arrayList.add(tq.getAnswer());
        }
        ArrayList<Integer> totals = new ArrayList<>();
        for (String key : arrayList) {
            totals.add(Collections.frequency(arrayList, key));
        }
        Collections.sort(totals);
//        System.out.println("GETHIGHESTANSWER: " + totals.get(totals.size() - 1));
        return totals.get(totals.size() - 1);

    }

    private ArrayList<Integer> getTotals(List<IQuestion> l) {

        ArrayList<String> arrayList = new ArrayList<>();
        for (IQuestion tq : l) {
            arrayList.add(tq.getAnswer());
        }
        ArrayList<Integer> totals = new ArrayList<>();
        for (String key : l.get(0).getQuestionChoices()) {
            totals.add(Collections.frequency(arrayList, key));
            //   System.out.println(Collections.frequency(arrayList,key));
        }

        return totals;

    }

    private int getHeightInPixels(View v, List<IQuestion> list, int answerIndex) {
//        System.out.println("answer index" + answerIndex);
        int highest = getHighestAnswer(list);
        int totalAnswers = list.size();
        ArrayList<String> arrayList = new ArrayList<>();
        for (IQuestion tq : list) {
            arrayList.add(tq.getAnswer());
        }
        int answers = Collections.frequency(arrayList,list.get(0).getQuestionChoices().get(answerIndex));
//        System.out.println("answers " + answers);
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
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
//            System.out.println(percentage * numberToMultiplyByAtEnd + " getheightinpixels");
        return (int) (percentage * numberToMultiplyByAtEnd);
    }


    private List<IQuestion> getMockData(View root) {
        List<IQuestion> inputList = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            IQuestion mockQuestion = new IQuestionWoodchuck5choices();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(0));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 8; i++) {
            IQuestion mockQuestion = new IQuestionWoodchuck5choices();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(1));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 5; i++) {
            IQuestion mockQuestion = new IQuestionWoodchuck5choices();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(2));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 10; i++) {
            IQuestion mockQuestion = new IQuestionWoodchuck5choices();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(3));
            inputList.add(mockQuestion);
        }
        for (int i = 0; i < 3; i++) {
            IQuestion mockQuestion = new IQuestionWoodchuck5choices();
            mockQuestion.answerQuestion(mockQuestion.getQuestionChoices().get(4));
            inputList.add(mockQuestion);
        }
        return inputList;
    }

    private void InitUI(View root) {
        List<IQuestion> inputList = null;
//        inputList = getMockData(root);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            inputList = (List<IQuestion>) bundle.getSerializable("inputList");
        }
        if (inputList != null) {
            DisplayResults(root,inputList);

        }

    }

    private void DisplayResults(View v,List<IQuestion> inputList) {


        List<String> choices = inputList.get(0).getQuestionChoices();
        View choiceLayout = v.findViewById(R.id.choicesLayout);
        View barLayout = v.findViewById(R.id.lin02);
        RelativeLayout r2 = (RelativeLayout) barLayout;
        TextView questionTv = new TextView(v.getContext());
        questionTv.setText(inputList.get(0).getQuestionText());
        questionTv.setTextSize(23);
        questionTv.setId('A' - 1);
        RelativeLayout rl = (RelativeLayout) choiceLayout;
        rl.addView(questionTv);




        char letters = 'A';
        ArrayList<TextView> choiceTextViews = new ArrayList<>();
        for (String tq : choices) {
            TextView tv = new TextView(v.getContext());
            tv.setId(letters);
            tv.setText(letters + ": " + tq);
            letters++;
            choiceTextViews.add(tv);
        }

        for (TextView tv : choiceTextViews) {
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params1.addRule(RelativeLayout.BELOW, tv.getId() -1);
            rl.addView(tv, params1);
        }

        TextView holder = new TextView(v.getContext());
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        int width = size.x / choices.size() - 55;
        holder.setHeight(0);
        holder.setWidth(0);
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        holder.setGravity(RelativeLayout.ALIGN_BOTTOM);
        holder.setId('A' - 61);
        holder.setBackground(new ColorDrawable(Color.BLUE));

        r2.addView(holder, params1);


        //todo make colors random
        int[] colors = new int[20];
        colors[0]= Color.BLUE;
        colors[1]= Color.RED;
        colors[2]= Color.GREEN;
        colors[3]= Color.YELLOW;
        colors[4]= Color.CYAN;
        colors[6]= Color.RED;



        letters = 'A';
        ArrayList<TextView> bars = new ArrayList<>();
        for (String cc : choices) {
            TextView bar = new TextView(v.getContext());
            bar.setHeight(getHeightInPixels(v,inputList, choices.indexOf(cc)));
            //     System.out.println(getHeightInPixels(testIQuestion, testIQuestion.get(0).getQuestionChoices().indexOf(cc)) + "Height");
            bar.setWidth(width);
            //      System.out.println(width + " width");
            bar.setId(letters - 60);
            bar.setBackground(new ColorDrawable(colors[letters - 65]));
            letters++;

            bars.add(bar);

        }
        //      System.out.println("num of bars: " + bars.size());
        for (TextView tv : bars) {
            params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params1.addRule(RelativeLayout.RIGHT_OF, tv.getId() - 1);
            r2.addView(tv, params1);
        }

        ArrayList<Integer> totals = getTotals(inputList);
//                for (int t : totals) {
//                    System.out.println(t);
//                }
        ArrayList<TextView> percentages = new ArrayList<>();
        for (Integer i: totals) {
            TextView tv = new TextView(v.getContext());
            String s = String.format("%.1f", (double) i / (double) inputList.size() * 100.0);
            tv.setText(s + "%");

            percentages.add(tv);
        }
        letters = 'A';

        for (TextView tv : percentages) {
            params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params1.addRule(RelativeLayout.ABOVE, letters - 60);
            params1.addRule(RelativeLayout.RIGHT_OF, letters - 61);
            r2.addView(tv, params1);
            params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params1.addRule(RelativeLayout.ALIGN_BOTTOM, letters - 60);
            params1.addRule(RelativeLayout.RIGHT_OF, letters - 61);
            TextView pct = new TextView(v.getContext());
            pct.setText(letters + "");
            r2.addView(pct, params1);

            letters++;
        }

        ScaleAnimation barGrow = new ScaleAnimation(
                1f, 1f,
                0f, 1f,
                Animation.ABSOLUTE, 0,
                Animation.RELATIVE_TO_SELF , 1);
        barGrow.setDuration(1000);
        barGrow.setRepeatCount(0);
        barGrow.setInterpolator(new AccelerateDecelerateInterpolator());
        barGrow.setFillAfter(true);
        barGrow.setFillBefore(true);
        barGrow.setFillEnabled(true);

        barLayout.startAnimation(barGrow);
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
