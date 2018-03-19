package com.example.admin.cryptowatcher.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.cryptowatcher.Currencies;
import com.example.admin.cryptowatcher.HomeActivity;
import com.example.admin.cryptowatcher.R;

import java.util.Locale;

/**
 * Created by admin on 28.09.17.
 */

public class detailFragment extends Fragment  {


    TextView hourDetailVal;
    TextView dayDetailVal;
    TextView weekDetailVal;
    TextView volumeDetailVal;
    TextView btcPriceVal;
    TextView pairNameText;
    TextView priceVal;
    TextView graphErrorText;
    ImageView graphErrorImg;

    protected String pairNameData;
    private String fsym = null;
    Context ctx;
    //DataPass mDataPasser;
    private String mParam1;
    private String mParam2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment,  container, false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("params1");
            mParam2 = getArguments().getString("market");
        }
        Log.d("transitFragQ", "2 -- " + mParam1);
        fsym = mParam1;

        return view;
    }



    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        graphErrorText =  (TextView)getView().findViewById(R.id.graphErrorText);
        hourDetailVal = (TextView)getView().findViewById(R.id.hourDetailVal);
        dayDetailVal = (TextView)getView().findViewById(R.id.dayDetailVal);
        weekDetailVal = (TextView)getView().findViewById(R.id.weekDetailVal);
        volumeDetailVal = (TextView)getView().findViewById(R.id.volumeDetailVal);
        btcPriceVal = (TextView)getView().findViewById(R.id.btcPriceVal);
        pairNameText = (TextView)getView().findViewById(R.id.pairNameText);
        priceVal = (TextView)getView().findViewById(R.id.priceVal);

        for (Currencies obj: HomeActivity.API_COLLECTION) { //passing through data array and finding our needed currency pair
            if (obj.getABBR().equals(fsym)) {


                pairNameText.setText(fsym);//API loves Upper-case

                if (obj.getHOUR_CHANGE() > 0) {
                    hourDetailVal.setText("+" + obj.getHOUR_CHANGE() + "%");
                    hourDetailVal.setTextColor(Color.parseColor("#FF99cc00"));//H--C
                } else {
                    hourDetailVal.setText("" + obj.getHOUR_CHANGE() + "%");
                    hourDetailVal.setTextColor(Color.parseColor("#FFFF4444"));//H--C
                }

                if (obj.getDAY_CHANGE() > 0) {
                    dayDetailVal.setText("+" + obj.getDAY_CHANGE() + "%");
                    dayDetailVal.setTextColor(Color.parseColor("#FF99cc00"));//H--C
                } else {
                    dayDetailVal.setText("" + obj.getDAY_CHANGE() + "%");
                    dayDetailVal.setTextColor(Color.parseColor("#FFFF4444"));//H--C
                }

                if (obj.getWEEK_CHANGE() > 0) {
                    weekDetailVal.setText("+" + obj.getWEEK_CHANGE() + "%");
                    weekDetailVal.setTextColor(Color.parseColor("#FF99cc00"));//H--C
                } else {
                    weekDetailVal.setText("" + obj.getWEEK_CHANGE() + "%");
                    weekDetailVal.setTextColor(Color.parseColor("#FFFF4444"));//H--C
                }

                priceVal.setText(obj.getPRICE().toString() + " " + fsym.substring(fsym.indexOf('/') + 1, fsym.length()));//H--C

                volumeDetailVal.setText(java.text.NumberFormat.getInstance(Locale.US).format(obj.getMARKET_CAP_USD()) + " USD");


                btcPriceVal.setText("" + String.format("%.10f", obj.getPRICE_BTC()) + " BTC");//H--C
            }
        }
    }
}
