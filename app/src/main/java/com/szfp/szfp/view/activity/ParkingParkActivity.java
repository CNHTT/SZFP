package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.R;
import com.szfp.szfplib.inter.OnItemClickListener;
import com.szfp.szfplib.weight.AlertView;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szfp.szfplib.utils.DataUtils.isNullString;
import static com.szfp.szfplib.utils.ToastUtils.error;

public class ParkingParkActivity extends BasePrintActivity implements OnItemClickListener {

    @BindView(R.id.et_parking_top_up)
    EditText etParkingTopUp;
    @BindView(R.id.ck_parking_conn_print)
    CheckBox ckParkingConnPrint;
    @BindView(R.id.bt_parking_save)
    StateButton btParkingSave;
    @BindView(R.id.bt_parking_cancel)
    StateButton btParkingCancel;


    private boolean isPrint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_park);
        ButterKnife.bind(this);


        ckParkingConnPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (BluetoothPrintDriver.IsNoConnection()) ckParkingConnPrint.setChecked(false);
                    else showDeviceList();

                } else {
                    isPrint = false;

                }
            }
        });
    }


    @Override
    protected void showConnecting() {
        ckParkingConnPrint.setText("Connecting.....");

    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        ckParkingConnPrint.setText("conn to " + mConnectedDeviceName);
        isPrint = true;
    }

    @OnClick({R.id.bt_parking_save, R.id.bt_parking_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_parking_save:
                save();
                break;
            case R.id.bt_parking_cancel:
                onBackPressed();
                break;
        }
    }

    private AlertView alertView;
    private  String input ;
    private void save() {
        input = etParkingTopUp.getText().toString();
        if (isNullString(input)){
            error("PLEASE INPUT NUMBER");return;
        }

        alertView = new AlertView("Vehicle Reg Number",input ,null, new String[]{"确定","取消"},null, ParkingParkActivity.this, AlertView.Style.Alert, this);


        alertView.show();



    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position==0){

            //Vehicle parking

        }
    }
}
