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
            return readAPODObject(reader);
        } finally {
            reader.close();
        }
    }

    public APODEntry readAPODObject(JsonReader reader) throws IOException {

        APODEntry we = new APODEntry();

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("date")) {
                we.date = reader.nextString();
            } else if (name.equals("explanation")){
                we.explanation = reader.nextString();
            } else if (name.equals("title")){
                we.title = reader.nextString();
            } else if (name.equals("url")){
                we.link = reader.nextString();
            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }

}
