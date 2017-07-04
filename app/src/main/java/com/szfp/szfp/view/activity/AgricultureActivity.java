package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgricultureActivity extends BaseActivity {

    @BindView(R.id.bt_agriculture_human)
    StateButton btAgricultureHuman;
    @BindView(R.id.bt_agriculture_daily_company)
    StateButton btAgricultureDailyCompany;
    @BindView(R.id.bt_agriculture_payments)
    StateButton btAgriculturePayments;
    @BindView(R.id.bt_agriculture_register)
    StateButton btAgricultureRegister;
    @BindView(R.id.bt_agriculture_daily_collection)
    StateButton btAgricultureDailyCollection;
    @BindView(R.id.bt_agriculture_reports)
    StateButton btAgricultureReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_agriculture_human, R.id.bt_agriculture_daily_company, R.id.bt_agriculture_payments, R.id.bt_agriculture_register, R.id.bt_agriculture_daily_collection, R.id.bt_agriculture_reports})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_agriculture_human:
                startActivity(new Intent(AgricultureActivity.this,AgricultureHumanActivity.class));
                break;
            case R.id.bt_agriculture_daily_company:
                startActivity(new Intent(AgricultureActivity.this,AgricultureDailyCompanyActivity.class));
                break;
            case R.id.bt_agriculture_payments:
                startActivity(new Intent(AgricultureActivity.this,AgriculturePaymentActivity.class));
                break;
            case R.id.bt_agriculture_register:
                startActivity(new Intent(AgricultureActivity.this,AgricultureFarmerActivity.class));
                break;
            case R.id.bt_agriculture_daily_collection:
                startActivity(new Intent(AgricultureActivity.this,AgricultureDailyCollectionActivity.class));
                break;
            case R.id.bt_agriculture_reports:
                startActivity(new Intent(AgricultureActivity.this,AgricultureReportActivity.class));
                break;
        }
    }
}
