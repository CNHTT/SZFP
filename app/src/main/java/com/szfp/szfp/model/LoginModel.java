package com.szfp.szfp.model;

import android.content.Context;

import com.szfp.szfp.inter.OnLoginListener;
import com.szfp.szfp.model.impl.ILoginModel;

/**
 * 作者：ct on 2017/6/19 09:56
 * 邮箱：cnhttt@163.com
 */

public class LoginModel implements ILoginModel {
    @Override
    public void LoginNet(Context context, String id, String pass, final OnLoginListener listener) {

//        Observable.timer(3, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Long value) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
                listener.loginSuccess();
//            }
//        });

    }
}
