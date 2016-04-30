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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    private Button test;
    private JSONArray categoryArr;
    private ArrayList<String> rootCategIdd;
    private ArrayList<String> categoryBuffer;
    private ArrayList<String> prevCategoryBuffer;
    private ArrayList<String> prevCategID;
    private CategoryBuilder buildCategory;
    //    private String BASE_API_URL = "http://uzkkce61853c.zz995.koding.io:3000";
    private ArrayList<Categories> categories = new ArrayList<Categories>();
    private CategoriesAdapter categoriesAdapter;
    private ListView lvMain;
    private Boolean chek;
    private SharedPreferences sPref;
    final String APP_PREFERENCES = "CATEGORIES";
    private SharedPreferences.Editor editor;
    private MyTask mt;
    //    Display display;
    private int width = 0, height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getBooleanExtra("Exit me", false)) {
//            Log.d("OK", "Exit me " + getIntent().getBooleanExtra("Exit me", false));
            MainActivity.this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
            finish();
        }

        FindViev();
        getStatus();
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
        lvMain.setAdapter(categoriesAdapter);
        fillData();
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
//                Log.d("ok", "click pos: " + position);
                try {
                    nextCall(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                startActivity(intent);*/
            }
        });

//--------------------------------Fill Content end-------------------------------------

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_LONG).show();
                Snackbar.make(view, getString(R.string.update_category_list), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
//                Log.d("ok", "fab click");
/*                if (isNetworkAvailable()) {

                }*/
//                Log.d("OK", "getAllCat: " + getAllCat);
                if (isNetworkAvailable()) {
                    /*ApiClient client = new ApiClient(getString(R.string.BASE_API_URL), MainActivity.this);
                    String getAllCat = client.getJsonArray(getString(R.string.api_category));*/
                    String getAllCat = "";
                    mt = new MyTask();
                    mt.execute();
//                getAllCat = client.getJsonArray(getString(R.string.api_category));
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
//                    rootCategIdd ;
                    try {
                        if (rootCategIdd.isEmpty()) {
                            rootCategIdd = buildCategory.rootCategId;
                        } else {
//                        if (rootCategIdd.size() > 0) {
                            rootCategIdd.clear();
//                        }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    rootCategIdd = buildCategory.rootCategId;
//                    Log.d("OK", "rootCategIdd.toString(): " + rootCategIdd.toString());
                    categories.clear();
                    categoriesAdapter.notifyDataSetChanged();
                    lvMain.deferNotifyDataSetChanged();
                    firstCall();
                } else {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
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
//    --------------------------------Navigation Drawer end-------------------------------------

    private void reload() {
        if (isNetworkAvailable()) {
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
            lvMain.deferNotifyDataSetChanged();
            firstCall();
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internet_connection_is_not), Toast.LENGTH_SHORT).show();
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
            prevCategID = new ArrayList<String>();
            categoryBuffer = new ArrayList<String>();
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
            rootCategIdd = buildCategory.rootCategId;
            firstCall();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void FindViev() {
//        test = (Button) findViewById(R.id.test_btn);
        lvMain = (ListView) findViewById(R.id.lwcategories);

    }

    private void firstCall() {
        try {
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
                categories.add(new Categories(buildCategory.nameById(rootCategIdd.get(i)), /*builder.toString()*/
                        buildCategory.imgById(rootCategIdd.get(i))));
                categoriesAdapter.notifyDataSetChanged();
                lvMain.deferNotifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prevCall(String parent) {
        prevCategoryBuffer = buildCategory.subcategoryPrev(parent);
        setTitle(buildCategory.nameById(parent));
//        rootCategIdd.clear();
        categories.clear();
        Log.d("OK", "parent: " + parent);

        if (parent.equals("null")) {
            Log.d("OK", "parent: " + parent);
        } else {
            for (int i = 0; i < prevCategoryBuffer.size(); i++) {
                categories.add(new Categories(buildCategory.nameById(prevCategoryBuffer.get(i)),
                        buildCategory.imgById(prevCategoryBuffer.get(i))));
//                    Log.d("OK", "SELECTEDID: " + rootCategIdd.get(i));
                categoriesAdapter.notifyDataSetChanged();
                lvMain.deferNotifyDataSetChanged();
//                rootCategIdd.add(prevCategoryBuffer.get(i));
            }
        }
    }

    private void nextCall(int pos) {
//        try {
//            Log.d("OK", "nextCAll()");
//        Log.d("OK","rootCategIdd.get "+rootCategIdd.get(pos));
        categoryBuffer = buildCategory.subcategoryNext(rootCategIdd.get(pos));
        String selectedCat = rootCategIdd.get(pos);

        Log.d("OK", "SELECTEDID: " + selectedCat);

        prevCategID.add(selectedCat);
            /*for(int i=0;i<prevCategID.size();i++){
                Log.d("OK","prevCategID: "+prevCategID.get(i));
            }*/
        setTitle(buildCategory.nameById(selectedCat));
        rootCategIdd.clear();
        categories.clear();
        for (int i = 0; i < categoryBuffer.size(); i++) {
//            Log.d("OK", "categoryBuffer.get(i): " + categoryBuffer.get(i));
            if (categoryBuffer.get(i).equals("null")) {
//                Log.d("OK", "NULL");
//                    Log.d("OK", "selectedCatId: " + selectedCat);
//                    Log.d("OK", "selectedCatName: " + buildCategory.nameById(selectedCat));
                Intent intent = new Intent(MainActivity.this, ExhibitsListActivity.class);
                intent.putExtra("CatId", selectedCat);
                intent.putExtra("CatName", "" + buildCategory.nameById(selectedCat));
                intent.putExtra("Check", true);
                startActivity(intent);
                finish();
            } else {
                categories.add(new Categories(buildCategory.nameById(categoryBuffer.get(i)),
                        buildCategory.imgById(categoryBuffer.get(i))));
//                    Log.d("OK", "SELECTEDID: " + rootCategIdd.get(i));
                categoriesAdapter.notifyDataSetChanged();
                lvMain.deferNotifyDataSetChanged();
                rootCategIdd.add(categoryBuffer.get(i));
            }
        }
        /*} catch (Exception e) {
            e.printStackTrace();
        }*/
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
            return "_sm.jpg";
        } else {
            if (width <= 800 && height <= 1280) {
                return "_md.jpg";
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
//                    Log.d("OK", "rootCategIdd.toString(): " + rootCategIdd.toString());
                categories.clear();
                categoriesAdapter.notifyDataSetChanged();
                lvMain.deferNotifyDataSetChanged();
                firstCall();
            /*for(int i=0; i<prevCategID.size();i++){
                String prevId = prevCategID.get(prevCategID.size()-1);
                Log.d("OK", "prevId: " + prevId);
                prevCall(prevId);
                prevCategID.remove(prevCategID.size()-1);
            }*/
//            prevCall();
//            super.onBackPressed();
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
//            setmatrixofbuttons();

//            Toast.makeText(this, "ЖМЯК", Toast.LENGTH_LONG).show();

            /*categories.clear();

            for (int i = 1; i <= 20; i++) {

                categories.add(new Categories("Category " + i,
                        android.R.drawable.ic_menu_report_image));
                categoriesAdapter.notifyDataSetChanged();
                lvMain.deferNotifyDataSetChanged();

            }*/

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
            Toast.makeText(this, getString(R.string.you_are_at_home), Toast.LENGTH_LONG).show();
            // Handle the camera action
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
        } else if (id == R.id.nav_exit) {
            openQuitDialog();
//            Log.d("OK", "EXIT");
//            this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
//            editor.remove(APP_PREFERENCES).commit();
//            editor.clear();
//            finish();
//            System.exit(0);
//        }else if (id == R.id.nav_share) {

//        } else if (id == R.id.nav_send) {

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
