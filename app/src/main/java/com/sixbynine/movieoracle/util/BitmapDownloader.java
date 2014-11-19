package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.sixbynine.movieoracle.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steviekideckel on 11/18/14.
 */
public abstract class BitmapDownloader<E>{

    private Map<String, Bitmap> mMap;

    public BitmapDownloader(){
        mMap = new HashMap<String, Bitmap>();
    }

    protected abstract String getUrl(E object);

    protected abstract void onSuccess(E e, Bitmap bmp);
    protected abstract void onFailure(E e);

    public void loadImage(E e){
        String url = getUrl(e);
        Bitmap bmp = mMap.get(url);
        if(bmp != null){
            onSuccess(e, bmp);
        }else{
            new DownloadPhotoTask(e, url).execute();
        }
    }


    private class DownloadPhotoTask extends AsyncTask<Void, Void, Bitmap>{
        private String mUrl;
        private E e;

        public DownloadPhotoTask(E e, String url){
            this.e = e;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                try {
                    ContextWrapper cw = new ContextWrapper(MyApplication.getInstance().getApplicationContext());

                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    File f=new File(directory, mUrl);
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    if(b != null){
                        return b;
                    }
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream fis = conn.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(fis);
                return image;
            }catch(Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap == null){
                onFailure(e);
            }else{
                onSuccess(e, bitmap);
                new SaveImageTask(mUrl, bitmap).execute();
                mMap.put(mUrl, bitmap);
            }

        }
    }

    private class SaveImageTask extends AsyncTask<Void, Void, Void> {

        private Bitmap file;
        private String url;

        public SaveImageTask(String url, Bitmap file) {
            this.file = file;
            this.url = url;
        }


        @Override
        protected Void doInBackground(Void... params) {
            ContextWrapper cw = new ContextWrapper(MyApplication.getInstance().getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath=new File(directory,url);

            FileOutputStream fos = null;
            try {

                fos = new FileOutputStream(mypath);

                // Use the compress method on the BitMap object to write image to the OutputStream
                file.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
