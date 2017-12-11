package com.example.adam.nasa_api;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adam on 12/11/2017.
 */

public class APODJSONParser {


    public APODEntry parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPollutionObject(reader);
        } finally {
            reader.close();
        }
    }

    public APODEntry readPollutionObject(JsonReader reader) throws IOException {

        APODEntry we = new APODEntry();

        float val1 = 0, val2 = 0, val3 = 0;

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("data")) {

            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }

}
