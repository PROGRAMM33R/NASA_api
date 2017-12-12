package com.example.adam.nasa_api;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 12/12/2017.
 */

public class ISSPeopleJSONParser {

    public List<ISSPeopleEntry> parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPeopleObject(reader);
        } finally {
            reader.close();
        }
    }

    public List<ISSPeopleEntry> readPeopleObject(JsonReader reader) throws IOException {

        List<ISSPeopleEntry> we = new ArrayList<ISSPeopleEntry>();

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("people")) {

                reader.beginArray();
                while(reader.hasNext()){

                    reader.beginObject();
                    while(reader.hasNext()){

                        String name2 = reader.nextName();
                        if (name2.equals("name")){
                            we.add(new ISSPeopleEntry(reader.nextString()));
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
