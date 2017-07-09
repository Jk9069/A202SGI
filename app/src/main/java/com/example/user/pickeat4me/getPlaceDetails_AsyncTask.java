package com.example.user.pickeat4me;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by user on 6/9/2017.
 */

public class getPlaceDetails_AsyncTask {
    public AsyncResponse async = null;

    /** A class, to download Google Place Details */
    public class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;

        public PlacesTask (AsyncResponse listener){
            async = listener;
        }

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        /** A method to download json data from url */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;

            try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();
                br.close();

            }catch(Exception e){
                Log.d("Download url exception", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Place Details in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, HashMap<String,String>>{
        JSONObject jObject;
        private AsyncResponse delegate = async;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String,String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            //placeDetailsJSONparse placeDetailsJsonParser = new placeDetailsJSONparse();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                //hPlaceDetails = placeDetailsJsonParser.parse(jObject);
                hPlaceDetails = parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return hPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String,String> hPlaceDetails){
            if (hPlaceDetails != null)
                delegate.getAllPlacesDetails(hPlaceDetails);
            else    //happens when quota maxed out
                Log.d ("PlaceDetailsError", "Failed to retrieve place details");
        }
    }

    private HashMap<String, String> parse(JSONObject jObj){
        JSONObject jPlaceDetails = null;

        try {
            jPlaceDetails = jObj.getJSONObject("result");
        } catch (JSONException e){
            e.printStackTrace();
        }

        return getPlaceDetails(jPlaceDetails);
    }

    private HashMap<String, String> getPlaceDetails(JSONObject jPlaceDetail){
        HashMap<String, String> placeDetails = new HashMap<String, String>();

        String name = "N/A";
        String address = "N/A";
        String placeID = "N/A";
        String photoRef = "N/A";
        String latitude = "";
        String longitude = "";
        String phoneNo = "N/A";
        String rating = "N/A";
        String website = "N/A";
        String price_level = "N/A";

        String openinghr = "N/A";
        String openTimesMon = "N/A";
        String openTimesTues = "N/A";
        String openTimesWed ="N/A";
        String openTimesThurs = "N/A";
        String openTimesFri = "N/A";
        String openTimesSat = "N/A";
        String openTimesSun = "N/A";


        try {
            if (!jPlaceDetail.isNull("name"))
                name = jPlaceDetail.getString("name");

            if (!jPlaceDetail.isNull("place_id"))
                placeID = jPlaceDetail.getString("place_id");

            if (!jPlaceDetail.isNull("formatted_address"))
                address = jPlaceDetail.getString("formatted_address");

            if (!jPlaceDetail.isNull("international_phone_number"))
                phoneNo = jPlaceDetail.getString("international_phone_number");

            if (!jPlaceDetail.isNull("rating"))
                rating = jPlaceDetail.getString("rating");

            if (!jPlaceDetail.isNull("opening_hours")) {
                openinghr = jPlaceDetail.getJSONObject("opening_hours").getString("open_now");
                openTimesMon = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(0).toString();
                openTimesTues = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(1).toString();
                openTimesWed = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(2).toString();
                openTimesThurs = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(3).toString();
                openTimesFri = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(4).toString();
                openTimesSat = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(5).toString();
                openTimesSun = jPlaceDetail.getJSONObject("opening_hours").getJSONArray("weekday_text").get(6).toString();

            }

            if (!jPlaceDetail.isNull("photos")){
                photoRef = jPlaceDetail.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            }

            if (!jPlaceDetail.isNull("website"))
                website = jPlaceDetail.getString("website");

            if (!jPlaceDetail.isNull("price_level"))
                price_level = jPlaceDetail.getString("price_level");

            latitude = jPlaceDetail.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlaceDetail.getJSONObject("geometry").getJSONObject("location").getString("lng");

            placeDetails.put("name", name);
            placeDetails.put("formatted_address", address);
            placeDetails.put("place_id", placeID);
            placeDetails.put("photoReference", photoRef);
            placeDetails.put("international_phone_number", phoneNo);
            placeDetails.put("rating", rating);
            placeDetails.put("website", website);
            placeDetails.put("lat", latitude);
            placeDetails.put("lng", longitude);
            placeDetails.put("price_level", price_level);

            placeDetails.put("opening_hour", openinghr);
            placeDetails.put("open_days_Mon", openTimesMon);
            placeDetails.put("open_days_Tues", openTimesTues);
            placeDetails.put("open_days_Wed", openTimesWed);
            placeDetails.put("open_days_Thurs", openTimesThurs);
            placeDetails.put("open_days_Fri", openTimesFri);
            placeDetails.put("open_days_Sat", openTimesSat);
            placeDetails.put("open_days_Sun", openTimesSun);

        }catch (JSONException e){
            e.printStackTrace();
        }

        return placeDetails;
    }

}
