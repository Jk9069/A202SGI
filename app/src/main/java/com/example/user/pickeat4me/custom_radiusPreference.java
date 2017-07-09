package com.example.user.pickeat4me;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * Created by user on 5/21/2017.
 */

public class custom_radiusPreference extends Preference {
    public custom_radiusPreference (Context context, AttributeSet attrs){
        this (context, attrs, 0);
    }

    public custom_radiusPreference (Context context, AttributeSet attrs, int defStyleAttr){
        super (context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateView (ViewGroup container){
        super.onCreateView(container);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_radius_preference, container, false);

        //initialise radius
        Activity activity = (Activity) getContext();
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.radius_seekbar);
        EditText seekBarValue = (EditText) view.findViewById(R.id.sliderVal);
        radius_Slider radiusSlider = new radius_Slider(activity, seekBar, seekBarValue);
        radiusSlider.setSeekbar();

        return view;
    }
}
