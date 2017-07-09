package com.example.user.pickeat4me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * Created by user on 5/20/2017.
 */

public class radius_Slider {
    private SeekBar seekBar;
    private Activity activity;
    private EditText seekbarVal;

    public radius_Slider (Activity acActivity, SeekBar sb, EditText sbVal){
        activity = acActivity;
        seekBar = sb;
        seekbarVal = sbVal;
        seekBar.setMax(50);
    }

    public void setSeekbar(){
        final SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(activity);

        //initialise slider
        //get the value of slider stored in the preference
        seekBar.setProgress(sharedpref.getInt("radiusPref", 0));
        seekbarVal.setText(String.valueOf(seekBar.getProgress()));
        seekbarVal.setSelection(seekbarVal.getText().length());

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener(){
                    @Override
                    //show value in the text view beside the slider
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekbarVal.setText(String.valueOf(progress));
                        //save new value to preference
                        sharedpref.edit().putInt("radiusPref", progress).apply();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );


        seekbarVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!android.text.TextUtils.isEmpty(seekbarVal.getText())){
                //if (!seekbarVal.getText().equals("0")){
                    if (Integer.parseInt(seekbarVal.getText().toString()) > 50){
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                        dialogBuilder.setMessage(activity.getString(R.string.maxVal));
                        dialogBuilder.setCancelable(true);

                        dialogBuilder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User clicks on OK button
                            }
                        });

                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();

                        //seekBar.setProgress(50);
                    }

                    try{
                        seekBar.setProgress(Integer.parseInt(seekbarVal.getText().toString()));

                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                seekbarVal.setSelection(seekbarVal.getText().length());
            }
        });
    }
}
