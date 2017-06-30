package com.szfp.szfplib.inter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szfp.szfplib.presenter.BasePresenter;

/**
 * 作者：ct on 2017/6/16 15:36
 * 邮箱：cnhttt@163.com
 */

public interface IBase {
    BasePresenter getPresenter();

    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void bindView(Bundle savedInstanceState);

    View getView();

    int getContentLayout();
}
