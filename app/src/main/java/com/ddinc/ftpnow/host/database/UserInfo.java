package com.ddinc.ftpnow.host.database;

/**
 * User information class, user name, password, write permission, whether enabled, default directory,
 * maximum upload rate, maximum download speed. All of the above information is public.
 */

public class UserInfo {
    public String name = "default";
    public String password = "default";
    public boolean writePermission = false;
    public boolean userEnabled = true;
    public String homeDirectory = "/storage/emulated/0/";
    public int uploadRate = 4800000;
    public int downloadRate = 4800000;

    public UserInfo() {
    }

    public UserInfo(
            String name,
            String password,
            boolean writePermission,
            String homeDirectory,
            int uploadRate,
            int downloadRate,
            boolean userEnabled) {
        this.name = name;
        this.password = password;
        this.writePermission = writePermission;
        this.homeDirectory = homeDirectory;
        this.uploadRate = uploadRate;
        this.downloadRate = downloadRate;
        this.userEnabled = userEnabled;
    }
}
