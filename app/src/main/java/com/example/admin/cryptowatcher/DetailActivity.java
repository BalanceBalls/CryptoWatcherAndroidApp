package com.example.admin.cryptowatcher;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;

import com.example.admin.cryptowatcher.fragments.detailFragment;
import com.example.admin.cryptowatcher.fragments.graphFragment;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import static java.security.AccessController.getContext;


/**
 * Created by Alex on 11.07.2017.
 * H--C means that smth needs to get hardcoded!!!
 */

public class DetailActivity extends SwipeBackActivity  {
    //SwipeBackActivity
    boolean isFirstOpen = true;

    public static final String TAG = "my_deta";

    public String BASE_URL = null;

    private String pairNameRecieved = null;
    private String marketRecieved = null;
    SwipeBackLayout swipeLay;
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        getSwipeBackLayout().setDragEdge(SwipeBackLayout.DragEdge.TOP);//set swipe direction

        //getting data from previous activity
        Intent intent = getIntent();
        String pairNameData = intent.getStringExtra("pairName");
        marketRecieved = intent.getStringExtra("marketName");
        Log.d(TAG, "Current market : " + marketRecieved);
        pairNameRecieved =pairNameData;

        //froming a bundle that will be send to fragments
        Bundle bundle = new Bundle();
        bundle.putString("params", pairNameRecieved);
        bundle.putString("market", marketRecieved);

        graphFragment youFragment = new graphFragment();
        youFragment.setArguments(bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putString("params1",pairNameRecieved);
        detailFragment youFragment1 = new detailFragment();

        youFragment.setArguments(bundle);
        youFragment1.setArguments(bundle1);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()          // получаем экземпляр FragmentTransaction
                .add(R.id.fragmentContainer,youFragment1)
                .add(R.id.graphContainer, youFragment)
                .addToBackStack("myStack")
                .commit();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getBackToMain();
    }
    @Override
    public boolean onSupportNavigateUp() {
        getBackToMain();
        return true;
    }

    private void getBackToMain(){
        Activity act = (Activity) getSwipeBackLayout().getContext();
        act.finish();
        act.overridePendingTransition(0, android.R.anim.fade_out);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(isFirstOpen) {                                                                          //if activity is first created
            isFirstOpen = false;
        } else {                                                                                   //if activity called again , update rate values
                Intent intent = new Intent(DetailActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
}

