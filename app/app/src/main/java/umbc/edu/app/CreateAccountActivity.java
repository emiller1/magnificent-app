package umbc.edu.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * @author elishiah miller
 * Created 3/17/17
 *
 * Notes: This Activity should use the UserAccount service. It should not bind to the service it
 * should only start the service using startService(). The Activity should create a pending
 * intent and send it to the service, in which the service should send infomration back to
 * the Activity using the sent pending intent
 */
public class CreateAccountActivity extends AppCompatActivity {

    protected String tag = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(tag, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(tag, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(tag, "onDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(tag, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(tag, "onStop()");
    }
}
