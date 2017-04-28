package umbc.edu.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import umbc.edu.pojo.Show;
import umbc.edu.services.GuideBoxService;

/**
 * @author elishiah miller
 * Created 3/17/17
 *
 * This is not the MainActivity but it is the home screen
 *
 * Note: This Activity should bind to the GuideBox service and unbind when it is done
 */
public class HomeActivity extends AppCompatActivity {

    String TAG = "HomeActivity";
    protected String tag = "HomeActivity";
    private GuideBoxService guideBoxService;
    ArrayList<Show> shows = new ArrayList<Show>();


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
        Intent intenet = new Intent(this, GuideBoxService.class);
        bindService(intenet,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(tag, "onStop()");
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service Connected");
            guideBoxService = ((GuideBoxService.LocalBinder)iBinder).getService();
            guideBoxService.requestShows();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            guideBoxService = null;
        }
    };
}
