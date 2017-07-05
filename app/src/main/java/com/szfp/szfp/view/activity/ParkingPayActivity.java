package com.szfp.szfp.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.ParkingInfoBean;
import com.szfp.szfp.inter.OnVerifyParkingListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_FAIL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_SUCCESS;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_IMAGE;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_MODEL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_PROGRESSDIALOG;
import static com.szfp.szfp.asynctask.AsyncFingerprint.UP_IMAGE_RESULT;
import static com.szfp.szfp.asynctask.AsyncFingerprint.VALIDATE_RESULT1;
import static com.szfp.szfp.asynctask.AsyncFingerprint.VALIDATE_RESULT2;

public class ParkingPayActivity extends BasePrintActivity {

    @BindView(R.id.bt_parking_pay_check)
    StateButton btParkingPayCheck;
    @BindView(R.id.bt_parking_register)
    StateButton btParkingRegister;

    private ProgressDialog progressDialog;
    private AsyncFingerprint asyncFingerprint;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;
                case SHOW_FINGER_IMAGE:
                    // imageNum++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
//                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;
                case SHOW_FINGER_MODEL:

                    if ((byte[]) msg.obj != null) {
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;
                        ToastUtils.showToast(getString(R.string.register_success) + "  pageId=" + id);
                    } else {
                        ToastUtils.showToast(R.string.register_success);
                    }

                    break;
                case REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(R.string.register_fail);
                    break;
                case VALIDATE_RESULT1:
                    cancleProgressDialog();
                    showValidateResult((Boolean) msg.obj);
                    break;
                case VALIDATE_RESULT2:
                    cancleProgressDialog();
                    Integer r = (Integer) msg.obj;
                    if (r != -1) {
                        showValidateResult(r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case UP_IMAGE_RESULT:
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

    /**
     * 验证用户是否注册
     * @param r
     */
    private void showValidateResult(Integer r) {
        DbHelper.getParkingBean(String.valueOf(r), new OnVerifyParkingListener() {
            @Override
            public void success(ParkingInfoBean bean) {
                showParkingInfo(bean);
            }
            @Override
            public void error(String s) {
                showErrorToast(s);
            }
        });
    }


    private TextView textView;
    private StateButton button;
    private Dialog dialog;
    private void showParkingInfo(ParkingInfoBean bean) {
        if (dialog == null){
            dialog = new Dialog(this,R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this,R.layout.dialog_parking_info);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(view,new LinearLayout.LayoutParams(-1,-1));
            textView = (TextView) view.findViewById(R.id.tv_dialog_parking_info);
            button = (StateButton) view.findViewById(R.id.bt_dialog_paring_cancel);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            textView.setText("NAME: "+bean.getName());
            textView.append("ID NUMBER: "+bean.getIdNumber());
            textView.append("VEHICLE REG NUMBER: "+bean.getVehicleRegNumber());
            textView.append("BALANCE:   "+bean.getBalance());
            textView.append("OTHER");
            switch (bean.getParameters_type()){
                case 0:textView.append("no have");break;
                case 1:textView.append("");break;
                case 2:textView.append("");break;
                case 3:textView.append("");break;
            }
        }
        if (!dialog.isShowing())
            dialog.show();

    }

    private void showValidateResult(boolean b) {
        ToastUtils.error(getResources().getString(R.string.verifying_fail));
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncFingerprint.setStop(true);
    }

    private void showProgressDialog(int resId) {
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
        if(!ParkingPayActivity.this.isFinishing()){
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
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_pay);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_parking_pay_check, R.id.bt_parking_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_parking_pay_check:
                asyncFingerprint.validate2();
                break;
            case R.id.bt_parking_register:
                startActivity(new Intent(ParkingPayActivity.this,ParkingRegisterActivity.class));
                break;
        }
    }
}
