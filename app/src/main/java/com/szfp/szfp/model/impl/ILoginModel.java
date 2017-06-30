package com.szfp.szfp.model.impl;

import android.content.Context;

import com.szfp.szfp.inter.OnLoginListener;
import com.szfp.szfplib.model.impl.BaseModel;

/**
 * 作者：ct on 2017/6/19 09:56
 * 邮箱：cnhttt@163.com
 */

public interface ILoginModel extends BaseModel
{
    public void  LoginNet(Context context , String id, String pass, OnLoginListener listener);
}
