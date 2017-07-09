package com.example.user.pickeat4me;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 5/20/2017.
 */

public class places_listviewAdapter extends BaseAdapter {
    private List<GooglePlace> allPlaces;
    private TextView resName;
    private RatingBar ratingBar;
    private ImageView resImg;
    private TextView open;
    private TextView telNo;
    private TextView price;

    @Override
    public int getCount() {
        return allPlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //constructor
    public places_listviewAdapter(List<GooglePlace> data){
        allPlaces = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.custom_near_me_listview, container, false);

        resName = (TextView) view.findViewById(R.id.nearMe_restaurantName);
        resImg = (ImageView) view.findViewById(R.id.nearMe_restaurantImg);
        telNo = (TextView) view.findViewById(R.id.nearMe_restaurantTelNo);
        ratingBar = (RatingBar) view.findViewById(R.id.nearMe_restaurantRating);
        open = (TextView) view.findViewById(R.id.nearMe_restaurantOpeningHr);
        price = (TextView) view.findViewById(R.id.nearMe_restaurantPriceRange);

        setValues(position);
        parseImage(position);

        return view;
    }

    //background task to load images into each listview row
    public void parseImage(int position){
        GooglePlace place  = allPlaces.get(position);
        String photoref = place.getPhotoReference();

        getPlacePhoto_AsyncTask getPlacePhoto = new getPlacePhoto_AsyncTask();
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=1000");
        sb.append("&photoreference=" + photoref);
        sb.append("&key=AIzaSyAMAWVFNa-a2VEpHq9Ggyf031R2PPJr72U");

        Log.i("GooglePlacesPhoto", sb.toString());

         if (photoref != null)
            getPlacePhoto.new PlacePhotoTask(resImg).execute(sb.toString());

    }

    private void setValues (int position){
        GooglePlace placeIndex = allPlaces.get(position);

        if (allPlaces != null) {
            String name = placeIndex.getPlaceName();
            String formatted_phone = placeIndex.getPhoneNo();
            String rating = placeIndex.getRating();
            boolean openinghr = placeIndex.getOpened();
            int priceLvl = placeIndex.getPriceLevel();

            resName.setText(name);
            telNo.setText(formatted_phone);

            if (openinghr)
                open.setText(R.string.openNow);
            else
                open.setText(R.string.closed);

            if (rating != null) {
                try {
                    float ratingVal = Float.parseFloat(rating);
                    ratingBar.setRating(ratingVal);
                } catch (NumberFormatException e) {
                    ratingBar.setRating(0f);
                }
            }

                switch (priceLvl) {
                    case 0:
                        price.setText(R.string.priceRangepref_veryAff);
                        break;
                    case 1:
                        price.setText(R.string.priceRangepref_Aff);
                        break;
                    case 2:
                        price.setText(R.string.priceRangepref_Avg);
                        break;
                    case 3:
                        price.setText(R.string.priceRangepref_Exp);
                        break;
                    case 4:
                        price.setText(R.string.priceRangepref_Exc);
                        break;
                    default:
                        price.setText(R.string.notAvailable);
            }
        }
    }
}
