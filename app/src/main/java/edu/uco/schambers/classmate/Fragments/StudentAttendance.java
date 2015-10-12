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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uco.schambers.classmate.R;
//import edu.uco.schambers.classmate.model.ClassMate;
//import edu.uco.schambers.classmate.sqlite.DatabaseHelper;

class AttendanceRecord{
    int number;
    String className;
    int attendances;
    int absences;
    String date;
    public int getNumber(){return number;}
    public String getClassName(){return className;}
    public int getAttendances(){return attendances;}
    public int getAbsences(){return absences;}
    public String getDate(){return date;}
    AttendanceRecord(int number,String className, int attendances, int absences,String date){
        this.number=number;
        this.className = className;
        this.attendances = attendances;
        this.absences = absences;
        this.date=date;
    }
}

public class StudentAttendance extends Fragment {
    private Spinner s;
    private int[] snumber;
    private AttendanceRecord [] attendanceRecords = new AttendanceRecord[47];
    private int[] sattendance;
    private int[] sabsences;
    private String[] sdate;

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

    public StudentAttendance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceRecords[0] = new AttendanceRecord(1,"Data Structures",1,0,"09/10/2015");
        attendanceRecords[1] = new AttendanceRecord(2,"Data Structures",1,0,"09/13/2015");
        attendanceRecords[2] = new AttendanceRecord(3,"Data Structures",1,0,"09/16/2015");
        attendanceRecords[3] = new AttendanceRecord(4,"Data Structures",1,0,"09/19/2015");
        attendanceRecords[4] = new AttendanceRecord(5,"Data Structures",0,1,"09/22/2015");
        attendanceRecords[5] = new AttendanceRecord(6,"Data Structures",1,0,"09/25/2015");
        attendanceRecords[6] = new AttendanceRecord(7,"Data Structures",0,1,"09/28/2015");
        attendanceRecords[7] = new AttendanceRecord(1,"Programming I",1,0,"09/10/2015");
        attendanceRecords[8] = new AttendanceRecord(2,"Programming I",1,0,"09/13/2015");
        attendanceRecords[9] = new AttendanceRecord(3,"Programming I",1,0,"09/16/2015");
        attendanceRecords[10] = new AttendanceRecord(4,"Programming I",1,0,"09/19/2015");
        attendanceRecords[11] = new AttendanceRecord(5,"Programming I",1,0,"09/22/2015");
        attendanceRecords[12] = new AttendanceRecord(6,"Programming I",1,0,"09/25/2015");
        attendanceRecords[13] = new AttendanceRecord(7,"Programming I",1,0,"09/28/2015");
        attendanceRecords[14] = new AttendanceRecord(1,"Programming II",1,0,"09/09/2015");
        attendanceRecords[15] = new AttendanceRecord(2,"Programming II",0,1,"09/12/2015");
        attendanceRecords[16] = new AttendanceRecord(3,"Programming II",1,0,"09/15/2015");
        attendanceRecords[17] = new AttendanceRecord(4,"Programming II",0,1,"09/18/2015");
        attendanceRecords[18] = new AttendanceRecord(5,"Programming II",0,1,"09/20/2015");
        attendanceRecords[19] = new AttendanceRecord(6,"Programming II",0,1,"09/22/2015");
        attendanceRecords[20] = new AttendanceRecord(7,"Programming II",1,0,"09/24/2015");
        attendanceRecords[21] = new AttendanceRecord(8,"Programming II",1,0,"09/26/2015");
        attendanceRecords[22] = new AttendanceRecord(9,"Programming II",1,0,"09/29/2015");
        attendanceRecords[23] = new AttendanceRecord(10,"Programming II",1,0,"09/31/2015");
        attendanceRecords[24] = new AttendanceRecord(1,"Mobile Apps",1,0,"09/09/2015");
        attendanceRecords[25] = new AttendanceRecord(2,"Mobile Apps",1,0,"09/11/2015");
        attendanceRecords[26] = new AttendanceRecord(3,"Mobile Apps",1,0,"09/13/2015");
        attendanceRecords[27] = new AttendanceRecord(4,"Mobile Apps",1,0,"09/15/2015");
        attendanceRecords[28] = new AttendanceRecord(5,"Mobile Apps",1,0,"09/17/2015");
        attendanceRecords[29] = new AttendanceRecord(6,"Mobile Apps",1,0,"09/19/2015");
        attendanceRecords[30] = new AttendanceRecord(7,"Mobile Apps",0,1,"09/21/2015");
        attendanceRecords[31] = new AttendanceRecord(8,"Mobile Apps",0,1,"09/23/2015");
        attendanceRecords[32] = new AttendanceRecord(9,"Mobile Apps",0,1,"09/25/2015");
        attendanceRecords[33] = new AttendanceRecord(10,"Mobile Apps",1,0,"09/27/2015");
        attendanceRecords[34] = new AttendanceRecord(11,"Mobile Apps",1,0,"09/29/2015");
        attendanceRecords[35] = new AttendanceRecord(12,"Mobile Apps",0,1,"09/31/2015");
        attendanceRecords[36] = new AttendanceRecord(13,"Mobile Apps",1,0,"10/02/2015");
        attendanceRecords[37] = new AttendanceRecord(1,"Web Server",1,0,"09/10/2015");
        attendanceRecords[38] = new AttendanceRecord(2,"Web Server",1,0,"09/13/2015");
        attendanceRecords[39] = new AttendanceRecord(3,"Web Server",1,0,"09/16/2015");
        attendanceRecords[40] = new AttendanceRecord(4,"Web Server",0,1,"09/19/2015");
        attendanceRecords[41] = new AttendanceRecord(5,"Web Server",0,1,"09/22/2015");
        attendanceRecords[42] = new AttendanceRecord(6,"Web Server",0,1,"09/25/2015");
        attendanceRecords[43] = new AttendanceRecord(7,"Web Server",0,1,"09/28/2015");
        attendanceRecords[44] = new AttendanceRecord(8,"Web Server",1,0,"09/31/2015");
        attendanceRecords[45] = new AttendanceRecord(9,"Web Server",0,1,"10/03/2015");
        attendanceRecords[46] = new AttendanceRecord(10,"Web Server",1,0,"10/06/2015");

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

       // List<ClassMate> lstClassMate = databaseHelper.getAllClassMate();

        final List<String> arraySpinner =  new ArrayList<String>();
        arraySpinner.add("Data Structures");
        arraySpinner.add("Programming I");
        arraySpinner.add("Programming II");
        arraySpinner.add("Mobile Apps");
        arraySpinner.add("Web Server");

        s = (Spinner) rootView.findViewById(R.id.classlist);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                String[] titles = new String[]{"attendance", "absences"};
                int attendances = 0;
                int absences = 0;

                TableLayout teacherAttendanceTable = (TableLayout) rootView.findViewById(R.id.studentAttendanceTable);
                teacherAttendanceTable.setStretchAllColumns(true);
                teacherAttendanceTable.bringToFront();
                teacherAttendanceTable.removeAllViews();
                for (int i = 0; i < attendanceRecords.length; i++) {
                    if (attendanceRecords[i].absences == 1 && attendanceRecords[i].className.equals(parent.getItemAtPosition(position))) { //Here where I change "parent.getItemAtPosition(position)"
                        TableRow tr = new TableRow(rootView.getContext());
                        TextView c0 = new TextView(rootView.getContext());
                        c0.setText(attendanceRecords[i].number + "");
                        TextView c1 = new TextView(rootView.getContext());
                        c1.setText(attendanceRecords[i].date + "");
                        tr.addView(c0);
                        tr.addView(c1);
                        teacherAttendanceTable.addView(tr);
                        absences++;
                    }
                    else if (attendanceRecords[i].attendances == 1 && attendanceRecords[i].className.equals(parent.getItemAtPosition(position))){
                        attendances++;
                    }
                }
                attendance.setText(attendances+"");
                missing.setText(absences+"");
                int[] values = new int[]{attendances, absences};
                drawPieChar(rootView, getContext(), titles, values);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }


    public void drawPieChar(View rootView, Context context, String[] title, int[] value) {
        // Color of each Pie Chart Sections
        int[] colors = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED, Color.TRANSPARENT};

        CategorySeries categorySeries = new CategorySeries("Draw Pie");
        double sum=0;
        for(int i=0;i<value.length;i++){
            sum+=value[i];
        }
        for (int i = 0; i < title.length; i++) {
            categorySeries.add(title[i]+" ("+(Math.round(value[i]/sum*100))+"%)", value[i]);
        }
        Log.d("LOOK!!!!",Math.round(value[1]/sum*100)+"");
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
