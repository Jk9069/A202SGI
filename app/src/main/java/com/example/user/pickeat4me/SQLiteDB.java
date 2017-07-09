package com.example.user.pickeat4me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ju on 19/06/2017.
 */

public class SQLiteDB extends SQLiteOpenHelper {
    private static  final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FavPlaces.db";
    private static final String TABLE_PLACES = "places";

    private static final String COLUMN_ID = "placeID";
    private static final String COLUMN_NAME = "placeName";

    private static final String COLUMN_ADD = "placeAddress";
    private static final String COLUMN_PHONE = "phoneNo";
    private static final String COLUMN_WEB = "placeWeb";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_PRICE = "priceLvl";
    private static final String COLUMN_OPENHR = "openingHours";
    private static final String COLUMN_OPEN = "openNow";
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LNG = "longitude";
    private static final String COLUMN_PHOTOREF = "photoRef";

    private static Context context;

    public SQLiteDB (Context cont, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(cont, DATABASE_NAME, factory, DATABASE_VERSION);
        context = cont;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PLACES + "(" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ADD + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_WEB + " TEXT, " +
                COLUMN_RATING + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_OPEN + " TEXT, " +
                COLUMN_OPENHR + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LNG + " TEXT, " +
                COLUMN_PHOTOREF + " TEXT " + ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }

    //add items into database
    public void addFavPlace(GooglePlace place){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, place.getPlaceID());
        values.put(COLUMN_NAME, place.getPlaceName());
        values.put(COLUMN_ADD, place.getAddress());
        values.put(COLUMN_PHONE, place.getPhoneNo());
        values.put(COLUMN_RATING, place.getRating());
        values.put(COLUMN_WEB, place.getWebsite());
        values.put(COLUMN_OPEN, place.getOpened());

        String price = "";
        switch (place.getPriceLevel()){
            case 0:
                price = context.getString(R.string.priceRangepref_veryAff);
                break;
            case 1:
                price = context.getString(R.string.priceRangepref_Aff);
                break;
            case 2:
                price = context.getString(R.string.priceRangepref_Avg);
                break;
            case 3:
                price = context.getString(R.string.priceRangepref_Exp);
                break;
            case 4:
                price = context.getString(R.string.priceRangepref_Exc);
                break;
            default:
                price = context.getString(R.string.notAvailable);
        }

        values.put(COLUMN_PRICE, price);

        String openingHrs = "";
        if (place.getWeekdayText().length != 0) {
            openingHrs = convertArrayToString(place.getWeekdayText());
            values.put(COLUMN_OPENHR, openingHrs);
        }
        else
            values.put(COLUMN_OPENHR, "N/A");

        values.put(COLUMN_LAT, place.getLatitude());
        values.put(COLUMN_LNG, place.getLongitude());
        values.put(COLUMN_PHOTOREF, place.getPhotoReference());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PLACES, null, values);
        db.close();
    }

    private static String strSeparator = "__,__";
    private static String convertArrayToString(String[] array){
        String openTimes = "";
        for (int i = 0; i < array.length; i++){
            openTimes += array[i];

            //no comma if already at last item
            if (i < array.length - 1)
                openTimes += strSeparator;
        }

        return openTimes;
    }

    private static String[] convertStringToArray(String openTimes){
        String[] array = openTimes.split(strSeparator);
        return array;
    }

    public List<GooglePlace> getFavPlaces (){
        SQLiteDatabase db = getWritableDatabase();
        List<GooglePlace> allGooglePlaces = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PLACES;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            GooglePlace googlePlace = new GooglePlace();
            googlePlace.setPlaceID(c.getString(0));
            googlePlace.setPlaceName(c.getString(1));
            googlePlace.setAddress(c.getString(2));
            googlePlace.setPhoneNo(c.getString(3));
            googlePlace.setWebsite(c.getString(4));
            googlePlace.setRating(c.getString(5));
            googlePlace.setPriceLevel(c.getString(6));
            googlePlace.setOpened(c.getString(7));

            String[] openingHr = convertStringToArray(c.getString(8));
            googlePlace.setWeekdayText(openingHr);

            googlePlace.setLat(c.getString(9));
            googlePlace.setLng(c.getString(10));
            googlePlace.setPhotoReference(c.getString(11));

            allGooglePlaces.add(googlePlace);

            c.moveToNext();
        }

        db.close();

        return allGooglePlaces;
    }

    //delete items in database
    public void deleteFavPlace (String placeID){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLACES + " WHERE "
                + COLUMN_ID + "=\"" + placeID + "\";");
    }

    public boolean checkExist (GooglePlace place){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLACES + " WHERE " + COLUMN_ID + "=?";
        Cursor c = db.rawQuery(query, new String[]{place.getPlaceID()});

        if (c.getCount() > 0)
            return true;
        else
            return false;

    }

    //get all placeIDs from database
    public String[] getPlaceID(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLACES;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String[] dbPlace = new String[c.getCount()];
        int i = 0;

        while (!c.isAfterLast()){
            dbPlace[i] = c.getString(0);
            i++;
            c.moveToNext();
        }

        db.close();

        return dbPlace;
    }
}
