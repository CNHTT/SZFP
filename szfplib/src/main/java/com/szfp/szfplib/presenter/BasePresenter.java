package com.szfp.szfplib.presenter;

import com.szfp.szfplib.view.impl.IBaseView;

/**
 * 作者：ct on 2017/6/16 15:37
 * 邮箱：cnhttt@163.com
 */
public abstract class BasePresenter<T extends IBaseView> {

    public T mView;

    public void attach(T mView) {
        this.mView = mView;
    }

    public void detachView() {
        if (mView != null) {
            mView = null;
        }
    }
}