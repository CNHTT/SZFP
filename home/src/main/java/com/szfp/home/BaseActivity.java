package com.szfp.home;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.szfp.szfplib.utils.StatusBarUtil;

/**
 * 项目名称：SZFP.
 * 创建人： CT.
 * 创建时间: 2017/7/30.
 * GitHub:https://github.com/CNHTT
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this,getResources().getColor(R.color.light_orange));
    }
}
