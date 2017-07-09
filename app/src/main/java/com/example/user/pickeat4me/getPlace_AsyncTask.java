package com.example.user.pickeat4me;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 6/9/2017.
 */

public class getPlace_AsyncTask {
    private String data = null;
    private AsyncResponse async = null;

    public class PlacesTask extends AsyncTask<String, Integer, String>{
        PlacesTask (AsyncResponse listener){
            async = listener;
        }

        // Invoked by .execute()
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

        private String downloadUrl (String strUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                istream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null)
                    sb.append(line);

                data = sb.toString();
                br.close();

            }catch (Exception e){
                Log.d("Download url exception", e.toString());
            }finally{
                istream.close();
                urlConnection.disconnect();
            }

            return data;
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
        JSONObject jObject;
        private AsyncResponse delegate = async;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                /* Getting the parsed data as a List construct */
                places = parse(jObject);
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
            if (list != null)
                delegate.getAllPlaces(list);
            else
                Log.d ("PlaceError", "Error retrieving places from API");
        }
    }

    //receive JSONobject and returns List. each JSONobject represents a place
    public List<HashMap<String,String>> parse(JSONObject jObj){
        JSONArray places = null;

        //try to retrieve all details in places array
        try {
            places = jObj.getJSONArray("results");
        }catch (JSONException e){
            e.printStackTrace();
        }

        return getPlaces(places);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jplaces){
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> place = null;

        //parse and adds place to list
        for (int i = 0; i < jplaces.length(); i++){
            try {
                place = getPlace((JSONObject)jplaces.get(i));
                placesList.add(place);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject jPlaceDetail){
        HashMap<String, String> place = new HashMap<String, String>();
        String name = "n/a";
        String placeID = "n/a";
        String latitude = "";
        String longitude = "";

        try {
            if (!jPlaceDetail.isNull("name"))
                name = jPlaceDetail.getString("name");

            if (!jPlaceDetail.isNull("place_id"))
                placeID = jPlaceDetail.getString("place_id");

            latitude = jPlaceDetail.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            longitude = jPlaceDetail.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");

            place.put("place_name", name);
            place.put("place_id", placeID);
            place.put("lat", latitude);
            place.put("lng", longitude);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return place;
    }

}
