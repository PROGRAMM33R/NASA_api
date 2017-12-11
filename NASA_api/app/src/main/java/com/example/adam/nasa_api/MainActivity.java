package com.example.adam.nasa_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String API_KEY = "?api_key=DOEYWC9T8bpSUVuZNcS3mXDEuDSoDyYs98Jxuuza";
    private static final String URL = "https://api.nasa.gov/planetary/apod";

    public static boolean wifiConnected = false;
    public static boolean mobileConnected = false;

    private MyNetworkReceiver receiver = new MyNetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new MyNetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateConnectedFlags();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
        } else if (id == R.id.nav_asteroids) {

        } else if (id == R.id.nav_earth) {

        } else if (id == R.id.nav_marsrover) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        if (((receiver.sPref.equals(receiver.ANY)) && (wifiConnected || mobileConnected))
                || ((receiver.sPref.equals(receiver.WIFI)) && (wifiConnected))) {
            new DownloadJsonTask(this).execute(URL + API_KEY);
        } else {
            showErrorPage();
            updateConnectedFlags();
        }
    }

    private void showErrorPage() {
        setContentView(R.layout.activity_main);
        Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
    }

    private class DownloadJsonTask extends AsyncTask<String, Void, APODEntry> {

        Context context;

        public DownloadJsonTask(Context context) {
            this.context = context;
        }

        @Override
        protected APODEntry doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(APODEntry result) {
            setContentView(R.layout.activity_main);

//            TextView text = (TextView)findViewById(R.id.textView);
//            TextView textOutput = (TextView)findViewById(R.id.textView2);

            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();

//                result_ = result;
//                weather_ = result.listMain.get(0);
//                if (weather_ != null) {
//                    String outputText = "Actual temperature: " + weather_.listMain.temp + " deg. C\n" +
//                            "Today min. temperature: " + weather_.listMain.temp_min + " deg. C\n" +
//                            "Today max. temperature: " + weather_.listMain.temp_max + " deg. C\n" +
//                            "Pressure: " + weather_.listMain.pressure + " hPa\n" +
//                            "Humidity: " + weather_.listMain.humidity + " %\n" +
//                            "Weather: " + weather_.weather.main + "\n" +
//                            "Description: " + weather_.weather.description + "\n" +
//                            "Cloudiness: " + weather_.clouds + " %\n" +
//                            "Wind speed: " + weather_.wind.speed + " m/s\n" +
//                            "Last update: " + weather_.dt_txt;
//                    textOutput.setText(outputText);
//
//                    DataPoint[] points = new DataPoint[result_.listMain.size()];
//                    DataPoint[] pointsHumidity = new DataPoint[result_.listMain.size()];
//                    for (int i = 0; i < result_.listMain.size(); i++){
//                        points[i] = new DataPoint(i, result_.listMain.get(i).listMain.temp);
//                        pointsHumidity[i] = new DataPoint(i, result_.listMain.get(i).listMain.humidity);
//                    }
//                    GraphView graph = (GraphView) findViewById(R.id.graph1);
//                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
//                    graph.addSeries(series);
//
//                    GraphView graph2 = (GraphView) findViewById(R.id.graph2);
//                    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(pointsHumidity);
//                    graph2.addSeries(series2);
//                }
            }

        }
    }

    private APODEntry loadJsonFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        APODEntry we = null;

        APODJSONParser jsonParser = new APODJSONParser();

        stream = receiver.downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

}
