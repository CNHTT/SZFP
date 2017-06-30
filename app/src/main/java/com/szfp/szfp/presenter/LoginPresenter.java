package com.szfp.szfp.presenter;

import com.szfp.szfp.inter.OnLoginListener;
import com.szfp.szfp.model.LoginModel;
import com.szfp.szfp.model.impl.ILoginModel;
import com.szfp.szfp.view.impl.ILoginView;
import com.szfp.szfplib.presenter.BasePresenter;

/**
 * 作者：ct on 2017/6/19 09:54
 * 邮箱：cnhttt@163.com
 */

public class LoginPresenter extends BasePresenter<ILoginView> implements OnLoginListener{
    @Override
    public void loginSuccess() {
        mView.loginSuccess();
    }

    @Override
    public void loginError(String reason) {

    }

    private ILoginModel mLoginModel;

    public LoginPresenter() {
        this.mLoginModel = new LoginModel();
    }

    public void login(){
        mLoginModel.LoginNet(mView.getContext(),mView.getUid(),mView.getPass(),this);
    }
}
