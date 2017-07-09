package com.example.user.pickeat4me;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/27/2017.
 */

public class favsFragment extends Fragment{
    private List<GooglePlace> googlePlaces = new ArrayList<>();
    private ProgressDialog progressDialog;
    private placesRequestManager placeReq;
    private  ListView historyList;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favs_fragment, container, false);
        final SQLiteDB dbHandler = new SQLiteDB(getActivity(), null, null, 1);
        historyList = (ListView) view.findViewById(R.id.favs_listView);
        placeReq = new placesRequestManager(getActivity(), historyList);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();

                Intent i = new Intent(getActivity(), MapsActivity.class);

                if (!((MainActivity) getActivity()).isNetworkAvailable())
                    googlePlaces = dbHandler.getFavPlaces();
                else
                    googlePlaces = placeReq.getGooglePlaces();

                i.putExtra("selectedPlace", googlePlaces.get(position));
                startActivity(i);

                progressDialog.dismiss();
            }
        });

        //no network available, load offline
        if (!((MainActivity) getActivity()).isNetworkAvailable()){
            googlePlaces = dbHandler.getFavPlaces();

            if (googlePlaces.size() != 0) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setMessage(getString(R.string.offlineView));
                dialogBuilder.setCancelable(true);

                dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User clicks on OK button
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                setListView();
            }
            else
                noFavs();
        }
        else {
            String[] favs = dbHandler.getPlaceID();

            if (favs.length != 0) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getActivity().getString(R.string.retrievingFav));
                progressDialog.show();

                placeReq.getFavourites(favs);
                googlePlaces = placeReq.getGooglePlaces();
                progressDialog.dismiss();
            }
            else
                noFavs();
        }

        return view;
    }

    private void noFavs(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(getString(R.string.noFavs));
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

    private void setListView (){
        final places_listviewAdapter listAdapter = new places_listviewAdapter(googlePlaces);
        historyList.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }
}
