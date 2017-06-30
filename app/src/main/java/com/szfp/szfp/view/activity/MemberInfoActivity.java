package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.bean.BankCustomerBean;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemberInfoActivity extends BaseActivity {

    @BindView(R.id.member_black)
    TextView memberBlack;
    @BindView(R.id.member_info_admin)
    TextView memberInfoAdmin;
    @BindView(R.id.member_photo)
    ImageView memberPhoto;
    @BindView(R.id.tv_fosa_account)
    TextView tvFosaAccount;
    @BindView(R.id.tv_member_number)
    TextView tvMemberNumber;
    @BindView(R.id.tv_member_full_name)
    TextView tvMemberFullName;
    @BindView(R.id.tv_member_model)
    TextView tvMemberModel;
    @BindView(R.id.tv_member_national_id)
    TextView tvMemberNationalId;
    @BindView(R.id.tv_member_registration_date)
    TextView tvMemberRegistrationDate;
    @BindView(R.id.tv_member_email)
    TextView tvMemberEmail;
    @BindView(R.id.tv_member_fingerprint_key_status)
    TextView tvMemberFingerprintKeyStatus;
    @BindView(R.id.bt_member_take_photo)
    StateButton btMemberTakePhoto;
    @BindView(R.id.bt_member_edit_account)
    StateButton btMemberEditAccount;
    @BindView(R.id.bt_member_reverse_transaction)
    StateButton btMemberReverseTransaction;
    @BindView(R.id.bt_member_customer_details)
    StateButton btMemberCustomerDetails;
    @BindView(R.id.bt_member_report)
    StateButton btMemberReport;
    @BindView(R.id.tv_member_acc_code)
    TextView tvMemberAccCode;
    @BindView(R.id.tv_member_acc_type)
    TextView tvMemberAccType;
    @BindView(R.id.tv_member_acc_status)
    TextView tvMemberAccStatus;
    @BindView(R.id.tv_member_deposits)
    TextView tvMemberDeposits;
    @BindView(R.id.tv_member_total)
    TextView tvMemberTotal;
    @BindView(R.id.tv_ac_balance)
    TextView tvAcBalance;


    private BankCustomerBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_activty);
        ButterKnife.bind(this);


        bean = (BankCustomerBean) getIntent().getSerializableExtra(ConstantValue.INFO);
        initDataView();
    }

    private void initDataView() {


        if (DataUtils.isNullString(bean.getFosaAccount()))
            tvFosaAccount.setText("NO FOUND");
            else tvFosaAccount.setText(bean.getFosaAccount());

        if (DataUtils.isNullString(bean.getName())) tvMemberFullName.setText("NO FOUND"); else tvMemberFullName.setText(bean.getName());

        if (DataUtils.isNullString(bean.getModel())) tvMemberModel.setText("NOT AVAILABLE"); else tvMemberModel.setText(bean.getModel());

        tvMemberNationalId.setText(bean.getNationalId());

        tvMemberRegistrationDate.setText(bean.getRegisterTimeStr());

        if (DataUtils.isNullString(bean.getFingerPrintFileUrl()))tvMemberFingerprintKeyStatus.setText("NO REGISTERED");else tvMemberFingerprintKeyStatus.setText("REGISTERED");

        tvMemberAccCode.setText("1");
        tvMemberAccType.setText(bean.getFosaAccount()+"Ordinary Savings");
        tvMemberAccStatus.setText("ACTIVE");
        tvMemberDeposits.setText(String.valueOf(bean.getDeposit()));
        tvAcBalance.setText(String.valueOf(bean.getTotal()));



    }

    @OnClick({R.id.bt_member_take_photo, R.id.bt_member_edit_account, R.id.bt_member_reverse_transaction, R.id.bt_member_customer_details, R.id.bt_member_report})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_member_take_photo:
                break;
            case R.id.bt_member_edit_account:
                break;
            case R.id.bt_member_reverse_transaction:
                break;
            case R.id.bt_member_customer_details:
                break;
            case R.id.bt_member_report:
                break;
        }
    }
}
