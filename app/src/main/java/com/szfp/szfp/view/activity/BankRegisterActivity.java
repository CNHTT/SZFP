package com.szfp.szfp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.szfp.szfp.R;
import com.szfp.szfp.bean.BankRegistrationBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.inter.onRequestPermissionsListener;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.PermissionsUtils;
import com.szfp.szfplib.utils.PhotoUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankRegisterActivity extends BaseActivity {

    @BindView(R.id.ed_bank_name)
    EditText edBankName;
    @BindView(R.id.ed_bank_address)
    EditText edBankAddress;
    @BindView(R.id.ed_bank_branches)
    EditText edBankBranches;
    @BindView(R.id.ed_bank_contacts)
    EditText edBankContacts;
    @BindView(R.id.ed_bank_account_types)
    EditText edBankAccountTypes;
    @BindView(R.id.bt_bank_register)
    StateButton btBankRegister;
    @BindView(R.id.bt_bank_cancel)
    StateButton btBankCancel;
    @BindView(R.id.iv_bank_photo)
    ImageView ivBankPhoto;
    private BankRegistrationBean bean;



    private boolean isPhoto=true;
    private DbHelper dbHelper;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_regsiter);
        ButterKnife.bind(this);
        context =this;
        dbHelper = new DbHelper();

        bean = new BankRegistrationBean();
        bean.setRegisterTime(TimeUtils.getCurTimeMills());
        bean.setRegisterTimeStr(TimeUtils.getCurTimeString());

    }

    @OnClick({R.id.bt_bank_register, R.id.bt_bank_cancel, R.id.iv_bank_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_bank_register:
                register();
                break;
            case R.id.bt_bank_cancel:
                break;
            case R.id.iv_bank_photo:

                isPhoto=false;
                PermissionsUtils.requestCamera(context, new onRequestPermissionsListener() {
                    @Override
                    public void onRequestBefore() {

                    }

                    @Override
                    public void onRequestLater() {
                        PhotoUtils.openCameraImage(BankRegisterActivity.this);
                    }
                });
                break;
        }
    }

    private String bankPhone;
    private String bankName;
    private String bankAddress;
    private String bankBranches;
    private String bankContacts;
    private String bankAccountTypes;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PhotoUtils.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                   /* data.getExtras().get("data");*/
                    PhotoUtils.cropImage(BankRegisterActivity.this, PhotoUtils.imageUriFromCamera);// 裁剪图片
//                    initUCrop(PhotoUtils.imageUriFromCamera);
                }

                break;
            case PhotoUtils.CROP_IMAGE://普通裁剪后的处理

                if (isPhoto) {
                    bankPhone = PhotoUtils.cropImageUri.toString();
                    Glide.with(context).
                            load(PhotoUtils.cropImageUri).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            placeholder(R.drawable.ic_check_white_48dp).
                            priority(Priority.LOW).
                            error(R.drawable.linecode_icon).
                            fallback(R.drawable.ic_clear_white_48dp).
                            dontAnimate().
                            into(ivBankPhoto);
                }else {
                    bankPhone = PhotoUtils.cropImageUri.toString();
                    Glide.with(context).
                            load(PhotoUtils.cropImageUri).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            placeholder(R.drawable.ic_check_white_48dp).
                            priority(Priority.LOW).
                            error(R.drawable.linecode_icon).
                            fallback(R.drawable.ic_clear_white_48dp).
                            dontAnimate().
                            into(ivBankPhoto);
                }

//                RequestUpdateAvatar(new File(PhotoUtils.getRealFilePath(mContext, RxPhotoUtils.cropImageUri)));
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void register() {
        bankName = edBankName.getText().toString();
        if (DataUtils.isNullString(bankName)) {
            showToastError();
            return;
        }
        bean.setBankName(bankName);

        bankAddress = edBankAddress.getText().toString();
        if (DataUtils.isNullString(bankAddress)) {
            showToastError();
            return;
        }
        bean.setBankAddress(bankAddress);

        bankBranches = edBankName.getText().toString();
        if (DataUtils.isNullString(bankBranches)) {
            showToastError();
            return;
        }
        bean.setBankBranches(bankBranches);

        bankContacts = edBankContacts.getText().toString();
        if (DataUtils.isNullString(bankContacts)) {
            showToastError();
            return;
        }
        bean.setBankContacts(bankContacts);

        bankAccountTypes = edBankAccountTypes.getText().toString();
        if (DataUtils.isNullString(bankAccountTypes)) {
            showToastError();
            return;
        }
        bean.setBankAccountTypes(bankAccountTypes);
        if (DataUtils.isNullString(bankPhone)){
            ToastUtils.error("Please take pictures");
            return;
        }

        bean.setBankPhotoUrl(bankPhone);


        if (dbHelper.insertBankInfo(bean)) {

            ToastUtils.success("SUCCESS");
            onBackPressed();

        } else {
            ToastUtils.error("ERROR");
        }


    }
}
