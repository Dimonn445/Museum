package com.example.dimonn445.museum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dimonn445 on 11.02.16.
 */
public class CategoriesAdapter extends BaseAdapter {
    customButtonListener customListner;
    customTextListener textListener;

    public interface customTextListener {
        public void onTextListener(int position);
    }

    public void setCustomTextListener(customTextListener listene) {
        this.textListener = listene;
    }

    public interface customButtonListener {
        public void onButtonClickListner(int position);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Categories> objects;

    CategoriesAdapter(Context context, ArrayList<Categories> categories) {
        ctx = context;
        objects = categories;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.categories_list_item, parent, false);
        }
        Categories p = getCategory(position);
        TextView tv = (TextView) view.findViewById(R.id.firstLine);
        tv.setText(p.name);
//        ((ImageView) view.findViewById(R.id.icon)).setImageResource(p.image);
        Picasso.with(ctx).load(p.image).error(R.drawable.ic_report_problem_black_36dp).placeholder(R.drawable.ic_loop_black_24dp)/*.resize(200, 200)*/.into(((ImageView) view.findViewById(R.id.icon)));

        ImageButton btn = (ImageButton) view.findViewById(R.id.childImageButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OK", "CLICK ON position: " + position);
                if (customListner != null) {
                    customListner.onButtonClickListner(position);
                }
            }
        });

        if (p.checkImgButon) {
            btn.setVisibility(View.INVISIBLE);
//            btn.setClickable(false);
//            btn.setFocusable(false);
        }else {
            btn.setVisibility(View.VISIBLE);
//            btn.setClickable(true);
//            btn.setEnabled(true);
        }

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("OK", "tv CLICK ON position: " + position);
                if (textListener != null) {
                    textListener.onTextListener(position);
                }
            }
        });
        return view;
    }

    /*public long setVisability(int position) {
        btn.setVisibility(View.INVISIBLE);
        return position;
    }*/
    // категория по позиции
    Categories getCategory(int position) {
        return ((Categories) getItem(position));
    }

}
