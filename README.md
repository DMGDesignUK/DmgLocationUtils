# DMgLocationUtils

#### A collection of utilities to make working with location in Android easier

---

[![](https://jitpack.io/v/DMGDesignUK/DmgLocationUtils.svg)](https://jitpack.io/#DMGDesignUK/DmgLocationUtils)
[![API](https://img.shields.io/badge/API-17%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=17)

### EasyLocationUtility

##### An abstraction layer for the Google Play Services FusedLocationProviderClient API

Provides a simple to use wrapper for some of the more common location-related tasks, such as:
- Get the device's last known location
- Get constant location updates (and control the update granularity)
- Get "smart" location (a combination of the above two functions)
- Test if user has granted permission to access the device's fine location and request it if not
- Test if required device setttings are satisfied and request the user enable them if not

See the [documentation](http://www.dmgdesignuk.com/pages/docs/dmglocationutils/index.html) for full details.

### EasyAddressUtility

##### An abstraction layer for Android's Geocoder

Provides a wrapper for some common reverse geocoding tasks, such as:
- Get a List<Address> object containing all the address data associated with a given location
- Get a specific address line for a given location (e.g. street address etc.)

See the [documentation](http://www.dmgdesignuk.com/pages/docs/dmglocationutils/index.html) for full details.

---

## Adding the library to your project

Add the Jitpack repository to your **root** *build.gradle* file at the end of the repositories section:

```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency to your **app** *build.gradle* file:

```java
	dependencies {
		implementation 'com.github.DMGDesignUK:DmgLocationUtils:v1.0.1'
	}
```

---

### Usage

Add the following permission in your Androidmanifest.xml file:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

The following is a very basic example of how you can implement some of the more common use-cases. For more examples have a look at the source code of the [example app](https://github.com/DMGDesignUK/DmgLocationUtils/blob/master/app/src/main/java/com/dmgdesignuk/locationutils/MainActivity.java), which is extensively commented, or read the full Javadoc [here](http://www.dmgdesignuk.com/pages/docs/dmglocationutils/index.html)
```java
    // Create instances of the utilities
    EasyLocationUtility locationUtility = new EasyLocationUtil(this);
    EasyAddressUtility addressUtility = new EasyAddressUtil(this);

    // Check if permission to access device location has been granted and ask for it if not
    if (locationUtility.permissionIsGranted()){
        // Permission is granted
    } else {
        // Permission not granted, ask for it
        locationUtility.requestPermission(EasyLocationUtility.RequestCodes.REQUEST_CODE);
    }

    // Get the device's last known location
    locationUtility.getLastKnownLocation(new LocationRequestCallback() {
        @Override
        public void onLocationResult(Location location) {
            // Location result successfully received. Handle the Location object here.
        }
        @Override
        public void onFailedRequest(String result) {
            // Location request failed. The result string will contain the reason for failure.
        }
    });

    // Get continuous current location updates
    locationUtility.getCurrentLocationUpdates(new LocationRequestCallback() {
        @Override
        public void onLocationResult(Location location) {
            // Location result successfully received. Handle the Location object here.
        }
        @Override
        public void onFailedRequest(String result) {
            // Location request failed. The result string will contain the reason for failure.
        }
    });

    // Get an address element from a location
    String street = addressUtility.getAddressElement(EasyAddressUtility.AddressCodes.STREET_NAME, location);
    String city = addressUtility.getAddressElement(EasyAddressUtility.AddressCodes.CITY_NAME, location);
```

### Bugs, Comments, Request and Feedback

Feedback is an important part of expanding and improving any project. Found a bug? [Open an issue](https://github.com/DMGDesignUK/DmgLocationUtils/issues). Got a suggestion or a feature request? [Contact me](mailto:dave@dmgdesignuk.com).

---
### License
Copyright(c) 2018 Dave Gibbons

[DMgDesignUK](http://www.dmgdesignuk.com/) | dave@dmgdesignuk.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
