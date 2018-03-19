package com.example.admin.cryptowatcher;

/**
 * Created by Alex on 05.02.2018.
 */

public class NotificationEntity {
    /*This class describes a notification*/

    private String marketName;
    private double supportLevel;
    private double resistanceLevel;
    private String pairName;
    private int waitTimeSeconds;

    public NotificationEntity(String market, String pair , double supLvl, double resLvl, int waitSeconds){
        marketName = market;
        pairName = pair;
        supportLevel = supLvl;
        resistanceLevel =  resLvl;
        waitTimeSeconds = waitSeconds;
    }


    public String getMarketName() {
        return marketName;
    }

    public double getSupportLevel() {
        return supportLevel;
    }

    public double getResistanceLevel() {
        return resistanceLevel;
    }

    public String getPairName() {
        return pairName;
    }

    public int getWaitTimeSeconds() {
        return waitTimeSeconds;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof NotificationEntity)) {
            return false;
        }

        NotificationEntity ent = (NotificationEntity) obj;

        return ent.marketName.equals(marketName) &&
                ent.pairName.equals(pairName) &&
                ent.resistanceLevel == (resistanceLevel) &&
                ent.supportLevel == supportLevel &&
                ent.waitTimeSeconds == waitTimeSeconds;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + marketName.hashCode();
        result = 31 * result + pairName.hashCode();
        result = 31 * result + String.valueOf(resistanceLevel).hashCode();
        result = 31 * result + String.valueOf(supportLevel).hashCode();
        result = 31 * result + String.valueOf(waitTimeSeconds).hashCode();
        return result;
    }


}
