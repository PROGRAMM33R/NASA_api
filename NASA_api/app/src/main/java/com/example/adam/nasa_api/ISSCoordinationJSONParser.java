package com.example.adam.nasa_api;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adam on 12/12/2017.
 */

public class ISSCoordinationJSONParser {

    public ISSCoordinationEntry parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readAPODObject(reader);
        } finally {
            reader.close();
        }
    }

    public ISSCoordinationEntry readAPODObject(JsonReader reader) throws IOException {

        ISSCoordinationEntry we = new ISSCoordinationEntry();

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("timezone_id")) {
                we.timeZone = reader.nextString();
            } else if (name.equals("offset")){
                we.offset = reader.nextDouble();
            } else if (name.equals("country_code")){
                we.countryCode = reader.nextString();
            } else if (name.equals("map_url")){
                we.mapLink = reader.nextString();
            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }

}
