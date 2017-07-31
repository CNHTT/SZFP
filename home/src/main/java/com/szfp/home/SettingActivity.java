package com.szfp.home;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView setWifi;
    TextView setBluetooth;
    TextView setCondition;
    TextView setMore;
    TextView setShow;
    TextView setVolume;
    TextView setSafety;
    TextView setDateTine;
    TextView setSoundNotification;
    TextView setLanguageInput;
    TextView setOtherMore;
    TextView setApp;
    TextView setAccessible;
    TextView setDeveloperOptions;
    TextView setAboutDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

                setWifi = (TextView) findViewById(R.id.set_wifi);
                setWifi.setOnClickListener(this);

                setBluetooth = (TextView) findViewById(R.id.set_bluetooth);
        setBluetooth.setOnClickListener(this);
                setCondition= (TextView) findViewById (R.id.set_condition);
        setCondition.setOnClickListener(this);
                setMore= (TextView) findViewById(R.id.set_more);
        setMore.setOnClickListener(this);
                setShow= (TextView) findViewById(R.id.set_show);
        setShow .setOnClickListener(this);
                setVolume= (TextView) findViewById(R.id.set_volume);
        setVolume .setOnClickListener(this);
                setSafety= (TextView) findViewById(R.id.set_safety);
        setSafety.setOnClickListener(this);
                setDateTine= (TextView) findViewById(R.id.set_date_tine);
        setDateTine.setOnClickListener(this);
                setSoundNotification= (TextView) findViewById(R.id.set_sound_notification);
        setSoundNotification.setOnClickListener(this);
                setLanguageInput= (TextView) findViewById(R.id.set_language_input);
        setLanguageInput.setOnClickListener(this);
                setOtherMore= (TextView) findViewById(R.id.set_other_more);
        setOtherMore.setOnClickListener(this);
                setApp= (TextView) findViewById(R.id.set_app);
        setApp.setOnClickListener(this);
                setAccessible= (TextView) findViewById(R.id.set_accessible);
        setAccessible .setOnClickListener(this);
        setDeveloperOptions= (TextView) findViewById(R.id.set_developer_options);
        setDeveloperOptions.setOnClickListener(this);
        setAboutDevice= (TextView) findViewById(R.id.set_about_device);
        setAboutDevice.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_wifi:
                Intent wifi = new Intent();
                wifi.setAction(Settings.ACTION_WIFI_SETTINGS);
                startActivity(wifi);
                break;
            case R.id.set_bluetooth:
                Intent nav_bluetooth = new Intent();
                nav_bluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(nav_bluetooth);
                break;
            case R.id.set_condition:
                Intent mIntent = new Intent();
                mIntent.setAction(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                startActivity(mIntent);

                break;
            case R.id.set_more:

                Intent ACTION_AIRPLANE_MODE_SETTINGS =  new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                startActivity(ACTION_AIRPLANE_MODE_SETTINGS);

                break;




            case R.id.set_show:
                Intent ACTION_DISPLAY_SETTINGS =  new Intent(Settings.ACTION_DISPLAY_SETTINGS
                );
                startActivity(ACTION_DISPLAY_SETTINGS);
                break;
            case R.id.set_volume:
                Intent ACTION_SOUND_SETTINGS =  new Intent(Settings.ACTION_VOICE_CONTROL_AIRPLANE_MODE);
                startActivity(ACTION_SOUND_SETTINGS);
                break;
            case R.id.set_safety:
                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                break;
            case R.id.set_date_tine:
                startActivity(new Intent(Settings.ACTION_DATE_SETTINGS  ));
                break;
            case R.id.set_sound_notification:
                startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
                break;
            case R.id.set_language_input:
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS ));
                break;
            case R.id.set_other_more:
                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                break;
            case R.id.set_app:
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                break;
            case R.id.set_accessible:
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;
            case R.id.set_developer_options:
                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS ));
                break;
            case R.id.set_about_device:
                startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
                break;
        }
    }
}
