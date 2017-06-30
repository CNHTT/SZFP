package com.szfp.szfp.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.R;
import com.szfp.szfp.presenter.LoginPresenter;
import com.szfp.szfp.view.impl.ILoginView;
import com.szfp.szfplib.inter.onRequestPermissionsListener;
import com.szfp.szfplib.presenter.BasePresenter;
import com.szfp.szfplib.utils.PermissionsUtils;
import com.szfp.szfplib.view.activity.BaseActivity;
import com.szfp.szfplib.weight.StateButton;

public class LoginActivity extends BaseActivity<LoginPresenter>  implements ILoginView, View.OnClickListener {

    private EditText enterID;
    private EditText enterPass;

    private StateButton button;


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this ;
    }

    @Override
    public BasePresenter getPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void bindView(Bundle savedInstanceState) {

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {

        PermissionsUtils.requestWriteExternalStorage(this, new onRequestPermissionsListener() {
            @Override
            public void onRequestBefore() {

            }

            @Override
            public void onRequestLater() {

            }
        });

    }

    @Override
    protected void initViewFindById() {
        button = (StateButton) findViewById(R.id.loginButton);


        button.setOnClickListener(this);

    }

    @Override
    public int getToolBarId() {
        return 0;
    }

    @Override
    public String getUid() {
        return "Q";
    }

    @Override
    public String getPass() {
        return "0";

    }

    @Override
    public void loginSuccess() {
        cancelLoading();
        startActivity(new Intent(this,MainActivity.class));
        finish();

    }

    @Override
    public void loginError() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.loginButton:
                showLoading();
                ((LoginPresenter)mPresenter).login();
                break;
            default:
        }
    }
}
