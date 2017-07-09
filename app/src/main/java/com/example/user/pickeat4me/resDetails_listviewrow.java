package com.example.user.pickeat4me;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by user on 6/2/2017.
 */

public class resDetails_listviewrow extends ArrayAdapter<String> {
    private String[] dailyOpenHr;
    private Context context;

    resDetails_listviewrow(Context c, String[] x, String[] open){
        super(c, R.layout.custom_mapsactivity_listviewrow, x);
        dailyOpenHr = open;
        context = c;
    }

    @NonNull
    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_mapsactivity_listviewrow, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.restaurantdetail_icon);
        TextView itemTitle = (TextView) view.findViewById(R.id.restaurantdetail_ItemTitle);
        TextView itemContent = (TextView) view.findViewById(R.id.restaurantdetail_ItemContent);
        String restaurantItem = getItem(position);

        switch (position){
            case 0:
                itemTitle.setText(R.string.place_address);
                icon.setImageResource(R.drawable.ic_near_me_black_24dp);
                itemContent.setText(restaurantItem);
                break;
            case 1:
                itemTitle.setText(R.string.place_phoneNumber);
                icon.setImageResource(R.drawable.ic_phone_black_24dp);
                itemContent.setText(restaurantItem);
                break;
            case 2:
                itemTitle.setText(R.string.place_openingHour);
                icon.setImageResource(R.drawable.ic_access_time_black_24dp);

                if (dailyOpenHr != null){
                    boolean value = false;
                    for (int i = 0; i < dailyOpenHr.length; i++){
                        if (!dailyOpenHr[i].equalsIgnoreCase("n/a")){
                            value = true;
                            break;
                        }
                    }

                    if (value){
                        StringBuilder builder = new StringBuilder();
                        //for each item inside dailyopenhr array, build all values into a string to be displayed
                        for (String item : dailyOpenHr) {
                            builder.append(item).append("\n");
                        }
                        itemContent.setText(builder.toString());
                    }
                    else
                        itemContent.setText(R.string.notAvailable);
                }
                else
                    itemContent.setText(R.string.notAvailable);

                break;
            case 3:
                itemTitle.setText(R.string.place_rating);
                icon.setImageResource(R.drawable.ic_star_black_24dp);
                itemContent.setText(restaurantItem);
                break;
            case 4:
                itemTitle.setText(R.string.place_priceLvl);

                switch (restaurantItem){
                    case "0":
                        restaurantItem = context.getString(R.string.priceRangepref_veryAff);
                        break;
                    case "1":
                        restaurantItem = context.getString(R.string.priceRangepref_Aff);
                        break;
                    case "2":
                        restaurantItem = context.getString(R.string.priceRangepref_Avg);
                        break;
                    case "3":
                        restaurantItem = context.getString(R.string.priceRangepref_Exp);
                        break;
                    case "4":
                        restaurantItem = context.getString(R.string.priceRangepref_Exc);
                        break;
                    default:
                        restaurantItem = context.getString(R.string.notAvailable);
                        break;
                }

                icon.setImageResource(R.drawable.ic_attach_money_black_24dp);
                itemContent.setText(restaurantItem);
                break;
            case 5:
                itemTitle.setText(R.string.place_website);
                itemContent.setText(restaurantItem);
                icon.setImageResource(R.drawable.ic_public_black_24dp);
                break;

            default:
                itemTitle.setText(R.string.notAvailable);
                itemContent.setText(restaurantItem);
        }

        return view;
    }
}
