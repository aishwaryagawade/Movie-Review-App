package com.example.trainingproject.utils;



import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveImageHelper implements Target {
    Context context;
    private String imageName;
    private String imageDir;
    ContextWrapper cw;
    File directory;


    public SaveImageHelper(Context context, String imageName, String imageDir){
        this.context = context;
        this.imageName = imageName;
        this.imageDir = imageDir;

        if(context!=null){
            cw = new ContextWrapper(context);
            directory = cw.getDir(imageDir, Context.MODE_PRIVATE);
        }



    }



    // path to /data/data/yourapp/app_imageDir


    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                final File myImageFile = new File(directory, imageName); // Create image file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(myImageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

            }
        }).start();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
