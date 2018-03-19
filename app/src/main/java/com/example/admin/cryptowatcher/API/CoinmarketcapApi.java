package com.example.admin.cryptowatcher.API;

import android.util.Log;

import com.example.admin.cryptowatcher.Currencies;
import com.example.admin.cryptowatcher.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alex on 11.01.2018.
 */

public class CoinmarketcapApi {


    //JSON Tags Block
    private static final int LIMIT = 55;
    private static final String CURRENCY = "USD";
    private static final String API_URL = "https://api.coinmarketcap.com/v1/ticker/?limit=" + LIMIT;
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PRICE_BTC = "price_btc";
    private static final String MARKET_CAP_USD = "market_cap_usd";
    private static final String ONE_HOUR_SHIFT = "percent_change_1h";
    private static final String TWENTYFOUR_HOUR_SHIFT = "percent_change_24h";
    private static final String WEEK_CHANGE = "percent_change_7d";
    private static final String DAY_VOLUME = "24h_volume_usd";
    private static final String PRICE = "price_usd";
    private static final String SYMBOL = "symbol";



    //Logging tag
    private static final String TAG = "my_tag_home";//"Coinmarketcap_api_tag";


    HttpURLConnection urlConnectionCoinmarketcap = null;
    BufferedReader reader = null;
    String resultJson = "";
    InputStream inputStreamCoinmarketcap = null;
    private ArrayList<CoinmarketcapApi> Coinmarketcap_collection = new ArrayList<>();

    private void initBlock(){
        try {

            URL url = new URL(API_URL);//URL address for API
            connectServer(url);

            Log.d(TAG," URL connection GOT input stream");

            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStreamCoinmarketcap));
            if(reader.toString().isEmpty()){
                connectServer(url);
                inputStreamCoinmarketcap.close();
                Log.d("INPbuffer",reader.toString());
            }else {
                Log.d(TAG, " Created reader");
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);

                }
                Log.d(TAG, "data from API has been obtained");
                resultJson = buffer.toString();
                inputStreamCoinmarketcap.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {                                                                                  //parsing API request result

            JSONArray jsonArr = new JSONArray(resultJson);

            for(int i = 0; i < LIMIT; i++){
                parseToList(jsonArr.getJSONObject(i));
                //Log.d(TAG,"parseToList is called from AsyncTask");
            }
            Log.d(TAG,"Data moved to the arraylist");
        } catch (final JSONException e){

            Log.d(TAG,"Json exceptipon catched");

        }

    }

    private void connectServer(URL url) throws IOException {


        System.setProperty("http.keepAlive", "false");

        urlConnectionCoinmarketcap = (HttpURLConnection) url.openConnection();
        urlConnectionCoinmarketcap.setRequestMethod("GET");
        //urlConnectionCoinmarketcap.setRequestProperty("Accept", "application/json");
        urlConnectionCoinmarketcap.setReadTimeout(3000);
        urlConnectionCoinmarketcap.setConnectTimeout(3000);
        urlConnectionCoinmarketcap.connect();
        Log.d(TAG," URL COINCAP connection established");

        //InputSource is = new InputSource(urlConnectionCoinmarketcap.getContent().toString());
        //Log.d("URL_DEB",is.toString());
        inputStreamCoinmarketcap = urlConnectionCoinmarketcap.getInputStream();//Potential bitch

    }

    public CoinmarketcapApi(boolean init){

        if(init){
            initBlock();
        }
        Log.d(TAG,"Init block is called");

    }


    private String checkIfNull(String value){
        if(value == "null"){
            return "0";
        }else{ return value;}

    }

    public void parseToList(JSONObject obj){

        try {

            float oneHourShift = Float.parseFloat(checkIfNull(obj.getString(ONE_HOUR_SHIFT)));
            float twentyFourHourShift = Float.parseFloat(checkIfNull(obj.getString(TWENTYFOUR_HOUR_SHIFT)));
            float dayVolume = Float.parseFloat(checkIfNull(obj.getString(DAY_VOLUME)));
            float buyPrice = Float.parseFloat(checkIfNull(obj.getString(PRICE)));
            double btcPrice = Double.parseDouble(obj.getString(PRICE_BTC));
            float weekChange = Float.parseFloat(checkIfNull(obj.getString(WEEK_CHANGE)));
            long marketCap = (obj.getLong(MARKET_CAP_USD));
           //long utcTime = (obj.getLong(UTC_TIME));
            String symbol = obj.getString(SYMBOL);
            String pairName = obj.getString(ID);
            symbol = symbol.toUpperCase();

            Currencies pair = new Currencies(pairName,oneHourShift,twentyFourHourShift,dayVolume,
                    buyPrice,symbol + "/USD",btcPrice,marketCap,weekChange,"Coinmarketcap");//String name,float hourChange, float dayChange, float dayVolume, float currentPrice,
            // String shortName,float btcPrice, long marketCap , float weekChange, String utcTime
           // Log.d(TAG,"Pair formed : " + pairName);


            HomeActivity.API_COLLECTION.add(pair);

        }catch (final JSONException e){

        }


    }



}
