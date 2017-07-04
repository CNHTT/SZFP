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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.inter.OnSaveListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.util.Calendar;

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

    AgricultureFarmerBean bean ;

    private String name;
    private String  fingerPrintId;
    private String  iDNumber;
    private boolean  gender; //性别
    private String  registrationNumber;
    private String  contact;    //联系方式
    private String     numberOfAnimals;
    private String  dataOfBirth;
    private String  homeTown;
    private String  collectionRoute;

    private AsyncFingerprint asyncFingerprint;
    private ProgressDialog progressDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agirculture_register_farmer);
        ButterKnife.bind(this);
        rgFarmerSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId ==R.id.male) gender =true;else gender =false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
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

    private void showFingerImage(int fingerType, byte[] data) {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        // saveImage(data);
        ivFarmerFingerPrint.setBackgroundDrawable(new BitmapDrawable(image));
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


    private void initData() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(),mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
        iDNumber = TimeUtils.generateSequenceNo();
        etFarmerIdNumber.setText(iDNumber);
    }

    @OnClick({R.id.tv_farmer_date_of_birth, R.id.iv_farmer_finger_print, R.id.bt_farmer_register, R.id.bt_farmer_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_farmer_date_of_birth:
                date_of_birth();
                break;
            case R.id.iv_farmer_finger_print:
                asyncFingerprint.register2();
                break;
            case R.id.bt_farmer_register:
                register();
                break;
            case R.id.bt_farmer_cancel:
                break;
        }
    }

    private void register() {
        bean = new AgricultureFarmerBean();
        bean.setIDNumber(iDNumber);
        if (DataUtils.isNullString(dataOfBirth)){
            ToastUtils.error("Please select the date "); return;} bean.setDataOfBirth(dataOfBirth);
        name = etFarmerName.getText().toString();
        if (DataUtils.isNullString(name)){ToastUtils.error("Please input name");return;} bean.setName(name);

        registrationNumber = etFarmerRegisterNumber.getText().toString();
        if (DataUtils.isNullString(registrationNumber)){ToastUtils.error("Please input registerNumber");return;}bean.setRegistrationNumber(registrationNumber);

        contact =etFarmerContact .getText().toString();
        if (DataUtils.isNullString(contact)){ToastUtils.error("please input contact");return;} bean.setContact(contact);


        homeTown    =   tvFarmerHomeTown.getText().toString();
        if (DataUtils.isNullString(homeTown)){showErrorToast("Please Input Home Town");return;} bean.setHomeTown(homeTown);

        collectionRoute     =   tvFarmerCollectionRoute.getText().toString();
        if (DataUtils.isNullString(collectionRoute)){showErrorToast("Please input CollectionRoute");return;}    bean.setCollectionRoute(collectionRoute);

        numberOfAnimals = etFarmerNumberAnimals .getText().toString();

        if (DataUtils.isNullString(numberOfAnimals)){showErrorToast("Please Select NumberAnimals");return;}
        bean.setNumberOfAnimals(Integer.valueOf(numberOfAnimals));
        if (DataUtils.isNullString(fingerPrintId)){showErrorToast("Please enter your fingerprint");return;} bean.setFingerPrintId(fingerPrintId);
        bean.setGender(gender);
        DbHelper.insertAgricultureFramer(bean, new OnSaveListener() {
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

    private void date_of_birth() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AgricultureRegisterFarmerActivity.this, 0,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ++month;
                        if (month>10)
                            tvFarmerDateOfBirth.setText(year+"-"+month+"-"+day);
                        else
                            tvFarmerDateOfBirth.setText(year+"-0"+month+"-"+day);
                        dataOfBirth = tvFarmerDateOfBirth.getText().toString();
                    }
                },year,month,day);
        datePickerDialog.show();
    }
}
