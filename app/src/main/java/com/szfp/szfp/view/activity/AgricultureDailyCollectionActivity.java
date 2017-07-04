package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.bean.AgricultureFarmerCollection;
import com.szfp.szfp.inter.OnVerifyDailyCollectionListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfp.utils.PrintUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_FAIL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_SUCCESS;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_MODEL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_PROGRESSDIALOG;
import static com.szfp.szfp.asynctask.AsyncFingerprint.UP_IMAGE_RESULT;
import static com.szfp.szfp.asynctask.AsyncFingerprint.VALIDATE_RESULT1;
import static com.szfp.szfp.asynctask.AsyncFingerprint.VALIDATE_RESULT2;

public class AgricultureDailyCollectionActivity extends BasePrintActivity {

    @BindView(R.id.et_amount_collected)
    EditText etAmountCollected;
    @BindView(R.id.bt_farmer_save)
    StateButton btFarmerSave;
    @BindView(R.id.bt_farmer_cancel)
    StateButton btFarmerCancel;
    @BindView(R.id.ck_daily_collection)
    CheckBox ckDailyCollection;
    @BindView(R.id.tv_result)
    TextView tvResult;

    private String input;
    private boolean isPrint = false;

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
                case SHOW_FINGER_MODEL:
                    cancleProgressDialog();
                    break;
                case REGISTER_SUCCESS:
                    cancleProgressDialog();//注册成功
                    break;
                case REGISTER_FAIL:
                    cancleProgressDialog();
                    break;//注册失败
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
                    ToastUtils.showToast(String.valueOf((Integer) msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancleProgressDialog();
        asyncFingerprint.setStop(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancleProgressDialog();
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(), mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
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


    /**
     * 验证是否注册
     *
     * @param r
     */
    private void showValidateResult(final Integer r) {
        DbHelper.checkAgricultureFramer(String.valueOf(r), input,new OnVerifyDailyCollectionListener() {

            @Override
            public void success(AgricultureFarmerBean bean, AgricultureFarmerCollection result) {
                etAmountCollected.setText("");
                tvResult.setText(bean.toString());
                tvResult.append(result.toString());
                if (isPrint) PrintUtils.printAgricultureDailyCollection(bean,result);
            }

            @Override
            public void error(String str) {
                showErrorToast(str);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture_daily_collection);
        ButterKnife.bind(this);

        ckDailyCollection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (BluetoothPrintDriver.IsNoConnection())
                        ckDailyCollection.setChecked(false);
                    showDeviceList();
                } else {
                    isPrint = false;
                }
            }
        });
    }

    @Override
    protected void showConnecting() {
        ckDailyCollection.setText("Connecting...");
    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        ckDailyCollection.setChecked(true);
        isPrint =true;
        ckDailyCollection.setText("Conn to " + mConnectedDeviceName);
    }

    @OnClick({R.id.bt_farmer_save, R.id.bt_farmer_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_farmer_save:
                input = etAmountCollected .getText().toString();
                if (DataUtils.isNullString(input)){showErrorToast("Please input number"); return;}
                asyncFingerprint.validate2();
                break;
            case R.id.bt_farmer_cancel:
                onBackPressed();
                break;
        }
    }
}
