package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.BankCustomerBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankCustomerRegisterActivity extends BaseActivity {


    @BindView(R.id.ed_fosa_account)
    EditText edFosaAccount;
    @BindView(R.id.ed_customer_names)
    EditText edCustomerNames;
    @BindView(R.id.b_c_male)
    RadioButton bCMale;
    @BindView(R.id.b_c_female)
    RadioButton bCFemale;
    @BindView(R.id.b_c_Sex)
    RadioGroup bCSex;
    @BindView(R.id.tv_Marital_Status)
    EditText tvMaritalStatus;
    @BindView(R.id.ed_bank_customer_contacts)
    EditText edBankCustomerContacts;
    @BindView(R.id.ed_bank_branch)
    EditText edBankBranch;
    @BindView(R.id.bt_bank_customer_register)
    StateButton btBankCustomerRegister;
    @BindView(R.id.bt_bank_customer_cancel)
    StateButton btBankCustomerCancel;
    @BindView(R.id.bank_customer_fingerphoto)
    ImageView bankCustomerFingerphoto;


    private BankCustomerBean bean;

    private AsyncFingerprint asyncFingerprint;
    private ProgressDialog progressDialog;
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
                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;
                case AsyncFingerprint.SHOW_FINGER_MODEL:

                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;
                        fingerPrintFileUrl = String.valueOf(id);

                        ToastUtils.showToast(
                                getString(R.string.register_success) + "  pageId="
                                        + id);
                    } else {
                        ToastUtils.showToast(
                                R.string.register_success);
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(
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
                        ToastUtils.showToast(
                                getString(R.string.verifying_through) + "  pageId="
                                        + r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    // failTime++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    break;
                default:
                    break;
            }
        }

    };

    private void showFingerImage(int fingerType, byte[] data) {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        // saveImage(data);
        bankCustomerFingerphoto.setBackgroundDrawable(new BitmapDrawable(image));
    }

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
        cancleProgressDialog();
        super.onDestroy();
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(), mHandler);

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

    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(
                    R.string.verifying_through);
        } else {
            ToastUtils.showToast(
                    R.string.verifying_fail);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_customer_register);
        ButterKnife.bind(this);

        bean = new BankCustomerBean();
        bean.setRegisterTimeStr(TimeUtils.getCurTimeString());
        bean.setRegisterTime(TimeUtils.getCurTimeMills());
        bean.setNationalId(TimeUtils.generateSequenceNo());
        initViewEvent();
    }

    private void initViewEvent() {
        bCSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.b_c_female) gender = false;
            }
        });
    }

    private String fosaAccount;
    private boolean gender = true;
    private String number;
    private String contacts;
    private String branch;
    private String name;
    private String email;
    private String fingerPrintFileUrl;
    private String maritalStatus;
    private String photoFileUrl;

    private void register() {
        bean.setGender(gender);

        fosaAccount = edFosaAccount.getText().toString();
        if (DataUtils.isNullString(fosaAccount)) {
            ToastUtils.error("PLEASE INPUT");
            return;
        }
        bean.setFosaAccount(fosaAccount);

        name = edCustomerNames.getText().toString();
        if (DataUtils.isNullString(name)) {
            ToastUtils.error("PLEASE INPUT");
            return;
        }
        bean.setName(name);

        maritalStatus = tvMaritalStatus.getText().toString();
        if (DataUtils.isNullString(maritalStatus)) {
            ToastUtils.error("PLEASE INPUT");
            return;
        }
        bean.setMaritalStatus(maritalStatus);

        contacts = edBankCustomerContacts.getText().toString();
        if (DataUtils.isNullString(contacts)) {
            ToastUtils.error("PLEASE INPUT");
            return;
        }
        bean.setContacts(contacts);
        branch = edBankBranch.getText().toString();
        if (DataUtils.isNullString(branch)) {
            ToastUtils.error("PLEASE INPUT");
            return;
        }
        bean.setBranch(branch);

        bean.setFingerPrintFileUrl(fingerPrintFileUrl);

        if (DbHelper.insertBankCustomer(bean)) {
            ToastUtils.success("SUCCESS");
            onBackPressed();
        } else {
            ToastUtils.error("ERROR");
        }
    }

    @OnClick({R.id.bt_bank_customer_register, R.id.bt_bank_customer_cancel,R.id.bank_customer_fingerphoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_bank_customer_register:
                register();
                break;
            case R.id.bt_bank_customer_cancel:
                break;
            case R.id.bank_customer_fingerphoto:
                asyncFingerprint.register2();
                break;
        }
    }
}
