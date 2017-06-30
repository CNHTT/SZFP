package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.SPUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DynamicActivity extends BaseActivity {

    @BindView(R.id.ed_minimum)
    EditText edMinimum;
    @BindView(R.id.ed_maximum)
    EditText edMaximum;
    @BindView(R.id.bt_dynamic_sava)
    StateButton btDynamicSava;
    @BindView(R.id.bt_dynamic_cancel)
    StateButton btDynamicCancel;


    private String min;
    private String max;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_dynamic_sava, R.id.bt_dynamic_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_dynamic_sava:
                max = edMaximum.getText().toString();
                min = edMinimum .getText().toString();

                if (DataUtils.isNullString(max)||DataUtils.isNullString(min)||Float.valueOf(min)>Float.valueOf(max)){
                    ToastUtils.error(getResources().getString(R.string.please_input));
                    return;
                }

                SPUtils.putString(this, ConstantValue.MAX,max);
                SPUtils.putString(this, ConstantValue.MIM,min);
                edMaximum.setText("");edMinimum.setText("");

                ToastUtils.success("SUCCESS");

                break;
            case R.id.bt_dynamic_cancel:
                edMaximum.setText("");edMinimum.setText("");
                onBackPressed();
                break;
        }
    }
}
