package com.dmgdesignuk.locationutils.easylocationutility;

import android.location.Location;


public interface DeviceLocationCallback {

    void onLocationResult(Location location);
    void onFailedRequest(String result);

}
