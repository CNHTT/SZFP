package com.szfp.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.StatusBarUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {
    Toolbar mToolbar;
    DrawerLayout drawerLayout;
    @BindView(R.id.main)
    LinearLayout main;
    GridView gridView;
    NavigationView navigationView;
    private int mStatusBarColor;
    private int mAlpha = 0;

    private ApkAdapter apkAdapter;

    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //数据初始化
        initView();
        initData();

        Log.e("W H", "Width:" + ContextUtils.getSreenWidth(this) + "Height: " + ContextUtils.getSreenHeight(this));
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("WangJ", "状态栏-方法1:" + statusBarHeight1);


    }

    private void initView() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        gridView = (GridView) findViewById(R.id.gridView);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        StatusBarUtil.setTranslucentForDrawerLayout(MainActivity.this, drawerLayout, mAlpha);
        mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_wifi:
                        Intent wifi = new Intent();
                        wifi.setAction(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(wifi);
                        break;
//                    case R.id.nav_app:
//                        Intent APP = getPackageManager().getLaunchIntentForPackage("com.estrongs.android.pop");
//                        startActivity(APP);
//                        break;
                    case R.id.nav_bluetooth:
                        Intent nav_bluetooth = new Intent();
                        nav_bluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(nav_bluetooth);
                        break;
                    case R.id.nav_camera:
                        Intent intent = new Intent(); //调用照相机
                        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                        startActivity(intent);
                        break;
                    case R.id.nav_gallery:
                        Intent nav_gallery = getPackageManager().getLaunchIntentForPackage("com.android.gallery3d");
                        startActivity(nav_gallery);
                        break;
                    case R.id.nav_manage:
                        Intent setting =  new Intent(Settings.ACTION_SETTINGS);
                        startActivity(setting);
                        break;
                }
                return false;
            }
        });


    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //扫描得到APP列表
                final List<AppInfo> appInfos = ApkTools.getApkList(MainActivity.this.getPackageManager());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        apkAdapter = new ApkAdapter(MainActivity.this,appInfos);
                        gridView.setAdapter(apkAdapter);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //扫描得到APP列表
                final List<AppInfo> appInfos = ApkTools.getApkList(MainActivity.this.getPackageManager());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        apkAdapter = new ApkAdapter(MainActivity.this,appInfos);
                        gridView.setAdapter(apkAdapter);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void setStatusBar() {
        mStatusBarColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setTranslucentForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout));
        StatusBarUtil.setTranslucent(this, 0);

    }
}
