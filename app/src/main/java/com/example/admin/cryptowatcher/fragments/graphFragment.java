package com.example.admin.cryptowatcher.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.cryptowatcher.CurrencyHist;
import com.example.admin.cryptowatcher.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 28.09.17.
 */

public class graphFragment extends Fragment {

    protected MaterialSpinner materialSpinnerGraph;
    protected GraphView graphF;
    protected ProgressBar graphLoaderBar;
    private ArrayList<CurrencyHist> HISTORICAL_DATA;

    public String BASE_URL = null;

    public String tsym = "USD";//in this value
    public String limitPeriod = "30";//period of time to gather data
    public String aggregate = "1";// interval
    public String market = "CCCAGG";// "CCCAGG";// take values from
    public static String fsym = null;//show this currency
    public static final String ARRAY_TAG = "Data";
    public static final String GET_REPSONSE = "Response";
    private static final String timeMark = "time";//json tag for utc time
    private static final String priceMark = "close";//json tag for price
    String mParam1;
    private String pairName;
    private String currentMarket;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
           mParam1 = getArguments().getString("params");
           currentMarket = getArguments().getString("market");
        }
        View view = inflater.inflate(R.layout.graph_fragment,  container, false);

        pairName = mParam1;
        if(currentMarket.equals("Coinmarketcap")){
            fsym = pairName.substring(0,pairName.indexOf("/"));
            tsym = pairName.substring(pairName.indexOf("/") + 1, pairName.length());
            currentMarket = "CCCAGG";
        }else if (currentMarket.equals("Bittrex")){
            tsym = pairName.substring(0,pairName.indexOf("/")).toUpperCase();
            fsym = pairName.substring(pairName.indexOf("/") + 1, pairName.length()).toUpperCase();

        }else{
            fsym = pairName.substring(0,pairName.indexOf("/"));
            tsym = pairName.substring(pairName.indexOf("/") + 1, pairName.length());

        }
        HISTORICAL_DATA = new ArrayList<>();

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        materialSpinnerGraph = (MaterialSpinner)getView().findViewById(R.id.materialSpinnerGraph1);
        materialSpinnerGraph.setItems(getString(R.string.monthly_chart_text), getString(R.string.weekly_chart_text), getString(R.string.daily_chart_text));//H--C
        materialSpinnerGraph.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                new getDataAsync(pairName,position + 1).execute();
                Log.d("transitString",pairName + " - spin");
            }
        });

        new getDataAsync(pairName, 1).execute();
    }


    @Override
    public void onPause() {
        super.onPause();
        HISTORICAL_DATA.clear();
    }

    private class getDataAsync extends AsyncTask<Void , Integer, String> {

        private String fsymAsync = null;//pairName
        private  int timePeriod = 1;//amount of hours or days for API call
        private boolean success = true;

        public getDataAsync(String pairName, int period) {
            super();
            fsymAsync = pairName;
            timePeriod = period;
        }

        HttpURLConnection urlConnectionCCAG = null;
        BufferedReader reader = null;
        String resultJson = "";



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            graphF = (GraphView)getView().findViewById(R.id.graphF);
            graphF.setVisibility(View.GONE);
            graphLoaderBar = (ProgressBar)getView().findViewById(R.id.graphLoaderBar);
            graphLoaderBar.setVisibility(View.VISIBLE);
            HISTORICAL_DATA.clear(); //clear previous data for the new graph

            //from requests for the API
            if(timePeriod ==1 ){
                limitPeriod = "30";
                BASE_URL = "https://min-api.cryptocompare.com/data/histoday?fsym=" + fsym + "&tsym=" + tsym
                        + "&limit="+ limitPeriod +"&aggregate="+ aggregate + "&e=" + currentMarket;

            } else if(timePeriod == 2 ){
                limitPeriod = "7";
                BASE_URL = "https://min-api.cryptocompare.com/data/histoday?fsym=" + fsym  + "&tsym=" + tsym
                        + "&limit="+ limitPeriod +"&aggregate="+ aggregate + "&e=" + currentMarket;

            }else if(timePeriod == 3){
                limitPeriod = "24";
                BASE_URL =  "https://min-api.cryptocompare.com/data/histohour?fsym=" + fsym  + "&tsym=" + tsym
                        + "&limit="+ limitPeriod +"&aggregate="+ aggregate + "&e=" + currentMarket;
            }

        }


        @Override
        protected String doInBackground(Void... params) {

            try {


                URL url = new URL(BASE_URL);
                System.setProperty("http.keepAlive", "false");
                urlConnectionCCAG = (HttpURLConnection) url.openConnection();
                urlConnectionCCAG.setRequestMethod("GET");
                urlConnectionCCAG.connect();

                InputStream inputStreamCCAG = urlConnectionCCAG.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStreamCCAG));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                inputStreamCCAG.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJson;

        }


        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            String rspn = null;//server response Success/Error

            try {

                JSONObject histObj = new JSONObject(resultJson);

                Log.d("TestP" , histObj.getString("Response"));
                if(histObj.getString("Response") == "Error"){
                   success = false;

                }else {
                    JSONArray jsonArr = histObj.getJSONArray(ARRAY_TAG);

                    int jsonLinesNum = Integer.parseInt(limitPeriod);

                    for (int i = 0; i < jsonLinesNum + 1; i++) {
                        parseHistory(jsonArr.getJSONObject(i)); //parsing data into CurrencyHist type and putting it into ArrayList
                        if(Double.parseDouble(jsonArr.getJSONObject(i).getString(priceMark)) < 0.00006){
                            success = false;
                            break;
                        }
                    }
                    initializeLineGraphView(graphF, timePeriod, success);
                }

            } catch (final JSONException e) {
                success = false;
                initializeLineGraphView(graphF,timePeriod,success);//initialize graph error
            }
        }
    }


    protected void parseHistory(JSONObject object) throws JSONException {

        long utcTime = (object.getLong(timeMark));

        double priceAt = Double.parseDouble(object.getString(priceMark));

        CurrencyHist temp = new CurrencyHist(utcTime,priceAt);
        HISTORICAL_DATA.add(temp);
    }


    protected void initializeLineGraphView(GraphView graph1, int periodOfTime, boolean ifSuccess) {
        Log.d("speedUP", "initialize graph called");

        graphLoaderBar.setVisibility(View.GONE);
        if(ifSuccess) {
            try {
                graph1.setVisibility(View.VISIBLE);
                graph1.removeAllSeries();

                final int pot = periodOfTime;


                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{

                        new DataPoint(HISTORICAL_DATA.get(0).getUtcTime(), HISTORICAL_DATA.get(0).getCloseValue())

                });
                for (int i = 1; i < HISTORICAL_DATA.size(); i++) {

                    DataPoint kek = new DataPoint(HISTORICAL_DATA.get(i).getUtcTime(), HISTORICAL_DATA.get(i).getCloseValue());
                    series.appendData(kek, false, HISTORICAL_DATA.size());
                }

                series.setDrawDataPoints(true);
                series.setDataPointsRadius(6);
                series.setThickness(3);
                series.setColor(getResources().getColor(R.color.primaryTextColor));
                series.setTitle(pairName);

                graph1.getLegendRenderer().setBackgroundColor(getResources().getColor(R.color.backgroundColor));//H--C
                graph1.getLegendRenderer().setTextSize(20f);
                graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                graph1.getLegendRenderer().setVisible(true);

                //set graph's MIN and MAX values on both axis
                graph1.getViewport().setMinX((int) HISTORICAL_DATA.get(0).getUtcTime());
                Log.d("GraphX", ((int) HISTORICAL_DATA.get(0).getUtcTime()) + "");
                graph1.getViewport().setMaxX((int) HISTORICAL_DATA.get(HISTORICAL_DATA.size() - 1).getUtcTime());
                Log.d("GraphX", ((int) HISTORICAL_DATA.get(HISTORICAL_DATA.size() - 1).getUtcTime()) + "");
                double minY = HISTORICAL_DATA.get(0).getCloseValue() - (HISTORICAL_DATA.get(0).getCloseValue() / 100) * 20;
                graph1.getViewport().setMinY(minY);


                graph1.getViewport().setScrollable(true);
                graph1.getViewport().setScalable(true);
                graph1.getViewport().setXAxisBoundsManual(true);
                if (pot == 2) {
                    graph1.getGridLabelRenderer().setNumHorizontalLabels(HISTORICAL_DATA.size());
                } else
                    graph1.getGridLabelRenderer().setNumHorizontalLabels(HISTORICAL_DATA.size() / 2);

                graph1.getGridLabelRenderer().setHorizontalLabelsAngle(50);

                graph1.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.secondaryTextColor));//H--C
                graph1.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.secondaryTextColor));
                graph1.getGridLabelRenderer().setTextSize(20.3f);


                graph1.getGridLabelRenderer().setGridColor(Color.parseColor("#505665"));//H--C
                graph1.getGridLabelRenderer().setLabelsSpace(10);

                graph1.addSeries(series);


                graph1.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {

                    @Override
                    public String formatLabel(double value, boolean isValueX) {

                        String dateFormat = "";

                        switch (pot) {
                            case 1:
                                dateFormat = "MM.dd";
                                break;
                            case 2:
                                dateFormat = "EEE";
                                break;
                            case 3:
                                dateFormat = "HH:mm";
                                break;
                            default:
                                dateFormat = "MM.dd";
                                break;
                        }
                        if (isValueX) {

                            java.util.TimeZone tz = java.util.TimeZone.getDefault();
                            int offset = tz.getOffset(new Date(0).getTime());
                            value = (long) value;
                            Date date = new Date((long) (value * 1000) + offset + 1500000);

                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat);

                            String formattedDate = sdf.format(date);

                            return formattedDate;
                        } else {
                            Log.d("time4", value + "");
                            return super.formatLabel(value, isValueX);
                        }
                    }

                });


            } catch (IndexOutOfBoundsException e) {

                Log.d("inGrap", "catch if triggered");
            }

        }
        else{
              Log.d("TestP" , "False Graph plotter called");
              graph1.setVisibility(View.INVISIBLE);
              graphLoaderBar.setVisibility(View.GONE);
              TextView graphErrorText = (TextView)getView().findViewById(R.id.graphErrorText);
              graphErrorText.setVisibility(View.VISIBLE);
              graphErrorText.setText("График для валютной пары " + fsym  + "/" + tsym  + " недоступен для биржи " + currentMarket);//H--C

        }

    }

}
