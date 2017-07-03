package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfplib.utils.SPUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransportSelectActivity extends BaseActivity {

    @BindView(R.id.bt_select_static)
    StateButton btSelectStatic;
    @BindView(R.id.bt_select_dynamic)
    StateButton btSelectDynamic;
    @BindView(R.id.bt_select_cash)
    StateButton btSelectCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_select);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_select_static, R.id.bt_select_dynamic, R.id.bt_select_cash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_select_static:
                if (!SPUtils.getBoolean(this, ConstantValue.STATIC_FARE_TYPE))
            {
                ToastUtils.error("Please set the static amount");
                return;
            }
                startActivity(new Intent(TransportSelectActivity.this,TransportChargeActivity.class));
                break;
            case R.id.bt_select_dynamic:

                startActivity(new Intent(TransportSelectActivity.this,StaticFareActivity.class));
                break;
            case R.id.bt_select_cash:
                startActivity(new Intent(TransportSelectActivity.this,TransportCashActivity.class));
                break;
        }
    }
}
