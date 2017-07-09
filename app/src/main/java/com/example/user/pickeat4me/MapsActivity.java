package com.example.user.pickeat4me;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private Toolbar resNametoolbar;
    private ListView placeDetailList;
    private SQLiteDB dbHandler;
    private GooglePlace place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_details);
        place = (GooglePlace) getIntent().getSerializableExtra("selectedPlace");
        dbHandler = new SQLiteDB(this, null, null, 1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout2 = (CollapsingToolbarLayout) findViewById(R.id.appbar_collapse2);
        collapsingToolbarLayout2.setTitleEnabled(false);
        collapsingToolbarLayout2 = (CollapsingToolbarLayout) findViewById(R.id.appbar_collapse);
        collapsingToolbarLayout2.setTitleEnabled(false);

        resNametoolbar = (Toolbar) findViewById(R.id.toolbar2);
        resNametoolbar.setTitleTextColor(Color.WHITE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);

                final double latitude = place.getLatitude();
                final double longitude = place.getLongitude();
                final String placeName = place.getPlaceName();

                LatLng placeLocation = new LatLng(latitude, longitude);

                CameraUpdate focus = CameraUpdateFactory.newLatLng(placeLocation);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                mMap.addMarker(new MarkerOptions().position(placeLocation).title(placeName));

                mMap.moveCamera(focus);
                mMap.animateCamera(zoom);
            }
        });

        placeDetailList = (ListView) findViewById(R.id.restaurant_recycleView);
        placeDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                if (position == 1){
                    if (!place.getPhoneNo().equalsIgnoreCase("N/A")){
                        i = new Intent(Intent.ACTION_DIAL);
                        //tel: keyword is required, else exception will be called
                        i.setData(Uri.parse("tel:"+place.getPhoneNo()));
                        startActivity(i);
                    }
                }
                //if website row is clicked, open the website link in browser
                else if (position == 5){
                    if (!place.getWebsite().equalsIgnoreCase("N/A")){
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getWebsite()));
                        startActivity(i);
                    }
                }
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addFav);
        if (dbHandler.checkExist(place))
            fab.setImageResource(R.drawable.ic_white_fav);
        else
            fab.setImageResource(R.drawable.ic_white_unfav);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                // true if data alr exist in database
                if (!dbHandler.checkExist(place)){

                    try {
                        dbHandler.addFavPlace(place);
                    }catch (SQLException e){
                        e.printStackTrace();
                    }

                    message = getString(R.string.addedFav);
                    fab.setImageResource(R.drawable.ic_white_fav);
                }
                else {

                    try{
                        dbHandler.deleteFavPlace(place.getPlaceID());
                    }catch (SQLException e){
                        e.printStackTrace();
                    }

                    message = getString(R.string.removeFav);
                    fab.setImageResource(R.drawable.ic_white_unfav);
                }

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        setPlaceDetailList(place);
    }

    public void setPlaceDetailList(GooglePlace place){
        String name = place.getPlaceName();
        String address = place.getAddress();
        String formatted_phone = place.getPhoneNo();
        String rating = String.valueOf(place.getRating());
        String website = place.getWebsite();
        String openinghr = String.valueOf(place.getOpened());
        String[] weekdayText= place.getWeekdayText();

        resNametoolbar.setTitle(name);

        String[] placeDetails = {address, formatted_phone, openinghr, rating, String.valueOf(place.getPriceLevel()), website};
        ListAdapter adapter = new resDetails_listviewrow(getBaseContext(), placeDetails, weekdayText);
        placeDetailList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
        }
        return true;
    }
}
