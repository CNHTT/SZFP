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
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.szfp.szfp.R;
import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.VehicleInfoBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.inter.onRequestPermissionsListener;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.PermissionsUtils;
import com.szfp.szfplib.utils.PhotoUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.utils.Utils;
import com.szfp.szfplib.weight.StateButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VehiclesActivity extends BaseActivity {

    @BindView(R.id.ed_vehicles_name_of_owner)
    EditText edVehiclesNameOfOwner;
    @BindView(R.id.ed_vehicles_id_no)
    EditText edVehiclesIdNo;
    @BindView(R.id.ed_vehicles_id_no_contacts)
    EditText edVehiclesIdNoContacts;
    @BindView(R.id.ed_vehicles_home_address)
    EditText edVehiclesHomeAddress;
    @BindView(R.id.bt_vehicles_fingerprint)
    StateButton btVehiclesFingerprint;
    @BindView(R.id.iv_vehicles_finger_print_photo)
    ImageView ivVehiclesFingerPrintPhoto;
    @BindView(R.id.iv_vehicles_user_photo)
    ImageView ivVehiclesUserPhoto;
    @BindView(R.id.ed_vehicles_name_of_primary_driver)
    EditText edVehiclesNameOfPrimaryDriver;
    @BindView(R.id.et_vehicles_driving_license_no)
    EditText etVehiclesDrivingLicenseNo;
    @BindView(R.id.et_vehicles_driver_home_address)
    EditText etVehiclesDriverHomeAddress;
    @BindView(R.id.et_vehicles_driver_contacts)
    EditText etVehiclesDriverContacts;
    @BindView(R.id.bt_vehicles_driver_fingerprint)
    StateButton btVehiclesDriverFingerprint;
    @BindView(R.id.primary_driver_photo)
    ImageView primaryDriverPhoto;
    @BindView(R.id.et_make_of_vehicle)
    EditText etMakeOfVehicle;
    @BindView(R.id.et_reg_no)
    EditText etRegNo;
    @BindView(R.id.et_model)
    EditText etModel;
    @BindView(R.id.et_year_of_vehicle)
    EditText etYearOfVehicle;
    @BindView(R.id.et_color)
    EditText etColor;
    @BindView(R.id.et_psv_no)
    EditText etPsvNo;
    @BindView(R.id.et_designated_route)
    EditText etDesignatedRoute;
    @BindView(R.id.et_passenger_no)
    EditText etPassengerNo;
    @BindView(R.id.et_conductor)
    EditText etConductor;
    @BindView(R.id.bt_registration)
    StateButton btRegistration;
    @BindView(R.id.bt_vehicles_cancel)
    StateButton btVehiclesCancel;
    @BindView(R.id.ed_vehicles_commuter_account)
    EditText edVehiclesCommuterAccount;
    @BindView(R.id.sl_vehicles)
    ScrollView slVehicles;


    private VehicleInfoBean vehicleInfoBean;
    private Context context;


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
                    VehiclesActivity.this.modelByte = (byte[]) msg.obj;
                    if (VehiclesActivity.this.modelByte != null) {
                        Log.i("whw", "#################model.length="
                                + VehiclesActivity.this.modelByte.length);
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;

                        if (isOwner) ownerFingerPrintFileUrl =String.valueOf(id);else driverFingerPrintFileUrl=String.valueOf(id);
                        ToastUtils.showToast(VehiclesActivity.this,
                                getString(R.string.register_success) + "  pageId="
                                        + id);
                    } else {
                        ToastUtils.showToast(VehiclesActivity.this,
                                R.string.register_success);
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(VehiclesActivity.this,
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
                        ToastUtils.showToast(VehiclesActivity.this,
                                getString(R.string.verifying_through) + "  pageId="
                                        + r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    ToastUtils
                            .showToast(VehiclesActivity.this, (Integer) msg.obj);
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
        ivVehiclesFingerPrintPhoto.setBackgroundDrawable(new BitmapDrawable(image));
        writeToFile(data);
    }

    /**
     * 指纹图片
     * @param data
     */
    private void writeToFile(byte[] data) {
        String dir = Environment.getExternalStorageDirectory().getPath() +SzfpApplication.DISK_CACHE_PATH+SzfpApplication.FINGERPRINT;
        File dirPath = new File(dir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        String filePath = dir + System.currentTimeMillis() + ".bmp";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(VehiclesActivity.this,
                    R.string.verifying_through);
        } else {
            ToastUtils.showToast(VehiclesActivity.this,
                    R.string.verifying_fail);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = ContextUtils.inflate(this,R.layout.activity_vehicles);
        setContentView(view);
        ButterKnife.bind(this);
        context = this;
        dbHelper = new DbHelper();
        Utils.hideKeyboard(view, this);


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
    @OnClick({R.id.iv_vehicles_user_photo, R.id.bt_vehicles_driver_fingerprint, R.id.primary_driver_photo, R.id.bt_registration, R.id.bt_vehicles_cancel, R.id.bt_vehicles_fingerprint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_vehicles_user_photo:
                isPhoto=true;
                PermissionsUtils.requestCamera(context, new onRequestPermissionsListener() {
                    @Override
                    public void onRequestBefore() {

                    }

                    @Override
                    public void onRequestLater() {
                        PhotoUtils.openCameraImage(VehiclesActivity.this);
                    }
                });

                break;
            case R.id.bt_vehicles_fingerprint:
                Log.d("录制指纹","开始");

                isOwner =true;
                asyncFingerprint.register2();

                break;
            case R.id.bt_vehicles_driver_fingerprint:
                isOwner =false;
                asyncFingerprint.register2();
                break;
            case R.id.primary_driver_photo:
                isPhoto=false;
                PermissionsUtils.requestCamera(context, new onRequestPermissionsListener() {
                    @Override
                    public void onRequestBefore() {

                    }

                    @Override
                    public void onRequestLater() {
                        PhotoUtils.openCameraImage(VehiclesActivity.this);
                    }
                });

                break;
            case R.id.bt_registration:

                registration();

                break;
            case R.id.bt_vehicles_cancel:
                break;
        }
    }



    private boolean isPhoto=true;
    private String commuterAccount;
    private String nameOfOwner;
    private String ownerIDNo;
    private String ownerContacts;
    private String ownerAddress;
    private String ownerFingerPrintFileUrl;
    private String ownerPhotoFileUrl;
    private boolean isOwner = false;
    private String driverName;
    private String driverNo;
    private String driverAddress;
    private String driverContacts;
    private String driverFingerPrintFileUrl;
    private String driverPhotoFileUrl;

    private String makeOfVehicle;
    private String regNo;
    private String model;
    private String yearOfVehicle;
    private String color;
    private String psvNo;
    private String desiginNatedRoute;
    private String passengerNo;
    private String conductor;
    private DbHelper dbHelper;

    private void registration() {
        if (vehicleInfoBean == null)
            vehicleInfoBean = new VehicleInfoBean();
        commuterAccount = edVehiclesCommuterAccount.getText().toString();
        if (DataUtils.isNullString(commuterAccount)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setCommuterAccount(commuterAccount);

        nameOfOwner = edVehiclesNameOfOwner.getText().toString();
        if (DataUtils.isNullString(commuterAccount)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setNameOfOwner(nameOfOwner);

        ownerIDNo = edVehiclesIdNo.getText().toString();
        if (DataUtils.isNullString(ownerIDNo)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setOwnerIDNo(ownerIDNo);

        ownerContacts = edVehiclesIdNoContacts.getText().toString();
        if (DataUtils.isNullString(ownerContacts)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setOwnerContacts(ownerContacts);

        ownerAddress = edVehiclesHomeAddress.getText().toString();
        if (DataUtils.isNullString(ownerContacts)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setOwnerAddress(ownerAddress);

        driverName = edVehiclesNameOfPrimaryDriver.getText().toString();
        if (DataUtils.isNullString(driverName)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setDriverName(driverName);

        driverNo = etVehiclesDrivingLicenseNo.getText().toString();
        if (DataUtils.isNullString(driverNo)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setDriverNo(driverNo);


        driverAddress = etVehiclesDriverHomeAddress.getText().toString();
        if (DataUtils.isNullString(driverAddress)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setDriverAddress(driverAddress);

        driverContacts = etVehiclesDriverContacts.getText().toString();
        if (DataUtils.isNullString(driverContacts)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setDriverContacts(driverContacts);


        makeOfVehicle = etMakeOfVehicle.getText().toString();
        if (DataUtils.isNullString(makeOfVehicle)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setMakeOfVehicle(makeOfVehicle);

        regNo = etRegNo.getText().toString();
        if (DataUtils.isNullString(regNo)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setRegNo(regNo);

        model = etModel.getText().toString();
        if (DataUtils.isNullString(model)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setModel(model);

        yearOfVehicle = etYearOfVehicle.getText().toString();
        if (DataUtils.isNullString(yearOfVehicle)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setYearOfVehicle(yearOfVehicle);


        color = etColor.getText().toString();
        if (DataUtils.isNullString(color)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setColor(color);

        psvNo = etPsvNo.getText().toString();
        if (DataUtils.isNullString(psvNo)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setPsvNo(psvNo);

        desiginNatedRoute = etDesignatedRoute.getText().toString();
        if (DataUtils.isNullString(desiginNatedRoute)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setDesiginnatedRoute(desiginNatedRoute);

        passengerNo = etPassengerNo.getText().toString();
        if (DataUtils.isNullString(passengerNo)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setPassengerNo(passengerNo);

        conductor = etConductor.getText().toString();
        if (DataUtils.isNullString(conductor)) {
            showToastError();
            return;
        }
        vehicleInfoBean.setConductor(conductor);

        vehicleInfoBean.setTimeMills(TimeUtils.getCurTimeMills());
        vehicleInfoBean.setTimeStr(TimeUtils.getCurTimeString());
        if (DataUtils.isNullString(ownerFingerPrintFileUrl)){
            ToastUtils.error("请输入指纹");
            return;
        }vehicleInfoBean.setOwnerFingerPrintFileUrl(ownerFingerPrintFileUrl);

        if (DataUtils.isNullString(driverFingerPrintFileUrl))
        {
            ToastUtils.error("请输入指纹");
            return;
        } vehicleInfoBean.setDriverFingerPrintFileUrl(driverFingerPrintFileUrl);


        vehicleInfoBean.setOwnerPhotoFileUrl(ownerPhotoFileUrl);
        vehicleInfoBean.setDriverPhotoFileUrl(driverPhotoFileUrl);


        if (dbHelper.insertVehicleInfo(vehicleInfoBean, context)) {
            ToastUtils.success("OK");
            onBackPressed();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PhotoUtils.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                   /* data.getExtras().get("data");*/
                    PhotoUtils.cropImage(VehiclesActivity.this, PhotoUtils.imageUriFromCamera);// 裁剪图片
//                    initUCrop(PhotoUtils.imageUriFromCamera);
                }

                break;
            case PhotoUtils.CROP_IMAGE://普通裁剪后的处理

                if (isPhoto) {
                    ownerPhotoFileUrl = PhotoUtils.cropImageUri.toString();
                    Glide.with(context).
                            load(PhotoUtils.cropImageUri).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            placeholder(R.drawable.ic_check_white_48dp).
                            priority(Priority.LOW).
                            error(R.drawable.linecode_icon).
                            fallback(R.drawable.ic_clear_white_48dp).
                            dontAnimate().
                            into(ivVehiclesUserPhoto);
                }else {
                    driverPhotoFileUrl = PhotoUtils.cropImageUri.toString();
                    Glide.with(context).
                            load(PhotoUtils.cropImageUri).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            placeholder(R.drawable.ic_check_white_48dp).
                            priority(Priority.LOW).
                            error(R.drawable.linecode_icon).
                            fallback(R.drawable.ic_clear_white_48dp).
                            dontAnimate().
                            into(primaryDriverPhoto);
                }

//                RequestUpdateAvatar(new File(PhotoUtils.getRealFilePath(mContext, RxPhotoUtils.cropImageUri)));
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //从Uri中加载图片 并将其转化成File文件返回
    private File roadImageView(Uri uri, ImageView imageView) {
        Glide.with(context).
                load(uri).
                diskCacheStrategy(DiskCacheStrategy.RESULT).
                thumbnail(0.5f).
                placeholder(R.drawable.code_icon).
                priority(Priority.LOW).
                error(R.drawable.code_icon).
                fallback(R.drawable.code_icon).
                dontAnimate().
                into(imageView);

        return (new File(PhotoUtils.getImageAbsolutePath(this, uri)));
    }

}
