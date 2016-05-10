package com.example.dimonn445.museum;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {
    WebView wv;
    String ExhName;
    String ExhId, catId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wv = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        String artdescr = intent.getStringExtra("artdescr");
        ExhName = intent.getStringExtra("ExhName");
        /*ExhId = intent.getStringExtra("ExhId");
        catId = intent.getStringExtra("catId");
        Log.d("OK", "ExhId IN DESCRACTI: "+ExhId);*/
        setTitle(ExhName);
        String html_value = artdescr;
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadData(html_value, "text/html;charset=UTF-8", null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_descr);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obp();
            }
        });
    }

    private void obp() {
        super.onBackPressed();
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(DescriptionActivity.this, ExhibitActivity.class);
        intent.putExtra("ExhName", ExhName);
        intent.putExtra("ExhId", ExhId);
        intent.putExtra("Check", true);
        Log.d("OK", "ExhId AFTER DESCRACTI: " + ExhId);
        intent.putExtra("catId", catId);
        startActivity(intent);
        finish();
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(DescriptionActivity.this, ExhibitActivity.class);
                intent.putExtra("ExhName", ExhName);
                intent.putExtra("ExhId", ExhId);
                intent.putExtra("Check", true);
                startActivity(intent);
//                Log.d("OK", "home pressed");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
