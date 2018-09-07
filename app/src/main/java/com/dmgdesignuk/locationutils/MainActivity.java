package com.dmgdesignuk.locationutils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    private TextView lastLocationText;
    private TextView currentLocationText;
    private TextView logOutputText;
    private Button lastLocationButton;
    private Button currentLocationButton;

    private int numUpdates = 0;

    private DeviceLocationUtility locationUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of DeviceLocationUtility
        locationUtility = new DeviceLocationUtility(this);

        // Set everything up...
        lastLocationText = (TextView)findViewById(R.id.textView_lastLocation);
        currentLocationText = (TextView)findViewById(R.id.textView_currentLocation);
        logOutputText = (TextView)findViewById(R.id.textView_logOutput);
        lastLocationButton = (Button)findViewById(R.id.button_lastLocation);
        currentLocationButton = (Button)findViewById(R.id.button_currentLocation);

        // Set click listeners on the buttons
        lastLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation(view);
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationUpdates(view);
            }
        });

    }


    /**
     * Gets the last known location from the device's cache. There is a chance this can be null,
     * for example on a new or recently factory-reset device or if location services are turned off
     * in device settings.
     */
    public void getLastLocation(View view){

        // First, check the user has granted the required permission.
        if (locationUtility.permissionIsGranted()){

            // Permission is already granted. First, we'll check the required device location settings
            // are satisfied. If they are not the user will automatically be prompted to enable them.
            // The result of this can be checked and handled by implementing and overriding the
            // onActivityResult callback in your calling Activity. The request code we're passing in
            // can be tested for in onActivityResult to determine where the request originated.
            locationUtility.checkDeviceSettings(DeviceLocationUtility.RequestCodes.LAST_KNOWN_LOCATION);

            // Now we can request the last known location from the device's cache.
            locationUtility.getLastKnownLocation(new DeviceLocationCallback() {
                @Override
                public void onLocationResult(Location location) {

                    // Location result successfully received. This should never be null as a null
                    // result will be returned via the onFailedRequest callback method instead.
                    lastLocationText.setText(getString(R.string.output_location,
                                             String.valueOf(location.getLatitude()),
                                             String.valueOf(location.getLongitude())));

                }
                @Override
                public void onFailedRequest(String result) {

                    // Location request failed, output the error.
                    logOutputText.setText(String.format(getString(R.string.output_failed), result));

                }
            });

        } else {

            // Permission not granted, ask for it. You must implement and override the
            // onRequestPermissionsResult callback method in your calling Activity in order to handle
            // the result of the request.
            // Here we're passing in a request code that corresponds to the type of location request
            // we're attempting to make. We can test for the result of this specific request in the
            // onRequestPermissionResult callback.
            locationUtility.requestPermission(DeviceLocationUtility.RequestCodes.LAST_KNOWN_LOCATION);

        }

    }


    /**
     * Requests constant location updates from the device. The default target frequency is every
     * 30 seconds, with a fastest update interval cap of 10 seconds and the priority set to
     * LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY.
     *
     * The default values will not suit all use cases so the frequency and accuracy of the updates
     * can be altered by calling locationUtility.setLocationRequestParams() if desired.
     *
     * See https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
     * for a list of power priority constants.
     */
    public void getLocationUpdates(View view){

        // OPTIONAL: Use the setLocationRequestParams() method to change the default location
        //           request values. Here we're setting up the location request with a target update
        //           interval of 10 seconds and a fastest update interval cap of 5 seconds using the
        //           high accuracy power priority.
        locationUtility.setLocationRequestParams(10000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check the permissions
        if (locationUtility.permissionIsGranted()){

            // Check device settings
            locationUtility.checkDeviceSettings(DeviceLocationUtility.RequestCodes.CURRENT_LOCATION_UPDATES);

            // Request location updates
            locationUtility.getCurrentLocationUpdates(new DeviceLocationCallback() {
                @Override
                public void onLocationResult(Location location) {

                    // Location result successfully returned
                    currentLocationText.setText(String.format(getString(R.string.output_location),
                                                String.valueOf(location.getLatitude()),
                                                String.valueOf(location.getLongitude())));
                    // Increment the counter every time a location update is received
                    numUpdates++;
                    logOutputText.setText(getString(R.string.output_updates, String.valueOf(numUpdates)));
                    getAddressElementsFromLocation(location);

                }
                @Override
                public void onFailedRequest(String result) {

                    // Location request failed, output the error.
                    logOutputText.setText(String.format(getString(R.string.output_failed), result));

                }
            });

        } else {

            // Permission not granted, ask for it
            locationUtility.requestPermission(DeviceLocationUtility.RequestCodes.CURRENT_LOCATION_UPDATES);

        }

    }


    public void getAddressElementsFromLocation(Location location){

        String street = locationUtility.getAddressElement(DeviceLocationUtility.AddressCodes.STREET_ADDRESS, location);
        String city = locationUtility.getAddressElement(DeviceLocationUtility.AddressCodes.CITY_NAME, location);

        currentLocationText.setText(currentLocationText.getText() + "\n" +
                                    "Street: " + street + "\n" +
                                    "City: " + city);

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

        if (requestGranted) {
            // We've got permission, yay! Now we can carry on where we left off...

            // Query the incoming request code to determine which request we're responding to
            switch (requestCode) {

                case DeviceLocationUtility.RequestCodes.LAST_KNOWN_LOCATION:
                    // Carry on...
                    getLastLocation(lastLocationButton);
                    break;

                case DeviceLocationUtility.RequestCodes.CURRENT_LOCATION_UPDATES:
                    // Carry on...
                    getLocationUpdates(currentLocationButton);
                    break;

                default:
                    break;

            }

        } else {

            // Permission denied, boo! Log our displeasure...
            Log.e(TAG, "You denied the permission request. You must hate us :(");

        }

    }


    /**
     * This callback method needs to be implemented and overridden in order to receive and handle for
     * the results of any request to change device settings.
     *
     * @param requestCode   When a settings change request is made a requset code is passed along to the
     *                      onActivityResult callback which can then be tested for if required to
     *                      determine which request you are responding to.
     * @param resultCode    An int value representing the result of the request. For the DeviceLocationUtility
     *                      this will always be either RESULT_OK or RESULT_CANCELED.
     * @param data          An Intent object containing any returned data from the calling Activity. For the
     *                      DeviceLocationUtility this will always be null and so can be ignored.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {

            // The user has either denied or cancelled out of the request to change device
            // settings so you will need to handle for that here.
            Log.e(TAG, "Unable to proceed: required device settings not satisfied");

        } else {

            // Location settings have been enabled, query the incoming request code to determine
            // which request we're responding to and take the appropriate action.
            switch (requestCode){

                case DeviceLocationUtility.RequestCodes.LAST_KNOWN_LOCATION:
                    // Carry on where we left off...
                    getLastLocation(lastLocationButton);
                    break;

                case DeviceLocationUtility.RequestCodes.CURRENT_LOCATION_UPDATES:
                    // Carry on where we left off...
                    getLocationUpdates(currentLocationButton);
                    break;

                default:
                    break;

            }

        }

    }


    /**
     * It is generally a good idea to stop receiving location updates when the Activity goes into a
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
