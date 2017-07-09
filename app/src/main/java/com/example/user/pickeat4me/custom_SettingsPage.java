package com.example.user.pickeat4me;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by user on 5/21/2017.
 */

public class custom_SettingsPage extends AppCompatActivity {

    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        //initialise toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.header_toolbar);
        headerToolbar myToolbar = new headerToolbar(toolbar, this);
        myToolbar.setToolbarTitle(getString(R.string.title_settings));
        myToolbar.setBackButton();

        //show the settings page layout
        Fragment f = new showSettings();
        getFragmentManager().beginTransaction().replace(R.id.settings_content, f).commit();
    }

    public static class showSettings extends PreferenceFragment {
        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_page);
        }
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
