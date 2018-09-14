package com.dmgdesignuk.locationutils.easylocationutility;

import android.location.Location;


public interface LocationRequestCallback {

    void onLocationResult(Location location);
    void onFailedRequest(String result);

}
