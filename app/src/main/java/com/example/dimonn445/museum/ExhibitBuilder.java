package com.example.dimonn445.museum;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by godun on 30.03.2016.
 */
public class ExhibitBuilder {
    private JSONObject dataJsonObj = null;
    private JSONObject exhArr;
    private JSONArray mediaArr;
    private JSONArray docsArr;
    private JSONArray characteristics;
    private JSONArray categoryArr;
    public ArrayList<String> imgCdn, mediaCdn;
    private Context context;
    private MainActivity mainActivity;

    ExhibitBuilder(String json, Context c) {
        try {
//            Log.d("OK", "JSON: " + json);
//            categoryArr = new JSONArray(json);
            dataJsonObj = new JSONObject(json);
            exhArr = dataJsonObj.getJSONObject("exhibit");
            imgCdn = new ArrayList<String>();
            mediaCdn = new ArrayList<String>();
            context = c;
            mainActivity = new MainActivity();
//            mediaArr = dataJsonObj.getJSONArray("mediaCDN");
//            characteristics = dataJsonObj.getJSONArray("characteristics");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBodyShort() {
        try {
            return exhArr.getString("bodyShort");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getBody() {
        try {
            return exhArr.getString("body");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getId() {
        try {
            return exhArr.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getExhTitle(){
        try {
            return exhArr.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getDateStarted(){
        try {
            return exhArr.getString("dateStarted");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getDateFinish(){
        try {
            return exhArr.getString("dateFinish");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getMainImgPref() {
        try {
            JSONObject data = exhArr.getJSONObject("img");
//            Log.d("OK", "getMainImg(): " + data.getString("relativepath"));
            return data.getString("relativepath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public void getMediaCDN() {
        try {
            mediaArr = exhArr.getJSONArray("mediaCDN");
            for (int i = 0; i < mediaArr.length(); i++) {
                JSONObject data = mediaArr.getJSONObject(i);
                Log.d("OK", "getMediaCDN(): " + data.getString("link"));
                if (data.getString("link").contains("http://")) {
                    if (data.getString("type").equals("Зображення")) {
                        imgCdn.add(data.getString("link"));
                    } else {
                        mediaCdn.add(data.getString("link"));
                    }
                } else {
                    mediaCdn.add("null");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mediaCdn.add("null");
        }
    }

    public void getMediaDocs() {
        try {
            mediaArr = exhArr.getJSONArray("docs");
            for (int i = 0; i < mediaArr.length(); i++) {
                JSONObject data = mediaArr.getJSONObject(i);
//                Log.d("OK", "getMediaDocs(): " + data.getString("relativepath"));
//                mediaCdn.add(context.getString(R.string.BASE_API_URL) + data.getString("relativepath"));
                String description;
                if(data.getString("description").isEmpty()){
                    description = data.getString("originalname");
                }else {
                    description = data.getString("description");
                }
                mediaCdn.add("<a href=\"" + context.getString(R.string.BASE_API_URL) + data.getString("relativepath") + "\">" + description + "</a>");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mediaCdn.add("null");
        }
    }

    public void getMediaImg() {
        try {
            mediaArr = exhArr.getJSONArray("images");
            for (int i = 0; i < mediaArr.length(); i++) {
                JSONObject data = mediaArr.getJSONObject(i);
                Log.d("OK", "void getMediaImg: " + data.getString("relativepath"));
//                Log.d("OK", "getDisplayMetrics: "+context.getResources().getDisplayMetrics().widthPixels);
//                Log.d("OK", "getDisplayMetrics: "+context.getResources().getDisplayMetrics().heightPixels);
//                Log.d("OK", "checkDisplResolution: " + mainActivity.checkDisplResolution(context));
                imgCdn.add(context.getString(R.string.BASE_API_URL) + data.getString("relativepath").concat(mainActivity.checkDisplResolution(context)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            imgCdn.add("null");
        }
    }

    public void getMainImg() {
        try {
            JSONObject data = exhArr.getJSONObject("img");
//            Log.d("OK", "getMainImg(): " + data.getString("relativepath"));
            imgCdn.add(context.getString(R.string.BASE_API_URL) + data.getString("relativepath").concat(mainActivity.checkDisplResolution(context)));
        } catch (JSONException e) {
            e.printStackTrace();
            imgCdn.add("null");
        }
    }

    public String getCharacteristics() {
        String name, value;
        StringBuilder builder = new StringBuilder();
        try {
            characteristics = exhArr.getJSONArray("characteristics");
            for (int i = 0; i < characteristics.length(); i++) {
                JSONObject data = characteristics.getJSONObject(i);
                name = data.getString("name");
                value = data.getString("value");
                builder.append(name)
                        .append(": ")
                        .append(value)
                        .append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }
        return builder.toString();
    }
}
