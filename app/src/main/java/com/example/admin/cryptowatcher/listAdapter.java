package com.example.admin.cryptowatcher;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alex on 01.09.2017.
 */

public class listAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Currencies> objects;

    listAdapter(Context context, ArrayList<Currencies> products) {
        ctx = context;
        objects = products;
        if (products.isEmpty()){
            Log.d("AdapterL" , "LIST EMPTY");
        }
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.home_item, parent, false);
        }

        Currencies pair = getPair(position);
        float noData = 100500;

        ((TextView) view.findViewById(R.id.priceVal)).setText(String.format("%.5f", pair.getPRICE()));

        if(pair.getHOUR_CHANGE() > 0 && pair.getHOUR_CHANGE() != noData) {
            ((TextView) view.findViewById(R.id.priceShift)).setText("+" + pair.getHOUR_CHANGE() + "%");
            ((TextView) view.findViewById(R.id.priceShift)).setTextColor(ctx.getResources().getColor(R.color.positiveShift));
        }else if (pair.getHOUR_CHANGE() <= 0 ){
            ((TextView) view.findViewById(R.id.priceShift)).setText("" + pair.getHOUR_CHANGE() + "%");
            ((TextView) view.findViewById(R.id.priceShift)).setTextColor(ctx.getResources().getColor(R.color.negativeShift));
        }else if (pair.getHOUR_CHANGE().equals(noData)){
            ((TextView) view.findViewById(R.id.priceShift)).setText("No data");
            ((TextView) view.findViewById(R.id.priceShift)).setTextColor(ctx.getResources().getColor(R.color.negativeShift));
        }

        if (pair.getEXCHANGE().equals("Coinmarketcap")) {
            ((TextView) view.findViewById(R.id.pairName)).setText(pair.getABBR());
            ((TextView) view.findViewById(R.id.nameTextView)).setText(pair.getPAIR_NAME());
        }else {
            ((TextView) view.findViewById(R.id.pairName)).setText(pair.getABBR());
            ((TextView) view.findViewById(R.id.nameTextView)).setText(pair.getPAIR_NAME());

        }
        return view;
    }
    Currencies getPair(int position) {
        return ((Currencies) getItem(position));
    }



}
