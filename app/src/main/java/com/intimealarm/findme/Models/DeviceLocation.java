package com.intimealarm.findme.Models;

import java.io.Serializable;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 01/03/2017.
 */

public class DeviceLocation implements Serializable {
    private double lat, lng;
    long time;
    String lable;

    public DeviceLocation() {
    }

    public DeviceLocation(double lat, double lng, long time, String lable) {

        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.lable = lable;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
