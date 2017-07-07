package com.szfp.szfp.view.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.asynctask.AsyncFingerprint;
import com.szfp.szfp.bean.ParkingInfoBean;
import com.szfp.szfp.bean.VehicleParkingBean;
import com.szfp.szfp.inter.OnSaveVehicleLeaveListener;
import com.szfp.szfp.inter.OnSaveVehicleParking;
import com.szfp.szfp.inter.OnVerifyParkingListener;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfp.utils.PrintUtils;
import com.szfp.szfplib.adapter.AlertViewAdapter;
import com.szfp.szfplib.inter.OnItemClickListener;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.SPUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.AlertView;
import com.szfp.szfplib.weight.StateButton;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.FingerprintAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_FAIL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.REGISTER_SUCCESS;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_FINGER_MODEL;
import static com.szfp.szfp.asynctask.AsyncFingerprint.SHOW_PROGRESSDIALOG;
import static com.szfp.szfp.asynctask.AsyncFingerprint.UP_IMAGE_RESULT;
import static com.szfp.szfp.asynctask.AsyncFingerprint.VALIDATE_RESULT1;
import static com.szfp.szfp.asynctask.AsyncFingerprint.VALIDATE_RESULT2;
import static com.szfp.szfplib.utils.DataUtils.isNullString;

public class ParkingLeaveActivity extends BasePrintActivity {
    @BindView(R.id.et_parking_top_up)
    EditText etParkingTopUp;
    @BindView(R.id.ck_parking_conn_print)
    CheckBox ckParkingConnPrint;
    @BindView(R.id.bt_parking_cash)
    StateButton btParkingCash;
    @BindView(R.id.bt_parking_save)
    StateButton btParkingSave;
    @BindView(R.id.bt_parking_cancel)
    StateButton btParkingCancel;
    private String input;
    private boolean isPrint = false;

    private ProgressDialog progressDialog;
    private AsyncFingerprint asyncFingerprint;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;
                case SHOW_FINGER_MODEL:
                    cancleProgressDialog();
                    break;
                case REGISTER_SUCCESS:
                    cancleProgressDialog();//注册成功
                    break;
                case REGISTER_FAIL:
                    cancleProgressDialog();
                    break;//注册失败
                case VALIDATE_RESULT1:
                    cancleProgressDialog();
                    showValidateResult((Boolean) msg.obj);
                    break;
                case VALIDATE_RESULT2:
                    cancleProgressDialog();
                    Integer r = (Integer) msg.obj;
                    if (r != -1) {
                        showValidateResult(r);
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    ToastUtils.showToast(String.valueOf((Integer) msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

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
        super.onDestroy();
        cancleProgressDialog();
    }

    private void initData() {
        asyncFingerprint = new AsyncFingerprint(handlerThread.getLooper(), mHandler);
        asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
    }

    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            ToastUtils.showToast(
                    R.string.verifying_through);
        } else {
            ToastUtils.showToast(
                    R.string.verifying_fail);
        }
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


    /**
     * 验是否
     *
     * @param r
     */
    private long beanEndTime;
    private int beanParameters_type;
    private void showValidateResult(final Integer r) {
        endTime = TimeUtils.getCurTimeMills();
        time = (int) (endTime-vehicleParkingBean.getStartTime())/60/1000/60;
        if (time==0) time =1;
        hourPee = Double.parseDouble(SPUtils.getString(ParkingLeaveActivity.this, ConstantValue.PARING_FEE_HOUR));
        number  = time*hourPee;
        vehicleParkingBean.setEndTime(endTime);
        vehicleParkingBean.setTime(time);
        vehicleParkingBean.setPayNum(DataUtils.getAmountValue(number));
        DbHelper.getParkingBean(String.valueOf(r), new OnVerifyParkingListener() {
            @Override
            public void success(final ParkingInfoBean bean) {


               beanParameters_type =  bean.getParameters_type();
                switch (beanParameters_type){

                    case 0:
                        fingerPay0(bean);
                        break;
                    case 1:

                        break;
                    case 2:
                        if (bean.getEndTime()>endTime){

                            DbHelper.updateParkingLeave(vehicleParkingBean, new OnSaveVehicleParking() {
                                @Override
                                public void success(VehicleParkingBean a) {
                                    if (isPrint)PrintUtils.printLeaveParking(bean,a);
                                }

                                @Override
                                public void error(String str) {

                                }
                            });

                        }else {
                            fingerPay0(bean);
                        }

                        break;
                    case 3:

                        if (bean.getEndTime()>endTime){
                            DbHelper.updateParkingLeave(vehicleParkingBean, new OnSaveVehicleParking() {
                                @Override
                                public void success(VehicleParkingBean a) {
                                    if (isPrint)PrintUtils.printLeaveParking(bean,a);
                                }

                                @Override
                                public void error(String str) {

                                }
                            });
                        }else {
                            fingerPay0(bean);
                        }

                        break;
                }
            }

            @Override
            public void error(String s) {
                showErrorToast(s);
            }
        });
    }

    private void fingerPay0(final ParkingInfoBean bean) {

        alertView=   new AlertView("Parking fares", DataUtils.getAmountValue(number), null, new String[]{"OK ", "BUY PARKING FEE"}, null, ParkingLeaveActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position==0){
                    vehicleParkingBean.setEndTime(endTime);
                    vehicleParkingBean.setTime(time);
                    vehicleParkingBean.setPayNum(DataUtils.getAmountValue(number));
                    DbHelper.updateParkingLeave(vehicleParkingBean, bean, new OnSaveVehicleLeaveListener() {
                        @Override
                        public void success(VehicleParkingBean vehicleParkingBean, ParkingInfoBean bean) {
                            PrintUtils.printLeaveParkingFinger0(vehicleParkingBean,bean);
                        }

                        @Override
                        public void error(String s) {
                            showErrorToast("Please try again");
                        }
                    });
                }else {
                    startActivity( new Intent(ParkingLeaveActivity.this,ParkingBuyParkingActivity.class));
                }
            }
        });
        alertView.show();
    }

    private TextView textView;
    private StateButton button;
    private Dialog dialog;
    private void showParkingInfo(ParkingInfoBean bean) {
        if (dialog == null){
            dialog = new Dialog(this,R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this,R.layout.dialog_parking_info);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(view,new LinearLayout.LayoutParams(-1,-1));
            textView = (TextView) view.findViewById(R.id.tv_dialog_parking_info);
            button = (StateButton) view.findViewById(R.id.bt_dialog_paring_cancel);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            textView.setText("NAME: "+bean.getName()+"\n");
            textView.append("ID NUMBER: "+bean.getIdNumber()+"\n");
            textView.append("VEHICLE REG NUMBER: "+bean.getVehicleRegNumber()+"\n");
            textView.append("BALANCE:   "+bean.getBalance()+"\n");
            textView.append("OTHER"+"\n");
            switch (bean.getParameters_type()){
                case 0:textView.append("no have");break;
                case 1:textView.append("");break;
                case 2:textView.append("");break;
                case 3:textView.append("");break;
            }
        }
        if (!dialog.isShowing())
            dialog.show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_leave);
        ButterKnife.bind(this);
        ckParkingConnPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (BluetoothPrintDriver.IsNoConnection()) ckParkingConnPrint.setChecked(false);

                    showDeviceList();

                } else {
                    isPrint = false;

                }
            }
        });
        if (isNullString(SPUtils.getString(ParkingLeaveActivity.this, ConstantValue.PARING_FEE_HOUR))){ToastUtils.error("PLEASE SET PARKING FEE");}

    }
    @Override
    protected void showConnecting() {
        ckParkingConnPrint.setText("Connecting.....");

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        ckParkingConnPrint.setText("conn to " + mConnectedDeviceName);
        ckParkingConnPrint.setChecked(true);
        isPrint = true;
    }

    @OnClick({R.id.bt_parking_cash, R.id.bt_parking_save, R.id.bt_parking_cancel})
    public void onClick(View view) {

        input =etParkingTopUp .getText().toString();
        if (isNullString(input)){ToastUtils.error("Please Input"); return;}
        switch (view.getId()) {
            case R.id.bt_parking_cash:
                cash();
                break;
            case R.id.bt_parking_save:
                check();
                break;
            case R.id.bt_parking_cancel:
                break;
        }
    }

    private Dialog adialog;
    private VehicleParkingBean vehicleParkingBean;
    private void check() {
        DbHelper.checkParkingLeave(input, new OnSaveVehicleParking() {
            @Override
            public void success(VehicleParkingBean bean) {
                vehicleParkingBean =bean;
                if (bean.getIsPay()){
                    ToastUtils.success("ALREADY PAID");
                }else {
                    showCheckDialog();
                }
            }

            @Override
            public void error(String str) {
                showErrorToast(str);
            }
        });
    }

    /**
     *
     */

    private ArrayList<String>   mDatas = new ArrayList<String>();
    private List<String>         mDestructive=new ArrayList<>();
    private ListView listView;
    private void showCheckDialog() {
        mDatas = new ArrayList<String>();
        mDestructive=new ArrayList<>();
        mDatas.add("CASH PAYMENT");
        mDatas.add("FEE DEDUCTION");
        mDatas.add("BUY PARKING FEE");
        mDestructive.add("CASH PAYMENT");
        mDestructive.add("FEE DEDUCTION");
        mDestructive.add("BUY PARKING FEE");
        mDatas.add("BACK");
        if (adialog == null) {
            adialog = new Dialog(this, R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this, R.layout.dialog_check);
            adialog.setCanceledOnTouchOutside(true);
            adialog.setContentView(view, new LinearLayout.LayoutParams(-1, -1));
            listView = (ListView) view.findViewById(R.id.checkListView);
            AlertViewAdapter adapter = new AlertViewAdapter(mDatas,mDestructive);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.d("Position","position"+position);
                    adialog.dismiss();switch (position){
                        case 0:  cash();break;
                        case 1:  asyncFingerprint.validate2(); break;
                        case 2: startActivity( new Intent(ParkingLeaveActivity.this,ParkingBuyParkingActivity.class));  break;
                        case 3:  break;
                    }
                }
            });

        }adialog.show();
    }


    /**
     * 停车的时间
     */
    private int time;
    private long endTime;
    private double number;
    private double hourPee;
    private AlertView alertView;
    private void cash() {
        DbHelper.getParkingLeave(input, new OnSaveVehicleParking() {
            @Override
            public void success(final VehicleParkingBean bean) {
                endTime = TimeUtils.getCurTimeMills();
                time = (int) (endTime-bean.getStartTime())/60/1000/60;
                if (time==0) time =1;
                if (isNullString(SPUtils.getString(ParkingLeaveActivity.this, ConstantValue.PARING_FEE_HOUR))){ToastUtils.error("PLEASE SET PARKING FEE");}else {
                    hourPee = Double.parseDouble(SPUtils.getString(ParkingLeaveActivity.this, ConstantValue.PARING_FEE_HOUR));
                    number  = time*hourPee;
                    alertView=   new AlertView("Parking fares", DataUtils.getAmountValue(number), null, new String[]{"OK", "NO"}, null, ParkingLeaveActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position==0){
                                bean.setEndTime(endTime);
                                bean.setTime(time);
                                bean.setPayNum(DataUtils.getAmountValue(number));
                                DbHelper.updateParkingLeave(bean, new OnSaveVehicleParking() {
                                    @Override
                                    public void success(VehicleParkingBean bean) {
                                        PrintUtils.printLeaveParking(bean);
                                    }
                                    @Override
                                    public void error(String str) {
                                        ToastUtils.error("Please try again");
                                    }
                                });
                            }
                        }
                    });
                    alertView.show();
                }
            }
            @Override
            public void error(String str) {
                showErrorToast(str);
            }
        });
    }
}
