package com.kspu.dimonn445.museum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by godun on 01.06.2016.
 */

public class ImageSaver {
    private String directoryName = "images";
    private String fileName = "image.png";
    private Context context;

    public ImageSaver(Context context) {
        this.context = context;
    }

    public ImageSaver setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ImageSaver setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public boolean deleteFile() {
        File file = createFile();
        return file.delete();
    }

    public void save(final String img_fav_url) {
        LoadingImageTask mt = new LoadingImageTask();
        mt.execute(img_fav_url);
    }

    @NonNull
    private File createFile() {
        File directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        return new File(directory, fileName);
    }

    public File load() {
        File directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        File f = new File(directory, fileName);
        Log.d("OK", "getAbsolutePath" + f.getAbsolutePath());
        return f;
    }

    class LoadingImageTask extends AsyncTask<String, Void, Void> {
        //        public ProgressDialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(String... url) {
            FileOutputStream fileOutputStream = null;
            try {
                InputStream is = (InputStream) new URL(url[0]).getContent();
                Drawable d = Drawable.createFromStream(is, fileName);
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = ((BitmapDrawable) d).getBitmap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fileOutputStream = new FileOutputStream(createFile());
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
