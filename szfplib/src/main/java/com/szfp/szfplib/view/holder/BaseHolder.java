package com.szfp.szfplib.view.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 戴尔 on 2017/6/16.
 */

public abstract class BaseHolder<T> extends RecyclerView.ViewHolder {

    protected View mView;
    protected  T mData;
    protected Context mContext;

    public View getView() {
        return mView;
    }

    public BaseHolder(View view) {
        super(view);
        this.mView = view;
        mContext = this.mView.getContext();
        initView();
    }
    protected abstract void initView();

    public void setData(T mData) {
        this.mData = mData;
    }
}
