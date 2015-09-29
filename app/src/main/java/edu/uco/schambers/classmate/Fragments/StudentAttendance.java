package edu.uco.schambers.classmate.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;


import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import edu.uco.schambers.classmate.R;
//import edu.uco.schambers.classmate.model.ClassMate;
//import edu.uco.schambers.classmate.sqlite.DatabaseHelper;

class AttendanceRecord{
    String className;
    int attendances;
    int absences;
    AttendanceRecord(String className, int attendances, int absences){
        this.className = className;
        this.attendances = attendances;
        this.absences = absences;
    }
}

public class StudentAttendance extends Fragment {
    private Spinner s;

    private String[] arraySpinner;
    private AttendanceRecord [] attrecords = new AttendanceRecord[5];
    private int[] sattendance;
    private int[] sabsences;

    private TextView missing, attendance;
   // private DatabaseHelper databaseHelper;
    private OnFragmentInteractionListener mListener;
    private View mChart;

    // TODO: Rename and change types and number of parameters
   /* public static StudentAttendance newInstance(String param1, String param2, DatabaseHelper databaseHelper) {
        StudentAttendance fragment = new StudentAttendance();
        fragment.databaseHelper = databaseHelper;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    } */

    public static StudentAttendance newInstance(String param1, String param2) {
        StudentAttendance fragment = new StudentAttendance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StudentAttendance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attrecords[0] = new AttendanceRecord("Data Structures",15,4);
        attrecords[1] = new AttendanceRecord("Programming I",10,0);
        attrecords[2] = new AttendanceRecord("Programming II",9,7);
        attrecords[3] = new AttendanceRecord("Mobile Apps",12,2);
        attrecords[4] = new AttendanceRecord("Web Server",11,3);
        //  missing=(TextView) missing.findViewById(R.id.textView3);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem mute = menu.add("Mute when checked-in");
        mute.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        final MenuItem vibrate = menu.add("Vibrate when checked-in");
        vibrate.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        vibrate.setVisible(!mute.isVisible());

        mute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(true);
                mute.setVisible(false);

                return true;
            }
        });

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
        initUI(rootView);
        missing = (TextView) rootView.findViewById(R.id.tvabsences);
        attendance = (TextView) rootView.findViewById(R.id.tvattendance);
        return rootView;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initUI(final View rootView) {
        // this.arraySpinner = new String[] {
        //        "Software Design and Development", "Web Server Programming", "Mobile Application Programming", "Programming II", "Programming Languages"
        //};
      //  List<ClassMate> lstClassMate = databaseHelper.getAllClassMate();

        arraySpinner = new String[5];
        sattendance = new int[5];
        sabsences = new int[5];
        sabsences = new int[5];
        for (int i = 0; i < attrecords.length; i++) {
            arraySpinner[i] = attrecords[i].className;
            sattendance[i] = attrecords[i].attendances;
            sabsences[i] = attrecords[i].absences;
        }
        s = (Spinner) rootView.findViewById(R.id.classlist);
        final TextView att = (TextView) rootView.findViewById(R.id.tvattendance);
        final TextView miss = (TextView) rootView.findViewById(R.id.tvabsences);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {


                attendance.setText(String.valueOf(attrecords[position].attendances));
                missing.setText(String.valueOf(attrecords[position].absences));
                String[] titles = new String[]{"attendance", "absences"};
                int[] values = new int[]{attrecords[position].attendances, attrecords[position].absences};
                drawPieChar(rootView, getActivity(), titles, values);
                //att.set
                //att.setText(sattendance(
                // s.getSelectedItemPosition(), -1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }


    public void drawPieChar(View rootView, Context context, String[] title, int[] value) {
        // Color of each Pie Chart Sections
        int[] colors = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED, Color.TRANSPARENT};
        // sattendance = getResources().getStringArray(R.array.attendance_list);
        // sabsences = getResources().getStringArray(R.array.missing_list);

        CategorySeries categorySeries = new CategorySeries("Draw Pie");
        double sum=0;
        for(int i=0;i<value.length;i++){
            sum+=value[i];
        }
        for (int i = 0; i < title.length; i++) {
            categorySeries.add(title[i]+" ("+(((double)value[i]/sum)*100)+"%)", value[i]);
        }
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        defaultRenderer.setShowLegend(true);
        defaultRenderer.setFitLegend(true);
        defaultRenderer.setLabelsColor(Color.BLACK);
        for (int i = 0; i < title.length; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);
            seriesRenderer.setDisplayChartValues(true);
            seriesRenderer.setShowLegendItem(false);
            defaultRenderer.setBackgroundColor(Color.TRANSPARENT);
            defaultRenderer.setApplyBackgroundColor(true);
            defaultRenderer.setLabelsTextSize(40);
            defaultRenderer.addSeriesRenderer(seriesRenderer);
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
