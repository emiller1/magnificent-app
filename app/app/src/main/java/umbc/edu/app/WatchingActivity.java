package umbc.edu.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import umbc.edu.services.GuideBoxService;
import umbc.edu.services.GuideBoxService.Result;

public class WatchingActivity extends HomeActivity {
    ArrayList<String> imdbIDList = new ArrayList<String>();
    final private String api_key = "8c6513c863495b95018e7ba2aa2ce49360dc418f";
    ArrayList<GuideBoxService.Result> final_watch_list = new ArrayList<Result>();
     ArrayList<Bitmap> artWorkList = new ArrayList<Bitmap>();
    ArrayList<String> descrList = new ArrayList<String>();
    private ArrayList<SharedPreferences> prefs = new ArrayList<SharedPreferences>();
    ListView watchListView;
    String userShowValue;
    private static browseListAdapter watch_List_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        userShowValue = myIntent.getStringExtra("UserShow");
        HomeActivity.content_layout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = (View) inflater.inflate(R.layout.activity_search_results2,null);
        HomeActivity.content_layout.addView(vi);
        watchListView = (ListView) findViewById(R.id.search_list_view);
        watchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = final_watch_list.get(position).getTitle();
                String imd_id = final_watch_list.get(position).getImdbID();
                long show_id = final_watch_list.get(position).getId();
                Toast.makeText(getApplicationContext(),title+" "+imd_id+" "+String.valueOf(show_id),Toast.LENGTH_LONG).show();
            }
        });
        SharedPreferences watchingSharedActivity = null;
        if(userShowValue.equals("0")){
            watchingSharedActivity = getSharedPreferences("WATCHING",MODE_PRIVATE);
            setTitle("Watcha | Currently Watching");
        }
        else if(userShowValue.equals("1")){
            watchingSharedActivity = getSharedPreferences("COMPLETED",MODE_PRIVATE);
            setTitle("Watcha | Completed Shows");
        }

        Map<String, ?> allEntries = watchingSharedActivity.getAll();
        try {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                imdbIDList.add(entry.getValue().toString());
             //   Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        WatchingAsynctask myWatchingtask = new WatchingAsynctask();
        myWatchingtask.execute(imdbIDList);
    }

    public class WatchingAsynctask extends AsyncTask<ArrayList<String>,Void,String>{

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            HttpURLConnection urlConnection = null;
            HttpURLConnection imgurlConnection = null;
            HttpURLConnection descriptionConnection = null;
            URL url;
            try {
                for(String tempID:params[0]) {
                    url = new URL("http://api-public.guidebox.com/v2/search?api_key=" + api_key + "&type=show&field=id&id_type=imdb&query=" + tempID);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    JsonReader watching_reader = new JsonReader(new InputStreamReader(in));
                    final_watch_list.add(readResult(watching_reader));
                    urlConnection.disconnect();
                }
                } catch (Exception e) {
                e.printStackTrace();
            }finally {
                for (GuideBoxService.Result tempResult: final_watch_list)
                {
                    URL tempURL= null;
                    try {
                        tempURL = new URL(tempResult.getArtwork());
                        imgurlConnection = (HttpURLConnection) tempURL.openConnection();
                        Bitmap artwork = BitmapFactory.decodeStream(tempURL.openStream());
                        artWorkList.add(artwork);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        imgurlConnection.disconnect();
                    }

                }
                for (GuideBoxService.Result descriptionResult: final_watch_list)
                {
                    URL tempURL= null;
                    String tempDescr;
                    try {
                        Log.d("id",String.valueOf(descriptionResult.getId()));
                        tempURL = new URL("http://api-public.guidebox.com/v2/shows/"+descriptionResult.getId()+"?api_key="+api_key);

                        descriptionConnection = (HttpURLConnection) tempURL.openConnection();
                        InputStream in = descriptionConnection.getInputStream();
                        tempDescr = readdescriptionJsonStream(in);
                        Log.d("description",tempDescr);
                        descrList.add(tempDescr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            prefs.add(getSharedPreferences("WATCHING",MODE_PRIVATE));
            prefs.add(getSharedPreferences("COMPLETED",MODE_PRIVATE));
            watch_List_adapter = new browseListAdapter(final_watch_list,artWorkList,descrList,getApplicationContext(),prefs);
            watchListView.setAdapter(watch_List_adapter);

        }
    }
    public GuideBoxService.Result readResult(JsonReader reader) throws IOException {
        reader.beginObject();
        Log.d("in readResult", "hi");
        long id = 0;
        String title = "";
        String imgurl = "";
        String imdb_id="";
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("title")) {
                title = reader.nextString();

            }else if(name.equals("imdb_id")){
                imdb_id = reader.nextString();
            }
            else if(name.equals("artwork_448x252")){
                imgurl = reader.nextString();
            } else{
                reader.skipValue();
            }
        }
        reader.endObject();
            Result tempResult = new GuideBoxService.Result(id, title, imgurl, imdb_id);
            Log.d("wait","wait");
            return tempResult;

    }
    public String readdescriptionJsonStream(InputStream in) throws IOException {
        Log.d("readdescriptionStream", "hi");
        String descr = "Description could not be loaded";
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            reader.beginObject();
            while (reader.hasNext()){
                String message = reader.nextName();
                if(message.equals("overview")){
                    descr = reader.nextString();
                    Log.d("description",descr);

                }
                else{
                    reader.skipValue();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally{
            reader.endObject();
            reader.close();
        }
        return descr;
    }
}
