package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.inter.onRequestPermissionsListener;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.PermissionsUtils;
import com.szfp.szfplib.utils.PhotoUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.ConstantValue.FINGERPRINT;
import static com.szfp.szfp.ConstantValue.FINGERPRINT_END;

public class CommuterActivity extends BaseActivity {

    @BindView(R.id.ed_comm_commuter_account)
    EditText edCommCommuterAccount;
    @BindView(R.id.ed_comm_first_name)
    EditText edCommFirstName;
    @BindView(R.id.ed_comm_last_name)
    EditText edCommLastName;
    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ed_national_id)
    EditText edNationalId;
    @BindView(R.id.comm_photo)
    ImageView commPhoto;
    @BindView(R.id.comm_finger_photo)
    ImageView commFingerPhoto;
    @BindView(R.id.bt_comm_register)
    StateButton btCommRegister;
    @BindView(R.id.bt_comm_cancel)
    StateButton btCommCancel;
    @BindView(R.id.ed_phone)
    EditText edPhone;

    private CommuterAccountInfoBean bean;

    private DbHelper dbHelper;

    private Context context;

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
                    CommuterActivity.this.modelByte = (byte[]) msg.obj;
                    if (CommuterActivity.this.modelByte != null) {
                        Log.i("whw", "#################model.length="
                                + CommuterActivity.this.modelByte.length);
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;

                        if (DataUtils.isNullString(fingerPrintFileUrl))
                        fingerPrintFileUrl=FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;

                        else {
                            //please user StringBuffer
                            fingerPrintFileUrl= fingerPrintFileUrl+"_"+FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;
                        }

                        ToastUtils.showToast(CommuterActivity.this,
                                getString(R.string.register_success) + "  pageId="
                                        + id);
                    } else {
                        ToastUtils.showToast(CommuterActivity.this,
                                R.string.register_success);
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(CommuterActivity.this,
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
                        ToastUtils.showToast(CommuterActivity.this,
                                getString(R.string.verifying_through) + "  pageId="
                                        + r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    ToastUtils
                            .showToast(CommuterActivity.this, (Integer) msg.obj);
                    // failTime++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    break;
                default:
                    break;
            }
        }

    };

    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(CommuterActivity.this,
                    R.string.verifying_through);
        } else {
            ToastUtils.showToast(CommuterActivity.this,
                    R.string.verifying_fail);
        }
    }

    private void showFingerImage(int fingerType, byte[] data) {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        // saveImage(data);
        commFingerPhoto.setBackgroundDrawable(new BitmapDrawable(image));
//        writeToFile(data);
    }


    private CommuterAccountInfoBean infoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commuter);
        ButterKnife.bind(this);
        context =this;
        bean = new CommuterAccountInfoBean();
        dbHelper = new DbHelper();


        infoBean = (CommuterAccountInfoBean) getIntent().getSerializableExtra(ConstantValue.INFO);
        if (DataUtils.isEmpty(infoBean)){
            nationalID = TimeUtils.generateSequenceNo();
            bean.setNationalID(nationalID);
            edNationalId.setText(nationalID);
            edNationalId.setEnabled(false);
        }else {
            nationalID = infoBean.getNationalID();
            edNationalId.setText(nationalID);
            edNationalId.setEnabled(false);
            Glide.with(context).
                    load(Uri.parse(infoBean.getPhotoFileUrl())).
                    diskCacheStrategy(DiskCacheStrategy.RESULT).
                    thumbnail(0.5f).
                    placeholder(R.drawable.ic_check_white_48dp).
                    priority(Priority.LOW).
                    error(R.drawable.linecode_icon).
                    fallback(R.drawable.ic_clear_white_48dp).
                    dontAnimate().
                    into(commPhoto);

            commFingerPhoto.setEnabled(false);

            commuterAccount = infoBean.getCommuterAccount();
            if (!DataUtils.isNullString(commuterAccount))edCommCommuterAccount.setText(commuterAccount);

            firstName =infoBean.getFirstName();
            if (!DataUtils.isNullString(firstName)) edCommFirstName.setText(firstName);


            lastName =infoBean.getLastName();
            if (!DataUtils.isNullString(lastName))  edCommLastName.setText(lastName);


            email =infoBean.getEmail();
            if (!DataUtils.isNullString(email))edEmail.setText(email);

            mobile = infoBean.getMobile();
            if (!DataUtils.isNullString(mobile))edPhone.setText(mobile);

            btCommRegister.setText("UPDATE");

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

    @OnClick({R.id.comm_photo, R.id.comm_finger_photo, R.id.bt_comm_register, R.id.bt_comm_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comm_photo:
                PermissionsUtils.requestCamera(context, new onRequestPermissionsListener() {
                    @Override
                    public void onRequestBefore() {

                    }

                    @Override
                    public void onRequestLater() {
                        PhotoUtils.openCameraImage(CommuterActivity.this);
                    }
                });
                break;
            case R.id.comm_finger_photo:
                asyncFingerprint.register2();
                break;
            case R.id.bt_comm_register:
                register();
                break;
            case R.id.bt_comm_cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PhotoUtils.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                   /* data.getExtras().get("data");*/
                    PhotoUtils.cropImage(CommuterActivity.this, PhotoUtils.imageUriFromCamera ,  Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.PHOTO);// 裁剪图片
//                    initUCrop(PhotoUtils.imageUriFromCamera);
                }

                break;
            case PhotoUtils.CROP_IMAGE://普通裁剪后的处理
                Log.d("普通裁剪后的处理地址",PhotoUtils.cropImageUri.toString());
                photoFileUrl =PhotoUtils.cropImageUri.toString();
                Glide.with(context).
                        load(PhotoUtils.cropImageUri).
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        thumbnail(0.5f).
                        placeholder(R.drawable.ic_check_white_48dp).
                        priority(Priority.LOW).
                        error(R.drawable.linecode_icon).
                        fallback(R.drawable.ic_clear_white_48dp).
                        dontAnimate().
                        into(commPhoto);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String commuterAccount;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobile;
    private String nationalID;
    private String email;
    private String fingerPrintFileUrl;
    private String photoFileUrl;

    private void register() {
        if (DataUtils.isEmpty(infoBean)){
        commuterAccount = edCommCommuterAccount.getText().toString();
        if (DataUtils.isNullString(commuterAccount)) {
            showToastError();
            return;
        }
        bean.setCommuterAccount(commuterAccount);

        firstName = edCommFirstName.getText().toString();
        if (DataUtils.isNullString(firstName)) {
            showToastError();
            return;
        }
        bean.setFirstName(firstName);

        lastName = edCommLastName.getText().toString();
        if (DataUtils.isNullString(lastName)) {
            showToastError();
            return;
        }
        bean.setLastName(lastName);
        bean.setFullName(firstName + " " + lastName);
        email = edEmail.getText().toString();
        if (DataUtils.isNullString(email)) {
            showToastError();
            return;
        }
        bean.setEmail(email);

        mobile = edPhone .getText().toString();
        if (DataUtils.isNullString(mobile)){
            showToastError();
            return;
        }
        bean.setMobile(mobile);

        bean.setTimeMills(TimeUtils.getCurTimeMills());
        bean.setTimeStr(TimeUtils.getCurTimeString());
        //判断指纹和图片是否保存下来

        bean.setPhotoFileUrl(photoFileUrl);


        if (DataUtils.isNullString(fingerPrintFileUrl)){
            ToastUtils.error("请输入指纹");
            return;
        }
        bean.setFingerPrintFileUrl(fingerPrintFileUrl);

        if (dbHelper.insertCommuter(bean)){
            ToastUtils.success("Register Ok");
            onBackPressed();
        }else {
            ToastUtils.error("ERROR");
        }

        }else {
            commuterAccount = edCommCommuterAccount.getText().toString();
            if (DataUtils.isNullString(commuterAccount)) {
                showToastError();
                return;
            }
            infoBean.setCommuterAccount(commuterAccount);

            firstName = edCommFirstName.getText().toString();
            if (DataUtils.isNullString(firstName)) {
                showToastError();
                return;
            }
            infoBean.setFirstName(firstName);

            lastName = edCommLastName.getText().toString();
            if (DataUtils.isNullString(lastName)) {
                showToastError();
                return;
            }
            infoBean.setLastName(lastName);
            infoBean.setFullName(firstName + " " + lastName);
            email = edEmail.getText().toString();
            if (DataUtils.isNullString(email)) {
                showToastError();
                return;
            }
            infoBean.setEmail(email);

            mobile = edPhone .getText().toString();
            if (DataUtils.isNullString(mobile)){
                showToastError();
                return;
            }
            infoBean.setMobile(mobile);
            if (DbHelper.isUpdateCommuterAccountInfoPhoto(infoBean)){
                ToastUtils.success("Register Ok");
                onBackPressed();
            }else {
                ToastUtils.error("ERROR");
            }
        }

    }


}
