package com.szfp.szfp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.bean.BankCustomerBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankingActivity extends BaseActivity {

    @BindView(R.id.bt_account_opening)
    StateButton btAccountOpening;
    @BindView(R.id.bt_deposit)
    StateButton btDeposit;
    @BindView(R.id.bt_funds_transfer)
    StateButton btFundsTransfer;
    @BindView(R.id.bt_account_statement)
    StateButton btAccountStatement;
    @BindView(R.id.bt_shopping)
    StateButton btShopping;
    @BindView(R.id.bt_bill_payment)
    StateButton btBillPayment;
    @BindView(R.id.bt_bank_admin)
    StateButton btBankAdmin;
    @BindView(R.id.bt_airtime_purchase)
    StateButton btAirtimePurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banking);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_account_opening, R.id.bt_deposit, R.id.bt_funds_transfer, R.id.bt_account_statement, R.id.bt_shopping, R.id.bt_bill_payment, R.id.bt_bank_admin, R.id.bt_airtime_purchase})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_account_opening:
                showBankRegister();
                break;
            case R.id.bt_deposit:
                startActivity(new Intent(BankingActivity.this,BankDepositActivity.class));
                break;
            case R.id.bt_funds_transfer:
                break;
            case R.id.bt_account_statement:
                startActivity(new Intent(BankingActivity.this,BankStatementActivity.class));
                break;
            case R.id.bt_shopping:
                break;
            case R.id.bt_bill_payment:
                break;
            case R.id.bt_bank_admin:
                showDeposit();
                break;
            case R.id.bt_airtime_purchase:
                break;
        }
    }

    private Dialog dialogDeposit=null;
    private EditText edDeposit;
    private String depositAcc;
    private void showDeposit() {
        if (dialogDeposit==null){
            dialogDeposit = new Dialog( this,R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this,R.layout.dialog_bank_deposit);
            dialogDeposit.setCanceledOnTouchOutside(true);
            dialogDeposit.setContentView(view, new LinearLayout.LayoutParams(-1, -1));
            edDeposit = (EditText) view.findViewById(R.id.ed_search_fosa_account);
            view.findViewById(R.id.bt_search_b_d).setOnClickListener(new OnClickDialog());
            view.findViewById(R.id.bt_cancel_b_d).setOnClickListener(new OnClickDialog());
        }

        if (!dialogDeposit.isShowing())
            dialogDeposit.show();
    }


    private Dialog dialogSelect=null;
    private void showBankRegister() {
        if (dialogSelect == null)
        {
            dialogSelect = new Dialog(this,R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this,R.layout.bank_register_select);
            dialogSelect.setCanceledOnTouchOutside(true);
            dialogSelect.setContentView(view, new LinearLayout.LayoutParams(-1, -1));
            view.findViewById(R.id.bt_bank_registration).setOnClickListener(new OnClickDialog());
            view.findViewById(R.id.bt_bank_customer_registration).setOnClickListener(new OnClickDialog());
        }
        if (!dialogSelect.isShowing())
        dialogSelect.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class OnClickDialog implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_bank_registration:
                    dialogSelect.dismiss();
                    startActivity(new Intent(BankingActivity.this,BankRegisterActivity.class));
                    break;
                case R.id.bt_bank_customer_registration:
                    dialogSelect.dismiss();
                    startActivity(new Intent(BankingActivity.this,BankCustomerRegisterActivity.class));
                    break;


                case R.id.bt_search_b_d:
                    search();
                    break;
                case R.id.bt_cancel_b_d:
                    dialogDeposit.dismiss();
                    break;
            }
        }
    }


    private BankCustomerBean bean;
    private void search() {
        depositAcc = edDeposit.getText().toString();
        if (DataUtils.isNullString(depositAcc)){
            ToastUtils.error("PLEASE INPUT");
            return;
        }

        bean = DbHelper.getBankCustomerBean(depositAcc);
        if (bean == null){
            ToastUtils.error("No Found");
            return;
        }
        dialogDeposit.dismiss();
        Intent  intent = new Intent();
        intent.setClass(BankingActivity.this,MemberInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantValue.INFO,bean);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
