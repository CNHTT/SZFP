package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.szfp.szfp.R;
import com.szfp.szfp.bean.AgricultureFarmerCollection;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.weight.StateButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgricultureReportActivity extends BaseNoActivity {

    @BindView(R.id.bt_framer_export)
    StateButton btFramerExport;
    @BindView(R.id.agri_all_list)
    ListView agriAllList;


    private List<AgricultureFarmerCollection> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ageiclture_report);
        ButterKnife.bind(this);

        list = DbHelper.getAllListAgriReport();
        if (DataUtils.isEmpty(list)){
            showErrorToast("No Report");
            btFramerExport.setEnabled(false);
        }else {

        }
    }

    @OnClick(R.id.bt_framer_export)
    public void onClick() {
        
    }
}
