package edu.uco.schambers.classmate.Activites;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.uco.schambers.classmate.Fragments.Debug;
import edu.uco.schambers.classmate.Fragments.StudentResponse;
import edu.uco.schambers.classmate.R;


public class MainActivity extends Activity implements StudentResponse.OnFragmentInteractionListener
{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        Fragment debugFragment = new Debug();
        trans.replace(R.id.fragment_container, debugFragment);
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

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri)
    {
        //todo
    }
}
