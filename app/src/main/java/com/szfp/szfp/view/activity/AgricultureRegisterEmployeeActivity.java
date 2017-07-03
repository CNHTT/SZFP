package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgricultureRegisterEmployeeActivity extends BaseActivity {

    @BindView(R.id.et_employee_name)
    EditText etEmployeeName;
    @BindView(R.id.et_employee_id_number)
    EditText etEmployeeIdNumber;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.rg_employee_Sex)
    RadioGroup rgEmployeeSex;
    @BindView(R.id.et_employee_register_number)
    EditText etEmployeeRegisterNumber;
    @BindView(R.id.et_employee_contact)
    EditText etEmployeeContact;
    @BindView(R.id.et_employee_job_title)
    EditText etEmployeeJobTitle;
    @BindView(R.id.et_employee_salary)
    EditText etEmployeeSalary;
    @BindView(R.id.tv_employee_date_of_birth)
    TextView tvEmployeeDateOfBirth;
    @BindView(R.id.tv_employee_date_employed)
    TextView tvEmployeeDateEmployed;
    @BindView(R.id.tv_employee_home_town)
    EditText tvEmployeeHomeTown;
    @BindView(R.id.tv_employee_collection_route)
    EditText tvEmployeeCollectionRoute;
    @BindView(R.id.sp_employee_nature)
    Spinner spEmployeeNature;
    @BindView(R.id.iv_employee_finger_print)
    ImageView ivEmployeeFingerPrint;
    @BindView(R.id.bt_employee_register)
    StateButton btEmployeeRegister;
    @BindView(R.id.bt_employee_cancel)
    StateButton btEmployeeCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agirculture_register_employee);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_employee_date_of_birth, R.id.tv_employee_date_employed, R.id.iv_employee_finger_print, R.id.bt_employee_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_employee_date_of_birth:
                break;
            case R.id.tv_employee_date_employed:
                break;
            case R.id.iv_employee_finger_print:
                break;
            case R.id.bt_employee_register:
                break;
        }
    }
}
