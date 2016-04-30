package com.example.dimonn445.museum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dimonn445 on 22.02.16.
 */
public class ExhibitsListAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<ExhibitsList> objects;

    ExhibitsListAdapter(Context context, ArrayList<ExhibitsList> exhibitses) {
        ctx = context;
        objects = exhibitses;
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

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.exhibits_list_item, parent, false);
        }

        ExhibitsList p = getExhibit(position);

        // заполняем View в пункте списка данными из категорий: наименование, описание
        // и картинка
        ((TextView) view.findViewById(R.id.exhibitName)).setText(p.exhibitname);
        ((TextView) view.findViewById(R.id.exhibitListDescription)).setText(p.description);
        ((TextView) view.findViewById(R.id.exhibitDate)).setText(p.date);
//        ((ImageView) view.findViewById(R.id.icon)).setImageResource(p.image);
//        ((ImageView) view.findViewById(R.id.icon));
        Picasso.with(ctx).load(p.image).error(R.drawable.ic_report_problem_black_36dp)/*.resize(200, 200)*/.into(((ImageView) view.findViewById(R.id.icon)));
        /*CheckBox cbFav = (CheckBox)view.findViewById(R.id.checkBoxFavorite);
        cbFav.setOnCheckedChangeListener(myCheckChangList);
        cbFav.setTag(position);
        cbFav.setChecked(p.check);*/
        return view;
    }
    /*ArrayList<ExhibitsList> getFav(){
        ArrayList<ExhibitsList> fav = new ArrayList<ExhibitsList>();
        for (ExhibitsList p : objects){
            if(p.check)
                fav.add(p);
        }
        return fav;
    }*/

    // категория по позиции
    ExhibitsList getExhibit(int position) {
        return ((ExhibitsList) getItem(position));
    }
    /*CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            Log.d("OK","Checked");
            getExhibit((Integer) buttonView.getTag()).check = isChecked;
        }
    };*/
}
