package com.kspu.dimonn445.museum;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CategoriesAdapter.customButtonListener, CategoriesAdapter.customTextListener {

    //    private Button test;
    private JSONArray categoryArr;
    private ArrayList<String> rootCategIdd = new ArrayList<String>();
    private ArrayList<String> SELECTEDcat = new ArrayList<String>();
//    private ArrayList<String> rootCategIddSave = new ArrayList<String>();
    private ArrayList<String> categoryBuffer = new ArrayList<String>();
    private ArrayList<String> prevCategoryBuffer;
    private ArrayList<String> prevCategID = new ArrayList<String>();
    private ArrayList<String> DDD = new ArrayList<String>();
    private CategoryBuilder buildCategory;
    //    private String BASE_API_URL = "http://uzkkce61853c.zz995.koding.io:3000";
    private ArrayList<Categories> categories = new ArrayList<Categories>();
    private CategoriesAdapter categoriesAdapter;
    private ListView lvMain;
    private Boolean chek, chek1;
    private SharedPreferences sPref;
    final String APP_PREFERENCES = "CATEGORIES";
    private SharedPreferences.Editor editor;
    private MyTask mt;
    private ImageButton btn;
    //    Display display;
    private int width = 0, height = 0;
    private View lvHeader;
    private String saveNav, prevCateIDs;
    private int POS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("Exit me", false)) {
//            Log.d("OK", "Exit me " + getIntent().getBooleanExtra("Exit me", false));
            MainActivity.this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
            finish();
        }
        chek1 = getIntent().getBooleanExtra("Check", false);

        FindViev();
        if(isNetworkAvailable()) {
            getStatus();
        }
//        chek= false;
        /*View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.test_btn:
                        Log.d("OK", "test_btn");
//                        setmatrixofbuttons();
                        //Intent intent = new Intent(MainActivity.this, test2Activity.class);
                        //startActivity(intent);
                        break;
                }
            }
        };
        test.setOnClickListener(onClickListener);*/
//--------------------------------Fill Content start-------------------------------------


        categoriesAdapter = new CategoriesAdapter(this, categories);
        categoriesAdapter.setCustomButtonListner(MainActivity.this);
        categoriesAdapter.setCustomTextListener(MainActivity.this);
        lvHeader = getLayoutInflater().inflate(R.layout.listview_header, null);
        lvMain.setAdapter(categoriesAdapter);

        if (isNetworkAvailable()) {
            fillData();
            lvMain.removeHeaderView(lvHeader);
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
//            lvMain.addHeaderView(lvHeader);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                lvMain.removeHeaderView(lvHeader);
                lvMain.addHeaderView(lvHeader);
            }
        }
        
        if (chek1) {
//            categories.clear();
//            saveNav = getIntent().getStringExtra("SaveNav");
            prevCateIDs = getIntent().getStringExtra("prevCategID");
//            Log.d("OK", "SaveNavMA " + saveNav);
            ArrayList<String> sss = new ArrayList<String>();
            /*String ss[] = saveNav.split(" ");
            for (int i = 0; i < ss.length; i++) {
                sss.add(ss[i]);
            }
            Log.d("OK", "rootCategIddSave_SSS " + sss.toString());
            rootCategIddSave = sss;
            DDD = sss;
            Log.d("OK", "DDD " + DDD.toString());
            sss.clear();
            */
            String ssss[] = prevCateIDs.split(" ");
            for (int i = 0; i < ssss.length; i++) {
                sss.add(ssss[i]);
            }
            Log.d("OK", "prevCategID_SSS " + sss.toString());
            prevCategID = sss;
//            POS = getIntent().getIntExtra("POS",0);
            /*String POSS = getIntent().getStringExtra("POSS");
            Log.d("OK", "POSS: " + POSS);*/
//            chek= false;
            fillData();
//            categories.clear();
            if (prevCategID.size() != 1 && prevCategID.size() != 0) {
                prevCall(/*POSS*/prevCategID.get(prevCategID.size() - 1));
            }else{
                prevCall(/*POSS*/prevCategID.get(prevCategID.size() - 1));
            }
//            prevCategID.remove(prevCategID.size() - 1);
//            prevCall(POSS);
            chek1 = false;
        }

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Log.d("OK", "lvMain.setOnItemClickListener click pos: " + position);
                try {
//                    nextCall(position);
                    newCall(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                startActivity(intent);*/
            }
        });

        lvHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("OK","HEADER CLICK");
                reload();
            }
        });

//--------------------------------Fill Content end-------------------------------------

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_LONG).show();
                /*Snackbar.make(view, getString(R.string.update_category_list), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();*/
                /*try {
                    if (isNetworkAvailable() || rootCategIdd.isEmpty()) {
                        lvMain.removeHeaderView(lvHeader);
                        if (buildCategory.parentById(rootCategIdd.get(0)).equals("null")) {
                            openQuitDialog();
                        } else {
                            String getAllCat;
                            getAllCat = loadStatus();
                            try {
                                buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            buildCategory.getCatId();
                            try {
                                if (rootCategIdd.isEmpty()) {
                                    rootCategIdd = buildCategory.rootCategId;
                                } else {
                                    rootCategIdd.clear();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            rootCategIdd = buildCategory.rootCategId;
                            categories.clear();
                            categoriesAdapter.notifyDataSetChanged();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                lvMain.deferNotifyDataSetChanged();
                            }
                            firstCall();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_loading_items), Toast.LENGTH_SHORT).show();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                        lvMain.addHeaderView(lvHeader);
                    }
                }*/
                try{
                Log.d("OK", "prevCategID.toString(): " + prevCategID.toString());
                Log.d("OK", "prevCategID.size(): " + prevCategID.size());
                if (prevCategID.size() != 1 && prevCategID.size() != 0) {
                    prevCall(prevCategID.get(prevCategID.size() - 2));
                    prevCategID.remove(prevCategID.size() - 1);
                } else {
//                    prevCall(prevCategID.get(prevCategID.size() - 1));
                    String getAllCat;
                    getAllCat = loadStatus();
                    try {
                        buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    buildCategory.getCatId();
                    try {
                        if (rootCategIdd.isEmpty()) {
                            rootCategIdd = buildCategory.rootCategId;
                        } else {
                            rootCategIdd.clear();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    rootCategIdd = buildCategory.rootCategId;
                    categories.clear();
                    categoriesAdapter.notifyDataSetChanged();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        lvMain.deferNotifyDataSetChanged();
                    }
                    firstCall();
                    prevCategID.clear();
                }
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                e.printStackTrace();
//            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_loading_items), Toast.LENGTH_SHORT).show();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    lvMain.removeHeaderView(lvHeader);
                    lvMain.addHeaderView(lvHeader);
                }
            }

            }
        });

//--------------------------------Navigation Drawer start-------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onButtonClickListner(int position) {
        /*Toast.makeText(MainActivity.this, "Button click " + position,
                Toast.LENGTH_SHORT).show();*/
//        newCall(position);
        try {
            if (isNetworkAvailable()) {
                nextCall(position);
                lvMain.removeHeaderView(lvHeader);
            } else {
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
                lvMain.removeHeaderView(lvHeader);
                lvMain.addHeaderView(lvHeader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*ImageButton btn = (ImageButton) findViewById(R.id.childImageButton);
        ArrayList<String> iii = new ArrayList<String>();
//        Log.d("OK", "categoryBuffer: " + categoryBuffer.toString());
        for (int i = 0; i < categoryBuffer.size(); i++) {
            iii.add(buildCategory.subcategoryNext(categoryBuffer.get(i)).toString());
        }
//        Log.d("OK", "III: " + iii.toString());
        for (int i=0; i<iii.size();i++){
            Log.d("OK","iii.get("+i+"): "+ iii.get(i));
            if (iii.get(i).equals("[null]")) {
                btn.setVisibility(View.INVISIBLE);
                categoriesAdapter.notifyDataSetChanged();
            }
        }*/
    }

    @Override
    public void onTextListener(int position) {
        /*Toast.makeText(MainActivity.this, "Button click Text" + position,
                Toast.LENGTH_SHORT).show();*/
        /*try {
            nextCall(position);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if (isNetworkAvailable()) {
            newCall(position);
        } else {
//            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
            lvMain.removeHeaderView(lvHeader);
            lvMain.addHeaderView(lvHeader);
        }

    }
//    --------------------------------Navigation Drawer end-------------------------------------

    private void reload() {
        if (isNetworkAvailable()) {
            lvMain.removeHeaderView(lvHeader);
            String getAllCat = "";
            mt = new MyTask();
            mt.execute();
            try {
                getAllCat = mt.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            saveStatus(getAllCat);
            try {
                buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            buildCategory.getCatId();
            try {
                if (rootCategIdd.isEmpty()) {
                    rootCategIdd = buildCategory.rootCategId;
                } else {
                    rootCategIdd.clear();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            rootCategIdd = buildCategory.rootCategId;
            categories.clear();
            categoriesAdapter.notifyDataSetChanged();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                lvMain.deferNotifyDataSetChanged();
            }
            firstCall();
        } else {
//            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
            lvMain.removeHeaderView(lvHeader);
            lvMain.addHeaderView(lvHeader);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    void fillData() {
        try {
            String getAllCat = "";
            if (!chek) {
                mt = new MyTask();
                mt.execute();
                try {
                    getAllCat = mt.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
                saveStatus(getAllCat);
            } else {
                getAllCat = loadStatus();
                buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
            }
            buildCategory.getCatId();
            /*if (chek1) {
                rootCategIdd = rootCategIddSave;
                chek1 = false;
            } else {*/
            rootCategIdd = buildCategory.rootCategId;
//            }
            Log.d("OK", "rootCategIdd FIRSTCALL: " + rootCategIdd);
            firstCall();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void FindViev() {
//        test = (Button) findViewById(R.id.test_btn);
        lvMain = (ListView) findViewById(R.id.lwcategories);
//        btn = (ImageButton) findViewById(R.id.childImageButton);
    }

    private void firstCall() {
        if (isNetworkAvailable()) {
            try {
                lvMain.removeHeaderView(lvHeader);
//            Log.d("OK", "firstCall()");
//        categories.clear();
//        Log.d("OK", "size(): " + rootCategIdd.size());
                for (int i = 0; i < rootCategIdd.size(); i++) {
//            buildCategory.nameById(rootCategIdd.get(i));
//            Log.d("nameById", buildCategory.nameById(rootCategIdd.get(i)));
                    Log.d("OK", "IMGBYID: " + buildCategory.imgById(rootCategIdd.get(i)));
                /*StringBuilder builder = new StringBuilder();
                builder.append(buildCategory.imgById(rootCategIdd.get(i)));
                builder.delete(builder.length() - 7, builder.length());
                builder.append(".png");*/
//                String url = buildCategory.imgById(rootCategIdd.get(i));
//                url.length();
                    /*if(chek1){
                        categories.add(new Categories(buildCategory.nameById(rootCategIddSave.get(i)), *//*builder.toString()*//*
                                buildCategory.imgById(rootCategIddSave.get(i)), false));
                        chek1= false;
                    }else{*/
                    categories.add(new Categories(buildCategory.nameById(rootCategIdd.get(i)), /*builder.toString()*/
                            buildCategory.imgById(rootCategIdd.get(i)), false));
//                    }
                    categoriesAdapter.notifyDataSetChanged();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        lvMain.deferNotifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
            lvMain.removeHeaderView(lvHeader);
            lvMain.addHeaderView(lvHeader);
        }
    }

    private void newCall(int pos) {
        categoryBuffer = buildCategory.subcategoryNext(rootCategIdd.get(pos));
        String selectedCat = rootCategIdd.get(pos);
//        Log.d("OK", "SELECTEDID: " + selectedCat);
//        rootCategIddSave = rootCategIdd;
//        Log.d("OK", "rootCategIddSaveNewCall " + rootCategIddSave.toString());
//        String CategIddSave = rootCategIddSave.toString();
        String prevCategIDs = prevCategID.toString();
//        SELECTEDcat.add(selectedCat);
        prevCategID.add(selectedCat);
        Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
        intent.putExtra("CatId", selectedCat);
        intent.putExtra("CatName", "" + buildCategory.nameById(selectedCat));
        intent.putExtra("Check", true);
//        intent.putExtra("SaveNav", CategIddSave/*rootCategIddSave.toString()*/);
//        intent.putExtra("POS", pos);
        intent.putExtra("prevCategID", prevCategIDs);
//        intent.putExtra("Check", true);
//        rootCategIdd.clear();
        prevCategID.clear();
//        rootCategIddSave.clear();
        startActivity(intent);
        finish();
    }

    private void nextCall(int pos) {
        ArrayList<String> arrayListNotifyAdapterChangeVisible = new ArrayList<String>();
//        try {
//            Log.d("OK", "nextCAll()");
        Log.d("OK", "rootCategIdd " + rootCategIdd.toString());
        categoryBuffer = buildCategory.subcategoryNext(rootCategIdd.get(pos));
        Log.d("OK", "categoryBuffer: " + categoryBuffer.toString());
        String selectedCat = rootCategIdd.get(pos);
        Log.d("OK", "SELECTEDID: " + selectedCat);
//        SELECTEDcat.add(selectedCat);
        prevCategID.add(selectedCat);
            /*for(int i=0;i<prevCategID.size();i++){
                Log.d("OK","prevCategID: "+prevCategID.get(i));
            }*/
        setTitle(buildCategory.nameById(selectedCat));
//        rootCategIddSave = rootCategIdd;
//        Log.d("OK", "rootCategIddSaveNextCall " + rootCategIddSave.toString());
        rootCategIdd.clear();
        categories.clear();
        for (int i = 0; i < categoryBuffer.size(); i++) {
            arrayListNotifyAdapterChangeVisible.add(buildCategory.subcategoryNext(categoryBuffer.get(i)).toString());
//            Log.d("OK", "categoryBuffer.get(i): " + categoryBuffer.get(i));
//            Log.d("OK", "arrayListNotifyAdapterChangeVisible.get(" + i + "): " + arrayListNotifyAdapterChangeVisible.get(i));
            if (categoryBuffer.get(i).equals("null")) {
//                Log.d("OK", "NULL");
//                    Log.d("OK", "selectedCatId: " + selectedCat);
//                    Log.d("OK", "selectedCatName: " + buildCategory.nameById(selectedCat));
                String prevCategIDs = prevCategID.toString();
//                String CategIddSave = rootCategIddSave.toString();
                Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                intent.putExtra("CatId", selectedCat);
                intent.putExtra("CatName", "" + buildCategory.nameById(selectedCat));
                intent.putExtra("Check", true);
//                intent.putExtra("SaveNav", CategIddSave);
                intent.putExtra("prevCategID", prevCategIDs);
//                intent.putExtra("POS", pos);
//                Log.d("OK", "SaveNav1 " + rootCategIddSave.toString());
                prevCategID.clear();
//                rootCategIddSave.clear();
                startActivity(intent);
                finish();
            } else {
                if (arrayListNotifyAdapterChangeVisible.get(i).equals("[null]")) {
                    categories.add(new Categories(buildCategory.nameById(categoryBuffer.get(i)),
                            buildCategory.imgById(categoryBuffer.get(i)), true));
                } else {
                    categories.add(new Categories(buildCategory.nameById(categoryBuffer.get(i)),
                            buildCategory.imgById(categoryBuffer.get(i)), false));
                }
//                    Log.d("OK", "SELECTEDID: " + rootCategIdd.get(i));
                categoriesAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    lvMain.deferNotifyDataSetChanged();
                }
                rootCategIdd.add(categoryBuffer.get(i));
//                Log.d("OK","rootCategIdd"+rootCategIdd.toString());
            }
        }
        Log.d("OK", "rootCategIddaferADD" + rootCategIdd.toString());
//        rootCategIddPrev = rootCategIdd;
        /*} catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void prevCall(String parent) {
        ArrayList<String> arrayListNotifyAdapterChangeVisible = new ArrayList<String>();
        prevCategoryBuffer = buildCategory.subcategoryPrev(parent);
        Log.d("OK", "prevCategoryBuffer: " + prevCategoryBuffer.toString());
//        categoryBuffer = prevCategoryBuffer;
//        Log.d("OK", "rootCategIddSaveQWQW: " + rootCategIddSave.toString());
//        if(prevCategoryBuffer.toString().equals("null")){
//            prevCategoryBuffer = rootCategIddSave;
//        }
        /*if (chek1) {
//            prevCategoryBuffer = rootCategIddSave;
//            prevCategoryBuffer = DDD;
//            Log.d("OK", "prevCategoryBuffer with chek1: " + prevCategoryBuffer.toString());
            chek1 = false;
        }*/
        rootCategIdd = prevCategoryBuffer;
//        rootCategIdd = rootCategIddPrev;
//        Log.d("OK", "catBuff2: " + categoryBuffer.toString());
        setTitle(buildCategory.nameById(parent));
//        rootCategIdd.clear();
        categories.clear();
        Log.d("OK", "parent: " + parent);
        if (parent.equals("null")) {
            Log.d("OK", "parent.equals(\"null\"): " + parent);
        } else {
            for (int i = 0; i < prevCategoryBuffer.size(); i++) {
                arrayListNotifyAdapterChangeVisible.add(buildCategory.subcategoryPrev(prevCategoryBuffer.get(i)).toString());
                /*categories.add(new Categories(buildCategory.nameById(prevCategoryBuffer.get(i)),
                        buildCategory.imgById(prevCategoryBuffer.get(i)), false));*/
                if (arrayListNotifyAdapterChangeVisible.get(i).equals("[null]")) {
                    categories.add(new Categories(buildCategory.nameById(prevCategoryBuffer.get(i)),
                            buildCategory.imgById(prevCategoryBuffer.get(i)), true));
                } else {
                    categories.add(new Categories(buildCategory.nameById(prevCategoryBuffer.get(i)),
                            buildCategory.imgById(prevCategoryBuffer.get(i)), false));
                }
//                    Log.d("OK", "SELECTEDID: " + rootCategIdd.get(i));
                categoriesAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    lvMain.deferNotifyDataSetChanged();
                }
//                rootCategIdd.add(prevCategoryBuffer.get(i));
            }
        }
    }

    public String checkDisplResolution(Context c) {
//        Display display;
//        display = getWindowManager().getDefaultDisplay();
        width = c.getResources().getDisplayMetrics().widthPixels;
        height = c.getResources().getDisplayMetrics().heightPixels;
        /*width = display.getWidth();
        height = display.getHeight();*/
//        Log.d("Resolution", "resolution: " + width + " x " + height);
        if (width <= 480 && height <= 800) {
            return "_md.jpg";
        } else {
            if (width <= 800 && height <= 1280) {
                return "_lg.jpg";
            } else {
                return "_lg.jpg";
            }
        }
//        return "";
    }

    //--------------------------------------SharedPreferences start----------------------------------------
    private void saveStatus(String categ) {
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sPref.edit();
        editor.putString(APP_PREFERENCES, categ);
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
        Log.d("OK", "savedText: " + savedPref);
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

    //--------------------------------------SharedPreferences end----------------------------------------
    //--------------------------------Navigation Drawer start-------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {

                /*if (buildCategory.parentById(rootCategIdd.get(0)).equals("null")) {
                    openQuitDialog();
                } else {
                    String getAllCat;
                    getAllCat = loadStatus();
                    try {
                        buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    buildCategory.getCatId();
                    try {
                        if (rootCategIdd.isEmpty()) {
                            rootCategIdd = buildCategory.rootCategId;
                        } else {
                            rootCategIdd.clear();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    rootCategIdd = buildCategory.rootCategId;
//                    Log.d("OK", "rootCategIdd.toString(): " + rootCategIdd.toString());
                    categories.clear();
                    categoriesAdapter.notifyDataSetChanged();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        lvMain.deferNotifyDataSetChanged();
                    }
                    firstCall();
                }*/
                Log.d("OK", "prevCategID.toString(): " + prevCategID.toString());
                Log.d("OK", "prevCategID.size(): " + prevCategID.size());
                if (prevCategID.size() != 1 && prevCategID.size() != 0) {
                    prevCall(prevCategID.get(prevCategID.size() - 2));
                    prevCategID.remove(prevCategID.size() - 1);
                } else {
//                    prevCall(prevCategID.get(prevCategID.size() - 1));
                    String getAllCat;
                    getAllCat = loadStatus();
                    try {
                        buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    buildCategory.getCatId();
                    try {
                        if (rootCategIdd.isEmpty()) {
                            rootCategIdd = buildCategory.rootCategId;
                        } else {
                            rootCategIdd.clear();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    rootCategIdd = buildCategory.rootCategId;
                    categories.clear();
                    categoriesAdapter.notifyDataSetChanged();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        lvMain.deferNotifyDataSetChanged();
                    }
                    firstCall();
                    prevCategID.clear();
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
//            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_loading_items), Toast.LENGTH_SHORT).show();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                lvMain.removeHeaderView(lvHeader);
                lvMain.addHeaderView(lvHeader);
            }
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MainActivity.this);
        quitDialog.setTitle(getString(R.string.quit_dialog_are_you_sure));
        quitDialog.setPositiveButton(getString(R.string.quit_dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Log.d("OK", "EXIT");
                MainActivity.this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
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
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

//        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchView search = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            search = (SearchView) menu.findItem(R.id.search).getActionView();
        } else {
            MenuItem searchItem = menu.findItem(R.id.search);
            search = (SearchView) MenuItemCompat.getActionView(searchItem);
        }

        search.setQueryHint(getString(R.string.search));
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // User pressed the search button
                Log.d("OK", "TextSubmit");
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                    intent.putExtra("search_data", true);
                    intent.putExtra("CatName", getString(R.string.search));
                    try {
                        intent.putExtra("search_query", URLEncoder.encode(query, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // User changed the text
                Log.d("OK", "TextChange");
                Log.d("OK", "query: " + query);
                return true;
            }

        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, MainActivity.class)));
        search.setIconifiedByDefault(false);

        return true;
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            Log.d("OK","SEARCH");
//            setmatrixofbuttons();

//            Toast.makeText(this, "ЖМЯК", Toast.LENGTH_LONG).show();

            *//*categories.clear();

            for (int i = 1; i <= 20; i++) {

                categories.add(new Categories("Category " + i,
                        android.R.drawable.ic_menu_report_image));
                categoriesAdapter.notifyDataSetChanged();
                lvMain.deferNotifyDataSetChanged();

            }*//*

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (isNetworkAvailable()) {
            if (id == R.id.nav_home) {
                // Handle the camera action
                try {
                    if (buildCategory.parentById(rootCategIdd.get(0)).equals("null")) {
                        Toast.makeText(this, getString(R.string.you_are_at_home), Toast.LENGTH_LONG).show();
//                Log.d("OK","\"");
                    } else {
                        String getAllCat;
                        getAllCat = loadStatus();
                        try {
                            buildCategory = new CategoryBuilder(getAllCat, MainActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        buildCategory.getCatId();
                        try {
                            if (rootCategIdd.isEmpty()) {
                                rootCategIdd = buildCategory.rootCategId;
                            } else {
                                rootCategIdd.clear();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        rootCategIdd = buildCategory.rootCategId;
                        categories.clear();
                        categoriesAdapter.notifyDataSetChanged();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            lvMain.deferNotifyDataSetChanged();
                        }
                        firstCall();
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else if (id == R.id.nav_favourite) {
                Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                intent.putExtra("fav_exh", true);
                intent.putExtra("CatName", getString(R.string.fav));
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_all_exhibits) {
                Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                intent.putExtra("all_exh", true);
                intent.putExtra("CatName", getString(R.string.all_exhibits));
                startActivity(intent);
                finish();

            } else if (id == R.id.nav_reload) {
                reload();
            } /*else if (id == R.id.nav_exit) {
                openQuitDialog();*/
//            Log.d("OK", "EXIT");
//            this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
//            editor.remove(APP_PREFERENCES).commit();
//            editor.clear();
//            finish();
//            System.exit(0);
//        }else if (id == R.id.nav_share) {

//        } else if (id == R.id.nav_send) {

//            }
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.nav_exit) {
            openQuitDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    //--------------------------------Navigation Drawer end-------------------------------------
    class MyTask extends AsyncTask<Void, Void, String> {
        //        public ProgressDialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... p) {
            ApiClient client = new ApiClient(getString(R.string.BASE_API_URL), MainActivity.this);
            String getAllCat;
            getAllCat = client.getJsonArray(getString(R.string.api_category));
            return getAllCat;
        }

        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Log.d("OK", MainActivity.this.getString(R.string.connection_is_missing));
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.connection_is_missing), Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }
}
