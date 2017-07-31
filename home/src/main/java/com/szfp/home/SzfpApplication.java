package com.szfp.home;

import android.app.Application;

import com.szfp.szfplib.utils.Utils;

/**
 * 作者：ct on 2017/6/22 10:19
 * 邮箱：cnhttt@163.com
 */

public class SzfpApplication extends Application {



    @Override
    public void onCreate() {

        super.onCreate();
        Utils.init(getApplicationContext());

    }

}
