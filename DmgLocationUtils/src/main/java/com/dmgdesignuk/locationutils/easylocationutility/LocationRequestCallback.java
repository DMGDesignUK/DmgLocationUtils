package com.dmgdesignuk.locationutils.easylocationutility;

import android.location.Location;


/**
 * A callback used by EasyLocationUtility to communicate the results
 * of a location request. Must be passed as a parameter to the calling
 * method.
 */
public interface LocationRequestCallback {

    void onLocationResult(Location location);
    void onFailedRequest(String result);

}
