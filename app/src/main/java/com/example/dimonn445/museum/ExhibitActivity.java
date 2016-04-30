package com.example.dimonn445.museum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExhibitActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView exhibitname, exhibitdescription, exhibiturl, exhibitCharacteristics, readMore;
    private SliderLayout mSlider;
    private String crop_string = "", bodyShort = "", art_title = "", body = "", exhId, dateStarted, dateFinish, mainImg;
    private String ExhId, ExhName, catName;
    final String APP_PREFERENCES = "EXHIBIT";
    final String EXHIBIT_PREFERENCES = "SAVED_EXHIBITS";
    private int width, height;
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private Boolean chek;
    private MyTask mt;
    private CheckBox checkFavourite;

//https://github.com/daimajia/AndroidImageSlider
//http://square.github.io/picasso/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit);
        width = this.getResources().getDisplayMetrics().widthPixels;
        height = this.getResources().getDisplayMetrics().heightPixels;
        getStatus();

        if (getIntent().getBooleanExtra("Check", false)) {
            ExhId = getIntent().getStringExtra("ExhId");
            ExhName = getIntent().getStringExtra("ExhName");
            catName = getIntent().getStringExtra("CatName");
//            Log.d("OK", "CATNAME: " + catName);
            chek = false;
            setTitle(ExhName);
        } else {
            catName = getIntent().getStringExtra("CatName");
            setTitle(ExhName);
            chek = true;
        }

        /*Log.d("OK", "ExhId: " + ExhId);
        Log.d("OK", "ExhName: " + ExhName);*/

        FindViev();

        FillData();
        checkExhPref();

        //--------------------------------Navigation Drawer start-------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
                Log.d("ok", "fab click");
                Intent intent = new Intent(ExhibitActivity.this, ExhibitsListActivity.class);
                intent.putExtra("Check", false);
                intent.putExtra("CatName", catName);
                startActivity(intent);
                finish();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //--------------------------------Navigation Drawer end-------------------------------------

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExhibitActivity.this, DescriptionActivity.class);
                intent.putExtra("artdescr", body);
                intent.putExtra("ExhName", ExhName);
                intent.putExtra("ExhId", ExhId);
                startActivity(intent);
                finish();
            }
        });

        exhibitdescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExhibitActivity.this, DescriptionActivity.class);
                intent.putExtra("artdescr", body);
                intent.putExtra("ExhName", ExhName);
                intent.putExtra("ExhId", ExhId);
                startActivity(intent);
                finish();
            }
        });

        checkFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Log.d("OK", "isChecked: " + isChecked);
                if (isChecked) {
                    SaveExhPref();
                    checkFavourite.setText(getString(R.string.fav_ch));
                } else {
                    DeleteExhref();
                    checkFavourite.setText(getString(R.string.add_to_fav));

                }
            }
        });


    }

    private void checkExhPref() {
        Prefs prefs = new Prefs(ExhibitActivity.this);
        String savedPref = "", json = "";
        if (prefs.containsPref(EXHIBIT_PREFERENCES)) {
            savedPref = prefs.getExhPref();
            if (savedPref.isEmpty()) {
                Log.d("OK", "emptyPref");
            } else {
                json = savedPref;
                try {
                    JSONObject dataJsonObj = new JSONObject(json);
                    JSONArray exhArr = dataJsonObj.getJSONArray("exhibits");
                    Log.d("OK", "exhArr.toString(): " + exhArr.toString());
                    for (int i = 0; i < exhArr.length(); i++) {
                        JSONObject data = exhArr.getJSONObject(i);
                        Log.d("OK", "_id: " + data.getString("_id") + " exhId: " + exhId);
                        if (data.getString("_id").equals(exhId)) {
                            Log.d("OK", "TRUE");
                            checkFavourite.setChecked(true);
                            checkFavourite.setText(getString(R.string.fav_ch));
                            return;
                        } else {
                            Log.d("OK", "FALSE");
                            checkFavourite.setChecked(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void DeleteExhref() {
        Prefs prefs = new Prefs(ExhibitActivity.this);
        String savedPref = "", json = "";
        if (prefs.containsPref(EXHIBIT_PREFERENCES)) {
            savedPref = prefs.getExhPref();
            if (savedPref.isEmpty()) {
                Log.d("OK", "emptyPref");
                return;
            } else {
                json = savedPref;
                try {
                    JSONObject dataJsonObj = new JSONObject(json);
                    JSONArray exhArr = dataJsonObj.getJSONArray("exhibits");
                    Log.d("OK", "BEFORE REMOVE: " + json);
                    JSONArray arr = new JSONArray();
                    for (int i = 0; i < exhArr.length(); i++) {
                        JSONObject data = exhArr.getJSONObject(i);
                        if (data.getString("_id").equals(exhId)) {
                            Log.d("OK", "REMOVED i=" + i);
                        } else {
                            arr.put(exhArr.getJSONObject(i));
                        }
                    }
                    json = "{\"count\":" + arr.length() + ",\"exhibits\":" + arr.toString() + "}";
                    prefs.setExhPref(json);
                    Log.d("OK", "REMOVE: " + prefs.getExhPref());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void SaveExhPref() {
        Prefs prefs = new Prefs(ExhibitActivity.this);
        String savedPref = "", json = "";
        if (prefs.containsPref(EXHIBIT_PREFERENCES)) {
            savedPref = prefs.getExhPref();
            Log.d("OK", "saved: " + savedPref);
            if (savedPref.isEmpty()) {
                json = "{\"count\":1,\"exhibits\":[{\"_id\":\"" + exhId + "\",\"img\":{\"relativepath\":\"" + mainImg + "\",\"originalname\":\".jpg\"},\"title\":\"" + art_title + "\",\"dateStarted\":" + dateStarted + ",\"dateFinish\":" + dateFinish + ",\"bodyShort\":\"" + bodyShort + "\"}]}";
            } else {
                try {
                    json = savedPref;
                    boolean ch = false;
                    JSONObject dataJsonObj = new JSONObject(json);
                    JSONArray exhArr = dataJsonObj.getJSONArray("exhibits");
                    for (int i = 0; i < exhArr.length(); i++) {
                        JSONObject data = exhArr.getJSONObject(i);
                        if (data.getString("_id").equals(exhId)) {
                            ch = true;
                        }
                    }
                    if (ch) {
                        return;
                    } else {
                        json = savedPref.substring(0, savedPref.length() - 2);
                        if(dataJsonObj.getString("count").equals("0")){
                            json += "{\"_id\":\"" + exhId + "\",\"img\":{\"relativepath\":\"" + mainImg + "\",\"originalname\":\".jpg\"},\"title\":\"" + art_title + "\",\"dateStarted\":" + dateStarted + ",\"dateFinish\":" + dateFinish + ",\"bodyShort\":\"" + bodyShort + "\"}]}";
                        }else {
                            json += ",{\"_id\":\"" + exhId + "\",\"img\":{\"relativepath\":\"" + mainImg + "\",\"originalname\":\".jpg\"},\"title\":\"" + art_title + "\",\"dateStarted\":" + dateStarted + ",\"dateFinish\":" + dateFinish + ",\"bodyShort\":\"" + bodyShort + "\"}]}";
                        }
                        dataJsonObj = new JSONObject(json);
                        exhArr = dataJsonObj.getJSONArray("exhibits");
                        String len = "";
                        len += exhArr.length();
                        char c = len.charAt(0);
                        json = replaceCharAt(json, 9, c);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            json = "{\"count\":1,\"exhibits\":[{\"_id\":\"" + exhId + "\",\"img\":{\"relativepath\":\"" + mainImg + "\",\"originalname\":\".jpg\"},\"title\":\"" + art_title + "\",\"dateStarted\":" + dateStarted + ",\"dateFinish\":" + dateFinish + ",\"bodyShort\":\"" + bodyShort + "\"}]}";
        }
        prefs.setExhPref(json);
        Log.d("OK", "SaveExhPref: " + json);
    }

    private static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    private void FindViev() {
        exhibitname = (TextView) findViewById(R.id.exhibitName);
        exhibitdescription = (TextView) findViewById(R.id.exhibitDescription);
        exhibitCharacteristics = (TextView) findViewById(R.id.exhibitCharacteristics);
        exhibiturl = (TextView) findViewById(R.id.exhibitUrls);
        readMore = (TextView) findViewById(R.id.exhibitReadMore);
        mSlider = (SliderLayout) findViewById(R.id.slider);
        checkFavourite = (CheckBox) findViewById(R.id.checkBoxFav);
        ViewGroup.LayoutParams params = mSlider.getLayoutParams();
        if (height <= 800)
            params.height = 200;
//        Log.d("OK", "height: " + params.height);
        mSlider.setLayoutParams(params);
    }

    private void FillData() {
        if (isNetworkAvailable()) {
            mt = new MyTask();
            mt.execute();

            String exhibitJson = "";
//        ApiClient client = new ApiClient(getString(R.string.BASE_API_URL), this);
//        exhibitJson = client.getJsonArray(getString(R.string.api_exhibit) + ExhId);
//        Log.d("OK","JSONNNN: "+exhibitJson);
            ExhibitBuilder exh;
//        exh = new ExhibitBuilder(exhibitJson, this);

            if (!chek) {
                try {
                    exhibitJson = mt.get();/*client.getJsonArray(getString(R.string.api_exhibit) + ExhId);*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                exh = new ExhibitBuilder(exhibitJson, this);
                saveStatus(exhibitJson);
//            Log.d("OK","CHEK: "+chek);
            } else {
                exhibitJson = loadStatus();
                exh = new ExhibitBuilder(exhibitJson, this);
//            Log.d("OK","CHEK: "+chek);
            }

        /*Log.d("OK", "BodyShort: " + exh.getBodyShort());
        Log.d("OK", "Body: " + exh.getBody());
        Log.d("OK", "Characteristics: " + exh.getCharacteristics());*/

            //crop_string="";
            art_title = exh.getExhTitle();
//        art_title = ExhName;
            bodyShort = exh.getBodyShort();
            dateStarted = exh.getDateStarted();
            dateFinish = exh.getDateFinish();
            mainImg = exh.getMainImgPref();
//        Log.d("received",""+bodyShort);
            if (bodyShort.length() > 80) {
                crop_string = bodyShort.substring(0, bodyShort.length()).trim()/* + "..."*/;
            } else {
                crop_string = bodyShort;
            }
            exhId = exh.getId();
            body = exh.getBody();
            if(body.isEmpty()||body.equals("null")){
                readMore.setVisibility(View.INVISIBLE);
            }
//        readMore.setText("Читати далі...");
            exhibitname.setText(ExhName + " " + getString(R.string.brief_description));
            exhibitdescription.setText(crop_string);
            String charackeristics = exh.getCharacteristics();
            if (charackeristics.isEmpty()) {
                exhibitCharacteristics.setText("");
            } else {
                exhibitCharacteristics.setText("\n" + getString(R.string.characteristics) + "\n" + exh.getCharacteristics());
            }
            exh.getMediaCDN();
            exh.getMediaImg();
            exh.getMediaDocs();
        /*for (int i = 0; i < exh.mediaCdn.size(); i++) {
            Log.d("OK", "mediaCdn: " + exh.mediaCdn.get(i));
        }
        for (int i = 0; i < exh.imgCdn.size(); i++) {
            Log.d("OK", "IMGCDN: " + exh.imgCdn.get(i));
        }
        */
            if (exh.imgCdn.isEmpty()) {
                exh.getMainImg();
            }
            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.Sifting)).append("<br>");
            for (int i = 0; i < exh.mediaCdn.size(); i++) {
//            Log.d("OK", "mediaCdn: " + exh.mediaCdn.get(i));
                if (exh.mediaCdn.get(i).contains("null")) {
                Log.d("OK", "mediaCdn contains null");
                    builder.append("");
                } else {
                    builder.append(exh.mediaCdn.get(i)).append("<br><br>");
                }
            }

            if (builder.length() <= 13) {
                builder.delete(0, builder.length());
            } else {
                builder.delete(builder.length() - 5, builder.length());
            }
//        Log.d("OK", "LEN: " + builder.length());
            exhibiturl.setText(Html.fromHtml(builder.toString()));
            exhibiturl.setMovementMethod(LinkMovementMethod.getInstance());

//--------------------------slider--------------------

            HashMap<String, String> url_maps = new HashMap<String, String>();
            for (int i = 0; i < exh.imgCdn.size(); i++) {
//            Log.d("OK", "IMGCDN: " + exh.imgCdn.get(i));
                if (exh.imgCdn.get(i).contains("null")) {
                    Log.d("OK", "IMGCDN: null");
                } else {
                    url_maps.put(getString(R.string.image) + i, exh.imgCdn.get(i));
                }
            }
//        url_maps.put("img","http://17047.s.time4vps.eu:3021/uploads/image/7/7/77757a49145dea723271fcf336fff670_md.jpg");
        /*url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");*/

        /*HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("rr1", R.drawable.dsc_0344);
        file_maps.put("rr2", R.drawable.f194dd6e0467d9c141288eb1cc07f520_md);
        file_maps.put("rr3", R.drawable.g7757a49145dea723271fcf336fff670_md);

        for (String name : file_maps.keySet()) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            Log.d("OK", "file_maps: " + file_maps.get(name));
            sliderView
//                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle()
                    .putString("extra", name);
            mSlider.addSlider(sliderView);
        }*/

            for (String name : url_maps.keySet()) {
                final DefaultSliderView sliderView = new DefaultSliderView(this);
                Log.d("OK", "url_maps: " + url_maps.get(name));
                sliderView
//                    .description(name)
                        .image(url_maps.get(name))
                        .error(R.drawable.ic_report_problem_black_48dp)
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
//                                Toast.makeText(ExhibitActivity.this, slider.getUrl() + "",Toast.LENGTH_SHORT).show();
                                Log.d("OK", "URL: " + slider.getUrl());
                                Intent intent = new Intent(ExhibitActivity.this, ImageActivity.class);
                                intent.putExtra("URL", slider.getUrl());
                                startActivity(intent);
                            }
                        });
                //add your extra information
                sliderView.bundle(new Bundle());
                sliderView.getBundle()
                        .putString("extra", name);
                mSlider.addSlider(sliderView);
            }
            mSlider.setCurrentPosition(0);
            mSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mSlider.setCustomAnimation(new DescriptionAnimation());
            mSlider.setDuration(6000);

//--------------------------slider--------------------
        }else {
            Toast.makeText(ExhibitActivity.this, ExhibitActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
            exhibitname.setVisibility(View.INVISIBLE);
            exhibitdescription.setVisibility(View.INVISIBLE);
            exhibitCharacteristics.setVisibility(View.INVISIBLE);
            exhibiturl.setVisibility(View.INVISIBLE);
            readMore.setVisibility(View.INVISIBLE);
            mSlider.setVisibility(View.INVISIBLE);
            checkFavourite.setVisibility(View.INVISIBLE);
        }
    }

    private void saveStatus(String exhibits) {
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sPref.edit();
        editor.putString(APP_PREFERENCES, exhibits);
        editor.apply();
    }

    private String loadStatus() {
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String savedPref = "";
        if (sPref.contains(APP_PREFERENCES)) {
            savedPref = sPref.getString(APP_PREFERENCES, "");
            if (savedPref.isEmpty()) {
                chek = false;
            }
        } else {
            chek = false;
        }
//        Log.d("OK", "savedText: " + savedPref);
        return savedPref;
    }

    private void getStatus() {
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String savedPref = "";
        if (sPref.contains(APP_PREFERENCES)) {
            savedPref = sPref.getString(APP_PREFERENCES, "");
            chek = true;
            if (savedPref.isEmpty()) {
                chek = false;
            } else {
                chek = true;
            }
        } else {
            chek = false;
        }
    }

    //--------------------------------Navigation Drawer start-------------------------------------
//    private static long back_pressed;

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(ExhibitActivity.this, ExhibitsListActivity.class);
            intent.putExtra("Check", false);
            intent.putExtra("CatName", catName);
            startActivity(intent);
            finish();
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                ExhibitActivity.this);
        quitDialog.setTitle(getString(R.string.quit_dialog_are_you_sure));

        quitDialog.setPositiveButton(getString(R.string.quit_dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                Log.d("OK", "EXIT");
                ExhibitActivity.this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
                Intent intent = new Intent(ExhibitActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
//                System.exit(0);
            }
        });

        quitDialog.setNegativeButton(getString(R.string.quit_dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        quitDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            Intent intent = new Intent(ExhibitActivity.this, ExhibitsListActivity.class);
//            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(ExhibitActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_favourite) {
            Intent intent = new Intent(ExhibitActivity.this, ExhibitsListActivity.class);
            intent.putExtra("fav_exh", true);
            intent.putExtra("CatName", getString(R.string.fav));
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_all_exhibits) {
            Intent intent = new Intent(ExhibitActivity.this, ExhibitsListActivity.class);
            intent.putExtra("all_exh", true);
            intent.putExtra("CatName", getString(R.string.all_exhibits));
            startActivity(intent);
            finish();

//        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_exit) {
            Log.d("OK", "EXIT");
            openQuitDialog();
            /*Intent intent = new Intent(ExhibitActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();*/
//        }else if (id == R.id.nav_share) {

//        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //--------------------------------Navigation Drawer end-------------------------------------
    //--------------------------slider--------------------
    @Override
    protected void onStop() {
        mSlider.stopAutoCycle();
        super.onStop();
    }


    //--------------------------slider--------------------

    class MyTask extends AsyncTask<Void, Void, String> {
        //        public ProgressDialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... p) {
            ApiClient client = new ApiClient(getString(R.string.BASE_API_URL), ExhibitActivity.this);
            String getAllCat;
            getAllCat = client.getJsonArray(getString(R.string.api_exhibit) + ExhId);
            return getAllCat;
        }

        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Log.d("OK", ExhibitActivity.this.getString(R.string.connection_is_missing));
                Toast.makeText(ExhibitActivity.this, ExhibitActivity.this.getString(R.string.connection_is_missing), Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }

}
