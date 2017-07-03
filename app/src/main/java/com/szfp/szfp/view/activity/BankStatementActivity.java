package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankStatementActivity extends BasePrintActivity {

    @BindView(R.id.et_statement_number)
    EditText etStatementNumber;
    @BindView(R.id.et_statement_by)
    EditText etStatementBy;
    @BindView(R.id.bt_statement_enter)
    StateButton btStatementEnter;
    @BindView(R.id.bt_statement_cancel)
    StateButton btStatementCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_statement);
        ButterKnife.bind(this);
    }

    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {

    }

    @OnClick({R.id.bt_statement_enter, R.id.bt_statement_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_statement_enter:
                break;
            case R.id.bt_statement_cancel:
                break;
        }
    }
}
