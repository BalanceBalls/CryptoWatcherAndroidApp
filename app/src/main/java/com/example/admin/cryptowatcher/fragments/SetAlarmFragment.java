package com.example.admin.cryptowatcher.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.admin.cryptowatcher.API.BittrexApi;
import com.example.admin.cryptowatcher.API.CoinmarketcapApi;
import com.example.admin.cryptowatcher.API.ExmoApi;
import com.example.admin.cryptowatcher.Currencies;
import com.example.admin.cryptowatcher.HomeActivity;
import com.example.admin.cryptowatcher.NotificationEntity;
import com.example.admin.cryptowatcher.NotificationIO;
import com.example.admin.cryptowatcher.R;
import com.example.admin.cryptowatcher.AlarmManagerService;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alex on 23.02.2018.
 */

public class SetAlarmFragment extends android.support.v4.app.Fragment {
    SeekBar seekBar;
    SeekBar supportSeekBar;
    SeekBar resistanceSeekBar;
    TextView resVal;
    TextView supVal;
    double initResVal = 0;
    double initSupVal = 0;
    int supBarLast = 0;
    int resBarLast = 0;
   // TextView timeDurationValue;
   // TextView timeWaitText;
    Spinner exchangeMarkets;
    Spinner pairsOnMarket;
    Button setAlarm;
    TextView supportLevelValue;
    TextView resistanceLevelValue;
    MaterialSpinner marketSpinner;
    MaterialSpinner pairSpinner;
    private final int SERVICE_ID = 4094;
    public static ArrayList<Currencies> pairList;
    int CurrentWaitTime = 0;
    protected String selectedExch = "Exmo";
    protected String selectedPair = "";


    public SetAlarmFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.alarm_set_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // supVal =  (TextView)getView().findViewById(R.id.support_value);
       // resVal =  (TextView)getView().findViewById(R.id.resistance_value);
        //timeWaitText = (TextView)getView().findViewById(R.id.waitTimeText);
       // timeDurationValue = (TextView)getView().findViewById(R.id.timeDurationValue);
      //  seekBar = (SeekBar)getView().findViewById(R.id.waitPeriod);
        //supportSeekBar = (SeekBar)getView().findViewById(R.id.support_seek_bar);
       // resistanceSeekBar = (SeekBar)getView().findViewById(R.id.resistance_seek_bar);
        supportLevelValue = (TextView)getView().findViewById(R.id.supportLevelValue);
        resistanceLevelValue = (TextView)getView().findViewById(R.id.resistanceLevelValue);
        setAlarm = (Button)getView().findViewById(R.id.set_alarm_button);

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlarmStartEvent(v);
            }
        });

        String[] marketsForSpinner = new String[]{
                "Exmo", "Bittrex"
        };

        marketSpinner = (MaterialSpinner)getView().findViewById(R.id.market_spinner);
        marketSpinner.setItems("Exmo", "Bittrex");
        marketSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                new AsyncTaskRunnerAlarm(item).execute();

            }
        });
        new AsyncTaskRunnerAlarm(marketsForSpinner[0]).execute();
        marketSpinner.setSelected(true);

       /* seekBar.setMax(600);


        seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
            {

                int minutes = (progress % 3600) / 60;
                int seconds = progress % 60;
                timeDurationValue.setText(minutes + " мин. " + seconds + " сек.");
            }
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                timeDurationValue.setTextSize(15);
                timeWaitText.setVisibility(View.INVISIBLE);
            }
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                timeDurationValue.setTextSize(14);
                timeWaitText.setVisibility(View.VISIBLE);
            }
        });*/

    }
    private void setInitValues(){
        String st =resistanceLevelValue.getText().toString();
        st = st.replace(',', '.');
        initResVal = Double.valueOf(st);
        initSupVal = initResVal;
    }

    public void onAlarmStartEvent(View v) {

        android.app.AlarmManager manager = (android.app.AlarmManager)getActivity().getSystemService(
                Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        long time = calendar.getTimeInMillis();

        //Create an alarmManager task. Service will be started every 65 seconds to check stocks
        Intent intent = new Intent(getActivity().getApplicationContext(), AlarmManagerService.class);
        PendingIntent alarmIntent = PendingIntent.getService(getActivity().getApplicationContext(),SERVICE_ID,intent,PendingIntent.FLAG_CANCEL_CURRENT);//consider using FLAG_UPDATE_CURRENT

        if (Build.VERSION.SDK_INT >= 23){
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 65000 , alarmIntent);
        }else {
            manager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 65000, alarmIntent);
        }

        //Writing new notification to a file
        new NotificationIO(getActivity().getApplicationContext(),new NotificationEntity(selectedExch,selectedPair,
                Double.parseDouble(supportLevelValue.getText().toString().replace(',','.')),
                Double.parseDouble(resistanceLevelValue.getText().toString().replace(',','.'))), "write");


        Snackbar.make(v, "Будильник установлен", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }
     class AsyncTaskRunnerAlarm extends AsyncTask<Void, Integer, String> {
        ArrayList<String> pairsForList = new ArrayList<>();

        private String Market = null;
        public AsyncTaskRunnerAlarm(String getMarket) {
            super();
            Market = getMarket;
            selectedExch = getMarket;

        }

        @Override
        protected void onPreExecute() {
            HomeActivity.API_COLLECTION.clear();

            pairsForList.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "failure";
            //Getting data from API
            try {

                switch (Market) {
                    case "Bittrex":
                        BittrexApi temp1 = new BittrexApi(true);
                        break;
                    case "Exmo":
                        ExmoApi temp3 = new ExmoApi(true);
                        break;
                }
                result = "Success";

            } catch (Exception e){

            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {


            for (Currencies item : HomeActivity.API_COLLECTION){

                    pairsForList.add(item.getABBR());

            }


            pairSpinner = (MaterialSpinner) getView().findViewById(R.id.pairs_spinner);
            pairSpinner.setItems(pairsForList);

            pairSpinner.setScrollContainer(true);
            supportLevelValue.setText(String.format("%.6f",HomeActivity.API_COLLECTION.get(0).getPRICE()));
            resistanceLevelValue.setText(String.format("%.6f",HomeActivity.API_COLLECTION.get(0).getPRICE()));

            selectedExch = HomeActivity.API_COLLECTION.get(0).getEXCHANGE();
            selectedPair = HomeActivity.API_COLLECTION.get(0).getABBR();
            pairSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {


                    for (Currencies items: HomeActivity.API_COLLECTION) {
                        if(items.getABBR().equals(item)){
                            selectedExch = items.getEXCHANGE();

                            selectedPair = item;
                            supportLevelValue.setText(String.format("%.6f",items.getPRICE()));
                            resistanceLevelValue.setText(String.format("%.6f",items.getPRICE()));

                        }
                    }

                }
            });

            pairSpinner.setSelected(true);
            pairSpinner.setSelectedIndex(0);


        }
    }
}
