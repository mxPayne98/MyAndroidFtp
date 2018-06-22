package com.ddinc.ftpnow;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ddinc.ftpnow.connect.ConnectMainActivity;
import com.ddinc.ftpnow.host.HostMainActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton btHost, btConnect, btFileExp;
    PermissionManager perMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        perMan = new PermissionManager(this);

        btHost = findViewById(R.id.bthost);
        btConnect = findViewById(R.id.btconnect);
        btFileExp = findViewById(R.id.btFileExp);

        btHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perMan.doWithPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE},
                        new PermissionManager.OnPermissionResult() {
                            @Override
                            public void onGranted(String permission) {
                                Intent i = new Intent(MainActivity.this, HostMainActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onDenied(String permission) {

                            }
                        });
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perMan.doWithPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE},
                        new PermissionManager.OnPermissionResult() {

                            @Override
                            public void onGranted(String permission) {
                                Intent i = new Intent(MainActivity.this, ConnectMainActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onDenied(String permission) {

                            }
                        });

            }
        });

        btFileExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perMan.doWithPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE},
                        new PermissionManager.OnPermissionResult() {

                            @Override
                            public void onGranted(String permission) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse(Environment.getRootDirectory().getPath());
                                intent.setDataAndType(uri, "*/*");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Intent.createChooser(intent, "Open folder"));
                            }

                            @Override
                            public void onDenied(String permission) {

                            }
                        });

            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        perMan.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void deniedDialog() {
        (new AlertDialog.Builder(this)).setMessage("We need this permission to read/write files \n Please grant the permission").setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialog, int i) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE}, 45);
            }
        }).setNegativeButton("NO THANKS", (new android.content.DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialog, int i) {
                Toast.makeText(MainActivity.this, "Sigh! I tried", Toast.LENGTH_SHORT).show();
            }
        })).create().show();
    }
}
