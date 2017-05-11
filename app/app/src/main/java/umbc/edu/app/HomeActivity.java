package umbc.edu.app;


import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import umbc.edu.services.GuideBoxService;

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
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,Serializable{

    private ArrayList<SharedPreferences> prefs = new ArrayList<SharedPreferences>();
    private static final String WATCHING = "WATCHING" ;
    private static final String COMPLETED = "COMPLETED" ;


    String TAG = "HomeActivity";
    protected String tag = "HomeActivity";

    GuideBoxService myservice;
    DrawerLayout myDrawer;
    static FrameLayout content_layout;
    Toolbar myToolbar;
    IntentFilter browseIntentFilter;
    List<GuideBoxService.Result> browseList = new ArrayList<GuideBoxService.Result>();
    List<Bitmap> browseImages = new ArrayList<Bitmap>();
     static browseListAdapter adapter;

    static ListView browserListView;
    ListView drawerListView;
    List<Bitmap> searchImages = new ArrayList<>();
    List<String> browseDescription = new ArrayList<>();
    List<String> searchDescription = new ArrayList<>();
    List<String> drawerList = new ArrayList<String>();
    List<GuideBoxService.Result> searchList = new ArrayList<GuideBoxService.Result>();
    Spinner mySpinner;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   // private GuideBoxService guideBoxService;
    //ArrayList<Show> shows = new ArrayList<Show>();
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");

        //Set the view for this activity
        setContentView(R.layout.activity_home);

        //create a toolbar for this activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(myToolbar);


        myToolbar.setTitleTextColor(Color.rgb(255,189,111));
        Intent guideboxIntent = new Intent(this,GuideBoxService.class);
        myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        content_layout = (FrameLayout) findViewById(R.id.content_frame);
        browserListView = (ListView) findViewById(R.id.browse_list_view);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
       // drawerList.add("My Lists");
       // drawerList.add("Browse");
       // drawerList.add("Log Out");
        drawerList.add("Watching");
        drawerList.add("Completed");
        drawerListView.setAdapter(new ArrayAdapter<String>(this,R.layout.navigation_drawer_list,drawerList));
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Toast.makeText(getApplicationContext(), String.valueOf(position),Toast.LENGTH_LONG).show();
                    Intent watchingShowIntent = new Intent(getApplicationContext(),WatchingActivity.class);
                    watchingShowIntent.putExtra("UserShow","0");
                    startActivity(watchingShowIntent);
                }
                else if(position ==1){
                    Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_LONG).show();
                    Intent completedShowIntent = new Intent(getApplicationContext(),WatchingActivity.class);
                    completedShowIntent.putExtra("UserShow","1");
                    startActivity(completedShowIntent);
                }
                else if(position ==2){
                    Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_LONG).show();
                    //start appropriate activity
                }
            }
        });
        browseIntentFilter = new IntentFilter();
        browseIntentFilter.addAction("BrowseDone");
        browseIntentFilter.addAction("SearchDone");
        bindService(guideboxIntent,mConnection, Context.BIND_AUTO_CREATE);
    }
    private BroadcastReceiver bintentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d("in Intent receiver",action);
            switch (action) {
                case "BrowseDone":
                    browseList = myservice.browseGuideboxService();
                    browseImages = myservice.getBrowseImages();
                    browseDescription = myservice.getDescrList();
                    for (int i = 0; i < browseList.size(); i++) {
                        Log.d("TitleMainActivity", String.valueOf(i + 1) + browseList.get(i).getTitle());
                    }
                    prefs.add(getSharedPreferences(WATCHING,MODE_PRIVATE));
                    prefs.add(getSharedPreferences(COMPLETED,MODE_PRIVATE));
                    adapter = new browseListAdapter(browseList,browseImages,browseDescription,getApplicationContext(), prefs);
                    browserListView.setAdapter(adapter);
                    browserListView.setOnItemClickListener(HomeActivity.this);
                    break;
                case "SearchDone":
                   try {


                       Log.d("in Intent Receiver", "SearchDone");
                       searchList = myservice.browseGuideboxService();
                       searchImages = myservice.getBrowseImages();
                       searchDescription = myservice.getDescrList();
                       Intent searchIntent = new Intent(getApplicationContext(), searchResults.class);
                       searchIntent.putExtra("search_list", new DataWrapper((ArrayList<GuideBoxService.Result>) searchList));
                       //searchIntent.putParcelableArrayListExtra("image_list", (ArrayList<Bitmap>) searchImages);
                       searchIntent.putStringArrayListExtra("description_list", (ArrayList<String>) searchDescription);


                       startActivity(searchIntent);
                       // search_adapter = new browseListAdapter(searchList,searchImages,searchDescription,getApplicationContext());
                       //browserListView.setAdapter(search_adapter);
                       break;
                   }catch (Exception e){
                       e.printStackTrace();
                   }
            }
        }
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        //SearchManager searchManager =
        //        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
      //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(),"in onquerysubmit",Toast.LENGTH_LONG).show();
                myservice.searchGuideboxService(query,"title");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_openRight:
                myDrawer.openDrawer(GravityCompat.END);
                return true;
            case R.id.action_search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bintentReceiver);
        Log.d("tag", "onPause()");
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
    protected void onResume() {
        super.onResume();


        registerReceiver(bintentReceiver, browseIntentFilter);
        Log.d("tag", "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("tag", "onDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tag", "onStart()");
        //Intent intenet = new Intent(this, GuideBoxService.class);
        //bindService(intenet,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
 //       unregisterReceiver(bintentReceiver);
        Log.w("tag", "onStop()");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String title = browseList.get(position).getTitle();
        String imdb_id = browseList.get(position).getImdbID();
        long show_id = browseList.get(position).getId();
        Toast.makeText(getApplicationContext(),title+" "+imdb_id+" "+String.valueOf(show_id),Toast.LENGTH_LONG).show();
    }
/*
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
    };*/
}
