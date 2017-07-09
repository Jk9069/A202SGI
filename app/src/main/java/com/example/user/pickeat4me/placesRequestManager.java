package com.example.user.pickeat4me;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 6/29/2017.
 */

public class placesRequestManager implements AsyncResponse{
    private List<GooglePlace> googlePlaces;
    private int placeType;
    private Activity activity;
    private int placesSize;
    private int counter;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ProgressDialog progressDialog;
    private radius_Slider radiusSlider;
    private boolean random;
    private ImageView imageView;
    private TextView text;
    private GooglePlace googlePlace;

    //nearMe_fragment
    placesRequestManager(Activity a, SwipeRefreshLayout r, int i, ListView l){
        googlePlaces = new ArrayList<>();
        counter = 0;
        activity = a;
        refreshLayout = r;
        placeType = i;
        listView = l;
        progressDialog = new ProgressDialog(activity);
        random = false;
    }

    //random
    placesRequestManager(Activity a, int i, radius_Slider ra, boolean rand, ImageView img, TextView t){
        googlePlaces = new ArrayList<>();
        activity = a;
        random = rand;
        placeType = i;
        radiusSlider = ra;
        imageView = img;
        text = t;
        progressDialog = new ProgressDialog(activity);
    }

    //favourites
    placesRequestManager(Activity a, ListView l){
        googlePlaces = new ArrayList<>();
        placesSize = 0;
        placeType = -1;
        counter = 0;
        activity = a;
        random = false;
        listView = l;
        progressDialog = new ProgressDialog(activity);
    }

    public List<GooglePlace> getGooglePlaces(){
        return googlePlaces;
    }

    public GooglePlace getGooglePlace(){return googlePlace;}

    public void resetCounter(){
        counter = 0;
    }

    public void setPlaceType(int i){placeType = i;}

    private boolean checkInternet(){
        boolean hasInternet = false;

        if (((MainActivity) activity).isNetworkAvailable()){
            hasInternet = true;
        }
        else{
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setMessage(activity.getString(R.string.noInternet));
            dialogBuilder.setCancelable(true);

            dialogBuilder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //User clicks on OK button
                }
            });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

        return hasInternet;
    }

    private StringBuilder stringBuilding(){
        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(activity);
        double latitude, longitude;
        latitude = ((MainActivity)activity).getDouble(sharedpref, "latitude", 0);
        longitude = ((MainActivity)activity).getDouble(sharedpref, "longitude", 0);

        //below section creates the request url
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+ latitude +","+ longitude);

        sb.append("&radius=" + (sharedpref.getInt("radiusPref", 1)) * 1000);

        //if checked, only show opened restaurants
        if (sharedpref.getBoolean("onlyOpenPref", false))
            sb.append("&opennow");

        if (sharedpref.getInt("pricePref", 0) != 0)
            sb.append("&minprice=" + (sharedpref.getInt("pricePref", 0) - 1));

        switch (placeType){
            case 0:
                sb.append("&types=bar");
                break;
            case 1:
                sb.append("&types=cafe");
                break;
            case 2:
                sb.append("&types=restaurant");
                break;
        }

        sb.append("&sensor=true");
        sb.append("&key=AIzaSyAMAWVFNa-a2VEpHq9Ggyf031R2PPJr72U");

        return sb;
    }

    //api is available. request places from api and populate custom listview
    protected void doPlacesRequest (){
        if (checkInternet()){
            progressDialog.setMessage(activity.getString(R.string.retrievingPlace));
            progressDialog.setCancelable(false);
            progressDialog.show();

            StringBuilder sb = stringBuilding();

            getPlace_AsyncTask getPlaceTask = new getPlace_AsyncTask();
            getPlaceTask.new PlacesTask(this).execute(sb.toString());

            //for testing, to make sure that the url works when tested in browser
            Log.i("GooglePlacesURL", sb.toString());
        }
    }

    @Override
    public void getAllPlaces(List<HashMap<String, String>> place) {
        getPlaceDetails_AsyncTask getPlaceDetailsTask = new getPlaceDetails_AsyncTask();

        progressDialog.dismiss();
        progressDialog.setMessage(activity.getString(R.string.preparingList));
        progressDialog.setCancelable(false);
        progressDialog.show();

        placesSize = place.size();

        if (placesSize != 0){
            if (random){
                progressDialog.setMessage(activity.getString(R.string.randomizing));
                progressDialog.setCancelable(false);
                progressDialog.show();

                int max = place.size();
                int randomIndex;

                Random random = new Random();
                randomIndex = random.nextInt(max);  // 0 to (max - 1)

                String placeID = place.get(randomIndex).get("place_id");

                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
                sb.append("place_id=" + placeID);
                sb.append("&sensor=true");
                sb.append("&key=AIzaSyAMAWVFNa-a2VEpHq9Ggyf031R2PPJr72U");

                Log.i("GooglePlacesDetailsURL", sb.toString());

                //get place details for all places
                getPlaceDetailsTask.new PlacesTask(this).execute(sb.toString());
            }
            else{
                for (int i = 0; i < place.size(); i++){
                    String placeID = place.get(i).get("place_id");

                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
                    sb.append("place_id=" + placeID);
                    sb.append("&sensor=true");
                    sb.append("&key=AIzaSyAMAWVFNa-a2VEpHq9Ggyf031R2PPJr72U");

                    Log.i("GooglePlacesDetailsURL", sb.toString());

                    //get place details for all places
                    getPlaceDetailsTask.new PlacesTask(this).execute(sb.toString());
                }
            }
        }
        else{
            if (random){
                progressDialog.dismiss();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setMessage(activity.getString(R.string.noResultsPrompt));
                dialogBuilder.setCancelable(true);

                dialogBuilder.setPositiveButton(activity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(activity);
                        int radius = sharedpref.getInt("radiusPref", 1);
                        radius += 1;
                        sharedpref.edit().putInt("radiusPref", radius).apply();
                        radiusSlider.setSeekbar();
                        doPlacesRequest();
                    }
                });

                dialogBuilder.setNegativeButton(activity.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
            else
                noResultsFound();
        }
    }

    private void noResultsFound(){
        progressDialog.dismiss();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        switch (placeType){
            case 0:
                dialogBuilder.setMessage(activity.getString(R.string.noBar));
                break;
            case 1:
                dialogBuilder.setMessage(activity.getString(R.string.noCafe));
                break;
            case 2:
                dialogBuilder.setMessage(activity.getString(R.string.noRes));
                break;
            default:
                dialogBuilder.setMessage(activity.getString(R.string.noResults));
                break;
        }

        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User clicks on OK button
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);
    }

    @Override
    public void getAllPlacesDetails(HashMap<String, String> placeDetails) {
        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(activity);
        Float minRatings = sharedpref.getFloat("ratingPref", 0);

        String name = placeDetails.get("name");
        String id = placeDetails.get("place_id");
        String photoRef = placeDetails.get("photoReference");
        String formatted_phone = placeDetails.get("international_phone_number");
        String address = placeDetails.get("formatted_address");
        String website = placeDetails.get("website");
        String latitude = placeDetails.get("lat");
        String longitude = placeDetails.get("lng");
        String rating = placeDetails.get("rating");
        String opened = placeDetails.get("opening_hour");
        String priceLvl = placeDetails.get("price_level");

        String openMon = placeDetails.get("open_days_Mon");
        String openTues = placeDetails.get("open_days_Tues");
        String openWed = placeDetails.get("open_days_Wed");
        String openThurs = placeDetails.get("open_days_Thurs");
        String openFri = placeDetails.get("open_days_Fri");
        String openSat = placeDetails.get("open_days_Sat");
        String openSun = placeDetails.get("open_days_Sun");

        GooglePlace place = new GooglePlace(id, name, latitude, longitude, address);

        if (formatted_phone != null)
            place.setPhoneNo(formatted_phone);

        if (website != null)
            place.setWebsite(website);

        if (rating != null)
            place.setRating(rating);

        if (opened != null)
            place.setOpened(opened);

        if (priceLvl != null)
            place.setPriceLevel(priceLvl);

        if (photoRef != null)
            place.setPhotoReference(photoRef);

        String[] dailyOpeningHr = {openMon, openTues, openWed, openThurs, openFri, openSat, openSun};
        place.setWeekdayText(dailyOpeningHr);

        switch (placeType){
            case 0:
            case 1:
            case 2:
                if (!random){
                    if (place.getRating().equalsIgnoreCase("N/A")) {
                        googlePlaces.add(place);
                        counter++;
                    }
                    else if (Float.valueOf(place.getRating()) >= minRatings) {
                        googlePlaces.add(place);
                        counter++;
                    }
                    else
                        placesSize--;
                }
                else{
                    if (photoRef != null) {
                        place.setPhotoReference(photoRef);
                        getPlacePhoto_AsyncTask getPlacePhoto = new getPlacePhoto_AsyncTask();

                        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
                        sb.append("maxwidth=1000");
                        sb.append("&photoreference=" + photoRef);
                        sb.append("&key=AIzaSyAMAWVFNa-a2VEpHq9Ggyf031R2PPJr72U");

                        Log.i("GooglePlacesPhoto", sb.toString());

                        getPlacePhoto.new PlacePhotoTask(imageView).execute(sb.toString());
                    }

                    if (place.getRating().equalsIgnoreCase("N/A")) {
                        googlePlace = place;
                        text.setText(place.getPlaceName());
                    }
                    else if (Float.valueOf(place.getRating()) >= minRatings) {
                        googlePlace = place;
                        text.setText(place.getPlaceName());
                    }
                    else
                        noResultsFound();

                    progressDialog.dismiss();
                }
                break;
            default:
                googlePlaces.add(place);
                placesSize++;
                counter++;
                break;
        }

        if (!random){
            if (counter == placesSize)
                setListView();
            else if (placesSize == 0)
                noResultsFound();
        }
    }

    private void setListView (){
        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);

        final places_listviewAdapter listAdapter = new places_listviewAdapter(googlePlaces);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        progressDialog.dismiss();
    }

    public void getFavourites(String[] favs){
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.retrievingFav));
        progressDialog.show();

        getPlaceDetails_AsyncTask getPlaceDetailsTask = new getPlaceDetails_AsyncTask();

        for (int i = 0; i < favs.length; i++){
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
            sb.append("place_id=" + favs[i]);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyAMAWVFNa-a2VEpHq9Ggyf031R2PPJr72U");

            Log.i("GooglePlacesDetailsURL", sb.toString());

            //get place details for all places
            getPlaceDetailsTask.new PlacesTask(this).execute(sb.toString());
        }
    }
}
