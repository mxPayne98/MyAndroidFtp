package com.ddinc.ftpnow.host;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ddinc.ftpnow.R;
import com.ddinc.ftpnow.host.database.DbHelper;
import com.ddinc.ftpnow.host.database.FinalValues;
import com.ddinc.ftpnow.host.database.ServerData;
import com.ddinc.ftpnow.host.database.UserInfo;
import com.ddinc.ftpnow.host.recycler_view.MyAdapter;
import com.ddinc.ftpnow.host.recycler_view.RecyclerItemClickListener;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;

public class HostMainActivity extends AppCompatActivity {
    public static final String TAG = "HOST";
    private DbHelper mDbHelper;
    ServerData serverData = new ServerData(this);

    private FtpServer mFtpServer;
    private String ftpConfigDir;
    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_main);

        ftpConfigDir = getFilesDir() + "/ftpConfig/";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Snackbar.make(
                findViewById(R.id.toolbar),
                getString(R.string.start_up_prompt),
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostMainActivity.this, EditUserActivity.class);
                intent.putExtra(FinalValues.INTENT_REQUEST, FinalValues.REQUEST_ADD_USER);
                startActivityForResult(intent, FinalValues.REQUEST_ADD_USER);
            }
        });

        //Set the prompt above the App text
        setPromptText();

        //Check if it is the first run
        testFirstRun();

        mDbHelper = new DbHelper(this, serverData);

        //Retrieve user information from the database
        getUserInfoFromDb();

        //Configure RecyclerView
        recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Configure custom Adapter
        recyclerViewAdapter = new MyAdapter(serverData.userInfoList, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    //Short press to bring up user information editing interface
                    @Override
                    public void onItemClick(View view, int position) {
                        editUserInfo(position);
                    }

                    //Long press to bring up the menu
                    @Override
                    public void onLongClick(View view, final int position) {
                        PopupMenu popupMenu = new PopupMenu(HostMainActivity.this, view);
                        Menu menu = popupMenu.getMenu();
                        menu.add(Menu.NONE, Menu.FIRST, 0, getString(R.string.menu_edit_user));
                        menu.add(Menu.NONE, Menu.FIRST + 1, 1, getString(R.string.menu_delete_user));
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case Menu.FIRST://edit
                                        editUserInfo(position);
                                        break;
                                    case Menu.FIRST + 1://delete
                                        AlertDialog.Builder builder = new AlertDialog.Builder(HostMainActivity.this);
                                        builder.setMessage(getString(R.string.alert_delete_user))
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        serverData.userInfoList.remove(position);
                                                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                                        serverData.putUserInfoToDb(db);
                                                        db.close();
                                                        recyclerViewAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();//pop-up menu
                    }

                    private void editUserInfo(int position) {//Edit user information for the location
                        UserInfo userInfo = serverData.userInfoList.get(position);
                        Intent intent = new Intent(HostMainActivity.this, EditUserActivity.class);
                        intent.putExtra(FinalValues.INTENT_USER_INDEX, position);
                        intent.putExtra(FinalValues.INTENT_USER_NAME, userInfo.name);
                        intent.putExtra(FinalValues.INTENT_WRITE_PERMISSION, userInfo.writePermission);
                        intent.putExtra(FinalValues.INTENT_UPLOAD_RATE, userInfo.uploadRate);
                        intent.putExtra(FinalValues.INTENT_DOWNLOAD_RATE, userInfo.downloadRate);
                        intent.putExtra(FinalValues.INTENT_DEFAULT_PATH, userInfo.homeDirectory);
                        intent.putExtra(FinalValues.INTENT_PASSWORD, userInfo.password);
                        intent.putExtra(FinalValues.INTENT_USER_ENABLED, userInfo.userEnabled);
                        intent.putExtra(FinalValues.INTENT_REQUEST, FinalValues.REQUEST_EDIT_USER);
                        startActivityForResult(intent, FinalValues.REQUEST_EDIT_USER);//EditUserActivity call for editing
                    }
                }));
    }

    //Used to configure and start the server
    private void configFtpServer() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

        //Get the port value from sharedPreferences, the default is 2221
        int port = Integer.parseInt(sharedPreferences.getString(getString(R.string.prefs_port_key), "2221"));

        //Read the properties format configuration file
        String fileName = ftpConfigDir + "userInfoList.properties";
        File files = new File(fileName);
        userManagerFactory.setFile(files);
        serverFactory.setUserManager(userManagerFactory.createUserManager());

        factory.setPort(port);
        serverFactory.addListener("default", factory.createListener());
        FtpServer server = serverFactory.createServer();
        this.mFtpServer = server;
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    //Destroy server before termination
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (null != mFtpServer) {
            mFtpServer.stop();
            mFtpServer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Respond to the menu on the Toolbar
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//Set
            Intent intent = new Intent(HostMainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_start_server) {//start up
            if (mFtpServer == null) {//If not started
                serverData.writeToFile(ftpConfigDir);
                Toast.makeText(this, "Server Activated", Toast.LENGTH_SHORT).show();
                configFtpServer();
                ActionMenuItemView menuItemView = findViewById(R.id.action_start_server);
                menuItemView.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_stop));
            } else {//If it is started
                mFtpServer.stop();
                mFtpServer = null;
                ActionMenuItemView menuItemView = findViewById(R.id.action_start_server);
                menuItemView.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_start));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Get user information from the database
    private void getUserInfoFromDb() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        serverData.getUserInfoFromDb(db);
        db.close();
    }

    //Write user information to the database
    private void putUserInfoToDb() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        serverData.putUserInfoToDb(db);
        db.close();
    }

    //Reset the database
    private void cleanDb() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.cleanDb(db);
        db.close();
    }

    //Intent Received to create or modify user information
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != requestCode)
            return;
        String name = data.getStringExtra(FinalValues.INTENT_USER_NAME);
        String password = data.getStringExtra(FinalValues.INTENT_PASSWORD);
        String defaultPath = data.getStringExtra(FinalValues.INTENT_DEFAULT_PATH);
        int uploadRate = data.getIntExtra(FinalValues.INTENT_UPLOAD_RATE, 0);
        int downloadRate = data.getIntExtra(FinalValues.INTENT_DOWNLOAD_RATE, 0);
        boolean userEnabled = data.getBooleanExtra(FinalValues.INTENT_USER_ENABLED, true);
        boolean writePermission = data.getBooleanExtra(FinalValues.INTENT_WRITE_PERMISSION, false);
        int userIndex = data.getIntExtra(FinalValues.INTENT_USER_INDEX, -1);


        UserInfo userInfo;
        if (requestCode == FinalValues.REQUEST_EDIT_USER) {//modify
            userInfo = serverData.userInfoList.get(userIndex);
            if (!serverData.checkNameLegal(userInfo)) {//Detect whether the name and the known repeat
                Toast.makeText(this, getString(R.string.same_name_warning), Toast.LENGTH_SHORT).show();
            }
            userInfo.name = name;
            userInfo.password = password;
            userInfo.writePermission = writePermission;
            userInfo.uploadRate = uploadRate;
            userInfo.downloadRate = downloadRate;
            userInfo.userEnabled = userEnabled;
            userInfo.homeDirectory = defaultPath;
        } else if (requestCode == FinalValues.REQUEST_ADD_USER) {//create
            userInfo = new UserInfo(
                    name,
                    password,
                    writePermission,
                    defaultPath,
                    uploadRate,
                    downloadRate,
                    userEnabled
            );
            serverData.userInfoList.add(userInfo);
        }

        cleanDb();
        putUserInfoToDb();
        serverData.writeToFile(ftpConfigDir);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    //Modify the prompt text above the App
    private void setPromptText() {
        TextView tvPrompt = (TextView) findViewById(R.id.text_view_prompt);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 0xff) + "." + ((ipAddress >> 8) & 0xff) + "." + ((ipAddress >> 16) & 0xff) + "." + ((ipAddress >> 24) & 0xff);
        String promptInfo = "ftp://" + ip + ":" + sharedPreferences.getString(getString(R.string.prefs_port_key), "2221") + "\n";
        tvPrompt.setText(promptInfo);
    }

    //Resume Refresh prompt text
    @Override
    protected void onResume() {
        super.onResume();

        setPromptText();
        serverData.maxLoginNumber = Integer.parseInt(sharedPreferences.getString(getString(R.string.prefs_max_login_number_key), "0"));
        serverData.maxLoginPerIp =
                Integer.parseInt(sharedPreferences.getString(getString(R.string.prefs_max_login_per_ip_key), "0"));
    }

    //Check if it is the first run
    private void testFirstRun() {
        String string = sharedPreferences.getString(FinalValues.FIRST_RUN_STR, "");
        if (string.equals("")) {//The first run, pop-up instructions
            Intent intent = new Intent(HostMainActivity.this, ManualActivity.class);
            startActivity(intent);
            sharedPreferences.edit().putString(FinalValues.FIRST_RUN_STR, "RUN").apply();
        }
    }
}
