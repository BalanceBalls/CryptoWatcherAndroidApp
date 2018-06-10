package com.example.admin.cryptowatcher;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Alex on 12.02.2018.
 *This class works with I/O operations on a notification list.
 *Data is stored in JSON form in a file
 *in order to remain independent from the app.
 *
 */

public class NotificationIO  {


    private ArrayList<NotificationEntity> finalList = new ArrayList<>();

    public ArrayList<NotificationEntity> getList(){
        return  finalList;
    }

    //This constructor is used to get data from file
    public NotificationIO (Context ctx){
        try {
            readFromFile(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This constructor is used for saving record to file or deleting one
    public NotificationIO (Context ctx, NotificationEntity ent,String action){
            switch (action) {
                case "write":   saveToFile(ent, ctx); break;
                case "delete": deleteFromFile(ent,ctx); break;
            }
    }

    private void deleteItemFromFile(Context ctx){
        JSONArray inputData = formJSON(finalList, ctx);

        File inpFile =  new File(ctx.getFilesDir(), "alarmlist.json");

        try { FileOutputStream fos =  new FileOutputStream(inpFile,false);
            try {fos.write(inputData.toString().getBytes());
            }finally {fos.close();}
        } catch (IOException e) { }
    }
    //Saves new notification to a file
    private void saveToFile(NotificationEntity alarmEnt, Context ctx){
        try {
            readFromFile(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finalList.add(alarmEnt);
        JSONArray inputData = formJSON(finalList, ctx);

        File inpFile =  new File(ctx.getFilesDir(), "alarmlist.json");

        try { FileOutputStream fos =  new FileOutputStream(inpFile,false);
            try {fos.write(inputData.toString().getBytes());
            }finally {fos.close();}
        } catch (IOException e) { }
    }
    private JSONArray formJSON(ArrayList<NotificationEntity> alarms , Context ctx){

       // finalList.addAll(alarms);
        JSONArray dataList = new JSONArray();
            for(NotificationEntity ent : finalList){
                JSONObject temp = new JSONObject();

                try {
                    temp.put("market" , ent.getMarketName());
                    temp.put("pair", ent.getPairName());
                    temp.put("supportlevel" , ent.getSupportLevel());
                    temp.put("resistlevel", ent.getResistanceLevel());
                    //temp.put("waittime", ent.getWaitTimeSeconds());
                    dataList.put(temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return dataList;
    }

    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    //This method forms a list of type NotificationEntity from JSON data
    //loaded from a file
    private void parseToList(String data) {

        JSONArray root = null;
        try {
            root = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!finalList.isEmpty()){ finalList.clear(); }

        for (int i = 0; i < root.length(); i++) {
            try {

                JSONObject temp = root.getJSONObject(i);
                String market =  temp.getString("market");
                String pair = temp.getString("pair");
                double sup = Double.parseDouble(temp.getString("supportlevel"));
                double res = Double.parseDouble(temp.getString("resistlevel"));
               // int time = Integer.parseInt(temp.getString("waittime"));
                NotificationEntity tempEnt =  new NotificationEntity(market,pair,sup,res);
                finalList.add(tempEnt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.d(TAG,"parseToList is called from AsyncTask");
        }
    }

    private void deleteFromFile(NotificationEntity deleteEntry, Context ctx){
        try {
            readFromFile(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int index = 0;
        boolean hasEntry = false;
        Log.d("IOcheck", "delete called for " + deleteEntry.getPairName());
        for(NotificationEntity ent : finalList){

            Log.d("IOcheck", "Going through " + ent.getPairName());
            if(ent.getPairName().equals(deleteEntry.getPairName()) & ent.getResistanceLevel() == deleteEntry.getResistanceLevel() & ent.getSupportLevel() == deleteEntry.getSupportLevel() & ent.getMarketName().equals(deleteEntry.getMarketName())){
                Log.d("IOcheck", "Found entry for " + deleteEntry.getPairName() + " , At index " + index + " ; Test indexOf = " + finalList.indexOf(ent));
                hasEntry = true;
                index = finalList.indexOf(ent);
                break;
            }
        }
        if(hasEntry) {
            finalList.remove(index);
            deleteItemFromFile(ctx);
            index = 0;
            hasEntry = false;

        }

    }

    private void readFromFile(Context ctx) throws IOException {
        StringBuffer data = new StringBuffer();
        File inpFile =  new File(ctx.getFilesDir(), "alarmlist.json");
        BufferedReader reader = new BufferedReader(new FileReader(inpFile));

        String line;

        while ((line = reader.readLine()) != null) {
            data.append(line);
        }
        Log.d("IOcheck", "File contains" + data);

        parseToList(data.toString());
        reader.close();
        for (NotificationEntity entTemp : finalList){
            Log.d("IOcheck", "Market = " + entTemp.getMarketName()  + " , Pair = " + entTemp.getPairName()  +
                    " , sup = " + entTemp.getSupportLevel()  + " , res= " + entTemp.getResistanceLevel() + ", List length = " + finalList.size());
        }
    }
}
