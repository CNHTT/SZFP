package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.inter.OnSaveListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StaticFareActivity extends BaseActivity implements OnSaveListener {

    @BindView(R.id.ed_fixed_fare)
    EditText edFixedFare;
    @BindView(R.id.bt_stat_sava)
    StateButton btStatSava;
    @BindView(R.id.bt_stat_cancel)
    StateButton btStatCancel;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
                        Log.i("whw", "#################model.length="
                                +  ((byte[]) msg.obj).length);
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;

                        ToastUtils.showToast(getString(R.string.register_success) + "  pageId="
                                + id);
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
                    if (r != -1) {
                        ToastUtils.success(
                                getString(R.string.verifying_through) + "  pageId="
                                        + r);
                        showValidateResult(r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    ToastUtils
                            .showToast(String.valueOf( (Integer) msg.obj));
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
    private void showValidateResult(Integer r) {
        String id = String.valueOf(r);
        bean = DbHelper.getCommuterInfo(id,input,this);
    }

    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(
                    R.string.verifying_through);
        } else {
            ToastUtils.showToast(
                    R.string.verifying_fail);
        }
    }
    private ProgressDialog progressDialog;
    private AsyncFingerprint asyncFingerprint;
    private void showProgressDialog(int resId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.setCanceledOnTouchOutside(false);
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
        progressDialog.show();
    }


    private void cancleProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(),mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancleProgressDialog();
        asyncFingerprint.setStop(true);
    }

    @Override
    protected void onDestroy() {
        cancleProgressDialog();
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_fare);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_stat_sava, R.id.bt_stat_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_stat_sava:
                save();
                break;
            case R.id.bt_stat_cancel:
                edFixedFare.setText("");
                break;
        }
    }

    private String input;
    private void save() {
        input =edFixedFare.getText().toString();
        if (DataUtils.isNullString(input)){
            ToastUtils.error(getResources().getString(R.string.please_input));
            return;
        }

        asyncFingerprint.validate2();

    }

    @Override
    public void success() {
        ToastUtils.success("SUCCESS");
        onBackPressed();
    }

    @Override
    public void error(String str) {
        ToastUtils.error(str);

    }
}
