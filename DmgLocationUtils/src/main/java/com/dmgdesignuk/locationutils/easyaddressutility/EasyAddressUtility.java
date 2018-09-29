package com.dmgdesignuk.locationutils.easyaddressutility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.dmgdesignuk.locationutils.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * <h1>An abstraction layer around Android's Geocoder implementation</h1>
 *
 * <p>Provides an easy way to obtain the address information associated with a given
 *    Latitude or Longitutde from reverse geocoding by passing in int constants that
 *    correspond to a specific address line.</p>
 *
 * <p>A List object containing all address data can also be retrieved. The calling method
 *    should implement a new AddressResultCallback interface to receive the result.</p>
 *
 * <p>Part of the DmgLocationUtils library</p>
 * @see <a href="https://github.com/DMGDesignUK/DmgLocationUtils">DmgLocationUtils on Github</a>
 *
 * @author  Dave Gibbons (dave@dmgdesignuk.com)
 * @version 1.0.1
 * @since	2018-08-14
 */
public class EasyAddressUtility
{
    private static final String TAG = EasyAddressUtility.class.getSimpleName();


    /**
     * Static nested class defining int constants for use with reverse Geocoding of address data
     */
    public static class AddressCodes
    {
        public static final int ADMIN_AREA = 0;
        public static final int CITY_NAME = 1;
        public static final int COUNTRY_CODE = 2;
        public static final int COUNTRY_NAME = 3;
        public static final int FEATURE_NAME = 4;
        public static final int FULL_ADDRESS = 5;
        public static final int PHONE_NUMBER = 6;
        public static final int POST_CODE = 7;
        public static final int PREMISES = 8;
        public static final int STREET_NAME = 9;
        public static final int SUB_ADMIN_AREA = 10;
        public static final int SUB_THOROUGHFARE = 11;
    }// End AddressCodes class


    private Context mContext;
    private Geocoder mGeocoder;


    /**
     * Constructor.
     *
     * Takes a reference to a Context as a parameter.
     *
     * Instantiates a Geocoder object using the device's default locale. If a different
     * locale is required the changeGeocoderLocale() method can be used to achieve this.
     */
    public EasyAddressUtility(Context context){

        this.mContext = context;
        this.mGeocoder = new Geocoder(mContext, Locale.getDefault());

    }


    /**
     * Changes the Locale used by the Geocoder to format address information.
     *
     * @param   locale  A user-specified Locale object
     */
    public void changeGeocoderLocale(Locale locale){

        this.mGeocoder = new Geocoder(mContext, locale);

    }


    /**
     * Returns a List object containing the address information for the supplied
     * Location object. Will be null if no address found. The caller should
     * implement an AddressResultCallback to receive and handle the result.
     *
     * @param   location    A Location object containing latitude and longitude.
     * @param   callback    An interface which must be implemented by the caller in order to
     *		                receive the results of the request.
     */
    public void getAddressList(Location location, AddressResultCallback callback){

        List<Address> addressList = null;

        try {

            addressList = mGeocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    1); // We only want one address to be returned.

        } catch (IOException e) {
            // Catch network or other IO problems
            callback.onAddressFailedResult(mContext.getString(R.string.deviceLocationUtil_geocoder_not_available));
            return;
        } catch (IllegalArgumentException e) {
            // Catch invalid latitude or longitude values
            callback.onAddressFailedResult(mContext.getString(R.string.deviceLocationUtil_geocoder_invalid_latLong));
            return;
        }

        // Handle case where no address is found
        if (addressList == null || addressList.size() == 0) {
            callback.onAddressFailedResult(mContext.getString(R.string.deviceLocationUtil_geocoder_address_not_found));
        } else {
            // Return the address list
            callback.onAddressSuccessfulResult(addressList);
        }

    }


    /**
     * Returns a String containing the requested address element or null if not found
     *
     * @param   elementCode A package-defined int constant representing the specific
     *                      address element to return.
     * @param   location    A Location object containing a latitude and longitude.
     *
     * @return  String containing the requested address element if found, a reason for
     *          failure if necessary or null if address element doesn't exist.
     */
    public String getAddressElement(int elementCode, Location location){

        List<Address> addressList;
        Address address;
        String elementString = null;

        try {

            addressList = mGeocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    1); // We only want one address to be returned.

        } catch (IOException e) {
            // Catch network or other IO problems
            return mContext.getString(R.string.deviceLocationUtil_geocoder_not_available);
        } catch (IllegalArgumentException e) {
            // Catch invalid latitude or longitude values
            return mContext.getString(R.string.deviceLocationUtil_geocoder_invalid_latLong);
        }

        // Handle case where no address is found
        if (addressList == null || addressList.size() == 0){
            return mContext.getString(R.string.deviceLocationUtil_geocoder_address_not_found);
        } else {
            // Create the Address object from the address list
            address = addressList.get(0);
        }

        // Get the specific address element requested by the caller
        switch (elementCode){

            case AddressCodes.ADMIN_AREA:
                elementString = address.getAdminArea();
                break;
            case AddressCodes.CITY_NAME:
                elementString = address.getLocality();
                break;
            case AddressCodes.COUNTRY_CODE:
                elementString = address.getCountryCode();
                break;
            case AddressCodes.COUNTRY_NAME:
                elementString = address.getCountryName();
                break;
            case AddressCodes.FEATURE_NAME:
                elementString = address.getFeatureName();
                break;
            case AddressCodes.FULL_ADDRESS:
                elementString = address.toString();
                break;
            case AddressCodes.PHONE_NUMBER:
                elementString = address.getPhone();
                break;
            case AddressCodes.POST_CODE:
                elementString = address.getPostalCode();
                break;
            case AddressCodes.PREMISES:
                elementString = address.getPremises();
                break;
            case AddressCodes.STREET_NAME:
                elementString = address.getThoroughfare();
                break;
            case AddressCodes.SUB_ADMIN_AREA:
                elementString = address.getSubAdminArea();
                break;
            case AddressCodes.SUB_THOROUGHFARE:
                elementString = address.getSubThoroughfare();
                break;
            default:
                elementString = mContext.getString(R.string.deviceLocationUtil_geocoder_invalid_element);
                break;
        }

        return elementString;
    }


}// End EasyAddressUtility class
