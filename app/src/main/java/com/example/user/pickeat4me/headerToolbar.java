package com.example.user.pickeat4me;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by user on 5/20/2017.
 */

public class headerToolbar{
    private Toolbar toolbar;
    private AppCompatActivity activity;

    public headerToolbar (Toolbar t, AppCompatActivity apActivity){
        activity = apActivity;
        toolbar = t;
        toolbar.setTitleTextColor(Color.WHITE);

    }

    public void setToolbarTitle (String title){
        toolbar.setTitle(title);
        activity.setSupportActionBar(toolbar);
    }

    public void setBackButton(){
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

}
