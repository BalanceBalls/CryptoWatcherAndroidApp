package com.example.admin.cryptowatcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

public class MainActivity extends Activity {


    ImageView splashImageBtc;
    TextView errorMessage;
    Button tryAgainButton;
    /*Splash screen Activity. Not finished at the moment*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);


        tryAgainButton = (Button) findViewById(R.id.try_again_button);
        errorMessage = (TextView) findViewById(R.id.splashscreen_error_msg);
        splashImageBtc = (ImageView) findViewById(R.id.splashImage);

       // SystemClock.sleep(3000);


        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);

                    startActivity(intent);
                    finish();
                }
            }
        });
        if(isNetworkConnected()){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);

            startActivity(intent);
            this.finish();

        }else {
            errorMessage.setVisibility(View.VISIBLE);
            tryAgainButton.setVisibility(View.VISIBLE);

        }
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
