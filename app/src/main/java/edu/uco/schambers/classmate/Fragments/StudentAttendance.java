package edu.uco.schambers.classmate.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.uco.schambers.classmate.Adapter.AttendanceAdapter;
import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.EnrollmentAdapter;
import edu.uco.schambers.classmate.AdapterModels.*;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.R;

// Creating class to handles passing values by making getter and setter methods
class AttendanceRecord {
    int number;
    String className;
    int attendances;
    int absences;
    String date;

    public int getNumber() {
        return number;
    }

    public String getClassName() {
        return className;
    }

    public int getAttendances() {
        return attendances;
    }

    public int getAbsences() {
        return absences;
    }

    public String getDate() {
        return date;
    }

    AttendanceRecord(int number, String className, int attendances, int absences, String date) {
        this.number = number;
        this.className = className;
        this.attendances = attendances;
        this.absences = absences;
        this.date = date;
    }
}

public class StudentAttendance extends Fragment {
    private Spinner s;
    ArrayAdapter<SpinnerItem> adapter;
    List<SpinnerItem> arraySpinner;
    private int userId;

    private TextView missing, attendance;
    // private DatabaseHelper databaseHelper;
    private OnFragmentInteractionListener mListener;
    private View mChart;

    // TODO: Rename and change types and number of parameters
    public static StudentAttendance newInstance(String param1, String param2) {
        StudentAttendance fragment = new StudentAttendance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // constructor of StudentAttendance class
    public StudentAttendance() {
        // Required empty public constructor
    }

    //overriding method onCreate  an overriden method is one having same name
    // and signature in child class this is between parent and child class
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //overriding method onCreateOptionsMenuan overriden method is one having same name
    // and signature in child class this is between parent and child class
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem mute = menu.add("Mute when checked-in");
        mute.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        final MenuItem vibrate = menu.add("Vibrate when checked-in");
        vibrate.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        vibrate.setVisible(!mute.isVisible());
        //making visible on screen setting property visible
        mute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            //overridden process here on menuitem
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(true); //set visible property of  vibrate true
                mute.setVisible(false); //set visible property of  mute   false

                return true;
            }
        });
        //implementing listener for vibrate
        vibrate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(false);
                mute.setVisible(true);

                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_attendance, container, false);
        initUI(rootView); //graphics for user interface
        missing = (TextView) rootView.findViewById(R.id.tvabsences);
        attendance = (TextView) rootView.findViewById(R.id.tvattendance);
        return rootView;
    }

    //Api usage making for userinterface
    @TargetApi(Build.VERSION_CODES.M)
    private void initUI(final View rootView) {
        arraySpinner = new ArrayList<>();
        s = (Spinner) rootView.findViewById(R.id.classlist);
        //decalraing adapter for layout
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        //  implementation of exception handling
        try {
            userId = TokenUtility.parseUserToken(getActivity()).getpKey();

            EnrollmentAdapter.getEnrolledClasses(userId, true, new Callback<ArrayList<Class>>() {
                //overridden method which throw exception on completion of array
                @Override
                public void onComplete(ArrayList<Class> result) throws Exception {
                    arraySpinner.add(new SpinnerItem("Select Class", "")); //adding record to array
                    for (Class c : result) {
                        arraySpinner.add(new SpinnerItem(c.getClass_name(), Integer.toString(c.getId())));
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, View view,
                                       final int position, long id) {
                // checking and selecting item from array or record
                if (parent.getSelectedItemPosition() == 0) return;
                //layout setting
                final TableLayout teacherAttendanceTable = (TableLayout) rootView.findViewById(R.id.studentAttendanceTable);
                teacherAttendanceTable.setStretchAllColumns(true);
                teacherAttendanceTable.bringToFront();
                teacherAttendanceTable.removeAllViews();
                SpinnerItem item = (SpinnerItem) s.getSelectedItem();
                int classId = Integer.parseInt(item.getValue());
   /*      setting record in the form of table  and checking attendance absence of
   students display there atendance and absence inthe form of table   */

                try {
                    AttendanceAdapter.getStudentAbsencesByClass(classId, userId, new Callback<ArrayList<StudentAbsenceByClass>>() {
                        @Override
                        public void onComplete(ArrayList<StudentAbsenceByClass> result) throws Exception {
                            int number = 0;
                            for (StudentAbsenceByClass sac : result) {
                                if (!sac.isPresent()) {
                                    TableRow tr = new TableRow(getActivity());
                                    TextView c0 = new TextView(getActivity());
                                    c0.setText(++number + "");
                                    TextView c1 = new TextView(getActivity());
                                    c1.setText(sac.getRollCall());
                                    tr.addView(c0);
                                    tr.addView(c1);
                                    teacherAttendanceTable.addView(tr);
                                }
                            }

                            int attendances = 0;
                            int absences = 0;
                            for (StudentAbsenceByClass sac : result) {
                                if (sac.isPresent())
                                    attendances++;
                                else
                                    absences++;
                            }

                            attendance.setText(attendances + "");
                            missing.setText(absences + "");
                            int[] values = new int[]{attendances, absences};
                            String[] titles = new String[]{"attendance", "absences"};
                            drawPieChar(rootView, getActivity(), titles, values);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
/*     drawing pie chart accoring to above given percentages of students
                weather how much students are presenr and how much not present
              this chart will display the summary of above whole record   */


    public void drawPieChar(View rootView, Context context, String[] title, int[] value) {
        // Color of each Pie Chart Sections
        int[] colors = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED, Color.TRANSPARENT};

        CategorySeries categorySeries = new CategorySeries("Draw Pie");
        double sum = 0;
        for (int i = 0; i < value.length; i++) {
            sum += value[i];
        }
        for (int i = 0; i < title.length; i++) {
            categorySeries.add(title[i] + " (" + (Math.round(value[i] / sum * 100)) + "%)", value[i]);
        }
        Log.d("LOOK!!!!", Math.round(value[1] / sum * 100) + "");
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        defaultRenderer.setShowLegend(true);
        defaultRenderer.setLabelsColor(Color.BLACK);
        for (int i = 0; i < title.length; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);
            seriesRenderer.setDisplayChartValues(true);
            seriesRenderer.setShowLegendItem(true);
            defaultRenderer.setBackgroundColor(Color.TRANSPARENT);
            defaultRenderer.setApplyBackgroundColor(true);
            defaultRenderer.setLabelsTextSize(40);
            defaultRenderer.setLegendTextSize(40);
            defaultRenderer.setShowLabels(false);
            defaultRenderer.addSeriesRenderer(seriesRenderer);
            defaultRenderer.setPanEnabled(false);
        }

        defaultRenderer.setChartTitle("ClassMate");
        defaultRenderer.setChartTitleTextSize(60);
        defaultRenderer.setZoomButtonsVisible(false);

        ChartFactory.getPieChartIntent(context, categorySeries, defaultRenderer, "ClassMate Pie Chart");
        LinearLayout chartContainer = (LinearLayout) rootView.findViewById(R.id.chart);
        chartContainer.removeAllViews();
        mChart = ChartFactory.getPieChartView(context,
                categorySeries, defaultRenderer);
        chartContainer.addView(mChart);
    }

    // TODO: Rename method, update argument and hook method into UI event
    //   implementing an event on pressing any button in reponse to any listener
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
