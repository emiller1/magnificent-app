package com.example.android.webservice;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LocationListener {
    LocationManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        String latitude = "49.5";
        String longitude = "-72.5";
        String timestamp = "2017-04-13 13:25:00";
        String id = "tom";
        String[] input = new String[4];
        input[0] = latitude;
        input[1] = longitude;
        input[2] = timestamp;
        input[3] = id;
        InvokeWebservice myWebservice = new InvokeWebservice();
        myWebservice.execute(input);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class InvokeWebservice extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {

            URL url;
            String response = "";
            String requestURL = "https://130.85.246.142/uploaddata.php";
            try {
                url = new URL(requestURL);
                HttpURLConnection myconnection = (HttpURLConnection) url.openConnection();
                myconnection.setReadTimeout(15000);
                myconnection.setConnectTimeout(15000);
                myconnection.setRequestMethod("POST");
                myconnection.setDoInput(true);
                myconnection.setDoOutput(true);

                OutputStream os = myconnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                StringBuilder str = new StringBuilder();
                str.append("latitude="+params[0]+"&").append("longitude="+params[1]+"&");
                str.append("timestamp="+params[2]+"&").append("id="+params[3]);

                String urstr = str.toString();
                writer.write(urstr);
                writer.flush();

                int responseCode = myconnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            myconnection.getInputStream()
                    ));

                    line = br.readLine();
                    while (line != null){
                        response += line;
                        line = br.readLine();
                    }

                    br.close();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }
}
