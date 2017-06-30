package com.szfp.szfp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfp.utils.PrintUtils;
import com.szfp.szfplib.inter.onRequestPermissionsListener;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.PermissionsUtils;
import com.szfp.szfplib.utils.PhotoUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommuterAccountActivity extends BasePrintActivity {


    @BindView(R.id.tv_fingerprint_key_status)
    TextView tvFingerprintKeyStatus;
    @BindView(R.id.bt_take_photo)
    StateButton btTakePhoto;
    @BindView(R.id.bt_edit_account)
    StateButton btEditAccount;
    @BindView(R.id.bt_top_up_fare)
    StateButton btTopUpFare;
    @BindView(R.id.bt_reverse_transaction)
    StateButton btReverseTransaction;
    @BindView(R.id.bt_full_report)
    StateButton btFullReport;
    @BindView(R.id.tv_a_c_number)
    TextView tvACNumber;
    @BindView(R.id.tv_ac_deposits)
    TextView tvAcDeposits;
    @BindView(R.id.tv_ac_date_deposited)
    TextView tvAcDateDeposited;
    @BindView(R.id.tv_ac_fare_paid)
    TextView tvAcFarePaid;
    @BindView(R.id.tv_ac_date)
    TextView tvAcDate;
    @BindView(R.id.tv_ac_balance)
    TextView tvAcBalance;
    @BindView(R.id.tv_cai_title_name)
    TextView tvCaiTitleName;
    @BindView(R.id.comm_account_photo)
    ImageView commAccountPhoto;
    @BindView(R.id.tv_commuter_account)
    TextView tvCommuterAccount;
    @BindView(R.id.tv_full_name)
    TextView tvFullName;
    @BindView(R.id.tv_model)
    TextView tvModel;
    @BindView(R.id.tv_national_id)
    TextView tvNationalId;
    @BindView(R.id.tv_registration_date)
    TextView tvRegistrationDate;
    @BindView(R.id.tv_email)
    TextView tvEmail;



    private CommuterAccountInfoBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commuter_account);
        ButterKnife.bind(this);
        bean = (CommuterAccountInfoBean) getIntent().getSerializableExtra(ConstantValue.INFO);
        initDataView();
    }


    private void initDataView() {

        if (!DataUtils.isNullString(bean.getFullName())){
            tvCaiTitleName.setText(bean.getFullName()+" ACC");
            tvFullName.setText(bean.getFullName());
        }
        if (!DataUtils.isNullString(bean.getCommuterAccount()))
            tvCommuterAccount.setText(bean.getCommuterAccount());
        tvACNumber.setText(bean.getCommuterAccount());

        if (!DataUtils.isNullString(bean.getMobile()))
            tvModel.setText(bean.getMobile());

        if (!DataUtils.isNullString(bean.getNationalID()))
            tvNationalId.setText(bean.getNationalID());

        if (!DataUtils.isNullString(bean.getTimeStr()))
            tvRegistrationDate.setText(bean.getTimeStr());

        if (!DataUtils.isNullString(bean.getEmail()))
            tvEmail.setText(bean.getEmail());

        if (!DataUtils.isNullString(bean.getTimeStr()))
            tvRegistrationDate.setText(bean.getTimeStr());

        if (DataUtils.isNullString(bean.getFingerPrintFileUrl()))
            tvFingerprintKeyStatus.setText("No Registered");

        if (!DataUtils.isNullString(bean.getPhotoFileUrl())){
            Glide.with(CommuterAccountActivity.this).
                    load(Uri.parse(bean.getPhotoFileUrl())).
                    diskCacheStrategy(DiskCacheStrategy.RESULT).
                    thumbnail(0.5f).
                    placeholder(R.drawable.ic_check_white_48dp).
                    priority(Priority.LOW).
                    error(R.drawable.linecode_icon).
                    fallback(R.drawable.ic_clear_white_48dp).
                    dontAnimate().
                    into(commAccountPhoto);
        }

        tvAcDeposits.setText(DataUtils.format2Decimals(String.valueOf(bean.getDeposits())));
        tvAcFarePaid.setText(DataUtils.format2Decimals(String.valueOf(bean.getFarePaid())));
        tvAcBalance.setText(DataUtils.format2Decimals(String.valueOf(bean.getDeposits()-bean.getFarePaid())));
    }

    @OnClick({R.id.bt_take_photo, R.id.bt_edit_account, R.id.bt_top_up_fare, R.id.bt_reverse_transaction, R.id.bt_full_report})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_take_photo:
                PermissionsUtils.requestCamera(getApplicationContext(), new onRequestPermissionsListener() {
                    @Override
                    public void onRequestBefore() {

                    }

                    @Override
                    public void onRequestLater() {
                        PhotoUtils.openCameraImage(CommuterAccountActivity.this);
                    }
                });

                break;
            case R.id.bt_edit_account:

                editAccount();


                break;
            case R.id.bt_top_up_fare:
                showTopUpFare();

                break;
            case R.id.bt_reverse_transaction:
                Intent intent =  new Intent(CommuterAccountActivity.this,ReverseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantValue.INFO,bean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.bt_full_report:

                Intent intent2 =  new Intent(CommuterAccountActivity.this,FullReportActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(ConstantValue.INFO,bean);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
    }



    private Dialog dialog;
    private EditText editText;
    private StateButton ok;
    private StateButton cel;
    private CheckBox checkPrint;
    private boolean isPrint=false;
    private void showTopUpFare() {

        if (dialog ==null){
            dialog = new Dialog(this,R.style.AlertDialogStyle);
            View view  = ContextUtils.inflate(this,R.layout.layout_top_up_fare);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(view,new LinearLayout.LayoutParams(-1,-1));
            editText = (EditText) view.findViewById(R.id.et_top_up_fare);
            checkPrint = (CheckBox) view.findViewById(R.id.ck_tup_checkbox);
            ok  = (StateButton) view .findViewById(R.id.bt_ok_tuf);
            cel = (StateButton) view.findViewById(R.id.bt_cel_tuf);
            checkPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){

                        showDeviceList();

                    }else isPrint = false;


                }
            });

            ok.setOnClickListener(new OnClickSelect());
            cel.setOnClickListener(new OnClickSelect());
        }
        if (!dialog.isShowing()) dialog.show();
    }


    private String  input;
    private float   inputFloat;
    private class OnClickSelect implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_ok_tuf:
                    input = editText.getText().toString();
                    if (DataUtils.isNullString(input)){
                        showToastError();
                        return;
                    }

                    inputFloat = Float.valueOf(input);






                    bean.setDeposits(bean.getDeposits()+inputFloat);
                    bean.setBalance(bean.getBalance()+inputFloat);

                    AccountReportBean  reportBean = new AccountReportBean();
                    //生成消费记录

                    reportBean.setDeposits(inputFloat);
                    reportBean.setDepositsDate(TimeUtils.getCurTimeMills());
                    reportBean.setBalance(bean.getBalance());

                    reportBean.setACNumber(bean.getCommuterAccount());
                    reportBean.setFarePaidDate(0);
                    reportBean.setFarePaid(0);
                    if (DbHelper.isUpdateCommuterAccountInfoPhoto(bean)){
                        ToastUtils.success("TOP UP FARE SUCCESS");
                        DbHelper.insertAccountReport(reportBean);
                        initDataView();

                        if (isPrint) PrintUtils.printDepositsReceipt(bean,reportBean);


                        dialog.dismiss();
                    }else {
                        ToastUtils.error("ERROR");
                    }




                    break;
                case R.id.bt_cel_tuf:
                    dialog.dismiss();
                    break;
            }

        }
    }





    private void editAccount() {

        Intent intent =  new Intent(CommuterAccountActivity.this,CommuterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantValue.INFO,bean);
        intent.putExtras(bundle);
        startActivity(intent);

    }




    private String photoStr;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PhotoUtils.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                   /* data.getExtras().get("data");*/
                    PhotoUtils.cropImage(CommuterAccountActivity.this, PhotoUtils.imageUriFromCamera ,  Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.PHOTO);// 裁剪图片
//                    initUCrop(PhotoUtils.imageUriFromCamera);
                }

                break;
            case PhotoUtils.CROP_IMAGE://普通裁剪后的处理
                Log.d("普通裁剪后的处理地址",PhotoUtils.cropImageUri.toString());
                photoStr =PhotoUtils.cropImageUri.toString();
                bean.setPhotoFileUrl(photoStr);
                if ( DbHelper.isUpdateCommuterAccountInfoPhoto(bean)){
                    Glide.with(getApplicationContext()).
                            load(PhotoUtils.cropImageUri).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            placeholder(R.drawable.ic_check_white_48dp).
                            priority(Priority.LOW).
                            error(R.drawable.linecode_icon).
                            fallback(R.drawable.ic_clear_white_48dp).
                            dontAnimate().
                            into(commAccountPhoto);
                    ToastUtils.success("照片更新成功");

                    /**
                     * 把原先的照片删除
                     */
                }else {
                    ToastUtils.error("更新失败");
                }



                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void showConnecting() {


    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        isPrint =true;
        checkPrint.setChecked(isPrint);
        ToastUtils.success("Connect success" + "   "+mConnectedDeviceName);
    }
}
