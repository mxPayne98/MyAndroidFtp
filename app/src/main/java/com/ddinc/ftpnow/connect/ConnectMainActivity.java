package com.ddinc.ftpnow.connect;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddinc.ftpnow.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConnectMainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final String TEMP_FILENAME = "test.txt";
    private Context context = null;

    private MyFTPClientFunctions ftpclient = null;

    private Button btnLoginFtp, btnUploadFile, btnDisconnect, btnExit;
    private EditText edtHostName, edtUserName, edtPassword, edtPort;
    private ProgressDialog pd;

    private String[] fileList;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (msg.what == 0) {
                getFTPFileList();
            } else if (msg.what == 1) {
                showCustomDialog(fileList);
            } else if (msg.what == 2) {
                Toast.makeText(ConnectMainActivity.this, "Uploaded Successfully!",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                Toast.makeText(ConnectMainActivity.this, "Disconnected Successfully!",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                Toast.makeText(ConnectMainActivity.this, "Downloaded Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ConnectMainActivity.this, "Unable to Perform Action!",
                        Toast.LENGTH_SHORT).show();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_main);

        context = this.getBaseContext();

        edtHostName = findViewById(R.id.edtHostName);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtPort = findViewById(R.id.edtPort);

        btnLoginFtp = findViewById(R.id.btnLoginFtp);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnDisconnect = findViewById(R.id.btnDisconnectFtp);
        btnExit = findViewById(R.id.btnExit);

        btnLoginFtp.setOnClickListener(this);
        btnUploadFile.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        // Create a temporary file. You can use this to upload
        createDummyFile();

        ftpclient = new MyFTPClientFunctions();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginFtp:
                if (isOnline(ConnectMainActivity.this)) {
                    connectToFTPAddress();
                } else {
                    Toast.makeText(ConnectMainActivity.this,
                            "Please check your internet connection!",
                            Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btnUploadFile:
                pd = ProgressDialog.show(ConnectMainActivity.this, "", "Uploading...",
                        true, false);
                new Thread(new Runnable() {
                    public void run() {
                        boolean status = false;
                        status = ftpclient.ftpUpload(
                                Environment.getExternalStorageDirectory()
                                        + "/test/" + TEMP_FILENAME,
                                TEMP_FILENAME, "/", context);
                        if (status) {
                            Log.d(TAG, "Upload success");
                            handler.sendEmptyMessage(2);
                        } else {
                            Log.d(TAG, "Upload failed");
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
                break;

            case R.id.btnDownloadFtp:
                pd = ProgressDialog.show(ConnectMainActivity.this, "", "Downloading...", true, false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                        boolean status;
                        String path = "<INSERT PATH HERE>";
                        status = ftpclient.ftpDownload(path,
                                Environment.getExternalStorageDirectory() + "/Ftp Downloads/");
                        if (status) {
                            Log.d(TAG, "Download success");
                            handler.sendEmptyMessage(4);
                        } else {
                            Log.d(TAG, "Download failed");
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
                break;

            case R.id.btnDisconnectFtp:
                pd = ProgressDialog.show(ConnectMainActivity.this, "", "Disconnecting...",
                        true, false);

                new Thread(new Runnable() {
                    public void run() {
                        ftpclient.ftpDisconnect();
                        handler.sendEmptyMessage(3);
                    }
                }).start();

                break;

            case R.id.btnExit:
                this.finish();
                break;
        }

    }

    private void connectToFTPAddress() {

        final String host = edtHostName.getText().toString().trim();
        final String username = edtUserName.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String port = edtPort.getText().toString().trim();

        if (host.length() < 1) {
            Toast.makeText(ConnectMainActivity.this, "Please Enter Host Address!",
                    Toast.LENGTH_LONG).show();
        } else if (username.length() < 1) {
            Toast.makeText(ConnectMainActivity.this, "Please Enter User Name!",
                    Toast.LENGTH_LONG).show();
        } else if (password.length() < 1) {
            Toast.makeText(ConnectMainActivity.this, "Please Enter Password!",
                    Toast.LENGTH_LONG).show();
        } else {

            pd = ProgressDialog.show(ConnectMainActivity.this, "", "Connecting...",
                    true, false);

            new Thread(new Runnable() {
                public void run() {
                    boolean status;
                    status = ftpclient.ftpConnect(host, username, password, Integer.valueOf(port));
                    if (status) {
                        Log.d(TAG, "Connection Success");
                        handler.sendEmptyMessage(0);
                    } else {
                        Log.d(TAG, "Connection failed");
                        handler.sendEmptyMessage(-1);
                    }
                }
            }).start();
        }
    }

    private void getFTPFileList() {
        pd = ProgressDialog.show(ConnectMainActivity.this, "", "Getting Files...",
                true, false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                fileList = ftpclient.ftpPrintFilesList("/");
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    public void createDummyFile() {

        try {
            File root = new File(Environment.getExternalStorageDirectory(),
                    "test");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, TEMP_FILENAME);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("Hi this is a sample file to upload for android FTP client!");
            writer.flush();
            writer.close();
//            Toast.makeText(this, "Saved : " + gpxfile.getAbsolutePath(),
//                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void showCustomDialog(String[] fileList) {
        // custom dialog
        final Dialog dialog = new Dialog(ConnectMainActivity.this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("/ Directory File List");

        TextView tvHeading = dialog.findViewById(R.id.tvListHeading);
        tvHeading.setText(" File List ");

        if (fileList != null && fileList.length > 0) {
            ListView listView = dialog.findViewById(R.id.lstItemList);
            ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, fileList);
            listView.setAdapter(fileListAdapter);
        } else {
            tvHeading.setText(":: No Files ::");
        }

        Button dialogButton = dialog.findViewById(R.id.btnOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.show();
    }
}
