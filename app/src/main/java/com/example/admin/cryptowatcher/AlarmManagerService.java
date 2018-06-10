package com.example.admin.cryptowatcher;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Alex on 20.02.2018.
 */

public class AlarmManagerService extends Service {
    /*This service checks stock prices and sends notifications if requirements are met*/

    private final int SERVICE_ID = 4094;


    NotificationManager notificationManager;
    ArrayList<Integer> removeIndex = new ArrayList<>() ;//
    double tempPriceValue;
    private final AtomicBoolean isAllowed = new AtomicBoolean(false);
    private final static String EXMO_API_URL = "https://api.exmo.me/v1/ticker/";
    private final static String BITTREX_API_URL = "https://bittrex.com/api/v1.1/public/getticker?market=";

    ArrayList<NotificationEntity> alarmList = new ArrayList<>();

    private final String TAG = "alarmMan"; // Tag used for logs


    private Object mPauseLock = new Object();
    private boolean mPaused;
    private boolean mFinished;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       if(isNetworkConnected()) {

           new ReadFromFile().execute();
           String formattedDate = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
           String inputData = "On start command called at " + formattedDate;
           Log.d(TAG, "On start command called ");
           File inpFile = new File(getApplicationContext().getFilesDir(), "logserv");

           try {
               FileOutputStream fos = new FileOutputStream(inpFile, true);
               try {
                   fos.write(inputData.toString().getBytes());
               } finally {
                   fos.close();
               }
           } catch (IOException e) {
           }
       }else {
            scheduleAlarms(false);
       }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60000,
                PendingIntent.getService(getApplicationContext(), SERVICE_ID, new Intent(this, AlarmManagerService.class), 0)
        );
    }


    private boolean BittrexCheck(String pair, double sup, double res) throws IOException{
        boolean state = false;
        Log.d(TAG, "BittrexCheck is called");


        pair = pair.replace('/','-');
        Log.d(TAG , " Pair NAME TRNSFRMD (BTRX) = " + pair);
        String data = connectToApi(BITTREX_API_URL + pair);

        JSONObject root = null;

        try {

            root = new JSONObject(data);

            JSONObject result = root.getJSONObject("result");

            double tempValBtrx = result.getDouble("Last");

            Log.d(TAG, "Current API value (Bittrex)  - " + tempValBtrx);

            if (tempValBtrx >= res){
                sendNotif("Пробит уровень сопротивления (Bittrex)", pair, tempValBtrx , "Bittrex");
                Log.d(TAG, "Level event triggered (Bittrex)! , API result - "  + tempValBtrx + "  , resistance level - " + res);
                state = true;
                tempPriceValue = tempValBtrx;

            }else if( tempValBtrx <= sup){
                sendNotif("Пробит уровень поддержки (Bittrex)", pair, tempValBtrx , "Bittrex");
                Log.d(TAG, "Level event triggered (Bittrex)! , API result - "  + tempValBtrx + "  , support level - " + sup);
                state = true;
                tempPriceValue = tempValBtrx;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return state;
    }

    private boolean ExmoCheck(String pair, double sup, double res) throws IOException {

        boolean state = false;
        Log.d(TAG , "ExmoCheck is called");
        String data = connectToApi(EXMO_API_URL);

        pair = pair.replace('/','_');
        Log.d(TAG , " Pair NAME TRNSFRMD (EXMO) = " + pair);
        JSONObject obj = null;
        try {
            obj = new JSONObject(data);


            for(int i = 0; i < obj.length(); i++){

                if( obj.names().get(i).toString().equals(pair)){
                    JSONObject temp = obj.getJSONObject((String) obj.names().get(i));
                    double tempVal = Double.parseDouble(temp.getString("sell_price"));

                    Log.d(TAG, "Current API value (EXMO) - " + tempVal);

                    if (tempVal >= res){
                        sendNotif("Пробит уровень сопротивления (Exmo)", pair, tempVal , "Exmo");
                        Log.d(TAG, "Level event triggered (Exmo)! , API result - "  + tempVal + "  , resistance level - " + res);
                        state = true;
                        tempPriceValue = tempVal;
                        break;
                    }else if( tempVal <= sup){
                        sendNotif("Пробит уровень поддержки (Exmo)", pair, tempVal , "Exmo");
                        Log.d(TAG, "Level event triggered (Exmo)! , API result - "  + tempVal + "  , support level - " + sup);
                        state = true;
                        tempPriceValue = tempVal;
                        break;
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return state;
    }
    //this method disables alarmManager if we have no notifications
    private void cancelAlarm (){
        AlarmManager alarmManagerService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent cancelIntent = new Intent(getApplicationContext(),
                AlarmManagerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                getApplicationContext(), SERVICE_ID, cancelIntent, 0);
        if(pendingIntent!= null) {
            alarmManagerService.cancel(pendingIntent);

        }
    }

    private void scheduleAlarms(boolean isConnected){
        String inputData = "";

        android.app.AlarmManager manager = (android.app.AlarmManager) getSystemService(
                Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmManagerService.class);
        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), SERVICE_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);//consider using FLAG_UPDATE_CURRENT
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        if (isConnected) {

            long time = calendar.getTimeInMillis();

            if (Build.VERSION.SDK_INT >= 23) {
                manager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 65000, alarmIntent);
                inputData = "Scheduled new alarm for SDK > 23; ";
            } else {
                manager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 65000, alarmIntent);
                inputData = "Scheduled new alarm for SDK < 23; ";
            }
            String formattedDate = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            inputData = inputData + " at " + formattedDate;
        }else{
            if (Build.VERSION.SDK_INT >= 23) {
                manager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 105000, alarmIntent);
                inputData = "Set long alarm for SDK > 23; ";
            } else {
                manager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 105000, alarmIntent);
                inputData = "Set long alarm for SDK < 23; ";
            }
        String formattedDate = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        inputData = inputData + " Not connected at the moment : " + formattedDate;


        }
        File inpFile = new File(getApplicationContext().getFilesDir(), "logserv");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(inpFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

            try {
                fos.write(inputData.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
         finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }
    private String connectToApi(String API_URL) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        InputStream inputStream = null;

        URL url = new URL(API_URL);//URL address for API

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        inputStream = urlConnection.getInputStream();//Potential bitch

        StringBuffer buffer = new StringBuffer();

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        resultJson = buffer.toString();
        inputStream.close();
        return resultJson;
    }
    void sendNotif(String msg, String pair ,double apiPrice, String market) {

        Notification.Builder notificationBuilder = new Notification.Builder(this);

        notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        notificationBuilder.setTicker(market + ", " + pair);
        notificationBuilder.setWhen(System.currentTimeMillis());

        Intent intent = new Intent(this, HomeActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        notificationBuilder.setContentTitle(pair + " , Цена : " + apiPrice);
        notificationBuilder.setContentText(msg);
        notificationBuilder.setContentIntent(pIntent);
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setVibrate(new long[] {20,30,40});
        notificationBuilder.setAutoCancel(true);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        notificationBuilder.setSound(notificationSound);
        notificationBuilder.build();
        Notification notification = notificationBuilder.getNotification();
        Random ran = new Random();
        int randomId = ran.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(randomId, notification);

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DoScanStocks extends AsyncTask<Void,Void,Void>{

        ArrayList<NotificationEntity> pairList =  new ArrayList<>();
        ArrayList<Integer> removeIndexAsync = new ArrayList<>() ;//
        public DoScanStocks(ArrayList<NotificationEntity> list) {

            this.pairList = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String formattedDate = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            String inputData = "Thread is running at " + formattedDate ;
            File inpFile =  new File(getApplicationContext().getFilesDir(), "logserv");

            try { FileOutputStream fos =  new FileOutputStream(inpFile,true);
                try {fos.write(inputData.toString().getBytes());
                }finally {fos.close();}
            } catch (IOException e) { }
        }
        protected Void doInBackground(Void... params){

            for (NotificationEntity alarmEntity : alarmList) {
                try {
                    Log.d(TAG, "Going through alarmList");
                    switch (alarmEntity.getMarketName()) {
                        case "Exmo": {

                            if (ExmoCheck(alarmEntity.getPairName(), alarmEntity.getSupportLevel(), alarmEntity.getResistanceLevel())) {
                                removeIndexAsync.add(alarmList.indexOf(alarmEntity));
                                Log.d(TAG, " Remove index is set to " + removeIndex + " for " + alarmEntity.getPairName());
                            }

                            break;
                        }
                        case "Bittrex": {
                            if (BittrexCheck(alarmEntity.getPairName(), alarmEntity.getSupportLevel(), alarmEntity.getResistanceLevel())) {
                                removeIndexAsync.add(alarmList.indexOf(alarmEntity));
                                Log.d(TAG, " Remove index is set to " +
                                        removeIndexAsync + " for " + alarmEntity.getPairName());
                            }
                            break;
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!removeIndexAsync.isEmpty()) {
                //If we have items in removeIndex array, then we delete those items from the storage file
                for (int i : removeIndexAsync) {

                    Log.d(TAG, " Record at " + i + " has been removed , " + alarmList.get(i).getPairName() + " , pair's index in the list is " + alarmList.indexOf(alarmList.get(i)));

                    new DeleteFromFile(i).execute();
                }


                for (NotificationEntity q : alarmList) {
                    Log.d(TAG, " Alarm queue :" + q.getMarketName() + " ," +
                            " " + q.getPairName() + " / Remove Index = " + removeIndexAsync);
                }

                Log.d(TAG, " Removing is done");
                removeIndex.clear();
            }
            if (!alarmList.isEmpty()){
               /* android.app.AlarmManager manager = (android.app.AlarmManager) getSystemService(
                        Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmManagerService.class);
                PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(),SERVICE_ID,intent,PendingIntent.FLAG_CANCEL_CURRENT);//consider using FLAG_UPDATE_CURRENT
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 10);
                long time = calendar.getTimeInMillis();
                String inputData = "" ;
                if (Build.VERSION.SDK_INT >= 23){
                    manager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 65000 , alarmIntent);
                    inputData = "Scheduled new alarm for SDK > 23";
                }else {
                    manager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 65000, alarmIntent);
                    inputData = "Scheduled new alarm for SDK < 23";
                }
                String formattedDate = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                inputData = inputData + " at " + formattedDate;
                File inpFile =  new File(getApplicationContext().getFilesDir(), "logserv");

                try { FileOutputStream fos =  new FileOutputStream(inpFile,true);
                    try {fos.write(inputData.toString().getBytes());
                    }finally {fos.close();}
                } catch (IOException e) { }*/
               scheduleAlarms(true);
            }
        }
    }
    class ReadFromFile extends AsyncTask<Void, Void, Void> {

        NotificationIO dataList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            dataList = new NotificationIO(getApplicationContext());

            //Wait until view gets inflated
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
          return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(TAG,"OnPostExecute was called");
            alarmList = dataList.getList();

            if(!alarmList.isEmpty()){

                new DoScanStocks(alarmList).execute();
            }else{
                mFinished = true;
                //onResumeThread();

                Log.d(TAG,"Do work is false, stopping service");
                cancelAlarm();
            }
        }
    }
    class DeleteFromFile extends AsyncTask<Void, Void, Void> {

        NotificationIO dataList;
        NotificationEntity removeRecord;
        int remIndex;
        DeleteFromFile (int i){
            this.remIndex = i;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            new NotificationIO(getApplicationContext(), alarmList.get(remIndex), "delete");// need to implement AsyncTask

            //Wait until view gets inflated
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(TAG,"OnPostExecute of Delete thread was called");

            alarmList.remove(remIndex);
        }
    }
}
