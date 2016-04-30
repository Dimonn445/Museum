package com.example.dimonn445.museum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dimonn445 on 11.02.16.
 */
public class CategoriesAdapter extends BaseAdapter {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.categories_list_item, parent, false);
        }
        Categories p = getCategory(position);
        ((TextView) view.findViewById(R.id.firstLine)).setText(p.name);
//        ((ImageView) view.findViewById(R.id.icon)).setImageResource(p.image);
        Picasso.with(ctx).load(p.image)/*.resize(200, 200)*/.into(((ImageView) view.findViewById(R.id.icon)));

        return view;
    }

    // категория по позиции
    Categories getCategory(int position) {
        return ((Categories) getItem(position));
    }
}
