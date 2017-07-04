package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgricultureFarmerActivity extends BaseActivity {

    @BindView(R.id.bt_farmer_register)
    StateButton btFarmerRegister;
    @BindView(R.id.bt_farmer_reports)
    StateButton btFarmerReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture_farmer);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_farmer_register, R.id.bt_farmer_reports})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_farmer_register:
                startActivity(new Intent(AgricultureFarmerActivity.this,AgricultureRegisterFarmerActivity.class));
                break;
            case R.id.bt_farmer_reports:
                break;
        }
    }
}
