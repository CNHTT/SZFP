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

import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.ParkingInfoBean;
import com.szfp.szfp.inter.OnSaveListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.ConstantValue.FINGERPRINT;
import static com.szfp.szfp.ConstantValue.FINGERPRINT_END;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_FAIL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_SUCCESS;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_IMAGE;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_MODEL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_PROGRESSDIALOG;

public class ParkingRegisterActivity extends BaseActivity {

    @BindView(R.id.et_prrk_name)
    EditText etPrrkName;
    @BindView(R.id.et_prrk_id_number)
    TextView etPrrkIdNumber;
    @BindView(R.id.et_park_vehicle_number)
    EditText etParkVehicleNumber;
    @BindView(R.id.bt_parking_register_finger)
    StateButton btParkingRegisterFinger;
    @BindView(R.id.bt_parking_register)
    StateButton btParkingRegister;
    @BindView(R.id.bt_paring_cancel)
    StateButton btParingCancel;

    private AsyncFingerprint asyncFingerprint;
    private ProgressDialog progressDialog;

    private ParkingInfoBean bean;
    private String name;
    private String idNumber;
    private String fingerID;
    private String vehicleRegNumber;

    private List<String> list= new ArrayList<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;

                case SHOW_FINGER_IMAGE:
//                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;

                case SHOW_FINGER_MODEL:
                    cancleProgressDialog();
                    break;

                case REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;
                        if (DataUtils.isNullString(fingerID))
                            fingerID=FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;

                        else {
                            //please user StringBuffer
                            fingerID= fingerID+"_"+FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;
                        }
                        list.add(String.valueOf(id));
                        Log.d("PRINT",fingerID);
                        ToastUtils.showToast(getString(R.string.register_success));
                    } else {
                        ToastUtils.showToast(R.string.register_success);
                    }


                    break;
                case REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.error("Registration failed!");
                    break;
                default:
                    cancleProgressDialog();
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
        asyncFingerprint.setStop(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancleProgressDialog();
    }

//    private void showFingerImage(int fingerType, byte[] data) {
//        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//        // saveImage(data);
//        ivFarmerFingerPrint.setBackgroundDrawable(new BitmapDrawable(image));
//    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        for (String s:list) {
            asyncFingerprint.PS_DeleteChar(Integer.valueOf(s),1);
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


    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(),mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
        idNumber = TimeUtils.generateSequenceNo();
        etPrrkIdNumber.setText(idNumber);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_regsiter);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_parking_register_finger, R.id.bt_parking_register, R.id.bt_paring_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_parking_register_finger:
                asyncFingerprint.register2();
                break;
            case R.id.bt_parking_register:

                register();
                break;
            case R.id.bt_paring_cancel:
                onBackPressed();
                break;
        }
    }

    private void register() {
        bean = new ParkingInfoBean();
        name = etPrrkName .getText().toString();
        if (DataUtils.isNullString(name)){
            showErrorToast("Please input name"); return;
        }
        bean.setName(name);
        vehicleRegNumber = etParkVehicleNumber.getText().toString();
        if (DataUtils.isNullString(vehicleRegNumber)||DataUtils.isNullString(fingerID)){
            showErrorToast("Please input Vehicle Reg Number  Or FingerPrint"); return;
        }

        bean.setFingerID(fingerID);
        bean.setVehicleRegNumber(vehicleRegNumber);
        bean.setIdNumber(idNumber);
        DbHelper.insertParking(bean, new OnSaveListener() {
            @Override
            public void success() {
                ToastUtils.success("OK OK OK OK OK OK OK");
                finish();
            }

            @Override
            public void error(String str) {
                showErrorToast(str);
            }
        });

    }
}
