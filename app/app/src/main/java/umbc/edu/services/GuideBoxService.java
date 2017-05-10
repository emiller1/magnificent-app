package umbc.edu.services;

import android.app.Service;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;


//import android.widget.Toast;

//import com.amazonaws.http.HttpClient;
//import com.amazonaws.http.HttpResponse;

//import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
//import java.nio.channels.AsynchronousCloseException;
//import java.util.ArrayList;

//import umbc.edu.pojo.Show;

/**
 * @author elishiah miller
 * Created 3/17/17
 */
public class GuideBoxService extends Service implements Serializable{

    String TAG = "GuideBoxService";
    final private String api_key = "8c6513c863495b95018e7ba2aa2ce49360dc418f";
  //  private ArrayList<Show> shows = new ArrayList<Show>();
    private ArrayList<String> showIds = new ArrayList<String>();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    long browse_total_results, browse_total_returned;
    Result search_Result;
    List<Result> final_result = null;
    transient List<Bitmap> artWorkList;
    List<String> descrList;
    public List<Result> browseGuideboxService() {
        return final_result;
    }
    public void startGuideboxservice(){
        String[] input = new String[1];
        input[0] = "browse";
        Log.d("in guidebox service","1");
        new guideboxAsyncTask().execute(input);
    }

    public Result searchGuideboxService(String value, String term) {
        String[] input = new String[3];
        input[0] = "search";
        input[1] = value;
        input[2] = term;
        new guideboxAsyncTask().execute(input);
        return search_Result;
    }


    public List<Bitmap> getBrowseImages() {
        return artWorkList;
    }

    public List<String> getDescrList(){ return descrList;}

    public class guideboxAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            descrList = new ArrayList<>();
            artWorkList = new ArrayList<>();
                URL url = null;
                HttpURLConnection urlConnection = null;
                HttpURLConnection imgurlConnection = null;
                HttpURLConnection descriptionConnection = null;
                try {
                    if((params[0] == "browse")|| (params[2] =="title")) {
                        if (params[0] == "browse") {
                            url = new URL("http://api-public.guidebox.com/v2/shows?api_key=8c6513c863495b95018e7ba2aa2ce49360dc418f");
                        } else if (params[2] == "title" ) {

                            String testURL = URLEncoder.encode(params[1],"UTF-8");
                            url = new URL("http://api-public.guidebox.com/v2/search?api_key="+api_key+"&type=show&field=title&query="+testURL);
                        }
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();
                        final_result = readJsonStream(in);
                        Log.d("finalresult count", String.valueOf(final_result.size()));
                        Log.d("total results", String.valueOf(browse_total_results));
                        Log.d("total returned", String.valueOf(browse_total_returned));

                    }
                    else if(params[2] == "imdbid"){
                        url = new URL("http://api-public.guidebox.com/v2/search?api_key="+api_key+"&type=show&field=id&id_type=imdb&query="+params[1]);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();
                        JsonReader search_reader = new JsonReader(new InputStreamReader(in));
                        final_result.add(readResult(search_reader));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                    for (Result tempResult: final_result)
                    {
                        URL tempURL= null;
                        try {
                            tempURL = new URL(tempResult.artwork_448x252);
                            imgurlConnection = (HttpURLConnection) tempURL.openConnection();
                            Bitmap artwork =BitmapFactory.decodeStream(tempURL.openStream());
                            artWorkList.add(artwork);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            imgurlConnection.disconnect();
                        }

                    }
                    for (Result descriptionResult: final_result)
                    {
                        URL tempURL= null;
                        String tempDescr;
                        try {
                            Log.d("id",String.valueOf(descriptionResult.id));
                            tempURL = new URL("http://api-public.guidebox.com/v2/shows/"+descriptionResult.id+"?api_key="+api_key);

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

                /*URL url = null;
                HttpURLConnection urlConnection = null;

                if (params[1] == "id") {
                    try {
                        url = new URL(""); //intialize url using params[2]
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else if (params[1] == "name") {
                    try {
                        url = new URL("http://api-public.guidebox.com/v2/search?api_key="+api_key+"&type=movie&field=title&query=Terminator");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    JsonReader search_reader = new JsonReader(new InputStreamReader(in));
                    search_Result = readResult(search_reader);
                    Log.d("Title", search_Result.title);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }*/

            if (params[0] == "browse") {
                return 1;
            }else{
                return 0;
            }

        }

        @Override
        protected void onPostExecute(Integer x) {
            super.onPostExecute(x);
            Log.d("in guideboxservice", String.valueOf(artWorkList.size()));

            Intent myIntent = new Intent();
            if(x==1) {
                myIntent.setAction("BrowseDone");
            }
            else if(x==0){
                myIntent.setAction("SearchDone");
            }
            getBaseContext().sendBroadcast(myIntent);
        }
    }

    public static class Result implements Serializable{
        public Result(List<Result> result_list) {
            this.result_list = result_list;
        }

        long id;
        String title;
 //       String artwork_208x117;
 //       String artwork_304x171;
        String artwork_448x252;
        String imdb_id;
 //       String artwork_608x342;
        List<Result> result_list = new ArrayList<>();

        public Result(long id, String title,String imgurl, String imdb_id) {
            this.id = id;
            this.title = title;
            this.artwork_448x252 = imgurl;
            this.imdb_id = imdb_id;
        }


        public Result() {
        }
        public String getTitle(){
            return this.title;
        }

        public String getArtwork() { return this.artwork_448x252;
        }

        public String getImdbID() { return this.imdb_id;
        }
        public long getId(){ return this.id;}
    }

    public List<Result> readJsonStream(InputStream in) throws IOException {
        Log.d("in readJsonStream", "hi");
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            return readMessagesObject(reader);
        } finally {
            reader.close();
        }
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

    public List<Result> readMessagesObject(JsonReader reader) throws IOException {
        // List<Result> messages = new ArrayList<Result>();
        List<Result> result_list = new ArrayList<Result>();

        Log.d("in readMessagesObject", "hi");
        reader.beginObject();
        while (reader.hasNext()) {
            Log.d("in readMessage hasNext", "hi");
            String message = reader.nextName();
           /* if (message.equals("total_results")) {
                browse_total_results = reader.nextLong();
                Log.d("readMessa total_results", String.valueOf(browse_total_results));
            } else if (message.equals("total_returned")) {
                browse_total_returned = reader.nextLong();
            } else*/ if (message.equals("results")) {
                result_list = readResultArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return result_list;
    }

    public List<Result> readResultArray(JsonReader reader) throws IOException {
        List<Result> result = new ArrayList<Result>();
        Log.d("in readResultAray", "hi");
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(readResult(reader));
        }
        reader.endArray();
        return result;
    }

    public Result readResult(JsonReader reader) throws IOException {
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
        return new Result(id, title,imgurl, imdb_id);
    }

   // public GuideBoxService() {}


    /**
     * Class used for the client Binder.
     */
    public class LocalBinder extends Binder implements Serializable {
        public GuideBoxService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GuideBoxService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //TODO: Implement methods for the REST-API

  /*
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
        @Override
        protected void onProgressUpdate(Integer... progress){
            Toast.makeText(GuideBoxService.this, String.valueOf(progress[0]) + "% downloaded of show ids", Toast.LENGTH_LONG).show();


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
*/


}
