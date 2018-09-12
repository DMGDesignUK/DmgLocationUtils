package com.dmgdesignuk.locationutils.easyaddressutility;

import android.location.Address;
import java.util.List;


public interface AddressResultCallback {

    void onAddressSuccessfulResult(List<Address> addressList);
    void onAddressFailedResult(String result);

}
