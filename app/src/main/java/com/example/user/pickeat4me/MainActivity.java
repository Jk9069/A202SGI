package com.example.user.pickeat4me;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity{
    private Fragment fragment;
    private headerToolbar tryToolbar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int botNaviSelected;
    private BottomNavigationView botnavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.header_toolbar);
        tryToolbar = new headerToolbar(toolbar, this);
        tryToolbar.setToolbarTitle(getString(R.string.title_random));

        botnavi = (BottomNavigationView) findViewById(R.id.bot_navigation);
        botnavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null){
            botNaviSelected = 0;
            setBotNaviSelected();
        }
        else {
            botNaviSelected = savedInstanceState.getInt("naviSelectedIndex");
            setBotNaviSelected();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);

        if (isNetworkAvailable())
            getCurrentLocation();
        else{
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(getString(R.string.noInternet));
            dialogBuilder.setCancelable(true);

            dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //User clicks on OK button
                }
            });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public SharedPreferences.Editor putDouble (final SharedPreferences.Editor edit, final String key, final double value){
        edit.putLong(key, Double.doubleToRawLongBits(value)).apply();
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    public double getDouble (final SharedPreferences sharedpref, final String key, final double defVal){
        return Double.longBitsToDouble(sharedpref.getLong(key, Double.doubleToLongBits(defVal)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.toolbar_item, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                Intent i = new Intent(this, custom_SettingsPage.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCurrentLocation(){
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                putDouble(sharedpref.edit(), "latitude", location.getLatitude());
                putDouble(sharedpref.edit(), "longitude", location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //get location updates for every seconds or 1 metre distance
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
        }catch (SecurityException e){
            Log.i("location error", e.toString());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        getCurrentLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt("naviSelectedIndex", botNaviSelected);
        super.onSaveInstanceState(savedInstanceState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.botnavi_random:
                    botNaviSelected = 0;
                    break;
                case R.id.botnavi_nearMe:
                    botNaviSelected = 1;
                    break;
                case R.id.botnavi_favs:
                    botNaviSelected = 2;
                    break;
            }

            setBotNaviSelected();
            return true;
        }
    };

    public void setBotNaviSelected(){
        String titleName = "";
        switch (botNaviSelected){
            case 0:
                fragment = new randomFragment();
                titleName = getString(R.string.title_random);
                break;
            case 1:
                fragment = new nearMeFragment();
                titleName = getString(R.string.title_nearMe);
                break;
            case 2:
                fragment = new favsFragment();
                titleName = getString(R.string.title_favs);
                break;
        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
        tryToolbar.setToolbarTitle(titleName);
    }

    public boolean checkGoogleAvailability(){
        boolean available = false;

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS){
            int requestCode = 10;
            Dialog dialog = apiAvailability.getErrorDialog(this, status, requestCode);
            dialog.show();
        }
        else
            available = true;

        return available;
    }
}
