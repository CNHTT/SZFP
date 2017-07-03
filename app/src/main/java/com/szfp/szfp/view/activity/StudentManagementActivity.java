package com.szfp.szfp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudentManagementActivity extends BaseActivity {

    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.bt_student_gate_pass)
    StateButton btStudentGatePass;
    @BindView(R.id.bt_student_class_attendance)
    StateButton btStudentClassAttendance;
    @BindView(R.id.bt_student_upload_student)
    StateButton btStudentUploadStudent;
    @BindView(R.id.bt_student_attendance_report)
    StateButton btStudentAttendanceReport;
    @BindView(R.id.bt_student_register_student_staff)
    StateButton btStudentRegisterStudentStaff;
    @BindView(R.id.bt_student_fee_statement)
    StateButton btStudentFeeStatement;
    @BindView(R.id.bt_student_extra_curriculums)
    StateButton btStudentExtraCurriculums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_student_gate_pass, R.id.bt_student_class_attendance, R.id.bt_student_upload_student, R.id.bt_student_attendance_report,
            R.id.bt_student_register_student_staff, R.id.bt_student_fee_statement, R.id.bt_student_extra_curriculums})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_student_gate_pass:
                startActivity(new Intent(StudentManagementActivity.this,StudentGatePassActivity.class));
                break;
            case R.id.bt_student_class_attendance:
                break;
            case R.id.bt_student_upload_student:
                break;
            case R.id.bt_student_attendance_report:
                break;
            case R.id.bt_student_register_student_staff:
                showStudentAndStaffRegister();
                break;
            case R.id.bt_student_fee_statement:
                break;
            case R.id.bt_student_extra_curriculums:
                break;
        }
    }


    private Dialog dialogSelect = null;
    private void showStudentAndStaffRegister() {
        if (dialogSelect == null)
        {
            dialogSelect = new Dialog(this,R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this,R.layout.student_register_select);
            dialogSelect.setCanceledOnTouchOutside(true);
            dialogSelect.setContentView(view, new LinearLayout.LayoutParams(-1, -1));
            view.findViewById(R.id.bt_register_student).setOnClickListener(new OnClickDialog());
            view.findViewById(R.id.bt_register_staff).setOnClickListener(new OnClickDialog());
        }
        if (!dialogSelect.isShowing())
            dialogSelect.show();
    }

    private class OnClickDialog implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_register_student:
                    dialogSelect.dismiss();
                    startActivity(new Intent(StudentManagementActivity.this,RegisterStudentActivity.class));
                    break;
                case R.id.bt_register_staff:
                    dialogSelect.dismiss();
                    ToastUtils.error("The need is not clear");
                    break;
            }
        }
    }
}
