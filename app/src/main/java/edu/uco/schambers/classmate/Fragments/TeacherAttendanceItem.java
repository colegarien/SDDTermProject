/* Team 9Lives
 *
 * Author: Rayan Al-Hammami (Modified version of Student Attendance Module
 *         with permission of Mossi Al-Obaid)
 * Purpose:
 *   UI backend for teacher attendance and class management
 *   module.
 * Edits:
      * 11/23/2015 2:54 AM	Rayan Al-Hammami*	Corrected (From what is apparent) Piechart Data Issue...
        11/10/2015 3:44 PM	Rayan Al-Hammami*	Updated Color Scheme of Pie Chart to Match Classmate Color Scheme...
        11/10/2015 3:44 PM	Rayan Al-Hammami*	Added Requirement to Avoid Empty Note Addition...
        11/10/2015 3:44 PM	Rayan Al-Hammami*	Implement Appending Notes in TeacherAttendanceItem...
        11/10/2015 12:35 PM	Steven	                change fragment based getContext() calls to getActivity()
        11/10/2015 12:15 PM	Rayan Al-Hammami*	Added DatePicker Object to AlertBuilder Dialog...
        11/10/2015 12:15 PM	Rayan Al-Hammami*	Corrected Note Functionality SharedPreferences...
        11/10/2015 12:15 PM	Rayan Al-Hammami*	Added Note Functionality for Individual Students...
        10/9/2015 2:41 PM	ralhamami	        Fixed Version Compatibility Error (Now Compatible with 18+)...
        10/9/2015 3:44 AM	Rayan Al-Hammami*	Updated Comments, Author Blocks, and Cleaned Up...
 */
package edu.uco.schambers.classmate.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import edu.uco.schambers.classmate.R;

public class TeacherAttendanceItem extends Fragment {

    //Will hold absence and attendance data received as argument
    private int absences,attendances;

    //Will hold student name received as argument
    private String studentName;

    //Will store individual notes that will be retrieved from and stored
    //in shared preferences
    private String note;

    //AlertDialog to house the layout for note entry
    private AlertDialog.Builder alert;

    //View that will hold the piechart
    private View mChart;

    //Used for incoming arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    //Shared preferences configuration strings
    private static final String MyPREFS = "MyPREFS";
    private static final String MySCHOOL = "MySCHOOL";

    //Shared preferences object, along with the editor
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    //Textview object linked to the note's textview in the UI controls
    private TextView tvNote;

    public TeacherAttendanceItem() {
        // Required empty public constructor
    }

    public static TeacherAttendanceItem newInstance(String param1, String param2, String param3) {
        TeacherAttendanceItem fragment = new TeacherAttendanceItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the absences, attendaces, and student name arguments,
        //and assign them to respective variables
        absences = Integer.parseInt(getArguments().getString(ARG_PARAM1));
        attendances = Integer.parseInt(getArguments().getString(ARG_PARAM2));
        studentName = getArguments().getString(ARG_PARAM3);

        //Get the shared preferences
        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        editor = sp.edit();
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

        //Get the notes from sharedpreferences for this students
        //and place them in the note textview multiline
        tvNote = (TextView) rootView.findViewById(R.id.tvNote);
        tvNote.setText(sp.getString("attNote-"+studentName,"N/A"));

        //Set the alertdialog
        alert = new AlertDialog.Builder(getActivity());

        //Add the titles and values for the piechart
        String[] titles = new String[]{" Attendance ", " Absences "};
        int[] values = new int[]{attendances, absences};

        //Create an object to reference the student name textview control
        //and then set the current student's name
        TextView tvStudentName = (TextView)rootView.findViewById(R.id.textView4);
        tvStudentName.setText(studentName);

        //Call the method to draw the piechart
        drawPieChar(rootView, rootView.getContext(), titles, values);

        //Set up the button for adding notes, along with an
        //on click listener
        Button addNote = (Button)rootView.findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a vertically oriented linear layout
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                //Create edittext and datepicker objects
                final EditText edittext = new EditText(getActivity());
                edittext.setHint("Enter your note");
                final DatePicker dp = new DatePicker(getActivity());

                //Add the previous two objects to the linear layout
                layout.addView(edittext);
                layout.addView(dp);

                //Add the whole linear layout to the alertdialog
                alert.setView(layout);

                //Configure the alertdialog
                //If save is clicked
                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        boolean isEmpty = false;

                        //Store the input note information
                        TextView tvNote = (TextView) rootView.findViewById(R.id.tvNote);
                        note = edittext.getText().toString();

                        //If no not was entered, by pass the next step
                        if (note.length() <= 0) {
                            isEmpty = true;
                        }
                        if (!isEmpty) {

                            //Store the date as formatted string
                            String date = (dp.getMonth() + 1) + "/" + dp.getDayOfMonth() + "/" + dp.getYear();

                            //Store the old notes so that we can append the new note to that
                            String prevNote = sp.getString("attNote-" + studentName, "");

                            //Add a formatted version of the new note
                            tvNote.setText(date + "-\t" + note + "\n" + prevNote);

                            //Add the note to shared preferences, tagging it for this student
                            //then commit the changes
                            editor.putString("attNote-" + studentName, date + "-\t" + note + "\n" + prevNote);
                            editor.commit();
                        }
                    }
                });

                //Do nothing if user hits cancel
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });
    }


    //Method for drawing the pie chart
    public void drawPieChar(View rootView, Context context, String[] title, int[] value) {
        // Color of each Pie Chart Sections
        int[] colors = {Color.BLUE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.RED, Color.TRANSPARENT};
        CategorySeries categorySeries = new CategorySeries("Draw Pie");

        double sum=0;
        for(int i=0;i<value.length;i++){
            sum+=value[i];
        }

        //Calculate the values and percentages for the legend
        for (int i = 0; i < title.length; i++) {
            categorySeries.add(title[i]+": " + value[i] +" ("+(Math.round(value[i]/sum*100))+"%)\t\t\t\t\t", value[i]);
        }

        //Set up a DefaultRendered for the piechart
        //as well as configure its properties
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

        //Add the piechart to the main layout
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
