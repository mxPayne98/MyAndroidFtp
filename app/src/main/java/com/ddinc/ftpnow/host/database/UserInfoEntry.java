package com.ddinc.ftpnow.host.database;

import android.provider.BaseColumns;

/**
 * User information in the database column
 */

public class UserInfoEntry implements BaseColumns {
    public static final String TABLE_NAME = "user_info";
    public static final String NAME = "NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String UPLOAD_RATE = "UPLOAD_RATE";
    public static final String DOWNLOAD_RATE = "DOWNLOAD_RATE";
    public static final String DEFAULT_PATH = "DEFAULT_PATH";
    public static final String USER_ENABLED = "USER_ENABLED";
    public static final String WRITE_PERMISSION = "WRITE_PERMISSION";
}
