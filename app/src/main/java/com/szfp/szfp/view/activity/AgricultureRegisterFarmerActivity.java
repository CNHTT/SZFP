package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgricultureRegisterFarmerActivity extends BaseActivity {

    @BindView(R.id.et_farmer_name)
    EditText etFarmerName;
    @BindView(R.id.et_farmer_id_number)
    EditText etFarmerIdNumber;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.rg_farmer_Sex)
    RadioGroup rgFarmerSex;
    @BindView(R.id.et_farmer_register_number)
    EditText etFarmerRegisterNumber;
    @BindView(R.id.et_farmer_contact)
    EditText etFarmerContact;
    @BindView(R.id.et_farmer_salary)
    EditText etFarmerSalary;
    @BindView(R.id.tv_farmer_date_of_birth)
    TextView tvFarmerDateOfBirth;
    @BindView(R.id.tv_farmer_home_town)
    EditText tvFarmerHomeTown;
    @BindView(R.id.tv_farmer_collection_route)
    EditText tvFarmerCollectionRoute;
    @BindView(R.id.et_farmer_number_animals)
    EditText etFarmerNumberAnimals;
    @BindView(R.id.iv_farmer_finger_print)
    ImageView ivFarmerFingerPrint;
    @BindView(R.id.bt_farmer_register)
    StateButton btFarmerRegister;
    @BindView(R.id.bt_farmer_cancel)
    StateButton btFarmerCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agirculture_register_farmer);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_farmer_date_of_birth, R.id.iv_farmer_finger_print, R.id.bt_farmer_register, R.id.bt_farmer_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_farmer_date_of_birth:
                break;
            case R.id.iv_farmer_finger_print:
                break;
            case R.id.bt_farmer_register:
                break;
            case R.id.bt_farmer_cancel:
                break;
        }
    }
}
