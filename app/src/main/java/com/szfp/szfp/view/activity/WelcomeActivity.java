package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.szfp.szfp.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG ="WelcomeActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


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

        Log.d("W H ",width +"sdasdas"+height);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_welcome);
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG,"onSubscribe");
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG,"onNext");
                        //正常接收数据调用
                        System.out.print(value);  //将接收到来自sender的问候"Hi，Weavey！"
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete");
                        //数据接收完成时调用
                        toLogin();
                    }
                });
    }

    private void toLogin()
    {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
