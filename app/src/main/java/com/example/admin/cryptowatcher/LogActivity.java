package com.example.admin.cryptowatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LogActivity extends AppCompatActivity {

    TextView logsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        StringBuffer data = new StringBuffer();
        File inpFile =  new File(getApplicationContext().getFilesDir(), "logserv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inpFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("IOcheck", "File contains" + data);

        logsView = (TextView) findViewById(R.id.logs_view);

        logsView.setText(data);
    }
}
