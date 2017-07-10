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
import com.szfp.szfp.inter.OnVerifyPaymentListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfp.utils.PrintUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.util.ArrayList;

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

public class AgriculturePaymentActivity extends BasePrintActivity {

    @BindView(R.id.et_amount_payment)
    EditText etAmountPayment;
    @BindView(R.id.ck_daily_payment)
    CheckBox ckDailyPayment;
    @BindView(R.id.bt_payment_save)
    StateButton btPaymentSave;
    @BindView(R.id.bt_payment_cancel)
    StateButton btPaymentCancel;
    @BindView(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriclture_payment);
        ButterKnife.bind(this);


        ckDailyPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (BluetoothPrintDriver.IsNoConnection())
                        ckDailyPayment.setChecked(false);
                    showDeviceList();
                } else {
                    isPrint = false;
                }
            }
        });
    }

    @Override
    protected void showConnecting() {
        ckDailyPayment.setText("Connecting...");
    }

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
        DbHelper.verifyAgricultureFramer(String.valueOf(r), new OnVerifyPaymentListener() {
            @Override
            public void success(AgricultureFarmerBean bean, ArrayList<AgricultureFarmerCollection> list) {
                tvResult.setText("Name: " +bean.getName()+"\n");
                tvResult.append("ID NUMBER: " +bean.getIDNumber()+"\n");
                tvResult.append("Amount paid: " +input+"\n");
                tvResult.append("NumberOfAnimals " +bean.getNumberOfAnimals()+"\n");
                tvResult.append("Should be paid: " +DataUtils.getAmountValue(bean.getAmount())+"\n\n");
                tvResult.append("provide basic reports \n");

                tvResult.append("Time           AmountCollected" +"\n");
                for (AgricultureFarmerCollection c:list) {
                    tvResult.append(TimeUtils.milliseconds2String(c.getTime())+"     "+c.getAmountCollected()+"\n");
                }
                if (isPrint) PrintUtils.printAgriculturePayment(bean,list,input);

            }
            @Override
            public void error(String str) {
                showErrorToast(str);
            }
        });
    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        ckDailyPayment.setChecked(true);
        isPrint = true;
        ckDailyPayment.setText("Conn to " + mConnectedDeviceName);

    }

    @OnClick({R.id.bt_payment_save, R.id.bt_payment_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_payment_save:
//                input =etAmountPayment.getText().toString();
//                if (DataUtils.isEmpty(input)){
//                    showErrorToast("Please input Amount");
//                    return;
//                }
                asyncFingerprint.validate2();
                break;
            case R.id.bt_payment_cancel:
                break;
        }
    }
}
