package umbc.edu.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import umbc.edu.services.GuideBoxService;

public class searchResults extends HomeActivity {
    List<Bitmap> searchImages = new ArrayList<>();
    List<String> searchDescription = new ArrayList<>();
    List<GuideBoxService.Result> searchList = new ArrayList<GuideBoxService.Result>();
    List<Bitmap> artWorkList = new ArrayList<>();
    private static browseListAdapter search_adapter;
    Intent myIntent;
    ListView searchListView;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_search_results2);
        Log.d("in second oncreate","in second on create");

        HomeActivity.content_layout.removeAllViews();
        myIntent = getIntent();
       DataWrapper dw = (DataWrapper)myIntent.getSerializableExtra("search_list");
        searchList = dw.getParliaments();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = (View) inflater.inflate(R.layout.activity_search_results2,null);
        HomeActivity.content_layout.addView(vi);
        searchListView = (ListView) findViewById(R.id.search_list_view);
        //content_layout.addView(searchListView);
        //searchImages = myIntent.getParcelableArrayListExtra("image_list");
        //searchDescription = (ArrayList<String>)myIntent.getStringArrayListExtra("description_list");
        //search_adapter = new browseListAdapter(searchList,searchImages,searchDescription,getApplicationContext());
        //HomeActivity.browserListView.setAdapter(search_adapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = searchList.get(position).getTitle();
                Toast.makeText(getApplicationContext(),title,Toast.LENGTH_LONG).show();
            }
        });
        for(int i=0;i<searchList.size();i++){
            Log.d("searchresultsactivity2",searchList.get(i).getTitle());
            Log.d("imageurlactivity2",searchList.get(i).getArtwork());
        }
        List<String> input = new ArrayList<>() ;

        int count = 0;
        for(GuideBoxService.Result tempList:searchList){
            input.add( tempList.getArtwork());
            count++;
        }
        ImageTask myImageTask = new ImageTask();
        Log.d("test", String.valueOf(count));
        myImageTask.execute(input);

    }

   public class ImageTask extends AsyncTask<List<String>,Void,String>{
        HttpURLConnection imgurlConnection;
        @Override
        protected String doInBackground(List<String>... params) {

            for (String tempResult: params[0])
            {

                URL tempURL= null;
                try {
                    tempURL = new URL(tempResult);
                    imgurlConnection = (HttpURLConnection) tempURL.openConnection();
                    Bitmap artwork = BitmapFactory.decodeStream(tempURL.openStream());
                    artWorkList.add(artwork);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    imgurlConnection.disconnect();
                }

            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DataWrapper dw = (DataWrapper)myIntent.getSerializableExtra("search_list");
            searchList = dw.getParliaments();
       //     ListView searchListView = (ListView) findViewById(R.id.browse_list_view);
           // ListView myListView = new ListView(getApplicationContext());
            searchDescription = (ArrayList<String>)myIntent.getStringArrayListExtra("description_list");
            search_adapter = new browseListAdapter(searchList,artWorkList,searchDescription,getApplicationContext());
            searchListView.setAdapter(search_adapter);
             //  HomeActivity.content_layout.addView(searchListView);
        }
    }
}
