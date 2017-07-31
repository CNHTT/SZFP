package com.szfp.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * author：ct on 2017/7/31 11:56
 * email：cnhttt@163.com
 */

public class AppInfo {
    private Drawable image;
    private String appName;
    private Intent intent;
    private String  pageName;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}
