package com.example.user.pickeat4me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by user on 6/12/2017.
 */

public class getPlacePhoto_AsyncTask {
    private ImageView imageView;

    public class PlacePhotoTask extends AsyncTask<String, Void, Bitmap>{
        public PlacePhotoTask (ImageView i){
            imageView = i;
        }

        // Invoked by execute() method of this object
        @Override
        protected Bitmap doInBackground(String... url) {
            Bitmap b = null;

            try{
                b = BitmapFactory.decodeStream((InputStream)new URL(url[0]).getContent());
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }

            return b;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(Bitmap result){
            super.onPostExecute(result);
            if (result != null)
                imageView.setImageBitmap(result);
        }

    }
}
