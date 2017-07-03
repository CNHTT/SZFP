package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.bean.StudentBean;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudentWillActivity extends BasePrintActivity {


    StudentBean bean;
    @BindView(R.id.bt_student_will_fee)
    StateButton btStudentWillFee;
    @BindView(R.id.bt_student_will_clocking)
    StateButton btStudentWillClocking;
    @BindView(R.id.bt_student_will_info)
    StateButton btStudentWillInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_will);
        ButterKnife.bind(this);
        bean = (StudentBean) getIntent().getSerializableExtra(ConstantValue.INFO);
    }

    @Override
    protected void showConnecting() {

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {

    }

    @OnClick({R.id.bt_student_will_fee, R.id.bt_student_will_clocking, R.id.bt_student_will_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_student_will_fee:
                break;
            case R.id.bt_student_will_clocking:
                break;
            case R.id.bt_student_will_info:
                Intent intent = new Intent(StudentWillActivity.this,RegisterStudentActivity.class) ;
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantValue.INFO,bean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
