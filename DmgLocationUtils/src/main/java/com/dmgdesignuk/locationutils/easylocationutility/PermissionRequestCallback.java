package com.dmgdesignuk.locationutils.easylocationutility;


/**
 * A callback used by EasyLocationUtility to communicate the results
 * of a user's interaction with a permission request dialog. Must be
 * passed as a parameter to the calling method.
 */
public interface PermissionRequestCallback {

    void onRationaleDialogOkPressed();

}
