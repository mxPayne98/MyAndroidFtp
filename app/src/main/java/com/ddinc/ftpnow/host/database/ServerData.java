package com.ddinc.ftpnow.host.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Management server properties (including each user information),
 * responsible for the management of data, read and write databases, the establishment of configuration files.
 */

public class ServerData {

    private Context context;

    public ArrayList<UserInfo> userInfoList = new ArrayList<>();
    public int maxLoginNumber = 0;
    public int maxLoginPerIp = 0;
    public int idleTime = 0;

    public ServerData(Context context) {
        this.context = context;
    }

    //Runtime Exception triggered when adding user
    public class AddUserException extends RuntimeException {
        public AddUserException(String str) {
            super();
        }
    }

    //Add user
    private void addUser(UserInfo userInfo) throws AddUserException {
        for (UserInfo u :
                userInfoList) {
            if (u.name.equals(userInfo.name))
                throw new AddUserException("Same Name:" + u.name);
        }
        userInfoList.add(userInfo);
    }

    //Write user information to the properties profile
    public void writeToFile(String ftpConfigDir) {
        File dir = new File(ftpConfigDir);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(ftpConfigDir, "userInfoList.properties");
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to create profile", Toast.LENGTH_SHORT).show();
        }
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file, true);
            for (UserInfo u :
                    userInfoList) {
                if (!u.userEnabled)
                    continue;
                Md5PasswordEncryptor encryptor = new Md5PasswordEncryptor();
                String encryptedPassword = encryptor.encrypt(u.password);

                String temp = "ftpserver.user." + u.name + ".";
                String strPassword = temp + "userpassword=" + encryptedPassword + "\n";
                String strHomeDirectory = temp + "homedirectory=" + u.homeDirectory + "\n";
                String strEnableFlag = temp + "enableflag=" +
                        (u.userEnabled ? "true" : "false") + "\n";
                String strWritePermission = temp + "writepermission=" +
                        (u.writePermission ? "true" : "false") + "\n";
                String strMaxLoginNumber = temp + "maxloginnumber=" + maxLoginNumber + "\n";
                String strMaxLoginPerIp = temp + "maxloginperip=" + maxLoginPerIp + "\n";
                String strIdleTime = temp + "idletime=" + idleTime + "\n";
                String strUploadRate = temp + "uploadrate=" + u.uploadRate + "\n";
                String strDownloadRate = temp + "downloadrate=" + u.downloadRate + "\n";
                outputStream.write(strPassword.getBytes());
                outputStream.write(strHomeDirectory.getBytes());
                outputStream.write(strEnableFlag.getBytes());
                outputStream.write(strWritePermission.getBytes());
                outputStream.write(strMaxLoginNumber.getBytes());
                outputStream.write(strMaxLoginPerIp.getBytes());
                outputStream.write(strIdleTime.getBytes());
                outputStream.write(strUploadRate.getBytes());
                outputStream.write(strDownloadRate.getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Read user information from the database
    public void getUserInfoFromDb(SQLiteDatabase db) {
        Cursor cursor = db.query(
                UserInfoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.name = cursor.getString(
                    cursor.getColumnIndex(UserInfoEntry.NAME)
            );
            userInfo.password = cursor.getString(
                    cursor.getColumnIndex(UserInfoEntry.PASSWORD)
            );
//            userInfo.writePermission = cursor.getString(
//                    cursor.getColumnIndex(UserInfoEntry.WRITE_PERMISSION)
//            );
            userInfo.uploadRate = cursor.getInt(
                    cursor.getColumnIndex(UserInfoEntry.UPLOAD_RATE)
            );
            userInfo.downloadRate = cursor.getInt(
                    cursor.getColumnIndex(UserInfoEntry.DOWNLOAD_RATE)
            );
            userInfo.writePermission = cursor.getString(
                    cursor.getColumnIndex(UserInfoEntry.WRITE_PERMISSION)).equals("true");
            userInfo.userEnabled = cursor.getString(
                    cursor.getColumnIndex(UserInfoEntry.USER_ENABLED)).equals("true");
            userInfo.homeDirectory = cursor.getString(
                    cursor.getColumnIndex(UserInfoEntry.DEFAULT_PATH)
            );
            try {
                addUser(userInfo);
            } catch (AddUserException e) {
                e.printStackTrace();
                Toast.makeText(context, "Username already exists!", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }

    //The default user information is written to the database, called only the first time it is run
    public void putDefaultUserInfoToDb(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(UserInfoEntry.NAME, "default");
        values.put(UserInfoEntry.PASSWORD, "123456");
        values.put(UserInfoEntry.WRITE_PERMISSION, "true");
        values.put(UserInfoEntry.USER_ENABLED, "true");
        values.put(UserInfoEntry.UPLOAD_RATE, 0);
        values.put(UserInfoEntry.DOWNLOAD_RATE, 0);
        values.put(UserInfoEntry.DEFAULT_PATH, "/storage/emulated/0/");
        db.insert(UserInfoEntry.TABLE_NAME, null, values);
    }

    //Write user information to the database
    public void putUserInfoToDb(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        for (UserInfo u :
                userInfoList) {
            values.put(UserInfoEntry.NAME, u.name);
            values.put(UserInfoEntry.PASSWORD, u.password);
            values.put(UserInfoEntry.WRITE_PERMISSION,
                    u.writePermission ? "true" : "false");
            values.put(UserInfoEntry.USER_ENABLED,
                    u.userEnabled ? "true" : "false");
            values.put(UserInfoEntry.UPLOAD_RATE, u.uploadRate);
            values.put(UserInfoEntry.DOWNLOAD_RATE, u.downloadRate);
            values.put(UserInfoEntry.DEFAULT_PATH, u.homeDirectory);
            db.insert(UserInfoEntry.TABLE_NAME, null, values);
        }
    }

    //Check whether the user name is legal
    public boolean checkNameLegal(UserInfo user) {
        for (UserInfo u : userInfoList) {
            if (u.name.equals(user.name) && u != user)
                return false;
        }
        return true;
    }
}
