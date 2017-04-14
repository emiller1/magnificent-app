package umbc.edu.services;

import android.app.Service;
import android.content.Intent;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author elishiah miller
 * Created 3/17/17
 */
public class GuideBoxService extends Service {

    //TODO: This key should be loaded upon successful login from the online db
    //TODO: We will keep it hard coded for now
    final private String api_key = "8c6513c863495b95018e7ba2aa2ce49360dc418f";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    long browse_total_results, browse_total_returned;
    public void browseGuideboxService() {
        new guideboxAsyncTask().execute("browse");

    }
    public void searchGuideboxService(){

    }

    private class guideboxAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            List<Result> final_Result = new ArrayList<Result>();
            try{
                url = new URL("http://api-public.guidebox.com/v2/shows?api_key=8c6513c863495b95018e7ba2aa2ce49360dc418f");
                urlConnection = (HttpURLConnection)url.openConnection();
                Log.d("jsonData","hi");
                InputStream in = urlConnection.getInputStream();
                Log.d("in async task","no");
                final_Result = readJsonStream(in);
                Log.d("finalresult count", String.valueOf(final_Result.size()));
                Log.d("total results", String.valueOf(browse_total_results));
                Log.d("total returned", String.valueOf(browse_total_returned));
                for (int i =0; i< final_Result.size();i++){
                    Log.d("Title",String.valueOf(i+1)+ final_Result.get(i).title);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }

            return null;
        }
    }

    public class Result{
        public Result(List<Result> result_list) {
            this.result_list = result_list;
        }

        long id;
        String title;
        String artwork_208x117;
        String artwork_304x171;
        String artwork_448x252;
        String artwork_608x342;
        List<Result> result_list = new ArrayList<Result>();

        public Result(long id, String title) {
            this.id = id;
            this.title = title;
        }
    }
    public List<Result> readJsonStream(InputStream in) throws IOException {
        Log.d("in readJsonStream","hi");
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            return readMessagesObject(reader);
        } finally {
            reader.close();
        }
    }

    public List<Result> readMessagesObject(JsonReader reader) throws IOException {
        // List<Result> messages = new ArrayList<Result>();
        List <Result> result_list = new ArrayList<Result>();

        Log.d("in readMessagesObject","hi");
        reader.beginObject();
        while (reader.hasNext()) {
            Log.d("in readMessage hasNext","hi");
            String message = reader.nextName();
            if (message.equals("total_results")) {
                browse_total_results = reader.nextLong();
                Log.d("readMessa total_results", String.valueOf(browse_total_results));
            }else if(message.equals("total_returned")){
                browse_total_returned = reader.nextLong();
            }else if(message.equals("results")){
                result_list = readResultArray(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return result_list;
    }
    public List<Result> readResultArray(JsonReader reader) throws IOException {
        List<Result> result = new ArrayList<Result>();
        Log.d("in readResultAray","hi");
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(readResult(reader));
        }
        reader.endArray();
        return result;
    }
    public Result readResult(JsonReader reader) throws IOException {
        reader.beginObject();
        Log.d("in readResult","hi");
        long id = 0;
        String title = "";
        while (reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("id")){
                id = reader.nextInt();
            }else if(name.equals("title")){
                title = reader.nextString();

            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Result(id,title);
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
