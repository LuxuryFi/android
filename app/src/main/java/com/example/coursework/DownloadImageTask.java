package com.example.coursework;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask {
    public DownloadImageTask(ProgressDialog mProgressDialog, Context context, ImageView image) {
        this.mProgressDialog = mProgressDialog;
        this.context = context;
        this.image = image;
    }

    ProgressDialog mProgressDialog;
    Context context;
    ImageView image;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Download Image Tutorial");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        //o: output - result of the processing
        super.onPostExecute(o);

        image.setImageBitmap((Bitmap) o);
        // Close progressdialog
        mProgressDialog.dismiss();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String imageURL = (String) objects[0];
        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        }catch (Exception ex){

        }
        return  bitmap;
    }
}