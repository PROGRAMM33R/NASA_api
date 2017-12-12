package com.example.adam.nasa_api;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ISSActivity extends AppCompatActivity {

    private static final String URL_1 = "https://api.wheretheiss.at/v1/satellites/25544";
    private static final String URL_2 = "https://api.wheretheiss.at/v1/coordinates/";
    private static final String URL_3 = "http://api.open-notify.org/astros.json";

    public static boolean wifiConnected = false;
    public static boolean mobileConnected = false;

    public double lat = 50.0, lon = 50.0;

    ListView listView1 = null;
    ListView listView2 = null;
    ListView listView3 = null;

    public Handler myHandler = null;

    private MyNetworkReceiver receiver = new MyNetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iss);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new MyNetworkReceiver();
        this.registerReceiver(receiver, filter);

        listView1 = (ListView)findViewById(R.id.listView1);
        listView2 = (ListView)findViewById(R.id.listView2);
        listView3 = (ListView)findViewById(R.id.listView3);

    }

    @Override
    public void onStart() {
        super.onStart();

        updateConnectedFlags();
        if (receiver.refreshDisplay) {
            loadPage();
        }

        myHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        new ISSActivity.DownloadJsonTask_2(ISSActivity.this).execute(URL_2 + lat + "," + lon);
                        break;
                    default:
                        break;
                }
            }
            
        };

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    private void loadPage() {
        if (receiver != null) {
            if (((receiver.sPref.equals(receiver.ANY)) && (wifiConnected || mobileConnected))
                    || ((receiver.sPref.equals(receiver.WIFI)) && (wifiConnected))) {

                new ISSActivity.DownloadJsonTask_1(this).execute(URL_1);
                new ISSActivity.DownloadJsonTask_3(this).execute(URL_3);

            } else {
                showErrorPage();
                updateConnectedFlags();
            }
        }
    }

    private void showErrorPage() {
        setContentView(R.layout.activity_asteroids);
        Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
    }

    private class DownloadJsonTask_1 extends AsyncTask<String, Void, ISSEntry> {

        Context context;

        public DownloadJsonTask_1(Context context) {
            this.context = context;
        }

        @Override
        protected ISSEntry doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork_1(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ISSEntry result) {

            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();

                lat = result.latitude;
                lon = result.longitude;

                List<String> iss1 = new ArrayList<>();

                iss1.add("Latitude: " + result.latitude);
                iss1.add("Longitude: " + result.longitude);
                iss1.add("Altitude: " + result.altitude);
                iss1.add("Velocity: " + result.velocity);
                iss1.add("Visibility: " + result.visibility);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        iss1 );

                listView1.setAdapter(arrayAdapter);


                myHandler.sendEmptyMessage(0);

            }

        }
    }

    private class DownloadJsonTask_2 extends AsyncTask<String, Void, ISSCoordinationEntry> {

        Context context;

        public DownloadJsonTask_2(Context context) {
            this.context = context;
        }

        @Override
        protected ISSCoordinationEntry doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork_2(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ISSCoordinationEntry result) {

            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();

                List<String> iss1 = new ArrayList<>();

                iss1.add("Time zone: " + result.timeZone);
                iss1.add("Country: " + result.countryCode);
                iss1.add("Offset: " + result.offset);
                iss1.add(result.mapLink);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        iss1 );

                listView2.setAdapter(arrayAdapter);

                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 3) {
                            Uri uri = Uri.parse(parent.getItemAtPosition(position).toString());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }

                    }
                });
            }

        }
    }

    private class DownloadJsonTask_3 extends AsyncTask<String, Void, List<ISSPeopleEntry>> {

        Context context;

        public DownloadJsonTask_3(Context context) {
            this.context = context;
        }

        @Override
        protected List<ISSPeopleEntry> doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork_3(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ISSPeopleEntry> result) {

            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();

                List<String> iss1 = new ArrayList<>();

                for (int i = 0; i < result.size(); i++){
                    iss1.add(result.get(i).name);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        iss1 );

                listView3.setAdapter(arrayAdapter);
            }

        }
    }

    private ISSEntry loadJsonFromNetwork_1(String urlString) throws IOException {
        InputStream stream = null;
        ISSEntry we = null;

        ISSJSONParser jsonParser = new ISSJSONParser();

        stream = receiver.downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

    private ISSCoordinationEntry loadJsonFromNetwork_2(String urlString) throws IOException {
        InputStream stream = null;
        ISSCoordinationEntry we = null;

        ISSCoordinationJSONParser jsonParser = new ISSCoordinationJSONParser();

        stream = receiver.downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

    private List<ISSPeopleEntry> loadJsonFromNetwork_3(String urlString) throws IOException {
        InputStream stream = null;
        List<ISSPeopleEntry> we = null;

        ISSPeopleJSONParser jsonParser = new ISSPeopleJSONParser();

        stream = receiver.downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

}
