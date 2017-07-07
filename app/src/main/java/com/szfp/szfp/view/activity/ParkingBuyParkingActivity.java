package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.ParkingInfoBean;
import com.szfp.szfp.inter.OnSaveByParkingListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.SPUtils;
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

public class ParkingBuyParkingActivity extends BasePrintActivity {

    @BindView(R.id.bt_show_day)
    StateButton btShowDay;
    @BindView(R.id.bt_show_month)
    StateButton btShowMonth;
    @BindView(R.id.day_remove)
    ImageView dayRemove;
    @BindView(R.id.day_number)
    TextView dayNumber;
    @BindView(R.id.day_add)
    ImageView dayAdd;
    @BindView(R.id.ll_day)
    LinearLayout llDay;
    @BindView(R.id.month_remove)
    ImageView monthRemove;
    @BindView(R.id.month_number)
    TextView monthNumber;
    @BindView(R.id.month_add)
    ImageView monthAdd;
    @BindView(R.id.ll_month)
    LinearLayout llMonth;
    @BindView(R.id.ck_parking_conn_print)
    CheckBox ckParkingConnPrint;
    @BindView(R.id.bt_parking_cash)
    StateButton btParkingCash;
    @BindView(R.id.bt_parking_confirm)
    StateButton btParkingConfirm;
    @BindView(R.id.bt_parking_cancel)
    StateButton btParkingCancel;
    @BindView(R.id.day_pay)
    TextView dayPay;
    @BindView(R.id.month_pay)
    TextView monthPay;


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


    private void showValidateResult(Integer r) {
        DbHelper.updateParkingInfo(String.valueOf(r),num,isDay,dayFee,monthFee, new OnSaveByParkingListener() {
            @Override
            public void success(ParkingInfoBean bean, String s) {
                ToastUtils.success("OK");
            }

            @Override
            public void error(String str) {
                ToastUtils.error(str);
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patking_buy_parking);
        ButterKnife.bind(this);
        initView();
    }

    private String dayFee;
    private String monthFee;
    private void initView() {
        if (DataUtils.isNullString(SPUtils.getString(this, ConstantValue.PARING_FEE_DAY))){
            onBackPressed();
            ToastUtils.error("please set Fee");
        }
        dayFee = SPUtils.getString(this, ConstantValue.PARING_FEE_DAY);
        monthFee = SPUtils.getString(this,ConstantValue.PARING_FEE_MONTH);
        dayPay.setText(dayFee);
        monthPay.setText(monthFee);
    }

    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {

    }

    private int dayNum;
    private int monthNum;
    private boolean isDay = true;

    @OnClick({R.id.bt_show_day, R.id.bt_show_month, R.id.day_remove, R.id.day_add, R.id.month_remove, R.id.month_add, R.id.bt_parking_confirm, R.id.bt_parking_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_show_day:
                llDay.setVisibility(View.VISIBLE);
                llMonth.setVisibility(View.GONE);
                isDay = true;
                break;
            case R.id.bt_show_month:
                llMonth.setVisibility(View.VISIBLE);
                llDay.setVisibility(View.GONE);
                isDay = false;
                break;
            case R.id.day_remove:
                dayNum = Integer.valueOf(dayNumber.getText().toString());
                if (dayNum > 1) {
                    dayNum--;
                    dayNumber.setText(String.valueOf(dayNum));
                }
                break;
            case R.id.day_add:
                dayNum = Integer.valueOf(dayNumber.getText().toString());
                dayNum++;
                dayNumber.setText(String.valueOf(dayNum));
                break;
            case R.id.month_remove:

                monthNum = Integer.valueOf(monthNumber.getText().toString());
                if (monthNum > 1) {
                    monthNum--;
                    monthNumber.setText(String.valueOf(monthNum));
                }
                break;
            case R.id.month_add:
                monthNum = Integer.valueOf(monthNumber.getText().toString());
                monthNum++;
                monthNumber.setText(String.valueOf(monthNum));
                break;


            case R.id.bt_parking_confirm:
                save();
                break;
            case R.id.bt_parking_cancel:
                break;
        }
    }

    private int num;

    private void save() {

        if (isDay) {

            num = Integer.valueOf(dayNumber.getText().toString());
            if (num>0){
             asyncFingerprint.validate2();
            }
        } else {
            num = Integer.valueOf(monthNumber.getText().toString());
            if (num>0){
                asyncFingerprint.validate2();
            }
        }

    }
}
