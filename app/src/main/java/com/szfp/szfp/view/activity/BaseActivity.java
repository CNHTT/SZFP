package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
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

    void  showToastError(String str){
        ToastUtils.error(this,str).show();
    }
    void  showToastError(){
        ToastUtils.error(this,"please input").show();
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
}