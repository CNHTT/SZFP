package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankDepositActivity extends BaseNoActivity {

    @BindView(R.id.bt_cash_deposit)
    StateButton btCashDeposit;
    @BindView(R.id.bt_withdrawal)
    StateButton btWithdrawal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_deposit);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_cash_deposit, R.id.bt_withdrawal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cash_deposit:
                startActivity(new Intent(BankDepositActivity.this,BankCashDepositActivity.class));
                break;
            case R.id.bt_withdrawal:
                startActivity(new Intent(BankDepositActivity.this,BankWithdrawalActivity.class));
                break;
        }
    }
}

