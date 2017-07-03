package com.szfp.szfp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.adapter.TransportListAdapter;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransportActivity extends BaseActivity {


    @BindView(R.id.tv_commuter_num)
    TextView tvCommuterNum;
    @BindView(R.id.tv_vehicles_num)
    TextView tvVehiclesNum;
    @BindView(R.id.li_search_commuter)
    LinearLayout liSearchCommuter;
    @BindView(R.id.trans_top_up)
    LinearLayout transTopUp;
    @BindView(R.id.trans_upload_data)
    LinearLayout transUploadData;
    @BindView(R.id.trans_reports)
    LinearLayout transReports;
    @BindView(R.id.trap_list)
    ListView listView;

    private String[] keys;

    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        ButterKnife.bind(this);

        keys = getResources().getStringArray(R.array.transports);
        dbHelper = new DbHelper();

        listView.setAdapter(new TransportListAdapter(this, Arrays.asList(keys)));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(TransportActivity.this, VehiclesActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(TransportActivity.this, CommuterActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(TransportActivity.this, DynamicActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(TransportActivity.this, TransportStaticFareActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(TransportActivity.this, ReverseActivity.class));
                        break;
                }
            }
        });
    }


    private Dialog dialog;
    private int type = 0;
    private Spinner spinner;
    private EditText etInput;
    private String input;

    private StateButton ok;
    private StateButton cancel;

    private void showSearchCommuter() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.AlertDialogStyle);
            View view = ContextUtils.inflate(this, R.layout.search_commuter_select);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(view, new LinearLayout.LayoutParams(-1, -1));
            spinner = (Spinner) view.findViewById(R.id.search_commuter_spinner);
            etInput = (EditText) view.findViewById(R.id.ed_search_commuter);
            ok = (StateButton) view.findViewById(R.id.bt_search_c_s);
            cancel = (StateButton) view.findViewById(R.id.bt_cancel_c_s);

            ok.setOnClickListener(new OnClickSelect());
            cancel.setOnClickListener(new OnClickSelect());
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        if (!dialog.isShowing())
            dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        tvVehiclesNum.setText(dbHelper.getVehiclesNum());
        tvCommuterNum.setText(dbHelper.getCommuterNum());
    }

    private CommuterAccountInfoBean c;

    @OnClick({R.id.li_search_commuter, R.id.trans_top_up, R.id.trans_upload_data, R.id.trans_reports,R.id.sbt_charge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.li_search_commuter:
                showSearchCommuter();
                break;
            case R.id.trans_top_up:
                ToastUtils.success("TOP UP");
                break;
            case R.id.trans_upload_data:
                ToastUtils.success("UPLOAD DATA");
                break;
            case R.id.trans_reports:
                startActivity(new Intent(TransportActivity.this,TransportAllReportsActivity.class));
                break;
            case R.id.sbt_charge:
//
//                if (SPUtils.getBoolean(this,ConstantValue.STATIC_FARE_TYPE))
//                {
//                    startActivity(new Intent(TransportActivity.this,TransportChargeActivity.class));
//                }else {
                    startActivity(new Intent(TransportActivity.this,TransportSelectActivity.class));
//                }
                break;
        }
    }

    private class OnClickSelect implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_search_c_s:
                    input = etInput.getText().toString();
                    if (DataUtils.isNullString(input)) {
                        showToastError();
                        return;
                    }

                    c = dbHelper.getCommuterInfo(type, input);
                    if (DataUtils.isEmpty(c)) {

                        showToastError("No Register");
                    } else {
                        dialog.dismiss();
                        Intent intent = new Intent(TransportActivity.this, CommuterAccountActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstantValue.INFO, c);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }


                    break;
                case R.id.bt_cancel_c_s:
                    dialog.dismiss();
                    break;
            }

        }
    }
}
