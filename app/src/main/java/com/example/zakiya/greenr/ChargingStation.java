package com.example.zakiya.greenr;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Arize on 4/3/2017.
 */
@IgnoreExtraProperties
public class ChargingStation {

    private String stationName;
    private String location;
    private int level;
    private boolean membershipRequired;

    public ChargingStation(){
    }

    public ChargingStation(String stationName, String location, int level, boolean membershipRequired) {
        this.stationName = stationName;
        this.location = location;
        this.level = level;
        this.membershipRequired = membershipRequired;
    }


    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isMembershipRequired() {
        return membershipRequired;
    }

    public void setMembershipRequired(boolean membershipRequired) {
        this.membershipRequired = membershipRequired;
    }

    @Override
    public String toString() {
        return "ChargingStation{" +
                "stationName='" + stationName + '\'' +
                ", location='" + location + '\'' +
                ", level=" + level +
                ", membershipRequired=" + membershipRequired +
                '}';
    }
}
