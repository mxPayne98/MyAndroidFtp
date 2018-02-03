package com.ddinc.ftpnow.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ddinc.ftpnow.R;
import com.ddinc.ftpnow.host.database.FinalValues;

import java.io.File;

/**
 * Edit user activity.
 * This class is called when MainActivity needs to add or edit user information.
 * Back to MainActivity if and only if the user presses the Save button to review the information.
 */

public class EditUserActivity extends Activity {

    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextUploadRate;
    private EditText editTextDownloadRate;
    private EditText editTextDefaultPath;
    private CheckBox checkBoxWritePermission;
    private CheckBox checkBoxUserEnabled;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        intent = getIntent();
        editTextName = findViewById(R.id.edit_user_name);
        editTextPassword = findViewById(R.id.edit_password);
        editTextUploadRate = findViewById(R.id.edit_upload_rate);
        editTextDownloadRate = findViewById(R.id.edit_download_rate);
        editTextDefaultPath = findViewById(R.id.edit_default_path);
        checkBoxWritePermission = findViewById(R.id.checkbox_write_permission);
        checkBoxUserEnabled = findViewById(R.id.checkbox_user_enabled);

        Button buttonSave = findViewById(R.id.button_save);
        //Set the save key action
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResult = new Intent(EditUserActivity.this, HostMainActivity.class);

                File dir = new File(editTextDefaultPath.getText().toString());
                if (!dir.exists()) {//The default directory set does not exist
                    Toast.makeText(EditUserActivity.this, "Path does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //The following is stored in the edited information
                intentResult.putExtra(FinalValues.INTENT_USER_NAME, editTextName.getText().toString());
                intentResult.putExtra(FinalValues.INTENT_PASSWORD, editTextPassword.getText().toString());
                intentResult.putExtra(FinalValues.INTENT_UPLOAD_RATE, Integer.parseInt(editTextUploadRate.getText().toString()));
                intentResult.putExtra(FinalValues.INTENT_DOWNLOAD_RATE, Integer.parseInt(editTextDownloadRate.getText().toString()));
                intentResult.putExtra(FinalValues.INTENT_DEFAULT_PATH, editTextDefaultPath.getText().toString());
                intentResult.putExtra(FinalValues.INTENT_WRITE_PERMISSION, checkBoxWritePermission.isChecked());
                intentResult.putExtra(FinalValues.INTENT_USER_ENABLED, checkBoxUserEnabled.isChecked());
                intentResult.putExtra(FinalValues.INTENT_USER_INDEX, intent.getIntExtra(FinalValues.INTENT_USER_INDEX, -1));
                setResult(intent.getIntExtra(FinalValues.INTENT_REQUEST, 0), intentResult);
            }
        });

        //Editing of other entries is prohibited when "User Enabled" is not checked
        checkBoxUserEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fillBlankWithInfo(intent, false);
                editTextName.setEnabled(isChecked);
                editTextPassword.setEnabled(isChecked);
                editTextUploadRate.setEnabled(isChecked);
                editTextDownloadRate.setEnabled(isChecked);
                checkBoxWritePermission.setEnabled(isChecked);
                editTextDefaultPath.setEnabled(isChecked);
            }
        });

        //Fill in the fields with default values
        fillBlankWithInfo(intent, true);
    }

    //Fill in the fields with default values
    private void fillBlankWithInfo(Intent intent, boolean changeUserEnabled) {
        String name = intent.getStringExtra(FinalValues.INTENT_USER_NAME);
        String password = intent.getStringExtra(FinalValues.INTENT_PASSWORD);
        boolean writePermission = intent.getBooleanExtra(FinalValues.INTENT_WRITE_PERMISSION, false);
        int uploadRate = intent.getIntExtra(FinalValues.INTENT_UPLOAD_RATE, 0);
        int downloadRate = intent.getIntExtra(FinalValues.INTENT_DOWNLOAD_RATE, 0);
        String defaultPath = intent.getStringExtra(FinalValues.INTENT_DEFAULT_PATH);
        boolean userEnabled = intent.getBooleanExtra(FinalValues.INTENT_USER_ENABLED, true);

        editTextName.setText(name);
        editTextPassword.setText(password);
        editTextUploadRate.setText(String.valueOf(uploadRate));
        editTextDownloadRate.setText(String.valueOf(downloadRate));
        editTextDefaultPath.setText(defaultPath);
        checkBoxWritePermission.setChecked(writePermission);
        if (changeUserEnabled) {
            checkBoxUserEnabled.setChecked(userEnabled);
        }
        if (editTextDefaultPath.getText().toString().equals(""))
            editTextDefaultPath.setText(FinalValues.DEFAULT_HOME_DIRECTORY);
    }


}