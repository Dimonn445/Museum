package com.example.dimonn445.museum;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by godun on 29.03.2016.
 */
public class ExhibitsListBuilder {
    private JSONArray exhibitsArr;
    public ArrayList<String> exhibitId;
    JSONObject dataJsonObj = null;

    ExhibitsListBuilder(String json) {
        try {
            dataJsonObj = new JSONObject(json);
            exhibitsArr = dataJsonObj.getJSONArray("exhibits");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exhibitId = new ArrayList<String>();
    }

    public void getExhId() {
        try {
//            Log.d("categoryArr ", exhibitsArr.toString());
            for (int i = 0; i < exhibitsArr.length(); i++) {
                JSONObject data = exhibitsArr.getJSONObject(i);
//            Log.d("_id ", "ID: " + data.getString("_id"));
                exhibitId.add(data.getString("_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e1){
            e1.printStackTrace();
        }
    }

    public String nameById(String id) {
        try {
            for (int i = 0; i < exhibitsArr.length(); i++) {
                JSONObject data = exhibitsArr.getJSONObject(i);
                if (data.getString("_id").equals(id)) {
                    return data.getString("title");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }
        return "null";
    }

    public String dateById(String id) {
        try {
            String buff = "";
            for (int i = 0; i < exhibitsArr.length(); i++) {
                JSONObject data = exhibitsArr.getJSONObject(i);
                if (data.getString("_id").equals(id)) {
                    if (data.getString("dateStarted").equals("null") || data.getString("dateFinish").equals("null")) {
                        return "null";
                    } else {
                        buff = data.getString("dateStarted");
                        buff += " / ";
                        buff += data.getString("dateFinish");
                        return buff;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }
        return "null";
    }

    public String bodyShortById(String id) {
        try {
            for (int i = 0; i < exhibitsArr.length(); i++) {
                JSONObject data = exhibitsArr.getJSONObject(i);
                if (data.getString("_id").equals(id)) {
                    return data.getString("bodyShort");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }
        return "null";
    }

    public String imgById(String id) {
        try {
            String buff = "";
            for (int i = 0; i < exhibitsArr.length(); i++) {
                JSONObject data = exhibitsArr.getJSONObject(i);
                JSONObject imj = data.getJSONObject("img");
                if (data.getString("_id").equals(id)) {
//                Log.d("OK", "IMG: " + imj.getString("relativepath"));
                    return imj.getString("relativepath").concat("_sm.jpg");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }
        return "null";
    }

    public int getCount(){
        int count=0;
        try {
            count = Integer.parseInt(dataJsonObj.getString("count"));
            Log.d("OK","COUNT: "+count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }

}


