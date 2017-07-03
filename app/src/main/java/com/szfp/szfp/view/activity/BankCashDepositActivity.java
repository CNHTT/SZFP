package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankCashDepositActivity extends BasePrintActivity {

    @BindView(R.id.et_cash_deposit_number)
    EditText etCashDepositNumber;
    @BindView(R.id.et_cash_deposit_by)
    EditText etCashDepositBy;
    @BindView(R.id.et_cash_deposit_name)
    EditText etCashDepositName;
    @BindView(R.id.et_cash_deposit_account)
    EditText etCashDepositAccount;
    @BindView(R.id.bt_cash_deposit_enter)
    StateButton btCashDepositEnter;
    @BindView(R.id.bt_cash_deposit_cancel)
    StateButton btCashDepositCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_cash_deposit);
        ButterKnife.bind(this);
    }

    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {

    }

    @OnClick({R.id.bt_cash_deposit_enter, R.id.bt_cash_deposit_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cash_deposit_enter:
                break;
            case R.id.bt_cash_deposit_cancel:
                break;
        }
    }
}
