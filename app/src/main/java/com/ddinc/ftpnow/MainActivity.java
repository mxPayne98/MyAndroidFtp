package com.ddinc.ftpnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.ddinc.ftpnow.connect.ConnectMainActivity;
import com.ddinc.ftpnow.host.HostMainActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton btHost, btConnect, btFileExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btHost = findViewById(R.id.bthost);
        btConnect = findViewById(R.id.btconnect);
        btFileExp = findViewById(R.id.btFileExp);

        btHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HostMainActivity.class);
                startActivity(i);
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ConnectMainActivity.class);
                startActivity(i);
            }
        });

        btFileExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getRootDirectory().getPath());
                intent.setDataAndType(uri, "*/*");
                startActivity(Intent.createChooser(intent, "Open folder"));
            }

        });
    }
}
