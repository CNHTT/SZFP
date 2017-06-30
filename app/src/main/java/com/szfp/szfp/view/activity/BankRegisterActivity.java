package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.R;
import com.szfp.szfp.bean.BankRegistrationBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
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
    private BankRegistrationBean bean;

    private DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_regsiter);
        ButterKnife.bind(this);

        dbHelper = new DbHelper();

        bean = new BankRegistrationBean();
        bean.setRegisterTime(TimeUtils.getCurTimeMills());
        bean.setRegisterTimeStr(TimeUtils.getCurTimeString());

    }

    @OnClick({R.id.bt_bank_register, R.id.bt_bank_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_bank_register:
                register();
                break;
            case R.id.bt_bank_cancel:
                break;
        }
    }
    private String  bankName;
    private String  bankAddress;
    private String  bankBranches;
    private String  bankContacts;
    private String  bankAccountTypes;
    private void register() {
        bankName =edBankName.getText().toString();
        if (DataUtils.isNullString(bankName)){
            showToastError();
            return;
        }
        bean.setBankName(bankName);

        bankAddress =edBankAddress.getText().toString();
        if (DataUtils.isNullString(bankAddress)){
            showToastError();
            return;
        }
        bean.setBankAddress(bankAddress);

        bankBranches =edBankName.getText().toString();
        if (DataUtils.isNullString(bankBranches)){
            showToastError();
            return;
        }
        bean.setBankBranches(bankBranches);

        bankContacts =edBankContacts.getText().toString();
        if (DataUtils.isNullString(bankContacts)){
            showToastError();
            return;
        }
        bean.setBankContacts(bankContacts);

        bankAccountTypes =edBankAccountTypes.getText().toString();
        if (DataUtils.isNullString(bankAccountTypes)){
            showToastError();
            return;
        }
        bean.setBankAccountTypes(bankAccountTypes);


        if (dbHelper.insertBankInfo(bean)){

            ToastUtils.success("SUCCESS");
            onBackPressed();

        }else {
            ToastUtils.error("ERROR");
        }



    }
}
