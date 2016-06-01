package com.kspu.dimonn445.museum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

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
        FileOutputStream fileOutputStream = null;
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Drawable> result = executor.submit(new Callable<Drawable>() {
                public Drawable call() throws Exception {
                    InputStream is = (InputStream) new URL(img_fav_url).getContent();
                    Drawable d = Drawable.createFromStream(is, fileName);
                    return d;
                }
            });
            Bitmap bitmapImage = null;
            try {
                bitmapImage = ((BitmapDrawable) result.get()).getBitmap();
            } catch (Exception e) {
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
        /*Thread thread = new Thread() {
            public void run() {
                try {
                    File directory = context.getDir(directoryName, Context.MODE_PRIVATE);
                    URL url = new URL(img_fav_url);
                    File file = new File(directory, fileName);
                    long startTime = System.currentTimeMillis();
                    Log.d("ImageManager", "download begining");
                    Log.d("ImageManager", "download url:" + url);
                    Log.d("ImageManager", "downloaded file name:" + fileName);

                    URLConnection ucon = url.openConnection();

                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);

                    ByteArrayBuffer baf = new ByteArrayBuffer(50);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baf.toByteArray());
                    fos.close();
                    Log.d("ImageManager", "download ready in"
                            + ((System.currentTimeMillis() - startTime) / 1000)
                            + " sec");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();*/


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
}
