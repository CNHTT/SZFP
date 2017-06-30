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
import android.widget.TextView;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReverseActivity extends BasePrintActivity {


    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.ed_reverse_commuter)
    EditText edReverseCommuter;
    @BindView(R.id.bt_reverse)
    StateButton btReverse;
    @BindView(R.id.ed_reverse_amount)
    EditText edReverseAmount;
    @BindView(R.id.bt_reverse_sava)
    StateButton btReverseSava;
    @BindView(R.id.bt_reverse_cancel)
    StateButton btReverseCancel;
    private CommuterAccountInfoBean bean;


    private byte[] modelByte;
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
                    ReverseActivity.this.modelByte = (byte[]) msg.obj;
                    if (ReverseActivity.this.modelByte != null) {
                        Log.i("whw", "#################model.length="
                                + ReverseActivity.this.modelByte.length);
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;

                        ToastUtils.showToast(ReverseActivity.this,
                                getString(R.string.register_success) + "  pageId="
                                        + id);
                    } else {
                        ToastUtils.showToast(ReverseActivity.this,
                                R.string.register_success);
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(ReverseActivity.this,
                            R.string.register_fail);
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
                            .showToast(ReverseActivity.this, (Integer) msg.obj);
                    // failTime++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    break;
                default:
                    break;
            }
        }

    };

    private void showValidateResult(Integer r) {
        String id = String.valueOf(r);
        if (id.equals(bean.getFingerPrintFileUrl())){
            bean.setDeposits(bean.getDeposits()-inputFloat);
            if (DbHelper.isUpdateCommuterAccountInfoPhoto(bean)){
                ToastUtils.success("SUCCESS");

                bean = DbHelper.getCommuterInfo(bean.getCommuterAccount());
                edReverseAmount.setText("");
            }else {
                ToastUtils.error("ERROR");
            }
        }else {
            ToastUtils.error("Please try again");
        }
    }

    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(ReverseActivity.this,
                    R.string.verifying_through);
        } else {
            ToastUtils.showToast(ReverseActivity.this,
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

        asyncFingerprint.setOnEmptyListener(new AsyncFingerprint.OnEmptyListener() {
            @Override
            public void onEmptySuccess() {
                ToastUtils.showToast(R.string.clear_flash_success);
            }

            @Override
            public void onEmptyFail() {
                ToastUtils.showToast(R.string.clear_flash_fail);
            }
        });


        asyncFingerprint.setOnCalibrationListener(new AsyncFingerprint.OnCalibrationListener() {
            @Override
            public void onCalibrationSuccess() {

                Log.d("whw", "onCalibrationSuccess");
                ToastUtils.showToast(R.string.calibration_success);
            }

            @Override
            public void onCalibrationFail() {

                Log.i("whw", "onCalibrationFail");
                ToastUtils.showToast(R.string.calibration_fail);
            }
        });

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
        setContentView(R.layout.activity_reverse);
        ButterKnife.bind(this);

        bean = (CommuterAccountInfoBean) getIntent().getSerializableExtra(ConstantValue.INFO);

        if (!DataUtils.isEmpty(bean)) {
            edReverseCommuter.setEnabled(false);
            edReverseCommuter.setText(bean.getCommuterAccount());
            btReverse.setEnabled(false);
        }

    }

    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {

    }


    private String   input;
    private float    inputFloat;

    private void save() {

        if (DataUtils.isEmpty(bean)){
            ToastUtils.error("PLEASE FIND COMMUTER");
            return;
        }

        input = edReverseAmount.getText().toString();
        if (DataUtils.isNullString(input)){
            ToastUtils.error("PLEASE INPUT");
            return;
        }
        inputFloat = Float.valueOf(input);

        if (inputFloat>(bean.getDeposits()-bean.getFarePaid())){
            ToastUtils.error("The reverse recharging amount should not exceed the balance");
            return;
        }



        asyncFingerprint.validate2();




    }

    @OnClick({R.id.bt_reverse, R.id.bt_reverse_sava, R.id.bt_reverse_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_reverse:
                String s = edReverseCommuter.getText().toString();
                if (DataUtils.isNullString(s)){
                    ToastUtils.error("PLEASE INPUT");
                    return;
                }
                bean = DbHelper.getCommuterInfo(s);
                if (DataUtils.isEmpty(bean)) ToastUtils.error("ERROR"); else ToastUtils.success("SUCCESS");

                break;
            case R.id.bt_reverse_sava:
                save();
                break;
            case R.id.bt_reverse_cancel:
                break;
        }
    }
}
