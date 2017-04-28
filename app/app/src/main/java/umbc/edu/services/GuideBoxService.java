package umbc.edu.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author elishiah miller
 * Created 3/17/17
 */
public class GuideBoxService extends Service{

    //TODO: This key should be loaded upon successful login from the online db
    //TODO: We will keep it hard coded for now
    final private String api_key = "8c6513c863495b95018e7ba2aa2ce49360dc418f";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    long browse_total_results, browse_total_returned;
    Result search_Result;
    List<Result> final_result = null;
    List<Bitmap> artWorkList = new ArrayList<Bitmap>();
    public List<Result> browseGuideboxService() {
        return final_result;
    }
    public void startGuideboxservice(){
        String[] input = new String[1];
        input[0] = "browse";
        Log.d("in guidebox service","1");
        new guideboxAsyncTask().execute(input);
    }

    public Result searchGuideboxService(String _type, String term) {
        String[] input = new String[3];
        input[0] = "search";
        input[1] = _type; // Elsie can send _type = "id" and Bharadwaz can send _type = "name"
        input[2] = term; // if _type = "id" term = value of id and if _type = "name" term = show name
        new guideboxAsyncTask().execute(input);
        return search_Result;
    }

    public List<Bitmap> getBrowseImages() {
        return artWorkList;
    }


    public class guideboxAsyncTask extends AsyncTask<String, Void, List<Result>> {

        @Override
        protected List<Result> doInBackground(String... params) {
            if (params[0] == "browse") {
                URL url;
                HttpURLConnection urlConnection = null;
                HttpURLConnection imgurlConnection = null;
                HttpURLConnection descriptionConnection = null;
                try {
                    url = new URL("http://api-public.guidebox.com/v2/shows?api_key=8c6513c863495b95018e7ba2aa2ce49360dc418f");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    Log.d("jsonData", "hi");
                    InputStream in = urlConnection.getInputStream();
                    Log.d("in async task", "no");
                    final_result = readJsonStream(in);
                    Log.d("finalresult count", String.valueOf(final_result.size()));
                    Log.d("total results", String.valueOf(browse_total_results));
                    Log.d("total returned", String.valueOf(browse_total_returned));
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
                        URL temURL= null;


                    }
                }
            } else if (params[0] == "search") {
                URL url = null;
                HttpURLConnection urlConnection = null;

                if (params[1] == "id") {
                    try {
                        url = new URL(""); //intialize url using params[2]
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else if (params[1] == "name") {
                    try {
                        url = new URL("http://api-public.guidebox.com/v2/search?api_key=YOUR_API_KEY&type=movie&field=title&query=Terminator");
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
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Result> results) {
            super.onPostExecute(results);
            Log.d("in guideboxservice", String.valueOf(artWorkList.size()));
            Intent myIntent = new Intent();
            myIntent.setAction("BrowseDone");
            getBaseContext().sendBroadcast(myIntent);
        }
    }

    public class Result {
        public Result(List<Result> result_list) {
            this.result_list = result_list;
        }

        long id;
        String title;
 //       String artwork_208x117;
 //       String artwork_304x171;
        String artwork_448x252;
 //       String artwork_608x342;
        List<Result> result_list = new ArrayList<Result>();

        public Result(long id, String title,String imgurl) {
            this.id = id;
            this.title = title;
            this.artwork_448x252 = imgurl;
        }

        public Result() {
        }
        public String getTitle(){
            return this.title;
        }
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

    public List<Result> readMessagesObject(JsonReader reader) throws IOException {
        // List<Result> messages = new ArrayList<Result>();
        List<Result> result_list = new ArrayList<Result>();

        Log.d("in readMessagesObject", "hi");
        reader.beginObject();
        while (reader.hasNext()) {
            Log.d("in readMessage hasNext", "hi");
            String message = reader.nextName();
            if (message.equals("total_results")) {
                browse_total_results = reader.nextLong();
                Log.d("readMessa total_results", String.valueOf(browse_total_results));
            } else if (message.equals("total_returned")) {
                browse_total_returned = reader.nextLong();
            } else if (message.equals("results")) {
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
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("title")) {
                title = reader.nextString();

            }else if(name.equals("artwork_448x252")){
                imgurl = reader.nextString();
            } else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Result(id, title,imgurl);
    }

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

    //TODO: Implement methods for the REST-API

}
