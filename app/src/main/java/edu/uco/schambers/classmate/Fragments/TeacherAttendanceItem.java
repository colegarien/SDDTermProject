/* Team 9Lives
 *
 * Author: Rayan Al-Hammami (Modified version of Student Attendance Module
 *         with permission of Mossi Al-Obaid)
 * Purpose:
 *   UI backend for teacher attendance and class management
 *   module.
 *      Edit 10/09/2015
 *      Added this backend code to create a modified, but similar format to
 *      student attendance module, in order to house individual student records
 *      for teacher attendance module
 */
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
import android.widget.LinearLayout;
import android.widget.TextView;
import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import edu.uco.schambers.classmate.R;

public class TeacherAttendanceItem extends Fragment {
    int absences;
    private TextView missing, attendance;
    private View mChart;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public TeacherAttendanceItem() {
        // Required empty public constructor
    }

    public static TeacherAttendanceItem newInstance(String param1, String param2) {
        TeacherAttendanceItem fragment = new TeacherAttendanceItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        absences = Integer.parseInt(getArguments().getString(ARG_PARAM1));
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
        View rootView = inflater.inflate(R.layout.fragment_teacher_attendance_item, container, false);
        initUI(rootView);
        return rootView;
    }


    //@TargetApi(Build.VERSION_CODES.M)
    private void initUI(final View rootView) {
        String[] titles = new String[]{" Attendance ", " Absences "};
        int[] values = new int[]{10, absences};
        missing = (TextView) rootView.findViewById(R.id.ra_tvAbsences);
        attendance = (TextView) rootView.findViewById(R.id.ra_tvAttendance);
        attendance.setText(10+"");
        missing.setText(absences+"");
        drawPieChar(rootView, rootView.getContext(), titles, values);
    }


    public void drawPieChar(View rootView, Context context, String[] title, int[] value) {
        // Color of each Pie Chart Section
        int[] colors = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED, Color.TRANSPARENT};

        //Set up the percentage data
        CategorySeries categorySeries = new CategorySeries("Draw Pie");
        double sum=0;
        for(int i=0;i<value.length;i++){
            sum+=value[i];
        }
        for (int i = 0; i < title.length; i++) {
            categorySeries.add(title[i]+" ("+(Math.round(value[i]/sum*100))+"%)", value[i]);
        }
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}