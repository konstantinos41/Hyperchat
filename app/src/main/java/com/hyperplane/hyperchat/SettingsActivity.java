package com.hyperplane.hyperchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.settingsmodule.MySettings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //load settings from memory
        MySettings.loadAllNotificationPreferences(getApplicationContext());
        //load stored values of settings
        Switch simpleSwitch = (Switch) findViewById(R.id.notifications_new_message);
        simpleSwitch.setChecked(MySettings.isHasNotificationSoundNewMessage());
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                MySettings.setHasNotificationSoundNewMessage(bChecked);
                MySettings.saveNotificationPreferences(getApplicationContext());
            }
        });

        simpleSwitch = (Switch) findViewById(R.id.notifications_new_message_vibrate);
        simpleSwitch.setChecked(MySettings.isHasNotificationVibrationNewMessage());
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                MySettings.setHasNotificationVibrationNewMessage(bChecked);
                MySettings.saveNotificationPreferences(getApplicationContext());
            }
        });

        simpleSwitch = (Switch) findViewById(R.id.notifications_new_message_led);
        simpleSwitch.setChecked(MySettings.isHasNotificationLedNewMessage());
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                MySettings.setHasNotificationLedNewMessage(bChecked);
                MySettings.saveNotificationPreferences(getApplicationContext());
            }
        });

        simpleSwitch = (Switch) findViewById(R.id.notifications_new_group);
        simpleSwitch.setChecked(MySettings.isHasNotificationSoundNewGroup());
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                MySettings.setHasNotificationSoundNewGroup(bChecked);
                MySettings.saveNotificationPreferences(getApplicationContext());
            }
        });

        simpleSwitch = (Switch) findViewById(R.id.notifications_new_group_vibrate);
        simpleSwitch.setChecked(MySettings.isHasNotificationVibrationNewGroup());
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                MySettings.setHasNotificationVibrationNewGroup(bChecked);
                MySettings.saveNotificationPreferences(getApplicationContext());
            }
        });
        simpleSwitch = (Switch) findViewById(R.id.notifications_new_group_led);
        simpleSwitch.setChecked(MySettings.isHasNotificationLedNewGroup());
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                MySettings.setHasNotificationLedNewGroup(bChecked);
                MySettings.saveNotificationPreferences(getApplicationContext());
            }
        });
    }


}
