package com.adi.exam.callbacks;

public interface ILocationCallback {

    void onLocationChange(double latitude, double longitude, double altitude, Object address);

    void onLocationFailed(Object address);

}
