package com.example.user.pickeat4me;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

/**
 * Created by user on 5/21/2017.
 */

public class custom_rateBarPreference extends Preference {
    public custom_rateBarPreference(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public custom_rateBarPreference(Context context, AttributeSet attrs, int defStyleAttr){
        super (context, attrs, defStyleAttr);
    }

    private RatingBar ratebar;

    @Override
    public View onCreateView (ViewGroup container){
        super.onCreateView(container);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_ratebar_preference, container, false);

        ratebar = (RatingBar) view.findViewById(R.id.ratingbarpref_rateBar);
        setRatingBarVal();

        return view;
    }

    private void setRatingBarVal(){
        final SharedPreferences sharedpref = getPreferenceManager().getSharedPreferences();

        //initialise ratingbar, 0 is the default value if no value found in shared preferences
        ratebar.setRating(sharedpref.getFloat("ratingPref", 0));

        ratebar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratebar, float rating, boolean fromUser){
                sharedpref.edit().putFloat("ratingPref", rating).apply();
            }
        });
    }
}
