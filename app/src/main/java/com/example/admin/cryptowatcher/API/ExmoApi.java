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
 * Created by Alex on 12.01.2018.
 */

public class ExmoApi {

    //JSON tags block


    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DAY_VOLUME = "vol";
    private static final String PRICE = "last_trade";
    private static final String SYMBOL = "MarketName";



    private HttpURLConnection urlConnectionExmo = null;
    private BufferedReader reader = null;
    private String resultJson = "";
    private InputStream inputStreamExmo = null;

    private static final String TAG = "Exmo_api";//"Coinmarketcap_api_tag";

    private static final String API_URL = "https://api.exmo.me/v1/ticker/";
    private void initBlock(){
        try {

            URL url = new URL(API_URL);//URL address for API
            System.setProperty("http.keepAlive", "false");
            urlConnectionExmo = (HttpURLConnection) url.openConnection();
            urlConnectionExmo.setRequestMethod("GET");
            urlConnectionExmo.connect();
            Log.d(TAG," URL EXMO connection established");

            inputStreamExmo = urlConnectionExmo.getInputStream();//Potential bitch

            Log.d(TAG," URL connection GOT input stream");

            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStreamExmo));

            Log.d(TAG," Created reader");
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);

            }
            Log.d(TAG,"data from API has been obtained");
            resultJson = buffer.toString();
            inputStreamExmo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {                                                                                  //parsing API request result

            JSONObject obj = new JSONObject(resultJson);

            for(int i = 0; i < obj.length(); i++){
                parseToList(obj.getJSONObject(obj.names().get(i).toString()),obj.names().get(i).toString());

            }
            Log.d(TAG,"Data moved to the arraylist");
        } catch (final JSONException e){

            Log.d(TAG,"Json exceptipon catched");

        }

    }
    private void parseToList(JSONObject obj, String name) {

        try {


            float dayVolume = Float.parseFloat(checkIfNull(obj.getString(DAY_VOLUME)));
            float buyPrice = Float.parseFloat(checkIfNull(obj.getString(PRICE)));

            String marketName = name.replace('_','/');


            float weekChange = (float) -2.85;
            Currencies pair = new Currencies("", (float)100500, (float)1.98, dayVolume,
                    buyPrice, marketName, 1332, 4446, weekChange, "Exmo");
            //String name,float hourChange, float dayChange, float dayVolume, float currentPrice,
            // String shortName,float btcPrice, long marketCap , float weekChange, String utcTime



            HomeActivity.API_COLLECTION.add(pair);

        } catch (final JSONException e) {

        }
    }
    private String checkIfNull(String value) {
        if (value == "null") {
            return "0";
        } else {
            return value;
        }

    }
    public ExmoApi(boolean init){

        if(init){
            initBlock();
        }
        Log.d(TAG,"Init block is called");



    }
}
