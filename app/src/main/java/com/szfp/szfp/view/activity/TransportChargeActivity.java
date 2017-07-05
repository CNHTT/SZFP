package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.inter.OnSaveListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.SPUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class TransportChargeActivity extends BasePrintActivity implements OnSaveListener {

    @BindView(R.id.sbt_charge)
    StateButton sbtCharge;
    @BindView(R.id.sbt_charge_print)
    StateButton sbtChargePrint;
    @BindView(R.id.sbt_charge_back)
    StateButton sbtChargeBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_charge);
        ButterKnife.bind(this);
    }

    private Subscription subscription;

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        isStop=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancleProgressDialog();
    }


    private boolean isPrint = false;
    private ProgressDialog progressDialog;
    private AsyncFingerprint asyncFingerprint;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AsyncFingerprint.SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;
                case AsyncFingerprint.SHOW_FINGER_IMAGE:
                    // imageNum++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
//                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;
                case AsyncFingerprint.SHOW_FINGER_MODEL:

                    if ((byte[]) msg.obj != null) {
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;
                        ToastUtils.showToast(getString(R.string.register_success) + "  pageId=" + id);
                    } else {
                        ToastUtils.showToast(R.string.register_success);
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(R.string.register_fail);
                    break;
                case AsyncFingerprint.VALIDATE_RESULT1:
                    cancleProgressDialog();
                    showValidateResult((Boolean) msg.obj);
                    break;
                case AsyncFingerprint.VALIDATE_RESULT2:
                    cancleProgressDialog();
                    Integer r = (Integer) msg.obj;
                    asyncFingerprint.setStop(true);
                    if (r != -1) {
                        showValidateResult(r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    ToastUtils
                            .showToast(String.valueOf((Integer) msg.obj));
                    // failTime++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    break;
                default:
                    break;
            }
        }

    };


    private CommuterAccountInfoBean bean;
    private String input;

    private void showValidateResult(Integer r) {
        String id = String.valueOf(r);
        input = SPUtils.getString(this, ConstantValue.STATIC_FARE);
        bean = DbHelper.getCommuterInfo(id, input, this, isPrint);
    }


    Disposables disposable;
    private void showValidateResult(boolean obj) {
        ToastUtils.error(getResources().getString(R.string.verifying_fail));

       Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long value) {
                        //正常接收数据调用
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (isStop) initData();
                    }
                });
    }

    private void showProgressDialog(int resId) {
        if (resId == R.string.print_finger) {
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    asyncFingerprint.setStop(true);
                }
                return false;
            }
        });
        if(!TransportChargeActivity.this.isFinishing()){
          progressDialog.show();
        }

    }


    private void cancleProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(), mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
        asyncFingerprint.validate2();
    }


    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        if (asyncFingerprint.isStop) {
            asyncFingerprint.validate2();
        }
        isPrint = true;


    }

    @OnClick({R.id.sbt_charge, R.id.sbt_charge_print,R.id.sbt_charge_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sbt_charge:
                if (asyncFingerprint.isStop) {
                    asyncFingerprint.validate2();
                }
                isPrint = false;
                break;
            case R.id.sbt_charge_print:
                if (BluetoothPrintDriver.IsNoConnection())
                    showDeviceList();
                break;
            case R.id.sbt_charge_back:
                Observable.timer(3, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Long value) {
                                //正常接收数据调用
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                stopAsy(asyncFingerprint);
                            }
                        });
                finish();
                break;
        }
    }

    @Override
    public void success() {
        ToastUtils.success("OK OK OK OK");
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long value) {
                        //正常接收数据调用
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (isStop)initData();
                    }
                });
    }

    @Override
    public void error(String str) {
        ToastUtils.error(str);
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long value) {
                        //正常接收数据调用
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (isStop)initData();
                    }
                });
    }

    private boolean isShow = true;

    @Override
    public void onBackPressed() {
        if (isShow) {
            isShow = false;
            sbtChargeBack.setVisibility(View.VISIBLE);

            Observable.timer(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Long value) {
                            //正常接收数据调用
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            isShow =true;
                            if (isStop)sbtChargeBack.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_HOME == keyCode) { //判断是否为home键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isStop =true;
    @Override
    protected void onPause() {
        super.onPause();
        stopAsy(asyncFingerprint);
        isStop = false;

    }
}
