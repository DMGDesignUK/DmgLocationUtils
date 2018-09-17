package com.dmgdesignuk.locationutils.easyaddressutility;

import android.location.Address;
import java.util.List;


/**
 * A callback used by EasyAddressUtility to communicate the results
 * of a reverse Geocoding request. Must be passed as a parameter to the calling
 * method.
 */
public interface AddressResultCallback {

    void onAddressSuccessfulResult(List<Address> addressList);
    void onAddressFailedResult(String result);

}
