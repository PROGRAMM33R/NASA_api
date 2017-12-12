package com.example.adam.nasa_api;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

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

        if (receiver.refreshDisplay) {
            loadPage();
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_asteroids) {
            Intent intent = new Intent(this, AsteroidsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_iss) {
            Intent intent = new Intent(this, ISSActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_marsrover) {
            Intent intent = new Intent(this, MarsRoverActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my apps at: http://www.adam-lasak.xf.cz");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_send) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent
                    .putExtra(Intent.EXTRA_TEXT,
                            "Hey check out my apps at: http://www.adam-lasak.xf.cz");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.facebook.orca");
            try {
                startActivity(sendIntent);
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
            }
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
        if (receiver != null) {
            if (((receiver.sPref.equals(receiver.ANY)) && (wifiConnected || mobileConnected))
                    || ((receiver.sPref.equals(receiver.WIFI)) && (wifiConnected))) {
                new DownloadJsonTask(this).execute(URL + API_KEY);
            } else {
                showErrorPage();
                updateConnectedFlags();
            }
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
                Toast.makeText(context, R.string.download_short, Toast.LENGTH_SHORT).show();
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(APODEntry result) {

            TextView title = (TextView)findViewById(R.id.textView5);
            TextView text = (TextView)findViewById(R.id.textView4);
            TextView url = (TextView)findViewById(R.id.textView2);

            url.setMovementMethod(LinkMovementMethod.getInstance());
            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();
                title.setText(result.title);
                text.setText(result.explanation);
                url.setText(result.link);
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
