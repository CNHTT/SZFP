package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfplib.utils.SPUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfplib.utils.DataUtils.isNullString;
import static com.szfp.szfplib.utils.ToastUtils.error;
import static com.szfp.szfplib.utils.ToastUtils.success;

public class ParkingSettingActivity extends BaseActivity {

    @BindView(R.id.et_hour_number)
    EditText etHourNumber;
    @BindView(R.id.et_park_day_number)
    EditText etParkDayNumber;
    @BindView(R.id.et_park_month_number)
    EditText etParkMonthNumber;
    @BindView(R.id.et_park_special_discounts)
    EditText etParkSpecialDiscounts;
    @BindView(R.id.bt_parking_save)
    StateButton btParkingSave;
    @BindView(R.id.bt_paring_cancel)
    StateButton btParingCancel;

    private String day;
    private String month;
    private String hour;
    private String dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_parking_save, R.id.bt_paring_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_parking_save:
                save();
                break;
            case R.id.bt_paring_cancel:
                onBackPressed();
                break;
        }
    }

    private void save() {

        dis = etParkSpecialDiscounts.getText().toString();
        hour = etHourNumber.getText().toString();
        day = etParkDayNumber .getText().toString();
        month = etParkMonthNumber.getText().toString();
        if (isNullString(day)||isNullString(hour)||isNullString(month)||isNullString(dis)){
            error("PLease Input number");
            return;
        }
        SPUtils.putString(this, ConstantValue.PARING_FEE_DAY,day);
        SPUtils.putString(this, ConstantValue.PARING_FEE_HOUR,hour);
        SPUtils.putString(this, ConstantValue.PARING_FEE_MONTH,month);
        SPUtils.putString(this, ConstantValue.PARING_FEE_DIS,dis);
        success("Save successful");
    }
}
