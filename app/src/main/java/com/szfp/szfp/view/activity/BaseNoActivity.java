package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.LoadingAlertDialog;

/**
 * author：ct on 2017/7/6 09:31
 * email：cnhttt@163.com
 */

public class BaseNoActivity  extends AppCompatActivity {



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
        handlerThread = application.getHandlerThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

