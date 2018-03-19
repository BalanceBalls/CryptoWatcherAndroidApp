package com.example.admin.cryptowatcher;

/**
 * Created by Alex on 12.09.2017.
 */

public class CurrencyHist {
    /*This class forms a structure for graph data*/
    private long utcTime;
    private double close;

    public CurrencyHist(long time, double value){

        utcTime = time;
        close = value;

    }

    public long getUtcTime(){
        return utcTime;
    }
    public double getCloseValue(){
        return close;
    }


}
