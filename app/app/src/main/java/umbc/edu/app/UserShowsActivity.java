package umbc.edu.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * @author elishiah miller
 * Created 3/17/17
 *
 * Note: This Activity should use two services:
 *  (1) Start UserShowsService to pull the user show (i.e show id and episode id ...) from the db
 *  Once you have the user shows/episode ids and status stop the UserSHowsService, when
 *  using an IntentService the service should stop itself.
 *  (2) Now bind to the GuideboxService to pull data
 */
public class UserShowsActivity extends AppCompatActivity {

    protected String tag = "UserShowsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_user_shows);
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
