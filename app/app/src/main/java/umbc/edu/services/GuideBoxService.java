package umbc.edu.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.http.HttpClient;
import com.amazonaws.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayList;

import umbc.edu.pojo.Show;

/**
 * @author elishiah miller
 * Created 3/17/17
 */
public class GuideBoxService extends Service {

    String TAG = "GuideBoxService";
    final private String api_key = "8c6513c863495b95018e7ba2aa2ce49360dc418f";
    private ArrayList<Show> shows = new ArrayList<Show>();
    private ArrayList<String> showIds = new ArrayList<String>();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public GuideBoxService() {}

    /**
     * Class used for the client Binder.
     */
    public class LocalBinder extends Binder {
        public GuideBoxService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GuideBoxService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public ArrayList<Show> getShows(){
        return shows;
    }

    private void setShowIds(ArrayList<String> ids){
        showIds = new ArrayList<String>(ids);
    }

    public void requestShows() {
        //Get the Show Ids for the browse screen
        try {
            new BackgroundTaaskGetShowIds().execute(new URL("http://api-public.guidebox.com/v2/shows?api_key="+ api_key));
        } catch (MalformedURLException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    //Define AsynchTasks
    public class BackgroundTaaskGetShowIds extends AsyncTask<URL, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(URL... urls) {
            ArrayList<String> ids = new ArrayList<String>();
            JsonReader reader = readJSONFeed(urls[0]);
            try {
                reader.beginObject();
                while(reader.hasNext()){
                    String name1 = reader.nextName();
                    if(name1.equals("results")){
                        reader.beginArray();
                        while(reader.hasNext()){
                            reader.beginObject();
                            while (reader.hasNext()){
                                String name2 = reader.nextName();
                                if(name2.equals("id")){
                                    int id = reader.nextInt();
                                    ids.add(String.valueOf(id));
                                }else{
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        }
                        reader.endArray();
                    }else{
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } catch (IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return ids;
        }

        // Uncomment this if you want to show the progress of the download to the user
        /*@Override
        protected void onProgressUpdate(Integer... progress){
            Toast.makeText(GuideBoxService.this, String.valueOf(progress[0]) + "% downloaded of show ids", Toast.LENGTH_LONG).show();
        }*/

        @Override
        protected void onPostExecute(ArrayList<String> ids){
            setShowIds(ids);
            Log.d(TAG, "Retreived show Ids");
            //TODO: Go Get The Shows Now That We Have All The Ids
            //TODO Perform a new AsynchTask
        }
    }

    public JsonReader readJSONFeed(URL url) {
        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        } catch (Exception e){
            Log.d("GuideBoxService", e.getLocalizedMessage());

        } finally{
            urlConnection.disconnect();
        }
        return reader;
    }

}
