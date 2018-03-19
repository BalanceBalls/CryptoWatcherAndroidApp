package com.example.admin.cryptowatcher.API;

import android.util.Log;

import com.example.admin.cryptowatcher.Currencies;
import com.example.admin.cryptowatcher.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alex on 11.01.2018.
 */

public class BittrexApi {


    //JSON Tags Block
    private static final int LIMIT = 55;
    private static final String CURRENCY = "USD";
    private static final String API_URL = "https://bittrex.com/api/v1.1/public/getmarketsummaries";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DAY_VOLUME = "Volume";
    private static final String PRICE = "Last";
    private static final String SYMBOL = "MarketName";
   // private static final String UTC_TIME = "last_updated";

    private static final String TAG = "BittrexApiTag";


    HttpURLConnection urlConnectionBittrex = null;
    BufferedReader reader = null;
    String resultJson = "";

    private String checkIfNull(String value) {
        if (value == "null") {
            return "0";
        } else {
            return value;
        }

    }
    public BittrexApi(boolean init){

        if(init){
            initBlock();
        }
        Log.d(TAG,"Init block is called");



    }

    private void initBlock(){
        try {

            URL url = new URL(API_URL);//URL address for API
            System.setProperty("http.keepAlive", "false");
            urlConnectionBittrex = (HttpURLConnection) url.openConnection();
            urlConnectionBittrex.setRequestMethod("GET");
            urlConnectionBittrex.connect();
            Log.d(TAG," URL Bittrex connection established");
            InputStream inputStreamBittrex = urlConnectionBittrex.getInputStream();

            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStreamBittrex));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            Log.d(TAG,"data from API has been obtained");
            resultJson = buffer.toString();
            inputStreamBittrex.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {                                                                                  //parsing API request result
            JSONObject root = new JSONObject(resultJson);
            JSONArray jsonArr = root.getJSONArray("result");

            for(int i = 0; i < jsonArr.length(); i++){
                parseToList(jsonArr.getJSONObject(i));
                //Log.d(TAG,"parseToList is called from AsyncTask");
            }
            Log.d(TAG,"Data moved to the arraylist");
        } catch (final JSONException e){

            Log.d(TAG,"Json exceptipon catched");

        }

    }
    public void parseToList(JSONObject obj) {

        try {


            Log.d("arrList",  " 1 ");
            float dayVolume = Float.parseFloat(checkIfNull(obj.getString(DAY_VOLUME)));
            float buyPrice = Float.parseFloat(checkIfNull(obj.getString(PRICE)));
            String marketName = obj.getString(SYMBOL);

            marketName = marketName.replace('-','/');
            float weekChange = (float) -2.85;
            Currencies pair = new Currencies("", (float)100500, (float)1.98, dayVolume,
                    buyPrice, marketName, 1332, 4446, weekChange, "Bittrex");
            //String name,float hourChange, float dayChange, float dayVolume, float currentPrice,
            // String shortName,float btcPrice, long marketCap , float weekChange, String utcTime



            HomeActivity.API_COLLECTION.add(pair);

        } catch (final JSONException e) {

        }
    }
}
