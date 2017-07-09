package com.example.user.pickeat4me;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class bar_tab extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private Intent i;
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;

    private List<GooglePlace> googlePlaces = new ArrayList<>();
    private ProgressDialog progressDialog;
    private placesRequestManager placeReq;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bar_tab, container, false);
        listView = (ListView) view.findViewById(R.id.nearMe_BarlistView);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.bar_refreshLayout);
        refreshLayout.setOnRefreshListener(this);

        //what happens when each item is selected
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    //when clicked, what happen
                    public void onItemClick (AdapterView<?> parent, View view, int position, long id){
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        googlePlaces = placeReq.getGooglePlaces();

                        if (googlePlaces != null){
                            GooglePlace place = googlePlaces.get(position);

                            i = new Intent(getActivity(), MapsActivity.class);
                            i.putExtra("selectedPlace", place);

                            startActivity(i);
                            progressDialog.dismiss();
                        }
                        else
                            Toast.makeText(getActivity(), "Null :(", Toast.LENGTH_LONG).show();
                    }
                }
        );

        /*
        if (isViewShown) {
            if (checkGoogleAvailability())
            doPlacesRequest();
        }*/

        placeReq = new placesRequestManager(getActivity(), refreshLayout, 0, listView);
        if (((MainActivity)getActivity()).checkGoogleAvailability())
            placeReq.doPlacesRequest();

        return view;
    }

    @Override
    public void onRefresh(){
        listView.setAdapter(null);

        if (((MainActivity)getActivity()).checkGoogleAvailability()) {
            placeReq.resetCounter();
            placeReq.doPlacesRequest();
        }

        refreshLayout.setRefreshing(false);
    }
}
