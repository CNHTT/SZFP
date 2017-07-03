package com.szfp.szfp.view.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.StudentBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.inter.onRequestPermissionsListener;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.PermissionsUtils;
import com.szfp.szfplib.utils.PhotoUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.ConstantValue.FINGERPRINT;
import static com.szfp.szfp.ConstantValue.FINGERPRINT_END;

public class RegisterStudentActivity extends BaseActivity {

    @BindView(R.id.student_school_img)
    ImageView studentSchoolImg;
    @BindView(R.id.student_school_name)
    TextView studentSchoolName;
    @BindView(R.id.student_school_city)
    TextView studentSchoolCity;
    @BindView(R.id.student_first_name)
    EditText studentFirstName;
    @BindView(R.id.student_last_name)
    EditText studentLastName;
    @BindView(R.id.student_admission_number)
    EditText studentAdmissionNumber;
    @BindView(R.id.bt_student_fingerprints)
    StateButton btStudentFingerprints;
    @BindView(R.id.iv_student_fingerprints_photo)
    ImageView ivStudentFingerprintsPhoto;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.rgSex)
    RadioGroup rgSex;
    @BindView(R.id.student_nationality)
    EditText studentNationality;
    @BindView(R.id.type_day)
    RadioButton typeDay;
    @BindView(R.id.type_hostel_part_time)
    RadioButton typeHostelPartTime;
    @BindView(R.id.rg_board_type)
    RadioGroup rgBoardType;
    @BindView(R.id.et_student_email)
    EditText etStudentEmail;
    @BindView(R.id.iv_student_capture_photo)
    ImageView ivStudentCapturePhoto;
    @BindView(R.id.et_student_admission_date)
    EditText etStudentAdmissionDate;
    @BindView(R.id.bt_student_admission_date)
    Button btStudentAdmissionDate;
    @BindView(R.id.et_student_date_of_birth)
    EditText etStudentDateOfBirth;
    @BindView(R.id.bt_student_date_of_birth)
    Button btStudentDateOfBirth;
    @BindView(R.id.tv_student_department)
    TextView tvStudentDepartment;
    @BindView(R.id.et_student_call_photo)
    EditText etStudentCallPhoto;
    @BindView(R.id.ed_student_first_guardians_names)
    EditText edStudentFirstGuardiansNames;
    @BindView(R.id.ed_student_first_relationship)
    EditText edStudentFirstRelationship;
    @BindView(R.id.ed_student_first_guardians_contacts)
    EditText edStudentFirstGuardiansContacts;
    @BindView(R.id.ed_student_second_guardians_names)
    EditText edStudentSecondGuardiansNames;
    @BindView(R.id.ed_student_second_relationship)
    EditText edStudentSecondRelationship;
    @BindView(R.id.ed_student_second_guardians_contacts)
    EditText edStudentSecondGuardiansContacts;
    @BindView(R.id.ed_student_permanent_home_address)
    EditText edStudentPermanentHomeAddress;
    @BindView(R.id.ed_student_present_home_address)
    EditText edStudentPresentHomeAddress;
    @BindView(R.id.bt_student_register)
    StateButton btStudentRegister;
    @BindView(R.id.bt_student_cancel)
    StateButton btStudentCancel;
    private Calendar cal;
    private int year,month,day;

    private StudentBean bean;





    private AsyncFingerprint asyncFingerprint;
    private ProgressDialog progressDialog;
    private byte[] modelByte;

    private String fingerPrintFileUrl;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AsyncFingerprint.SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;
                case AsyncFingerprint.SHOW_FINGER_IMAGE:
                    // imageNum++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;
                case AsyncFingerprint.SHOW_FINGER_MODEL:
                    modelByte = (byte[]) msg.obj;
                    if (modelByte != null) {
                        Log.i("whw", "#################model.length="
                                + modelByte.length);
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+"  store="+msg.arg2);
                    break;
                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;
                        if (DataUtils.isNullString(fingerPrintFileUrl))
                            fingerPrintFileUrl=FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;

                        else {
                            //please user StringBuffer
                            fingerPrintFileUrl= fingerPrintFileUrl+"_"+FINGERPRINT+ String.valueOf(id)+FINGERPRINT_END;
                        }


                        ToastUtils.showToast(getString(R.string.register_success) + "  pageId=" + id);
                    } else {
                        ToastUtils.showToast(R.string.register_success);
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    ToastUtils.showToast(R.string.register_fail);
                    break;
                case AsyncFingerprint.VALIDATE_RESULT1:
                    cancleProgressDialog();
                    showValidateResult((Boolean) msg.obj);
                    break;
                case AsyncFingerprint.VALIDATE_RESULT2:
                    cancleProgressDialog();
                    Integer r = (Integer) msg.obj;
                    if (r != -1) {
                        ToastUtils.showToast( getString(R.string.verifying_through) +"  pageId=" + r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    ToastUtils
                            .showToast( (Integer) msg.obj);
                    // failTime++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    break;
                default:
                    break;
            }
        }

    };


    private void showFingerImage(int fingerType, byte[] data) {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        // saveImage(data);
        ivStudentFingerprintsPhoto.setBackgroundDrawable(new BitmapDrawable(image));
        writeToFile(data);
    }

    /**
     * 指纹图片
     * @param data
     */
    private void writeToFile(byte[] data) {
        String dir = Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.FINGERPRINT;
        File dirPath = new File(dir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        String filePath = dir + System.currentTimeMillis() + ".bmp";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(R.string.verifying_through);
        } else {
            ToastUtils.showToast(R.string.verifying_fail);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancleProgressDialog();
        asyncFingerprint.setStop(true);
    }

    @Override
    protected void onDestroy() {
        cancleProgressDialog();
        super.onDestroy();
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(),mHandler);

        asyncFingerprint.setOnEmptyListener(new AsyncFingerprint.OnEmptyListener() {
            @Override
            public void onEmptySuccess() {
                ToastUtils.showToast(R.string.clear_flash_success);
            }

            @Override
            public void onEmptyFail() {
                ToastUtils.showToast(R.string.clear_flash_fail);
            }
        });


        asyncFingerprint.setOnCalibrationListener(new AsyncFingerprint.OnCalibrationListener() {
            @Override
            public void onCalibrationSuccess() {

                Log.d("whw", "onCalibrationSuccess");
                ToastUtils.showToast(R.string.calibration_success);
            }

            @Override
            public void onCalibrationFail() {

                Log.i("whw", "onCalibrationFail");
                ToastUtils.showToast(R.string.calibration_fail);
            }
        });

        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
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

























    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
        ButterKnife.bind(this);

        bean = (StudentBean) getIntent().getCharSequenceExtra(ConstantValue.INFO);

        if (!DataUtils.isEmpty(bean))initBeanView();
        else

        bean = new StudentBean();
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);


        initEvent();

    }

    private void initBeanView() {
        if (!DataUtils.isNullString(bean.getFirstName()))studentFirstName.setText(bean.getFirstName());
        if (!DataUtils.isNullString(bean.getLastName()))studentLastName.setText(bean.getLastName());
        if (!DataUtils.isNullString(bean.getAdmissionNumber()))studentAdmissionNumber.setText(bean.getAdmissionNumber());
        if (!DataUtils.isNullString(bean.getNationality())) studentNationality.setText(bean.getNationality());
        if (!DataUtils.isNullString(bean.getEmail()))etStudentEmail.setText(bean.getEmail());
        if (!DataUtils.isNullString(bean.getDataOfBirthStr())) etStudentDateOfBirth.setText(bean.getDataOfBirthStr());
        if (!DataUtils.isNullString(bean.getAdmissionDateStr()))etStudentAdmissionDate.setText(bean.getAdmissionDateStr());
        if (!DataUtils.isNullString(String.valueOf(bean.getDepartment())))  tvStudentDepartment.setText(String.valueOf(bean.getDepartment()));
        if (!DataUtils.isNullString(bean.getCellPhone()))etStudentCallPhoto.setText(bean.getCellPhone());
        if (!DataUtils.isNullString(bean.getPermanentHomeAddress()))edStudentPresentHomeAddress.setText(bean.getPermanentHomeAddress());
        if (!DataUtils.isNullString(bean.getPresentHomeAddress()))edStudentPresentHomeAddress.setText(bean.getPresentHomeAddress());
        if (!DataUtils.isNullString(bean.getFirstGuardianName())) edStudentFirstGuardiansNames.setText(bean.getFirstGuardianName());
        if (!DataUtils.isNullString(bean.getFirstGuardianContacts())) edStudentFirstGuardiansContacts.setText(bean.getFirstGuardianContacts());
        if (!DataUtils.isNullString(bean.getFirstGuardianRelationship())) edStudentFirstRelationship.setText(bean.getFirstGuardianRelationship());
        if (!DataUtils.isNullString(bean.getSecondGuardianName())) edStudentSecondGuardiansNames.setText(bean.getSecondGuardianName());
        if (!DataUtils.isNullString(bean.getSecondGuardianContacts())) edStudentSecondGuardiansContacts.setText(bean.getSecondGuardianContacts());
        if (!DataUtils.isNullString(bean.getSecondGuardianRelationship())) edStudentSecondRelationship.setText(bean.getSecondGuardianRelationship());

        if (bean.getGender())rgSex.check(R.id.male); else rgSex.check(R.id.female);
    }

    private void initEvent() {
        gender=true;
        boardingType = "day";
        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId==R.id.female)gender =false;else  gender=true;
            }
        });

        rgBoardType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.type_day) boardingType = "DAY" ;else boardingType="HOSTEL PART TIME";
            }
        });
    }

    @OnClick({R.id.bt_student_admission_date, R.id.bt_student_date_of_birth, R.id.bt_student_register, R.id.iv_student_capture_photo,R.id.bt_student_cancel, R.id.bt_student_fingerprints})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_student_admission_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterStudentActivity.this, 0,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                ++month;
                                if (month>10)
                                etStudentAdmissionDate.setText(year+"-"+month+"-"+day);
                                else
                                etStudentAdmissionDate.setText(year+"-0"+month+"-"+day);
                                admissionDateStr = etStudentAdmissionDate.getText().toString();
                                admissionDateLong= TimeUtils.stringMillis(admissionDateStr);

                            }
                        },year,month,day);
                datePickerDialog.show();
                break;
            case R.id.bt_student_date_of_birth:
                DatePickerDialog date1PickerDialog = new DatePickerDialog(RegisterStudentActivity.this, 0,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                ++month;
                                if (month>10)
                                    etStudentDateOfBirth.setText(year+"-"+month+"-"+day);
                                else
                                    etStudentDateOfBirth.setText(year+"-0"+month+"-"+day);
                                dataOfBirthStr = etStudentDateOfBirth.getText().toString();
                                dataOfBirthLong= TimeUtils.stringMillis(dataOfBirthStr);
                                department = TimeUtils.getAge(dataOfBirthStr);
                                tvStudentDepartment.setText(String.valueOf(department));


                            }
                        },year,month,day);
                date1PickerDialog.show();
                break;
            case R.id.bt_student_register:
                register();
                break;
            case R.id.bt_student_cancel:
                break;
            case R.id.bt_student_fingerprints:
                asyncFingerprint.register2();
                break;
            case R.id.iv_student_capture_photo:
                openCamera();
                break;
        }
    }


    /**
     * open camera
     */
    private void openCamera() {
        PermissionsUtils.requestCamera(RegisterStudentActivity.this, new onRequestPermissionsListener() {
            @Override
            public void onRequestBefore() {

            }

            @Override
            public void onRequestLater() {
                PhotoUtils.openCameraImage(RegisterStudentActivity.this,Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PhotoUtils.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
//                    Log.d("普通裁剪后的处理地址",PhotoUtils.imageUriFromCamera.toString());
//
//                    if (!DataUtils.isNullString(studentPhotoFileURl)){
//                        File file = new File(studentPhotoFileURl);
//                        file.delete();
//                    }
//                    studentPhotoFileURl =PhotoUtils.imageUriFromCamera.toString();
//                    Glide.with(this).
//                            load(PhotoUtils.imageUriFromCamera).
//                            diskCacheStrategy(DiskCacheStrategy.RESULT).
//                            thumbnail(0.5f).
//                            placeholder(R.drawable.ic_check_white_48dp).
//                            priority(Priority.LOW).
//                            error(R.drawable.linecode_icon).
//                            fallback(R.drawable.ic_clear_white_48dp).
//                            dontAnimate().
//                            into(ivStudentCapturePhoto);

                    PhotoUtils.cropImage(RegisterStudentActivity.this, PhotoUtils.imageUriFromCamera ,  Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.PHOTO);// 裁剪图片
                }

                break;
            case PhotoUtils.CROP_IMAGE://普通裁剪后的处理
                //同上
                Log.d("普通裁剪后的处理地址",PhotoUtils.cropImageUri.toString());

                if (!DataUtils.isNullString(studentPhotoFileURl)){
                    File file = new File(studentPhotoFileURl);
                    file.delete();
                }
                studentPhotoFileURl =PhotoUtils.cropImageUri.toString();
                Glide.with(this).
                        load(PhotoUtils.cropImageUri).
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        thumbnail(0.5f).
                        placeholder(R.drawable.ic_check_white_48dp).
                        priority(Priority.LOW).
                        error(R.drawable.linecode_icon).
                        fallback(R.drawable.ic_clear_white_48dp).
                        dontAnimate().
                        into(ivStudentCapturePhoto);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private String firstName;
    private String lastName;
    private String fullName;
    private String admissionNumber;
    private String studentPhotoFileURl;
    private boolean gender;
    private String  admissionDateStr;
    private long    admissionDateLong;
    private String  dataOfBirthStr;
    private long    dataOfBirthLong;
    private String nationality;
    private int department;
    private String boardingType;
    private String email;
    private String cellPhone;
    private String firstGuardianName;
    private String firstGuardianRelationship;
    private String firstGuardianContacts;
    private String secondGuardianName;
    private String secondGuardianRelationship;
    private String secondGuardianContacts;
    private String permanentHomeAddress;
    private String presentHomeAddress;
    private void register() {
        firstName = studentFirstName.getText().toString();
        if (DataUtils.isNullString(firstName)){
            ToastUtils.error("PLEASE INPUT");
            return;
        } bean.setFirstName(firstName);

        lastName = studentLastName.getText().toString();
        if (DataUtils.isNullString(lastName)){
            ToastUtils.error("PLEASE INPUT");
            return;
        } bean.setLastName(lastName);

        bean.setFullName(firstName+" "+lastName);

        admissionNumber= studentAdmissionNumber.getText().toString();
        if (DataUtils.isNullString(admissionNumber)){
            ToastUtils.error("PLEASE INPUT");
            return;
        } bean.setAdmissionNumber(admissionNumber);

        bean.setGender(gender); bean.setBoardingType(boardingType);

        nationality = studentNationality .getText().toString();


        cellPhone= etStudentCallPhoto.getText().toString();
        if (DataUtils.isNullString(cellPhone)){
                ToastUtils.error("PLEASE INPUT");
                return;
            } bean.setCellPhone(cellPhone);

        email = etStudentEmail.getText().toString();
        if (DataUtils.isNullString(email))
        {
            ToastUtils.error(getResources().getString(R.string.please_input)); return;
        }bean.setEmail(email);


        firstGuardianName = edStudentFirstGuardiansNames.getText().toString();
        if (DataUtils.isNullString(firstGuardianName)) {ToastUtils.error(getResources().getString(R.string.please_input)); return;}
        bean.setFirstGuardianName(firstGuardianName);

        firstGuardianRelationship = edStudentFirstRelationship.getText().toString();
        if (DataUtils.isNullString(firstGuardianRelationship)) {ToastUtils.error(getResources().getString(R.string.please_input)); return;}
        bean.setFirstGuardianRelationship(firstGuardianRelationship);

        firstGuardianContacts = edStudentFirstGuardiansContacts.getText().toString();
        if (DataUtils.isNullString(firstGuardianContacts)) {ToastUtils.error(getResources().getString(R.string.please_input)); return;}
        bean.setFirstGuardianContacts(firstGuardianContacts);



        secondGuardianName  = edStudentSecondGuardiansNames.getText().toString();
        if (DataUtils.isNullString(secondGuardianName)) {ToastUtils.error(getResources().getString(R.string.please_input)); return;}
        bean.setSecondGuardianName(secondGuardianName);

        secondGuardianContacts = edStudentSecondGuardiansContacts .getText().toString();
        if (DataUtils.isNullString(secondGuardianContacts)){ToastUtils.error(getResources().getString(R.string.please_input)); return;}
        bean.setSecondGuardianContacts(secondGuardianContacts);


        secondGuardianRelationship =edStudentSecondRelationship .getText().toString();
        if (DataUtils.isNullString(secondGuardianRelationship)){ToastUtils.error(getResources().getString(R.string.please_input)); return;}
        bean.setSecondGuardianRelationship(secondGuardianRelationship);

        permanentHomeAddress = edStudentPermanentHomeAddress.getText().toString();
        if (DataUtils.isNullString(permanentHomeAddress)){ToastUtils.error(getResources().getString(R.string.please_input));return;}
        bean.setPermanentHomeAddress(permanentHomeAddress);

        presentHomeAddress = edStudentPresentHomeAddress.getText().toString();
        if (DataUtils.isNullString(presentHomeAddress)){ToastUtils.error(getResources().getString(R.string.please_input));return;}
        bean.setPresentHomeAddress(presentHomeAddress);



        //保持图片地址
        if (DataUtils.isNullString(studentPhotoFileURl)){ToastUtils.error(getResources().getString(R.string.please_input));return;} bean.setStudentPhotoFileURl(studentPhotoFileURl);


        if (DataUtils.isNullString(fingerPrintFileUrl)){ToastUtils.error(getString(R.string.please_input_fingerprint));return;}   bean.setCaptureFingerprintFileURl(fingerPrintFileUrl);


        bean.setDataOfBirthStr(dataOfBirthStr);
        bean.setDataOfBirthLong(dataOfBirthLong);
        bean.setDepartment(department);
        bean.setAdmissionDateStr(admissionDateStr);
        bean.setAdmissionDateLong(admissionDateLong);


        try {
            DbHelper.insertStudentBean(bean);
            ToastUtils.success("OK");
            onBackPressed();
        }catch (Exception e){
            ToastUtils.error("please try again");
        }






    }
}
