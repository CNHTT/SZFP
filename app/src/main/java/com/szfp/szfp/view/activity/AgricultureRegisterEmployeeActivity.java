package com.szfp.szfp.view.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.AgricultureEmployeeBean;
import com.szfp.szfp.inter.OnSaveListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.ConstantValue.FINGERPRINT;
import static com.szfp.szfp.ConstantValue.FINGERPRINT_END;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_FAIL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_SUCCESS;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_IMAGE;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_MODEL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_PROGRESSDIALOG;

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




    private AgricultureEmployeeBean bean ;
    private String name;
    private String  fingerPrintId;
    private String  iDNumber;
    private boolean  gender; //性别
    private String  registrationNumber;
    private String  contact;    //联系方式
    private String  jobTitle;
    private String  salary;
    private String  dataOfBirth;
    private String  employedDate;
    private String  homeTown;
    private String  collectionRoute;
    /**
     * Permanent, contractor, day laborer
     */
    private String  natureOfEmployment;
    private AsyncFingerprint asyncFingerprint;
    private ProgressDialog progressDialog;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Calendar cal;
    private int year,month,day;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;

                case SHOW_FINGER_IMAGE:
                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;

                case SHOW_FINGER_MODEL:
                    cancleProgressDialog();
                    break;

                case REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;
                        if (DataUtils.isNullString(fingerPrintId))
                            fingerPrintId=FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;

                        else {
                            //please user StringBuffer
                            fingerPrintId= fingerPrintId+"_"+FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;
                        }
                        Log.d("PRINT",fingerPrintId);
                        ToastUtils.showToast(getString(R.string.register_success));
                    } else {
                        ToastUtils.showToast(R.string.register_success);
                    }


                    break;
                case REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.error("Registration failed!");
                    break;
                default:
                    cancleProgressDialog();
                    break;


            }
        }
    };
    private void showFingerImage(int fingerType, byte[] data) {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        // saveImage(data);
        ivEmployeeFingerPrint.setBackgroundDrawable(new BitmapDrawable(image));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agirculture_register_employee);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(),mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncFingerprint.setStop(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancleProgressDialog();
    }

    private void initView() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
        list.add("Permanent");
        list.add("Contractor");
        list.add("Day Laborer");
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmployeeNature.setAdapter(adapter);
        spEmployeeNature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                natureOfEmployment = list.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        iDNumber = TimeUtils.generateSequenceNo();
        etEmployeeIdNumber.setText(iDNumber);

        rgEmployeeSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId ==R.id.male) gender =true;else gender =false;
            }
        });

    }

    @OnClick({R.id.tv_employee_date_of_birth, R.id.tv_employee_date_employed, R.id.iv_employee_finger_print, R.id.bt_employee_register,R.id.bt_employee_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_employee_date_of_birth:
                showDateOfBirth();
                break;
            case R.id.tv_employee_date_employed:
                employee_date_employed();
                break;
            case R.id.iv_employee_finger_print:
                asyncFingerprint.register2();
                break;
            case R.id.bt_employee_register:
                register();
                break;
            case R.id.bt_employee_cancel:
                onBackPressed();
                break;
        }
    }

    private void register() {
        bean = new AgricultureEmployeeBean();
        bean.setIDNumber(iDNumber);
        if (DataUtils.isNullString(dataOfBirth)){
            ToastUtils.error("Please select the date "); return;} bean.setDataOfBirth(dataOfBirth);
        if (DataUtils.isNullString(employedDate)){
            ToastUtils.error("Please select the date "); return;} bean.setEmployedDate(employedDate);
        name = etEmployeeName.getText().toString();
        if (DataUtils.isNullString(name)){ToastUtils.error("Please input name");return;} bean.setName(name);

        registrationNumber = etEmployeeRegisterNumber.getText().toString();
        if (DataUtils.isNullString(registrationNumber)){ToastUtils.error("Please input registerNumber");return;}bean.setRegistrationNumber(registrationNumber);

        contact =etEmployeeContact .getText().toString();
        if (DataUtils.isNullString(contact)){ToastUtils.error("please input contact");return;} bean.setContact(contact);

        jobTitle = etEmployeeJobTitle.getText().toString();
        if (DataUtils.isNullString(jobTitle)){showToastError("Please Input Job Title");return;} bean.setJobTitle(jobTitle);

        salary = etEmployeeSalary .getText().toString();
        if (DataUtils.isNullString(salary)){showErrorToast("Please Input Salary"); return;}     bean.setSalary(salary);

        homeTown    =   tvEmployeeHomeTown.getText().toString();
        if (DataUtils.isNullString(homeTown)){showErrorToast("Please Input Home Town");return;} bean.setHomeTown(homeTown);

        collectionRoute     =   tvEmployeeCollectionRoute.getText().toString();
        if (DataUtils.isNullString(collectionRoute)){showErrorToast("Please input CollectionRoute");return;}    bean.setCollectionRoute(collectionRoute);

        if (DataUtils.isNullString(natureOfEmployment)){showErrorToast("Please Select Nature Of Employment");return;}   bean.setNatureOfEmployment(natureOfEmployment);


        if (DataUtils.isNullString(fingerPrintId)){showErrorToast("Please enter your fingerprint");return;} bean.setFingerPrintId(fingerPrintId);

        bean.setGender(gender);
        DbHelper.insertAgricultureEmployee(bean, new OnSaveListener() {
            @Override
            public void success() {
                ToastUtils.success("Registration Successful");
                finish();
            }

            @Override
            public void error(String str) {
                showErrorToast(str);
            }
        });

    }

    private void employee_date_employed() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AgricultureRegisterEmployeeActivity.this, 0,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ++month;
                        if (month>10)
                            tvEmployeeDateEmployed.setText(year+"-"+month+"-"+day);
                        else
                            tvEmployeeDateEmployed.setText(year+"-0"+month+"-"+day);
                        employedDate= tvEmployeeDateEmployed.getText().toString();
                    }
                },year,month,day);
        datePickerDialog.show();
    }

    private void showDateOfBirth() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AgricultureRegisterEmployeeActivity.this, 0,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ++month;
                        if (month>10)
                            tvEmployeeDateOfBirth.setText(year+"-"+month+"-"+day);
                        else
                            tvEmployeeDateOfBirth.setText(year+"-0"+month+"-"+day);
                        dataOfBirth = tvEmployeeDateOfBirth.getText().toString();
                    }
                },year,month,day);
        datePickerDialog.show();
    }


    private void showProgressDialog(int resId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    asyncFingerprint.setStop(true);
                }
                return false;
            }
        });
        progressDialog.show();
    }


    private void cancleProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }
}
