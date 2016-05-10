package com.example.dimonn445.museum;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageActivity extends AppCompatActivity {

    private ImageView mContentView;
    //    float x1, x2;
    float y1, y2;
    private SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*getWindow().requestFeature(Window.FEATURE_ACTION_BAR);*/
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mSlider = (SliderLayout) findViewById(R.id.imgslider);
//        mContentView = (ImageView) findViewById(R.id.fullscreen_content);
//        String url = getIntent().getStringExtra("URL");
        ArrayList<String> arrayList;
        arrayList = getIntent().getStringArrayListExtra("img_arr");
        int cur_pos = getIntent().getIntExtra("cur_pos",0);
        /*for (int i = 0; i < arrayList.size(); i++) {
            Log.d("OK", "img_arr: " + arrayList);
        }*/
//        Log.d("OK", "URL: " + url);
//        Picasso.with(ImageActivity.this).load(url).error(R.drawable.ic_report_problem_white_48dp).into(mContentView);
//        ------Slider-----
        HashMap<String, String> url_maps = new HashMap<String, String>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).contains("null")) {
                Log.d("OK", "IMGCDN: null");
            } else {
                url_maps.put(getString(R.string.image) + i, arrayList.get(i));
            }
        }

        for (String name : url_maps.keySet()) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
//            Log.d("OK", "url_maps: " + url_maps.get(name));
            sliderView
//                    .description(name)
                    .image(url_maps.get(name))
                    .error(R.drawable.ic_report_problem_white_48dp)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            obp();
                        }
                    });
            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle()
                    .putString("extra", name);
            mSlider.addSlider(sliderView);
        }
        mSlider.setCurrentPosition(cur_pos);
        mSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(6000);
    }

    private void obp() {
        super.onBackPressed();
    }

    /*public boolean onTouchEvent(MotionEvent touchevent) {
        Log.d("OK", "touchevent.getAction()" + touchevent.getAction());
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                y2 = touchevent.getY();
                // if UP to Down sweep event on screen
                if (y1 < y2) {
                    super.onBackPressed();
                    Log.d("OK", "UP to Down");
                }
                //if Down to UP sweep event on screen
                if (y1 > y2) {
                    super.onBackPressed();
                    Log.d("OK", "Down to UP");
                }
                break;
            }
        }
        return false;
    }*/
}
