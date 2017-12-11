package com.example.adam.nasa_api;

import android.util.JsonReader;

import com.example.adam.nasa_api.APODEntry;
import com.example.adam.nasa_api.AsteroidsEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 12/11/2017.
 */

public class AsteroidsJSONParser {

    public List<AsteroidsEntry> parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readAsteroidsObject(reader);
        } finally {
            reader.close();
        }
    }

    public List<AsteroidsEntry> readAsteroidsObject(JsonReader reader) throws IOException {

        List<AsteroidsEntry> we = new ArrayList<AsteroidsEntry>();
        AsteroidsEntry tmp = new AsteroidsEntry();

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("near_earth_objects")){

                reader.beginObject();
                while(reader.hasNext()){

                    String name0 = reader.nextName();
                    if (name0.equals(name0)){

                        reader.beginArray();
                        while(reader.hasNext()) {

                            reader.beginObject();
                            while(reader.hasNext()) {
                                String name2 = reader.nextName();
                                if (name2.equals("name")){
                                    tmp.name = reader.nextString();
                                } else if (name2.equals("absolute_magnitude_h")){
                                    tmp.absoluteMagnitude = reader.nextDouble();
                                } else if (name2.equals("estimated_diameter")){

                                    reader.beginObject();
                                    while(reader.hasNext()){
                                        String name3 = reader.nextName();
                                        if (name3.equals("meters")){

                                            reader.beginObject();
                                            while(reader.hasNext()) {
                                                String name4 = reader.nextName();
                                                if (name4.equals("estimated_diameter_min")) {
                                                    tmp.diameterMin = reader.nextDouble();
                                                } else if (name4.equals("estimated_diameter_max")) {
                                                    tmp.diameterMax = reader.nextDouble();
                                                } else {
                                                    reader.skipValue();
                                                }
                                            }
                                            reader.endObject();

                                        } else {
                                            reader.skipValue();
                                        }
                                    }
                                    reader.endObject();

                                } else if (name2.equals("is_potentially_hazardous_asteroid")){
                                    tmp.isHazard = reader.nextBoolean();
                                } else if (name2.equals("close_approach_data")){

                                    reader.beginArray();
                                    while(reader.hasNext()) {

                                        reader.beginObject();
                                        while(reader.hasNext()) {
                                            String name5 = reader.nextName();
                                            if (name5.equals("relative_velocity")){

                                                reader.beginObject();
                                                while(reader.hasNext()){
                                                    String name6 = reader.nextName();
                                                    if (name6.equals("kilometers_per_second")){
                                                        tmp.velocity = reader.nextDouble();
                                                    } else {
                                                        reader.skipValue();
                                                    }
                                                }
                                                reader.endObject();

                                            } else if (name5.equals("miss_distance")){

                                                reader.beginObject();
                                                while(reader.hasNext()){
                                                    String name7 = reader.nextName();
                                                    if (name7.equals("lunar")){
                                                        tmp.missDistance = reader.nextDouble();
                                                        we.add(tmp);
                                                        tmp = new AsteroidsEntry();
                                                    } else {
                                                        reader.skipValue();
                                                    }
                                                }
                                                reader.endObject();

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

                        }
                        reader.endArray();

                    } else {
                        reader.skipValue();
                    }


                }
                reader.endObject();

            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }

}
