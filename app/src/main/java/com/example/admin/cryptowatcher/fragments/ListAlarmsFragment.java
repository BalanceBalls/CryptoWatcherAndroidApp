package com.example.admin.cryptowatcher.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.cryptowatcher.AlarmViewAdapter;
import com.example.admin.cryptowatcher.NotificationEntity;
import com.example.admin.cryptowatcher.NotificationIO;
import com.example.admin.cryptowatcher.OnItemClickListener;
import com.example.admin.cryptowatcher.R;

import java.util.ArrayList;

/**
 * Created by Alex on 23.02.2018.
 */

public class ListAlarmsFragment extends android.support.v4.app.Fragment {
    private ArrayList<NotificationEntity> alarmCardsList;
    private RecyclerView alarmViewList;
    AlarmViewAdapter alarmAdapter;
    LinearLayoutManager layoutManager;
    protected ProgressBar loader;
    View fragView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public ListAlarmsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loader = (ProgressBar) getView().findViewById(R.id.list_loader);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_list_fragment, container, false);

        loader = (ProgressBar)view.findViewById(R.id.list_loader);
        loader.setVisibility(View.GONE);
        alarmViewList = (RecyclerView) view.findViewById(R.id.alarmView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.alarms_refresh_swipe) ;
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        alarmViewList.setLayoutManager(layoutManager);
        mSwipeRefreshLayout.setRefreshing(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new MyTask().execute();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmCardsList =  new ArrayList<>();

        new MyTask().execute();

    }

    private void showBar(){

        mSwipeRefreshLayout.setRefreshing(true);
    }
    private void hideBar(){
        mSwipeRefreshLayout.setRefreshing(false);

    }



    private class MyTask extends AsyncTask<Void, Void, Void> {
        NotificationIO initRec;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBar();
        }

        @Override
        protected Void doInBackground(Void... params) {
            initRec = new NotificationIO(getActivity().getApplicationContext());
            alarmCardsList = initRec.getList();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            alarmAdapter = new AlarmViewAdapter(alarmCardsList);
            alarmViewList.setAdapter(alarmAdapter);
            alarmAdapter.setOnItemClickListener(new OnItemClickListener() {

                //Overriding interface's method ( AlarmViewAdapter implementation adds up to this one)
                @Override
                public void onItemLongClicked(NotificationEntity item) {
                    alarmCardsList.remove(item);
                    alarmViewList.getAdapter().notifyDataSetChanged();

                }
            });

            hideBar();

        }
    }
}
