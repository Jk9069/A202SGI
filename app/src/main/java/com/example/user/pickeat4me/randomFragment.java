package com.example.user.pickeat4me;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 5/20/2017.
 */

//implements SensorEventListener for onShake event
public class randomFragment extends Fragment{
    private GooglePlace googlePlace;
    private Spinner placeType;
    private ProgressDialog progressDialog;
    private placesRequestManager placeReq;
    private radius_Slider radiusSlider;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.random_fragment, container, false);
        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ImageView resImg = (ImageView) view.findViewById(R.id.resImg);
        TextView shake = (TextView) view.findViewById(R.id.shake_instruction);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.radius_seekbar);
        EditText seekBarValue = (EditText) view.findViewById(R.id.sliderVal);
        Button randomBtn = (Button) view.findViewById(R.id.randomize_btn);
        placeType = (Spinner) view.findViewById(R.id.sp_placeType);
        setSpinner();

        //initialise UI components
        radiusSlider = new radius_Slider(getActivity(), seekBar, seekBarValue);
        radiusSlider.setSeekbar();

        placeReq = new placesRequestManager(getActivity(), sharedpref.getInt("placeType", 0), radiusSlider, true, resImg, shake);

        resImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googlePlace = placeReq.getGooglePlace();
                if (googlePlace != null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage(getString(R.string.loading));
                    progressDialog.show();

                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    i.putExtra("selectedPlace", googlePlace);

                    startActivity(i);
                    progressDialog.dismiss();
                }
                else{
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    dialogBuilder.setMessage(getString(R.string.randomFirst));
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
        });

        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)getActivity()).isNetworkAvailable()) {
                    if (((MainActivity)getActivity()).checkGoogleAvailability())
                        placeReq.doPlacesRequest();
                }else{
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        //set the value of seekbar in random_fragment after changing radius in settings
        radiusSlider.setSeekbar();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void setSpinner(){
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //initialise Spinner
        String array[] = {"Bar", "Cafe", "Restaurant"};
        ArrayAdapter<String> Spinner_adp = new ArrayAdapter<String>(getActivity().getBaseContext(),
                android.R.layout.simple_spinner_item, array);

        //this line to set what is shown when drop down occurs
        Spinner_adp.setDropDownViewResource(android.R.layout.simple_list_item_1);

        //apply adapter to spinner
        placeType.setAdapter(Spinner_adp);

        //set value from preferences
        placeType.setSelection(sharedpref.getInt("placeType", 0));
        placeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedpref.edit().putInt("placeType", position).apply();
                placeReq.setPlaceType(sharedpref.getInt("placeType", 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
