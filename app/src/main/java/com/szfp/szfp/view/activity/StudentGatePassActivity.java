package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.StudentBean;
import com.szfp.szfp.bean.StudentStaffBean;
import com.szfp.szfp.inter.OnStaffGatePassVerify;
import com.szfp.szfp.inter.OnStudentGatePassVerify;
import com.szfp.szfp.utils.DbHelper;
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

public class StudentGatePassActivity extends BaseActivity implements OnStaffGatePassVerify ,OnStudentGatePassVerify{

    @BindView(R.id.bt_staff_gate_pass)
    StateButton btStaffGatePass;
    @BindView(R.id.bt_student_gate_pass)
    StateButton btStudentGatePass;

    private ProgressDialog progressDialog;
    private AsyncFingerprint asyncFingerprint;
    private boolean isStudent =true;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
                case  REGISTER_FAIL:
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_gate_pass);
        ButterKnife.bind(this);
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
     * @param r
     */
    private void showValidateResult(Integer r) {
        if (isStudent)//student
        {
            DbHelper.getStudentInfo(String.valueOf(r),this);
        }else {//staff
            DbHelper.getStaffInfo(String.valueOf(r),this);
        }
    }


    @OnClick({R.id.bt_staff_gate_pass, R.id.bt_student_gate_pass})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_staff_gate_pass:
                isStudent=false;
                asyncFingerprint.validate2();
                break;
            case R.id.bt_student_gate_pass:
                asyncFingerprint.validate2();
                isStudent=true;
                break;
        }
    }

    @Override
    public void studentGatePassSuccess(StudentBean bean) {

        Intent intent = new Intent(StudentGatePassActivity.this,StudentWillActivity.class) ;
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantValue.INFO,bean);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void studentGatePassError(String str) {
        ToastUtils.error(str);

    }

    @Override
    public void staffGatePassSuccess(StudentStaffBean bean) {

        Intent intent = new Intent(StudentGatePassActivity.this,StudentStaffWillActivity.class) ;
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantValue.INFO,bean);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void staffGatePassError(String str) {
        ToastUtils.error(str);
    }
}
