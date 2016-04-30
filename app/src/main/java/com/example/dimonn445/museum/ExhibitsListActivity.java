package com.example.dimonn445.museum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ExhibitsListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ArrayList<ExhibitsList> exhibits = new ArrayList<ExhibitsList>();
    private ExhibitsListAdapter exhibitsListAdapter;
    private String catId, catName;
    private ExhibitsListBuilder buildExhibits;
    private ArrayList<String> exhibitsId = new ArrayList<String>();
    private ListView lvMain;
    final String APP_PREFERENCES = "EXHIBITS";
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private Boolean chek;
    private int MAX_COUNT;
    private int PAGES = 1;
    private View footer;
    private LoadMoreAsyncTask loadingTask = new LoadMoreAsyncTask();
    private String ALLdata = "";
    private boolean all_exh, fav_exh;
    final String EXHIBIT_PREFERENCES = "SAVED_EXHIBITS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getStatus();
        all_exh = getIntent().getBooleanExtra("all_exh", false);
        fav_exh = getIntent().getBooleanExtra("fav_exh", false);

        if (getIntent().getBooleanExtra("Check", false)) {
            catId = getIntent().getStringExtra("CatId");
            catName = getIntent().getStringExtra("CatName");
            setTitle(catName);
            chek = false;
            /*Log.d("OK", "CatId: " + catId);
            Log.d("OK", "CatName: " + catName);*/
        }
        catName = getIntent().getStringExtra("CatName");
        setTitle(catName);
        //--------------------------------Fill Content start-------------------------------------
        fillData();
        exhibitsListAdapter = new ExhibitsListAdapter(this, exhibits);

        lvMain = (ListView) findViewById(R.id.lwexhibits);
        lvMain.setLongClickable(true);
        footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvMain.addFooterView(footer);
        lvMain.setAdapter(exhibitsListAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                buildExhibits = new ExhibitsListBuilder(ALLdata);
                buildExhibits.getExhId();
                exhibitsId = buildExhibits.exhibitId;

                Log.d("ok", "exhibitsId.toString(): " + exhibitsId.toString());
                Log.d("ok", "click pos: " + position);
                Log.d("ok", "exhibitsIdclick pos: " + exhibitsId.get(position));
                Log.d("OK", "" + buildExhibits.nameById(exhibitsId.get(position)));

                Intent intent = new Intent(ExhibitsListActivity.this, ExhibitActivity.class);
                intent.putExtra("ExhId", exhibitsId.get(position));
                intent.putExtra("CatName", catName);
                intent.putExtra("ExhName", "" + buildExhibits.nameById(exhibitsId.get(position)));
                intent.putExtra("Check", true);
                startActivity(intent);
                finish();
            }

        });

        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                Log.d("OK", "totalCount: " + totalItemCount);
                Log.d("ok", "exhibitsId.size: " + exhibitsId.size());
//                setTitle(catName + " (" + totalItemCount+")");
                if (MAX_COUNT > totalItemCount) {
                    if (totalItemCount >= 10 && loadMore &&
                            loadingTask.getStatus() == AsyncTask.Status.FINISHED) {
                        lvMain.addFooterView(footer);
                        loadingTask = new LoadMoreAsyncTask();
                        loadingTask.execute();
                    }
                }
            }
        });

        if (fav_exh) {
            lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                               final int position, long id) {
//                    Log.d("OK", "Long Clicked");
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                            ExhibitsListActivity.this);
                    quitDialog.setTitle(getString(R.string.del_fav));
                    quitDialog.setPositiveButton(getString(R.string.quit_dialog_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    DeleteExhref(exhibitsId.get(position));
                                    Intent intent = new Intent(ExhibitsListActivity.this,
                                            ExhibitsListActivity.class);
                                    intent.putExtra("fav_exh", true);
                                    intent.putExtra("CatName", getString(R.string.fav));
                                    startActivity(intent);
                                    finish();
                                }
                            });

                    quitDialog.setNegativeButton(getString(R.string.quit_dialog_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            });
                    quitDialog.show();

                    return true;
                }
            });
        }
        //--------------------------------Fill Content end-------------------------------------
        //--------------------------------Navigation Drawer start-------------------------------------

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
                Log.d("ok", "fab click");
                getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
                Intent intent = new Intent(ExhibitsListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //--------------------------------Navigation Drawer end-------------------------------------

    }

    private void DeleteExhref(String exhId) {
        Prefs prefs = new Prefs(ExhibitsListActivity.this);
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

    private void fillData() {
        loadingTask.execute();
/*
        String getAllExhibits = "";
        ApiClient client = new ApiClient(getString(R.string.BASE_API_URL), this);

        if (!chek) {
            getAllExhibits = client.getJsonArray(getString(R.string.api_exhibit_category) + catId);
            buildExhibits = new ExhibitsListBuilder(getAllExhibits);
            saveStatus(getAllExhibits);
        } else {
            getAllExhibits = loadStatus();
            buildExhibits = new ExhibitsListBuilder(getAllExhibits);
        }

//        Log.d("OK", "AllEx: " + getAllExhibits);
        buildExhibits.getExhId();
        exhibitsId = new ArrayList<String>();
        exhibitsId = buildExhibits.exhibitId;

        *//*Log.d("OK", "dateById: " + buildExhibits.dateById(exhibitsId.get(1)));
        Log.d("OK", "nameById: " + buildExhibits.nameById(exhibitsId.get(1)));
        Log.d("OK", "bodyShortById: " + buildExhibits.bodyShortById(exhibitsId.get(1)));
        Log.d("OK", "imgById: " + getString(R.string.BASE_API_URL) + buildExhibits.imgById(exhibitsId.get(1)));
*//*

//        Log.d("OK", "size(): " + exhibitsId.size());
        for (int i = 0; i < exhibitsId.size(); i++) {
            *//*Log.d("OK", "dateById: " + buildExhibits.dateById(exhibitsId.get(i)));
            Log.d("OK", "nameById: " + buildExhibits.nameById(exhibitsId.get(i)));
            Log.d("OK", "bodyShortById: " + buildExhibits.bodyShortById(exhibitsId.get(i)));
            Log.d("OK", "imgById: " + getString(R.string.BASE_API_URL) + buildExhibits.imgById(exhibitsId.get(i)));*//*
            String date, name, body, img;
            date = buildExhibits.dateById(exhibitsId.get(i));
            name = buildExhibits.nameById(exhibitsId.get(i));
            body = buildExhibits.bodyShortById(exhibitsId.get(i));
            if (date.contains("null")) {
                date = "";
            }
            if (buildExhibits.imgById(exhibitsId.get(i)).contains("null")) {
                img = buildExhibits.imgById(exhibitsId.get(i));
            } else {
                img = getString(R.string.BASE_API_URL) + buildExhibits.imgById(exhibitsId.get(i));
            }
//            Log.d("OK", "IMG: " + img);

            exhibits.add(new ExhibitsList(name, body, img, date*//*, false*//*));
//            exhibits.add(new ExhibitsList(name, body, android.R.drawable.ic_menu_share, date));
            *//*exhibits.add(new ExhibitsList("Exhibit_name " + i, "Exhibit_description Exhibit_description Exhibit_description " + i,
                    android.R.drawable.ic_menu_share, i + ".02.2016 / " + (1 + i) + ".02.2016"));*//*
            *//*exhibitsListAdapter.notifyDataSetChanged();
            lvMain.deferNotifyDataSetChanged();*//*
        }*/
    }

    /*private void showresult(View v) {
        String result = "";
        for (ExhibitsList p : exhibitsListAdapter.getFav()) {
            if (p.check)
                result += "\n" + p.exhibitname;
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }*/

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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
//            getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
            Intent intent = new Intent(ExhibitsListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                ExhibitsListActivity.this);
        quitDialog.setTitle(getString(R.string.quit_dialog_are_you_sure));

        quitDialog.setPositiveButton(getString(R.string.quit_dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//                Log.d("OK", "EXIT");
                ExhibitsListActivity.this
                        .getSharedPreferences(APP_PREFERENCES, 0)
                        .edit()
                        .clear()
                        .apply();
                Intent intent = new Intent(ExhibitsListActivity.this, MainActivity.class);
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
            /*Intent intent = new Intent(ExhibitsListActivity.this, MainActivity.class);
            startActivity(intent);*/
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
            Intent intent = new Intent(ExhibitsListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_favourite) {
//            Toast.makeText(this, getString(R.string.fav), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ExhibitsListActivity.this, ExhibitsListActivity.class);
            intent.putExtra("fav_exh", true);
            intent.putExtra("CatName", getString(R.string.fav));
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_all_exhibits) {
            Intent intent = new Intent(ExhibitsListActivity.this, ExhibitsListActivity.class);
            intent.putExtra("all_exh", true);
            intent.putExtra("CatName", getString(R.string.all_exhibits));
            startActivity(intent);
            finish();
            // } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_exit) {
//            Log.d("OK", "EXIT");
            openQuitDialog();
            /*this.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
            Intent intent = new Intent(ExhibitsListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();*/
            // }else if (id == R.id.nav_share) {

            // } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

/*
    private void writeToFile(String data) {
        BufferedWriter out;
        try {

            FileWriter fileWriter= new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/tsxt.txt");

            out = new BufferedWriter(fileWriter);

            out.write(data);

            out.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    //--------------------------------Navigation Drawer end-------------------------------------
    private class LoadMoreAsyncTask extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(Void... params) {
            try {
//                Thread.sleep(500);
                String param = "?limit=10&page=" + PAGES;
                String getAllExhibits = "";
                ApiClient client = new ApiClient(getString(R.string.BASE_API_URL), ExhibitsListActivity.this);
                Prefs prefs = new Prefs(ExhibitsListActivity.this);
                if (fav_exh) {
                    if (prefs.containsPref(EXHIBIT_PREFERENCES)) {
                        if (prefs.getExhPref().isEmpty()) {
                            Log.d("OK", "emptyPref");
                            Toast.makeText(ExhibitsListActivity.this, getString(R.string.error_loading_items), Toast.LENGTH_SHORT).show();
                        } else {
                            getAllExhibits = prefs.getExhPref();
                        }
                    }
                } else {
                    if (all_exh) {
                        getAllExhibits = client.getJsonArray(getString(R.string.api_exhibit_all) + param);
                    } else {
                        if (!chek) {
                            getAllExhibits = client.getJsonArray(getString(R.string.api_exhibit_category) + catId + param);
                        } else {
                            getAllExhibits = loadStatus();
                        }
                    }
                }
                return getAllExhibits;
            } catch (Exception e) {
                Log.e("OK", "Loading data", e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String data) {
//            try {
            if (data.isEmpty()) {
                Toast.makeText(ExhibitsListActivity.this, getString(R.string.error_loading_items), Toast.LENGTH_SHORT).show();
                lvMain.removeFooterView(footer);
                return;
            }
            /*} catch (NullPointerException e) {
                e.printStackTrace();
                Toast.makeText(ExhibitsListActivity.this, getString(R.string.error_loading_items), Toast.LENGTH_SHORT).show();
                return;
            }*/
            if (PAGES > 1) {
                try {
                    JSONObject dataJsonObj = new JSONObject(data);
                    JSONArray exhibitsArr = dataJsonObj.getJSONArray("exhibits");
                    String buff = exhibitsArr.toString();
                    StringBuilder builder = new StringBuilder();
                    builder.append(ALLdata.substring(0, ALLdata.length() - 2)).append(",").append(buff.substring(1, buff.length())).append("}");
                    ALLdata = builder.toString();
//                    writeToFile(ALLdata);
                    buildExhibits = new ExhibitsListBuilder(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                buildExhibits = new ExhibitsListBuilder(data);
                ALLdata = data;
            }

            saveStatus(ALLdata);
            buildExhibits.getExhId();

            exhibitsId = buildExhibits.exhibitId;
            MAX_COUNT = buildExhibits.getCount();
            setTitle(catName + " (" + MAX_COUNT + ")");
            PAGES++;
            Log.d("OK", "PAGES: " + PAGES);
            for (int i = 0; i < exhibitsId.size(); i++) {
                String date, name, body, img;
                date = buildExhibits.dateById(exhibitsId.get(i));
                name = buildExhibits.nameById(exhibitsId.get(i));
                body = buildExhibits.bodyShortById(exhibitsId.get(i));
                if (date.contains("null")) {
                    date = "";
                }
                if (buildExhibits.imgById(exhibitsId.get(i)).contains("null")) {
                    img = buildExhibits.imgById(exhibitsId.get(i));
                } else {
                    img = getString(R.string.BASE_API_URL) + buildExhibits.imgById(exhibitsId.get(i));
                }

                exhibits.add(new ExhibitsList(name, body, img, date));
            }
            int index = lvMain.getFirstVisiblePosition();
            int top = (lvMain.getChildAt(0) == null) ? 0 : lvMain.getChildAt(0).getTop();
            lvMain.setSelectionFromTop(index, top);

            exhibitsListAdapter.notifyDataSetChanged();
            lvMain.deferNotifyDataSetChanged();
            lvMain.removeFooterView(footer);
        }
    }
}
