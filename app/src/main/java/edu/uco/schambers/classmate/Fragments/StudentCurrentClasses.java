package edu.uco.schambers.classmate.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.EnrollmentAdapter;
import edu.uco.schambers.classmate.Adapter.HttpResponse;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.AdapterModels.TokenUtility;
import edu.uco.schambers.classmate.R;

/**
 * Created by Nelson.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentCurrentClasses.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentCurrentClasses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentCurrentClasses extends Fragment {
    private OnFragmentInteractionListener mListener;

    ArrayList<Class> classList;

    TableLayout classTable;
    Context context;
    TableRow classTableHeader;

    EnrollmentAdapter enrollmentAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StudentCurrentClasses.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentCurrentClasses newInstance() {
        StudentCurrentClasses fragment = new StudentCurrentClasses();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StudentCurrentClasses() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        classTable.removeAllViews();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_student_drop_classes, null);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void initUI(final View rootView) throws JSONException {

        //Create a table layout object and link it to the fragment's table layout
        classTable = (TableLayout) rootView.findViewById(R.id.drop_table);


        context = rootView.getContext();
        resetTable();

        // instantiate arraylists
        classList = new ArrayList<>();


        //final User user = TokenUtility.parseUserToken(getActivity());
        enrollmentAdapter = new EnrollmentAdapter();

        refreshList();
    }

    public void refreshList() {
        try {
            enrollmentAdapter.getEnrolledClasses(TokenUtility.parseUserToken(getActivity()).getpKey(), false, new Callback<ArrayList<Class>>() {
                @Override
                public void onComplete(ArrayList<Class> result) throws Exception {
                    classList.clear();
                    for (Class c : result) {
                        classList.add(c);
                    }

                    buildTable();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTable(){
        resetTable();

        //Loops through the entire list of students
        for (final Class classItem : classList) {
            //Create a new table row
            TableRow tr = new TableRow(context);

            //Set each column's data in the row
            TextView c0 = new TextView(context);
            c0.setText(classItem.getClass_name());
            TextView c1 = new TextView(context);
            c1.setText(classItem.getProfessor_name());
            final Button c2 = new Button(context);
            c2.setText("Drop");

            c2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        enrollmentAdapter.dropClass(classItem.getId(), TokenUtility.parseUserToken(getActivity()).getpKey(), new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse result) throws Exception {
                                Log.d("EnrollDebug", "Drop result" + result);
                                if (result.getHttpCode() == 403) {
                                    Toast.makeText(getActivity(), "Unable to drop class with attendance", Toast.LENGTH_LONG).show();
                                } else if (result.getHttpCode() >= 400) {
                                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Drop was Successful ", Toast.LENGTH_LONG).show();
                                    refreshList();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //Add the data to the row
            tr.addView(c0, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr.addView(c1, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tr.addView(c2, new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            //Add the row to the table
            classTable.addView(tr);
        }
    }

    private void resetTable(){
        if (classTable == null){
            return;
        }

        classTable.bringToFront();
        classTable.removeAllViews();

        if (classTableHeader == null) {
            //Create a new table row
            classTableHeader = new TableRow(context);
            //Set up the clickable functionality for the table row
            classTableHeader.setClickable(false);

            TextView hClassName = new TextView(context);
            hClassName.setText("Class");
            TextView hProf = new TextView(context);
            hProf.setText("Professor");
            TextView hEnrollStatus = new TextView(context);
            hEnrollStatus.setText("Drop");

            Drawable cellBackground = ContextCompat.getDrawable(context, R.drawable.cell_shape);
            hClassName.setBackground(cellBackground);
            hProf.setBackground(cellBackground);
            hEnrollStatus.setBackground(cellBackground);

            hClassName.setTypeface(Typeface.DEFAULT_BOLD);
            hProf.setTypeface(Typeface.DEFAULT_BOLD);
            hEnrollStatus.setTypeface(Typeface.DEFAULT_BOLD);

            hClassName.setPadding(10, 0, 0, 0);
            hProf.setPadding(10, 0, 0, 0);
            hEnrollStatus.setPadding(10, 0, 0, 0);

            hClassName.setTextSize(18);
            hProf.setTextSize(18);
            hEnrollStatus.setTextSize(18);

            classTableHeader.addView(hClassName, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            classTableHeader.addView(hProf, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            classTableHeader.addView(hEnrollStatus, new TableRow.LayoutParams(2, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        }

        classTable.addView(classTableHeader);
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
