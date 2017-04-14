package umbc.edu.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

    protected String tag = "HomeActivity";
    GuideBoxService myservice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_home);
        Intent guideboxIntent = new Intent(this,GuideBoxService.class);
        bindService(guideboxIntent,mConnection, Context.BIND_AUTO_CREATE);
        Log.d("hi1","befor service call");

    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GuideBoxService.LocalBinder binder = (GuideBoxService.LocalBinder) service;
            Log.d("hi","in mConnection");
            myservice = binder.getService();
            myservice.browseGuideboxService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
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
