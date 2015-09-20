package edu.uco.schambers.classmate.Activites;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.uco.schambers.classmate.BroadcastReceivers.CallForStudentQuestionResponseReceiver;
import edu.uco.schambers.classmate.Fragments.Debug;
import edu.uco.schambers.classmate.Fragments.Login;
import edu.uco.schambers.classmate.Fragments.StudentInterface;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.Fragments.TeacherInterface;
import edu.uco.schambers.classmate.Fragments.TeacherQuestionResults;
import edu.uco.schambers.classmate.Fragments.UserInformation;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;


public class MainActivity extends Activity implements StudentResponseFragment.OnFragmentInteractionListener, Login.OnFragmentInteractionListener,
        StudentInterface.OnFragmentInteractionListener, TeacherInterface.OnFragmentInteractionListener, TeacherQuestionResults.OnFragmentInteractionListener, UserInformation.OnFragmentInteractionListener
{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFragmentAccordingToIntentAction(getIntent());

    }

    private void startFragmentAccordingToIntentAction(Intent intent)
    {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        switch(intent.getAction())
        {

            case CallForStudentQuestionResponseReceiver.ACTION_REQUEST_QUESTION_RESPONSE:
                Bundle bundle= intent.getExtras();
                IQuestion question =(IQuestion) bundle.getSerializable(StudentResponseFragment.ARG_QUESTION);
                StudentResponseFragment studentResponseFragment= StudentResponseFragment.newInstance(question);
                trans.replace(R.id.fragment_container,studentResponseFragment);
                break;

            default:
                Fragment loginFragment = new Login();
                trans.replace(R.id.fragment_container, loginFragment);
                break;

        }
        trans.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_debug)
        {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            Fragment debugFragment = new Debug();
            trans.replace(R.id.fragment_container, debugFragment).addToBackStack(null);
            trans.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri)
    {
        //todo
    }
}
