package umbc.edu.app;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import umbc.edu.services.GuideBoxService;

/**
 * @author elishiah miller
 * Created 3/17/17
 *
 * This is not the MainActivity but it is the home screen
 *
 * Note: This Activity should bind to the GuideBox service and unbind when it is done
 */
public class HomeActivity extends AppCompatActivity{

    protected String tag = "HomeActivity";
    GuideBoxService myservice;
    DrawerLayout myDrawer;
    FrameLayout content_layout;
    Toolbar myToolbar;
    IntentFilter browseIntentFilter;
    List<GuideBoxService.Result> browseList = new ArrayList<GuideBoxService.Result>();
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_home);
        Intent guideboxIntent = new Intent(this,GuideBoxService.class);
        bindService(guideboxIntent,mConnection, Context.BIND_AUTO_CREATE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.rgb(255,189,111));
        myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        content_layout = (FrameLayout) findViewById(R.id.content_frame);
        browseIntentFilter = new IntentFilter();
        browseIntentFilter.addAction("BrowseDone");
        registerReceiver(bintentReceiver, browseIntentFilter);
    }
    private BroadcastReceiver bintentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "BrowseDone":
                    browseList = myservice.browseGuideboxService();

                    for (int i = 0; i < browseList.size(); i++) {
                        Log.d("TitleMainActivity", String.valueOf(i + 1) + browseList.get(i).getTitle());
                    }
                    break;
            }
        }
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
     /*   SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
*/
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_openRight:
                myDrawer.openDrawer(GravityCompat.END);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GuideBoxService.LocalBinder binder = (GuideBoxService.LocalBinder) service;
            Log.d("hi","in mConnection");
            myservice = binder.getService();
            myservice.startGuideboxservice();
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
