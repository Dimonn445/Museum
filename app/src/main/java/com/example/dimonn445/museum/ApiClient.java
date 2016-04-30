package com.example.dimonn445.museum;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by godun on 21.03.2016.
 */
public class ApiClient {
    private String host;
    private Context context;

    public ApiClient(String base_address, Context c) {
        host = base_address;
        context = c;
    }

    private String get(String address) {
        /*String buff = "";
        if (isNetworkAvailable()) {
            address = host + address;
            MyTask mt = new MyTask();
            mt.execute(address);
            try {
                buff = mt.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
        }
        return buff;*/
        String buff = "";
        if (isNetworkAvailable()) {
            address = host + address;
            String body = "";
            Log.d("address ", address);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(address)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                body = response.body().string();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("HTTP Results ", body);
            buff = body;
        } else {
            Toast.makeText(context, context.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
        }
        return buff;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public String getJsonArray(String s) {
        return get(s);
    }

    class MyTask extends AsyncTask<String, Void, String> {
        //        public ProgressDialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, context.getString(R.string.loading), Toast.LENGTH_SHORT).show();
            /*Log.d("OK", "dialog");

            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();*/
        }

        protected String doInBackground(String... urls) {
            /*DefaultHttpClient httpClient;
            HttpGet httpGet;
            BasicHttpResponse httpResponse = null;
            String line, buff = "";
            StringBuilder builder = new StringBuilder();
            builder.append("");
            String address = urls[0];
            Log.d("address ", address);

            try {
                httpClient = new DefaultHttpClient();
                httpGet = new HttpGet(address);
                httpGet.setHeader("Accept", "application/json");
                httpResponse = (BasicHttpResponse) httpClient.execute(httpGet);
                Log.d("OK", "httpResponse: " + httpResponse);
                Log.d("OK", "StatusCode: " + httpResponse.getStatusLine().getStatusCode());

                assert httpResponse != null;
//        if(httpResponse.getStatusLine().getStatusCode()==200){
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        httpResponse.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
//                    buff += line;
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "null";
            }
            Log.i("HTTP Results ", builder.toString());*/
//            return builder.toString();
            String body = "";
            String address = urls[0];
            Log.d("address ", address);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(address)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                body = response.body().string();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("HTTP Results ", body);
            return body;
        }

        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Log.d("OK", context.getString(R.string.connection_is_missing));
                Toast.makeText(context, context.getString(R.string.connection_is_missing), Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }
}
