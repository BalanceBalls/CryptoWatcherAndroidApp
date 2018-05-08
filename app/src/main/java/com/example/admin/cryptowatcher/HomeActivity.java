package com.example.admin.cryptowatcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.cryptowatcher.API.BittrexApi;
import com.example.admin.cryptowatcher.API.CoinmarketcapApi;
import com.example.admin.cryptowatcher.API.ExmoApi;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseDrawerItem;
import com.mikepenz.materialdrawer.model.ContainerDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeActivity extends AppCompatActivity {


    Drawer result = null;
    public long updateTime = 0;
    private String CurrentMarket = "Coinmarketcap";
    ListView lvMain;
    public static ArrayList<Currencies> API_COLLECTION; //List for parsed data from API
    listAdapter listAdapter;

    private static final String TAG = "my_tag_home";                                               //tag for logs
    private SwipeRefreshLayout swipeContainer;

    boolean isFirstOpen = true;
    //checks if activity is opened of the first time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        API_COLLECTION = new ArrayList<>();
        lvMain = (ListView) findViewById(R.id.homeList);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Toolbar init block
        if(toolbar != null){
            toolbar.setTitle(CurrentMarket);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        //NavigationDrawer init block
        AccountHeader headerResult = getAccountHeader();

        setDrawer(toolbar, headerResult);

        Log.d(TAG, "OnCreate is called");

        if(API_COLLECTION.isEmpty()){

         new AsyncTaskRunner(true, CurrentMarket).execute();

            Log.d(TAG,"ON empty API_COLLECTION triggered");
        }
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String pairName = API_COLLECTION.get(position).getABBR();

                Intent detailScreen = new Intent(HomeActivity.this, DetailActivity.class);
                detailScreen.putExtra("pairName", pairName);

                detailScreen.putExtra("marketName", CurrentMarket);
                Log.d("transitString",CurrentMarket + "1");
                startActivity(detailScreen);

            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeContainer.setDistanceToTriggerSync(200);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(System.currentTimeMillis() - updateTime <  60000){
                Toast.makeText(getBaseContext(), R.string.update_unnecessary_msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Cancel event called");
                swipeContainer.setRefreshing(false);
                }
                else {
                    new AsyncTaskRunner(false, CurrentMarket).execute();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    protected void setDrawer(final Toolbar toolbar, AccountHeader headerResult) {
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .withSliderBackgroundColor(getResources().getColor(R.color.navDrawerColor))
                .withTranslucentNavigationBar(true)

                .addDrawerItems(
                        new SectionDrawerItem()
                                .withName(R.string.menu_markets_avaliable_text).withIdentifier(19)
                                .withTextColor(getResources().getColor(R.color.navDrawerItemColor))

                                .withDivider(false),


                        new SecondaryDrawerItem().withName(R.string.bittrex_text).withLevel(2).withIdentifier(2).withIcon(R.mipmap.ic_star_rate_black_18dp).withTextColor(getResources().getColor(R.color.navDrawerItemColor)),
                        new SecondaryDrawerItem().withName(R.string.exmo_text).withLevel(2).withIdentifier(3).withIcon(R.mipmap.ic_star_rate_black_18dp).withTextColor(getResources().getColor(R.color.navDrawerItemColor)),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem()
                                .withTextColor(getResources().getColor(R.color.navDrawerItemColor))
                                .withName(R.string.coinmarketcap_text)
                                .withIcon(getDrawable(R.mipmap.ic_list_black_24dp))

                                .withLevel(1)
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withTextColor(getResources().getColor(R.color.navDrawerItemColor))
                                .withName(R.string.news_menu_item_text)
                                .withIcon(getDrawable(R.mipmap.ic_chrome_reader_mode_black_18dp))
                                .withLevel(1)
                                .withIdentifier(4),
                        new PrimaryDrawerItem()
                                .withTextColor(getResources().getColor(R.color.navDrawerItemColor))
                                .withIcon(getDrawable(R.mipmap.ic_alarm_black_18dp))
                                .withName(R.string.alarms_menu_item_text)
                                .withLevel(1)
                                .withIdentifier(5),
                        new PrimaryDrawerItem()
                                .withTextColor(getResources().getColor(R.color.navDrawerItemColor))
                                .withIcon(getDrawable(R.mipmap.ic_list_black_24dp))
                                .withName("Логи")
                                .withLevel(1)
                                .withIdentifier(6),
                        new PrimaryDrawerItem()
                                .withTextColor(getResources().getColor(R.color.navDrawerItemColor))
                                .withIcon(getDrawable(R.mipmap.ic_list_black_24dp))
                                .withName("Удалить логи")
                                .withLevel(1)
                                .withIdentifier(7)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                         switch ((int) drawerItem.getIdentifier()){
                             case 1:
                                 drawerItem.withSetSelected(true);
                                 new AsyncTaskRunner(true, "Coinmarketcap").execute();
                                 CurrentMarket = getString(R.string.coinmarketcap_text);
                                 toolbar.setTitle(CurrentMarket);
                                 break;
                             case 2:
                                 drawerItem.withSetSelected(true);
                                 new AsyncTaskRunner(true, "Bittrex").execute();
                                 CurrentMarket = getString(R.string.bittrex_text);
                                 toolbar.setTitle(CurrentMarket);
                                 break;
                             case 3:
                                 drawerItem.withSetSelected(true);
                                 new AsyncTaskRunner(true, "Exmo").execute();
                                 CurrentMarket = getString(R.string.exmo_text);
                                 toolbar.setTitle(CurrentMarket);
                                 break;
                             case 4:
                                Intent intent = new Intent(HomeActivity.this, NewsActivity.class);
                                startActivity(intent);
                                break;
                             case 5:
                                 Intent intent1 = new Intent(HomeActivity.this, AlarmMain.class);
                                 startActivity(intent1);
                                 break;
                             case 6:
                                 Intent logIntent = new Intent(HomeActivity.this,LogActivity.class);
                                 startActivity(logIntent);
                                 break;
                             case 7:
                                 String inputData = "--- Logs cleared ---";
                                 File inpFile =  new File(getApplicationContext().getFilesDir(), "logserv");

                                 try { FileOutputStream fos =  new FileOutputStream(inpFile,false);
                                     try {fos.write(inputData.getBytes());
                                     }finally {fos.close(); Toast.makeText(getApplicationContext(),"Logs cleared", Toast.LENGTH_SHORT).show();}
                                 } catch (IOException e) { }
                                 break;
                         }
                        return  false;
                    }
                })
                .build();
    }
    final IProfile profile = new ProfileDrawerItem()
            .withName("Pied Piper")
            .withEmail("potniyDeveloper@gmail.com")
            .withIcon(R.drawable.bighead)
            .withIdentifier(100);
    protected AccountHeader getAccountHeader() {
        return new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.color.navDrawerHeaderColor)
                    .addProfiles(profile)
                    .withTextColor(getResources().getColor(R.color.md_white_1000))
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                            return false;
                        }
                    })

                    .build();
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Integer, String> {

        /*This class obtains data from API in a background thread.*/

        ProgressDialog mProgressDialog;     //progress dialog init for updating rates'values

        private boolean showProgressDialog = true;
        private String Market = null;
        public AsyncTaskRunner(boolean showLoading, String getMarket) {
            super();
            Market = getMarket;
            showProgressDialog = showLoading;
        }


        @Override
        protected void onPreExecute() {
            Log.d(TAG," PRE_EXECUTE CALLED");
            API_COLLECTION.clear();
            if(showProgressDialog) {
                //Initializing ProgressDialog
                mProgressDialog = new ProgressDialog(
                        HomeActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);                    //set style for progressDialog
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);                        //set spinner
                mProgressDialog.setMessage(getString(R.string.loading_pls_wait_text));//H--C
                mProgressDialog.setCanceledOnTouchOutside(false);

                mProgressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG," DO_IN_BACKGROUND CALLED");
            String result = "failure";
            //Getting data from API
            try {

                    switch (Market) {
                        case "Coinmarketcap":
                            CoinmarketcapApi temp = new CoinmarketcapApi(true);
                            break;
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

            Log.d(TAG," ON_POST_EXECUTE CALLED");
            //Updating listView and setting adapter
            listAdapter = new listAdapter(getApplicationContext(), HomeActivity.API_COLLECTION);
            lvMain.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

            //Setting time mark for update restriction
            String formattedDate = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            //updateTextView.setText("Обновлено: " + formattedDate);
            getSupportActionBar().setSubtitle(getString(R.string.updated_at_text) + formattedDate);

            updateTime = System.currentTimeMillis();

            Log.d(TAG,"onPostExecute is called from AsyncTask");
            if(showProgressDialog) {

                mProgressDialog.dismiss();

            }
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    protected void onStart() {
       super.onStart();
        Log.d(TAG,"onStart is called");

    }


    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed is called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkConnected()){
            Intent splashIntent =  new Intent(HomeActivity.this,MainActivity.class);
            startActivity(splashIntent);
        }
        Log.d(TAG,"onResume is called ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        result.closeDrawer();
        Log.d(TAG, "on Pause Home called");
        Log.d(TAG, "HomeActivity: onPause()" + isFirstOpen);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "HomeActivity: onStop()" + isFirstOpen);
    }
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "HomeActivity: onDestroy()" );
    }


}




