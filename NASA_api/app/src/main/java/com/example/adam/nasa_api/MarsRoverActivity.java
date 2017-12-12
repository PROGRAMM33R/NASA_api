package com.example.adam.nasa_api;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MarsRoverActivity extends AppCompatActivity {

    private String API_KEY = "&api_key=DOEYWC9T8bpSUVuZNcS3mXDEuDSoDyYs98Jxuuza";
    private static final String URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos";
    private static final String SOL = "?sol=";
    private static final String PAGE = "&page=";
    private static final String CAMERA = "&camera=";

    public static boolean wifiConnected = false;
    public static boolean mobileConnected = false;

    Button buttonConfirm = null;
    Spinner dropdown = null;

    EditText editTextSub = null;
    EditText editTextPage = null;

    ListView listView1 = null;

    private MyNetworkReceiver receiver = new MyNetworkReceiver();

    public String[] items = new String[]{
            "Another camera",
            "FHAZ - Front Hazard Avoidance Camera",
            "RHAZ - Rear Hazard Avoidance Camera",
            "MAST - Mast Camera",
            "CHEMCAM - Chemistry and Camera Complex",
            "MAHLI - Mars Hand Lens Imager",
            "MARDI - Mars Descent Imager",
            "NAVCAM - Navigation Camera",
            "PANCAM - Panoramic Camera",
            "MINITES - Miniature Thermal Emission Spectrometer"};

    public String[] itemsShort = new String[]{
            "",
            "FHAZ",
            "RHAZ",
            "MAST",
            "CHEMCAM",
            "MAHLI",
            "MARDI",
            "NAVCAM",
            "PANCAM",
            "MINITES"};

    public int selectedId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mars_rover);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new MyNetworkReceiver();
        this.registerReceiver(receiver, filter);

        dropdown = (Spinner)findViewById(R.id.spinner1);
        buttonConfirm = (Button)findViewById(R.id.confirmButton);
        editTextSub = (EditText)findViewById(R.id.editTextSub);
        editTextPage = (EditText)findViewById(R.id.editTextPage);
        listView1 = (ListView)findViewById(R.id.listView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        dropdown.setAdapter(adapter);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (receiver.refreshDisplay) {
                    loadPage();
                }

            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                dropdown = parent.getItemAtPosition(position).toString();
//                count = position; //this would give you the id of the selected item
                selectedId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
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

                if (selectedId == 0){
                    new MarsRoverActivity.DownloadJsonTask(this).execute(URL + SOL + editTextSub.getText() + PAGE + editTextPage.getText() + API_KEY);
                } else {
                    new MarsRoverActivity.DownloadJsonTask(this).execute(URL + SOL + editTextSub.getText() + PAGE + editTextPage.getText() + CAMERA + itemsShort[selectedId] + API_KEY);
                }
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

    private class DownloadJsonTask extends AsyncTask<String, Void, List<MarsRoverEntry>> {

        Context context;

        public DownloadJsonTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<MarsRoverEntry> doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MarsRoverEntry> result) {

            if (result == null){
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.connection_success, Toast.LENGTH_SHORT).show();

                List<String> links = new ArrayList<>();

                for (int i = 0; i < result.size(); i++){
                    links.add(result.get(i).linkImg);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        links );

                listView1.setAdapter(arrayAdapter);

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Uri uri = Uri.parse(parent.getItemAtPosition(position).toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);

                    }
                });

            }

        }
    }

    private List<MarsRoverEntry> loadJsonFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        List<MarsRoverEntry> we = null;

        MarsRoverJSONParser jsonParser = new MarsRoverJSONParser();

        stream = receiver.downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

}
