package com.szfp.szfp.view.impl;

import android.app.Activity;
import android.content.Context;

import com.szfp.szfplib.view.impl.IBaseView;

/**
 * 作者：ct on 2017/6/19 10:04
 * 邮箱：cnhttt@163.com
 */

public interface ILoginView extends IBaseView {



    String getUid();
    String getPass();
    Context getContext();
    Activity getActivity();
    void loginSuccess();
    void loginError();

}
