package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParkingActivity extends BaseActivity {

    @BindView(R.id.bt_parking_pay_parking)
    StateButton btParkingPayParking;
    @BindView(R.id.bt_parking_top_up)
    StateButton btParkingTopUp;
    @BindView(R.id.bt_parking_vehicle_status)
    StateButton btParkingVehicleStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingctivity);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_parking_pay_parking, R.id.bt_parking_top_up, R.id.bt_parking_vehicle_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_parking_pay_parking:
                break;
            case R.id.bt_parking_top_up:
                break;
            case R.id.bt_parking_vehicle_status:
                break;
        }
    }
}
