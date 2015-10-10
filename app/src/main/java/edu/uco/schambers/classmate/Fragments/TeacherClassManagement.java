package edu.uco.schambers.classmate.Fragments;

import edu.uco.schambers.classmate.Database.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Set;

import edu.uco.schambers.classmate.Database.TokenUtility;
import edu.uco.schambers.classmate.Database.User;
import edu.uco.schambers.classmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeacherClassManagement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherClassManagement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherClassManagement extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Class newClass;
    public SharedPreferences sharedPref;
    public String token;
    public User user;
    public static final String MyPREFS = "MyPREFS";
    SharedPreferences.Editor editor;
    ListView addedClasses;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    Button addButton;
    Spinner semester;
    Spinner year;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherClassManagement.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherClassManagement newInstance(String param1, String param2) {
        TeacherClassManagement fragment = new TeacherClassManagement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherClassManagement() {
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
        View rootView =  inflater.inflate(R.layout.fragment_teacher_class_management, container, false);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void initUI(final View rootView) throws JSONException {
        addedClasses = (ListView)rootView.findViewById(R.id.add_class_lst);
        addButton = (Button)rootView.findViewById(R.id.add_class_btn);
        semester = (Spinner)rootView.findViewById(R.id.semester_sp);
        year = (Spinner)rootView.findViewById(R.id.year_sp);
        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, listItems);
        addedClasses.setAdapter(adapter);
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String classNameTemp = ((EditText)rootView.findViewById(R.id.classname_et)).getText().toString();
                String schoolName = ((EditText)rootView.findViewById(R.id.schoolname_et)).getText().toString();
                sharedPref = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
                token = sharedPref.getString("AUTH_TOKEN", null);
                try {
                    user = TokenUtility.parseUserToken(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newClass = new Class();
                newClass.setId(user.getId());
                newClass.setClass_name(classNameTemp);
                newClass.setSchool(schoolName);
                Log.d("LOOOK!!!", classNameTemp);
                listItems.add(classNameTemp + "-" +
                        semester.getSelectedItem() + "-" + year.getSelectedItem());
                adapter.notifyDataSetChanged();
                ((EditText)rootView.findViewById(R.id.classname_et)).setText("");
                ((EditText)rootView.findViewById(R.id.classname_et)).requestFocus();
                ((EditText)rootView.findViewById(R.id.schoolname_et)).setText("");
                editor = sharedPref.edit();
                editor.putStringSet("courseList", (Set<String>) listItems);
            }
        });
        addedClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_LONG)
                        .show();
            }
        });

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
