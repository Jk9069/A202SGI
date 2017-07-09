package com.example.user.pickeat4me;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by user on 5/21/2017.
 */

public class custom_priceRangePreference extends Preference {
    public custom_priceRangePreference(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    private custom_priceRangePreference(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    private TextView text_howExp;
    private SeekBar priceRangebar;

    @Override
    public View onCreateView (ViewGroup container){
        super.onCreateView(container);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_pricerange_preference, container, false);

        text_howExp = (TextView) view.findViewById(R.id.pricerangepref_howExp);
        priceRangebar = (SeekBar) view.findViewById(R.id.pricerangepref_seekbar);
        priceRangebar.setMax(5);
        setPriceRangebar();

        return view;
    }

    private void setPriceRangebar(){
        final SharedPreferences sharedpref = getPreferenceManager().getSharedPreferences();
        //to make seekbar only move 5 times to reach the end
        //priceRangebar.incrementProgressBy(1);

        //initialise
        priceRangebar.setProgress(sharedpref.getInt("pricePref", 0));
        setPriceRange(priceRangebar.getProgress());

        priceRangebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPriceRange(progress);
                sharedpref.edit().putInt("pricePref", progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setPriceRange(int sbProgress){
        switch (sbProgress){
            case 0:
                text_howExp.setText(getContext().getString(R.string.priceRangepref_AnyRange));
                break;
            case 1:
                text_howExp.setText(getContext().getString(R.string.priceRangepref_veryAff));
                break;
            case 2:
                text_howExp.setText(getContext().getString(R.string.priceRangepref_Aff));
                break;
            case 3:
                text_howExp.setText(getContext().getString(R.string.priceRangepref_Avg));
                break;
            case 4:
                text_howExp.setText(getContext().getString(R.string.priceRangepref_Exp));
                break;
            case 5:
                text_howExp.setText(getContext().getString(R.string.priceRangepref_Exc));
                break;
            default:
                break;
        }
    }
}
