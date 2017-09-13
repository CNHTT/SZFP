package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.BankCustomerBean;
import com.szfp.szfp.bean.BankDepositBean;
import com.szfp.szfp.bean.BankRegistrationBean;
import com.szfp.szfp.inter.OnVerifyDepositListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfp.utils.PrintUtils;
import com.szfp.szfplib.inter.OnItemClickListener;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.AlertView;
import com.szfp.szfplib.weight.StateButton;

import java.util.ArrayList;
import java.util.List;

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
import static com.szfp.szfplib.utils.DataUtils.isNullString;

public class BankCashDepositActivity extends BasePrintActivity {

    @BindView(R.id.et_cash_deposit_number)
    EditText etCashDepositNumber;
    @BindView(R.id.et_cash_deposit_name)
    EditText etCashDepositName;
    @BindView(R.id.et_cash_deposit_account)
    EditText etCashDepositAccount;
    @BindView(R.id.bt_cash_deposit_enter)
    StateButton btCashDepositEnter;
    @BindView(R.id.bt_cash_deposit_cancel)
    StateButton btCashDepositCancel;
    @BindView(R.id.bt_comm_print)
    StateButton btCommPrint;
    @BindView(R.id.ll_bank_statement)
    LinearLayout llBankStatement;
    @BindView(R.id.tv_cash_deposit_by)
    TextView tvCashDepositBy;

    private String acNumber;
    private String acName;
    private String deposiyed;
    private String number;
    private String finegerId;

    private BankDepositBean bean;

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
        DbHelper.getBankCustomer(String.valueOf(r),bean, new OnVerifyDepositListener() {
            @Override
            public void success(BankDepositBean bean) {
                ToastUtils.success("OKOKOK");
                if (isPrint)PrintUtils.printBankCash(bean);
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
        setContentView(R.layout.activity_bank_cash_deposit);
        ButterKnife.bind(this);

        initEvent();
    }
    BankCustomerBean sbean;
    private void initEvent() {
        etCashDepositNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    acNumber = etCashDepositNumber.getText().toString();
                    if (!DataUtils.isNullString(acNumber))
                    sbean=  DbHelper.selectBankCashDeposit(acNumber);

                    if (!DataUtils.isEmpty(sbean))
                        etCashDepositName.setText(sbean.getName());
                }
            }
        });
    }

    @Override
    protected void showConnecting() {
        btCommPrint.setText("Connecting...");
    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        btCommPrint.setText("Conn to "+mConnectedDeviceName);
        isPrint = true;
    }


    private List<BankRegistrationBean> bankList;
    private String[] bankNameList;

    @OnClick({R.id.tv_cash_deposit_by, R.id.bt_comm_print, R.id.bt_cash_deposit_enter, R.id.bt_cash_deposit_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cash_deposit_by:
                bankList = new ArrayList<>();
                bankList = DbHelper.getAllBank();
                if (DataUtils.isEmpty(bankList)){
                    showErrorToast(" NO Bank");
                }else {
                    bankNameList = new String[bankList.size()];
                    for (int i = 0; i <bankList.size() ; i++) {
                        bankNameList[i]=bankList.get(i).getBankName();
                    }
                }
                new AlertView(null, null, null, bankNameList, null,
                        this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        tvCashDepositBy.setText(bankNameList[position]);
                    }
                }).show();



                break;
            case R.id.bt_comm_print:
                if (BluetoothPrintDriver.IsNoConnection()){
                showDeviceList();
            }
                break;
            case R.id.bt_cash_deposit_enter:
                save();
                break;
            case R.id.bt_cash_deposit_cancel:
                onBackPressed();
                break;
        }
    }

    private void save() {
        acNumber =etCashDepositNumber.getText().toString();
        acName  = etCashDepositName.getText().toString();
        deposiyed = tvCashDepositBy.getText().toString();
        number = etCashDepositAccount.getText().toString();

        if (isNullString(acName)||isNullString(acName)||isNullString(deposiyed)||isNullString(number)){
            showErrorToast("Please Input");
            return;
        }
        bean = new BankDepositBean();
        bean.setAcNumber(acNumber);
        bean.setAcName(acName);
        bean.setBankName(deposiyed);
        bean.setCashNumber(Float.valueOf(number));
        asyncFingerprint.validate2();

    }
}
