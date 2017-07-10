package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgricultureHumanActivity extends BaseActivity {

    @BindView(R.id.bt_human_register)
    StateButton btHumanRegister;
    @BindView(R.id.bt_human_reports)
    StateButton btHumanReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture_human);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_human_register, R.id.bt_human_reports})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_human_register:
                startActivity(new Intent(AgricultureHumanActivity.this,AgricultureRegisterEmployeeActivity.class));
                break;
            case R.id.bt_human_reports:
                startActivity(new Intent(AgricultureHumanActivity.this,AgricultureReportEmployeeActivity.class));
                break;
        }
    }
}
