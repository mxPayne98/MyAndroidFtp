package com.ddinc.ftpnow.host.database;

public final class FinalValues {
    //The name of the tag key to run for the first time in the sharedPreference
    public static final String FIRST_RUN_STR = "RUN";

    //Default path
    public static final String DEFAULT_HOME_DIRECTORY = "/storage/emulated/0/";

    // used to distinguish between editing and adding users
    public static final int REQUEST_ADD_USER = 1;
    public static final int REQUEST_EDIT_USER = 2;

    //Intent communication between EditActivity and MainActivity
    public static final String INTENT_USER_INDEX = "USER_INDEX";
    public static final String INTENT_USER_NAME = "USER_NAME";
    public static final String INTENT_PASSWORD = "PASSWORD";
    public static final String INTENT_UPLOAD_RATE = "UPLOAD_RATE";
    public static final String INTENT_DOWNLOAD_RATE = "DOWNLOAD_RATE";
    public static final String INTENT_DEFAULT_PATH = "DEFAULT_PATH";
    public static final String INTENT_WRITE_PERMISSION = "WRITE_PERMISSION";
    public static final String INTENT_USER_ENABLED = "USER_ENABLED";
    public static final String INTENT_REQUEST = "REQUEST";
}
