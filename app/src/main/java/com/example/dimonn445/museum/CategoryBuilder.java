package com.example.dimonn445.museum;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by godun on 28.03.2016.
 */
public class CategoryBuilder {
    private JSONArray categoryArr;
    public ArrayList<String> rootCategId;
    private Context context;
    private MainActivity mainActivity;

    CategoryBuilder(String json, Context c) throws JSONException {
        categoryArr = new JSONArray(json);
        rootCategId = new ArrayList<String>();
        context = c;
        mainActivity = new MainActivity();
    }

    // возвращает ид потомок
    public ArrayList<String> subcategoryNext(String id) {
        ArrayList<String> bbb = new ArrayList<String>();
        try {
            for (int i = 0; i < categoryArr.length(); i++) {
                JSONObject data = categoryArr.getJSONObject(i);
                if (data.getString("parent").equals(id)) {
                    bbb.add(data.getString("_id"));
                }
            }
            if (bbb.isEmpty()) {
                bbb.add("null");
                return bbb;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bbb;
    }

    // возвращает ид предка
    public ArrayList<String> subcategoryPrev(String id) {
        ArrayList<String> bbb = new ArrayList<String>();
        try {
            for (int i = 0; i < categoryArr.length(); i++) {
                JSONObject data = categoryArr.getJSONObject(i);
                if (data.getString("parent").equals(id)) {
                    bbb.add(data.getString("_id"));
                }
            }
            if (bbb.isEmpty()) {
                bbb.add("null");
//                Log.d("OK","NULL");
                return bbb;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bbb;
    }

    public String nameById(String id) {
        try {
            for (int i = 0; i < categoryArr.length(); i++) {
                JSONObject data = categoryArr.getJSONObject(i);
                if (data.getString("_id").equals(id)) {
                    return data.getString("name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String parentById(String id) {
        try {
            for (int i = 0; i < categoryArr.length(); i++) {
                JSONObject data = categoryArr.getJSONObject(i);
                if (data.getString("_id").equals(id)) {
                    return data.getString("parent");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String imgById(String id) {
        try {
            for (int i = 0; i < categoryArr.length(); i++) {
                JSONObject data = categoryArr.getJSONObject(i);
                JSONObject imj = data.getJSONObject("img");
                if (data.getString("_id").equals(id)) {
                    if(imj.getString("extname").equals(".png")){
                        return context.getString(R.string.BASE_API_URL)
                                .concat(imj.getString("path")).concat(imj.getString("extname"));
                    }else {
                        return context.getString(R.string.BASE_API_URL)
                                .concat(imj.getString("path"))/*.concat(mainActivity.checkDisplResolution(context));*/
                                .concat("_sm.jpg");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public void getCatId() {
        try {
//            Log.d("categoryArr ", categoryArr.toString());
            for (int i = 0; i < categoryArr.length(); i++) {
                JSONObject data = categoryArr.getJSONObject(i);
                if (data.getString("parent").equals("null")) {
                    rootCategId.add(data.getString("_id"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
