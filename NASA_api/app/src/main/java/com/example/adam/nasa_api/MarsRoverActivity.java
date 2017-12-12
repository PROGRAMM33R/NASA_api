package com.example.adam.nasa_api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MarsRoverActivity extends AppCompatActivity {

    private String API_KEY = "&api_key=DOEYWC9T8bpSUVuZNcS3mXDEuDSoDyYs98Jxuuza";
    private static final String URL = "https://api.nasa.gov/neo/rest/v1/feed";
    private static final String DATE_FROM = "?start_date=";
    private static final String DATE_TO = "&end_date=";

    public static boolean wifiConnected = false;
    public static boolean mobileConnected = false;

    Button buttonConfirm = null;
    EditText editTextFrom = null;
    EditText editTextTo = null;
    ListView listOfAsteroids = null;

    private MyNetworkReceiver receiver = new MyNetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mars_rover);
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
                new MarsRoverActivity.DownloadJsonTask(this).execute(URL + DATE_FROM + editTextFrom.getText() + DATE_TO + editTextTo.getText() + API_KEY);
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

    private class DownloadJsonTask extends AsyncTask<String, Void, List<AsteroidsEntry>> {

        Context context;

        public DownloadJsonTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<AsteroidsEntry> doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<AsteroidsEntry> result) {

            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();

                List<String> asteroids = new ArrayList<>();
                AsteroidsEntry sr = null;

                for (int i = 0; i < result.size(); i++){

                    sr = result.get(i);
                    asteroids.add(
                            sr.name + "\n" +
                                    "Velocity: " + sr.velocity + " km/h\n" +
                                    "Diameter min.: " + sr.diameterMin + " m\n" +
                                    "Diameter max.: " + sr.diameterMax + " m\n" +
                                    "Magnitude: " + sr.absoluteMagnitude + " m\n" +
                                    "Miss distance: " + sr.missDistance+ " lunar\n" +
                                    "Is Hazard: " + sr.isHazard
                    );
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        asteroids );

                listOfAsteroids.setAdapter(arrayAdapter);

            }

        }
    }

    private List<AsteroidsEntry> loadJsonFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        List<AsteroidsEntry> we = null;

        AsteroidsJSONParser jsonParser = new AsteroidsJSONParser();

        stream = receiver.downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

}
