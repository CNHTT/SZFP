package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.R;
import com.szfp.szfp.utils.PrintUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransportCashActivity extends BasePrintActivity {

    @BindView(R.id.ed_fixed_fare)
    EditText edFixedFare;
    @BindView(R.id.ck_staic_fare_checkbox)
    CheckBox ckStaicFareCheckbox;
    @BindView(R.id.bt_cash_ok)
    StateButton btCashOk;
    @BindView(R.id.bt_cash_cancel)
    StateButton btCashCancel;
    private  boolean isPrint =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_cash);
        ButterKnife.bind(this);

        ckStaicFareCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (BluetoothPrintDriver.IsNoConnection())
                        ckStaicFareCheckbox.setChecked(false);
                    showDeviceList();
                }else  isPrint =false;
            }
        });
    }

    @Override
    protected void showConnecting() {
        ckStaicFareCheckbox.setText("Connecting...");
    }

    @Override
    protected void showConnectedDeviceName(String mConnectedDeviceName) {
        isPrint =true;
        ckStaicFareCheckbox.setChecked(isPrint);
        ckStaicFareCheckbox.setText("Connected to "+mConnectedDeviceName);
    }



    @OnClick({R.id.bt_cash_ok, R.id.bt_cash_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cash_ok:
                print();
                break;
            case R.id.bt_cash_cancel:
                break;
        }
    }

    private String input;
    private void print() {
        input = edFixedFare .getText().toString();
        if (DataUtils.isNullString(input)){
            ToastUtils.error("PlEASE INPUT");
            return;
        }
        if (!isPrint){
            ToastUtils.error("Please connect the printer");
            return;
        }
        PrintUtils.printFareCash(input);

    }
}
