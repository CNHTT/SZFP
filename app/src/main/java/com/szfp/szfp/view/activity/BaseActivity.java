package com.szfp.szfp.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.LoadingAlertDialog;

import android_serialport_api.SerialPortManager;

/**
 * 作者：ct on 2017/6/20 17:17
 * 邮箱：cnhttt@163.com
 */

public abstract class BaseActivity extends AppCompatActivity {



    private LoadingAlertDialog loadingAlertDialog;
    protected SzfpApplication application;
    protected HandlerThread handlerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;//屏幕宽度
        int height = dm.heightPixels;//屏幕高度

        int orientation = getResources().getConfiguration().orientation;

        if (orientation==1){

            if (width <500)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {

            if (width <900)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
         super.onCreate(savedInstanceState);

        application = (SzfpApplication) getApplicationContext();
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (!SerialPortManager.getInstance().isOpen()){
            SerialPortManager.getInstance().openSerialPort();
        }
        Log.i("whw", "onResume=" + SerialPortManager.getInstance().isOpen());
        handlerThread = application.getHandlerThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SerialPortManager.getInstance().closeSerialPort();
        handlerThread = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void  showToastError(String str){
        ToastUtils.error(this,str).show();
    }
    void  showToastError(){
        showErrorToast("Please Input");
    }


    public   void showLoadingDialog(){
        if (loadingAlertDialog==null)
            loadingAlertDialog = new LoadingAlertDialog(this);
        loadingAlertDialog.show("加载中...");
    }

    public void dismissLoadingDialog(){
        if (loadingAlertDialog!=null)loadingAlertDialog.dismiss();
    }
    public void stopAsy(AsyncFingerprint asyncFingerprint){
        asyncFingerprint.setStop(true);
    };
    void showErrorToast(String s){
        ToastUtils.error(s);
    }
}
