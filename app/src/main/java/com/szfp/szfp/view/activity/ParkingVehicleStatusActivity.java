package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParkingVehicleStatusActivity extends BaseNoActivity {

    @BindView(R.id.bt_parking_park)
    StateButton btParkingPark;
    @BindView(R.id.bt_parking_leave)
    StateButton btParkingLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_vehicle_status);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.bt_parking_park, R.id.bt_parking_leave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_parking_park:
                startActivity(new Intent(ParkingVehicleStatusActivity.this,ParkingParkActivity.class));
                break;
            case R.id.bt_parking_leave:
                startActivity(new Intent(ParkingVehicleStatusActivity.this,ParkingLeaveActivity.class));
                break;
        }
    }
}
