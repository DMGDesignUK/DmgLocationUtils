package com.dmgdesignuk.locationutils;

import android.support.annotation.NonNull;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.dmgdesignuk.devicelocationutility.DeviceLocationCallback;
import com.dmgdesignuk.devicelocationutility.DeviceLocationUtility;
import com.google.android.gms.location.LocationRequest;


/**
 * A simple Activity class giving an example of how you can leverage DeviceLocationUtility to quickly and easily
 * implement location functionality in your app, along with the permissions checking/requesting and device
 * settings checks that go hand-in-hand with it.
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{

    private static final String TAG = MainActivity.class.getSimpleName();

    // Declare a DeviceLocationUtility member variable
    private DeviceLocationUtility locationUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign a new instance of DeviceLocationUtility to our member variable
        locationUtility = new DeviceLocationUtility(this);

    }


    /**
     * Gets the last known location from the device's cache. There is a chance this can be null,
     * for example on a new or recently factory-reset device or if location services are turned off
     * in device settings.
     */
    public void getLastLocation(){

        // First, check the user has granted the required permission. If permission has not yet been
        // granted the DeviceLocationUtility will automatically ask for it. You must override the
        // onRequestPermissionsResult callback method in your calling Activity in order to handle
        // the result of the request.
        if (locationUtility.checkPermissionGranted()){

            // Permission is already granted. First, we'll check the required device location settings
            // are satisfied. If they are not the user will automatically be prompted to enable them.
            // The result of this can be checked and handled by implementing and overriding the
            // onActivityResult callback in your calling Activity.
            locationUtility.checkDeviceSettings();

            // Now we can request the last known location from the device's cache.
            locationUtility.getLastKnownLocation(new DeviceLocationCallback() {
                @Override
                public void onLocationResult(Location location) {

                    // Location result successfully received. This should never be null as a null
                    // result will be returned via the onFailedRequest callback method instead.
                    Log.i(TAG, "Device's last known location: " + location.toString());
                    // Do UI stuff if necessary.

                }
                @Override
                public void onFailedRequest(String result) {

                    // Location request failed, log an error to the console.
                    Log.e(TAG, "Unable to get location: " + result);
                    // Do UI stuff if necessary.

                }
            });

        } else {

            // Permission not granted, ask for it. Here we're passing in a request code that
            // corresponds to the type of location request we're attempting to make. We can test for
            // the result of this specific request in onRequestPermissionResult.
            locationUtility.requestPermission(DeviceLocationUtility.RequestCodes.LAST_KNOWN_LOCATION);

        }

    }


    /**
     * Requests constant location updates from the device. The default target frequency is every
     * 30 seconds, with a fastest update interval cap of 10 seconds and the priority set to
     * LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY.
     *
     * The default values will not suit all uses cases so the frequency and accuracy of the updates
     * can be altered by calling locationUtility.setLocationRequestParams() if desired.
     *
     * See https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
     * for a list of power priority constants.
     */
    public void getLocationUpdates(){

        // OPTIONAL: Use the setLocationRequestParams() method to change the default location
        //           request values. Here we're setting up the location request with a target update
        //           interval of 20 seconds and a fastest update interval cap of 10 seconds using the
        //           high accuracy power priority.
        locationUtility.setLocationRequestParams(20000, 10000, LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check the permissions
        if (locationUtility.checkPermissionGranted()){

            // Check device settings
            locationUtility.checkDeviceSettings();

            // Request location updates
            locationUtility.getCurrentLocationUpdates(new DeviceLocationCallback() {
                @Override
                public void onLocationResult(Location location) {

                    // Location result successfully returned
                    Log.i(TAG, "Current location: " + location.toString());

                }
                @Override
                public void onFailedRequest(String result) {

                    // Location request failed
                    Log.e(TAG, "Unable to get location: " + result);

                }
            });

        } else {

            // Permissions not granted, ask for it
            locationUtility.requestPermission(DeviceLocationUtility.RequestCodes.CURRENT_LOCATION_UPDATES);

        }

    }


    /**
     * This callback method needs to be implemented and overridden in order to receive and handle for
     * the results of any permission requests made.
     *
     * @param requestCode   When a permission request is made a requset code is passed along to the
     *                      onRequestPermissionsResult callback which can then be tested for if
     *                      required to determine which request you are responding to.
     * @param permissions   A String array containing the permission(s) requested. For the
     *                      DeviceLocationUtility this will always be Manifest.permission.ACCESS_FINE_LOCATION.
     * @param grantResults  An int array containing the result for each requested permission. It will either
     *                      be PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the permission request has been granted and store the result in a boolean
        boolean requestGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        // Query the incoming request code to determine which request we're responding to
        switch (requestCode) {

            case DeviceLocationUtility.RequestCodes.LAST_KNOWN_LOCATION:
                if (requestGranted) {
                    // We've got permission, yay! Now we can carry on where we left off...
                    getLastLocation();
                } else {
                    // Permission denied, boo! Log our displeasure...
                    Log.e(TAG, "You denied the permission request. You must hate us :(");
                }
                break;

            case DeviceLocationUtility.RequestCodes.CURRENT_LOCATION_UPDATES:
                if (requestGranted) {
                    // We've got permission, yay! Now we can carry on where we left off...
                    getLocationUpdates();
                } else {
                    // Permission denied, boo! Log our displeasure...
                    Log.e(TAG, "You denied the permission request. You must hate us :(");
                }
                break;

            default:
                break;

        }

    }


    /**
     * It's generally a good idea to stop receiving location updates when the Activity goes into a
     * paused state (unless your app specifically requires background location updates)
     */
    @Override
    protected void onPause(){
        super.onPause();

        // No need to check if location updates are currently being received as the
        // stopLocationUpdates() method will take care of that.
        locationUtility.stopLocationUpdates();
    }


    /**
     * Resume location updates when the Activity comes back into focus
     */
    @Override
    protected void onResume(){
        super.onResume();

        // Once again, no need for any state checks as the resumeLocationUpdates() method
        // will take care of it.
        locationUtility.resumeLocationUpdates();
    }


}// End MainActivity class
