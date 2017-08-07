package com.szfp.szfplib.view.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.szfp.szfplib.R;
import com.szfp.szfplib.inter.IBase;
import com.szfp.szfplib.presenter.BasePresenter;
import com.szfp.szfplib.utils.AppManager;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.SystemBarTintManager;
import com.szfp.szfplib.view.impl.IBaseView;

/**
 * 作者：ct on 2017/6/16 15:39
 * 邮箱：cnhttt@163.com
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements IBase {

    /**
     * 主线程
     */
    private long mUIThreadId;

    private void setActionBar(){};

    private void getIntentValue(){};


    /**
     * 是否设置沉浸式
     *
     * @return
     */
    protected boolean isSetStatusBar() {
        return false;
    }


    protected BasePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;//屏幕宽度
        int height = dm.heightPixels;//屏幕高度

        int orientation = getResources().getConfiguration().orientation;

        if (orientation==1){

            if (width <500)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {

            if (width <900)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        mUIThreadId = android.os.Process.myTid();
        AppManager.getAppManager().addActivity(this);
        mPresenter = getPresenter();
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.attach((IBaseView) this);
        }
        initWindow();
        getIntentValue();
        mRootView = createView(null, null, savedInstanceState);
        setContentView(mRootView);

        if (getToolBarId()!=0){
            mToolbar = (Toolbar) findViewById(getToolBarId());
            setSupportActionBar(mToolbar);
        }
        setActionBar();
        initViewFindById();
        init();
        bindView(savedInstanceState);
    }

    protected abstract void init();

    protected abstract void initViewFindById();

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isSetStatusBar()) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.outside_color_trans);
        }
    }
    private SystemBarTintManager tintManager;

    /**
     * add new color to window
     * @param color
     */
    protected void setStatusBarTintRes(int color) {
        if (tintManager != null) {
            tintManager.setStatusBarTintResource(color);
        }
    }

    public abstract int getToolBarId();

    public Toolbar getToolbar() {
        return mToolbar;
    }

    private Toolbar mToolbar;
    protected View mRootView;



    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = ContextUtils.inflate(this, getContentLayout());
        return view;
    }
    /**
     * 获取UI线程ID
     *
     * @return UI线程ID
     */
    public long getUIThreadId() {
        return mUIThreadId;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        mUIThreadId = Process.myTid();
        super.onNewIntent(intent);
    }


    @Override
    public View getView() {
        return mRootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().finishActivity(this);
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.detachView();
            mPresenter = null;
        }
        super.onDestroy();
    }

    private ProgressDialog progressDialog = null;
    public void showLoading(){

        if (progressDialog==null)
        progressDialog = ProgressDialog.show(this, "请稍等...", "获取数据中...", true);
    }
    public void cancelLoading(){
        if (progressDialog!=null)
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
    }

}