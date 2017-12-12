package com.example.adam.nasa_api;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adam on 12/12/2017.
 */

public class ISSJSONParser {

    public ISSEntry parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readISSObject(reader);
        } finally {
            reader.close();
        }
    }

    public ISSEntry readISSObject(JsonReader reader) throws IOException {

        ISSEntry we = new ISSEntry();

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("latitude")) {
                we.latitude = reader.nextDouble();
            } else if (name.equals("longitude")){
                we.longitude = reader.nextDouble();
            } else if (name.equals("altitude")){
                we.altitude = reader.nextDouble();
            } else if (name.equals("velocity")){
                we.velocity = reader.nextDouble();
            } else if (name.equals("visibility")){
                we.visibility = reader.nextString();
            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }

}
