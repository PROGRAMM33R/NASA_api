package com.example.adam.nasa_api;

import android.util.JsonReader;

import com.example.adam.nasa_api.MarsRoverEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 12/12/2017.
 */

public class MarsRoverJSONParser {

    public List<MarsRoverEntry> parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readAPODObject(reader);
        } finally {
            reader.close();
        }
    }

    public List<MarsRoverEntry> readAPODObject(JsonReader reader) throws IOException {

        List<MarsRoverEntry> we = new ArrayList<MarsRoverEntry>();

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("photos")) {

                reader.beginArray();
                while(reader.hasNext()){

                    reader.beginObject();
                    while(reader.hasNext()){

                        String name2 = reader.nextName();
                        if (name2.equals("img_src")){
                            we.add(new MarsRoverEntry(reader.nextString()));
                        } else {
                            reader.skipValue();
                        }

                    }
                    reader.endObject();

                }
                reader.endArray();

            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }

}
