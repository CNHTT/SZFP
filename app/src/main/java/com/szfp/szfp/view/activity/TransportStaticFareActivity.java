package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.SPUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransportStaticFareActivity extends BaseActivity {

    @BindView(R.id.ed_fixed_fare)
    EditText edFixedFare;
    @BindView(R.id.bt_stat_sava)
    StateButton btStatSava;
    @BindView(R.id.bt_stat_cancel)
    StateButton btStatCancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_static_fare);
        ButterKnife.bind(this);
    }


    private String input;
    @OnClick({R.id.bt_stat_sava, R.id.bt_stat_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_stat_sava:
                input = edFixedFare .getText().toString();
                if (DataUtils.isNullString(input)){
                    ToastUtils.error("PLEASE INPUT");
                    return;
                }
                SPUtils.putString(this, ConstantValue.STATIC_FARE,input);
                SPUtils.putBoolean(this,ConstantValue.STATIC_FARE_TYPE,true);
                ToastUtils.success("SUCCESS");

                break;
            case R.id.bt_stat_cancel:
                onBackPressed();
                break;
        }
    }

}
