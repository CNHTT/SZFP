package com.szfp.szfp.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.szfp.szfp.R;
import com.szfp.szfplib.weight.StateButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransportAllReportsActivity extends AppCompatActivity {

    @BindView(R.id.all_export)
    StateButton allExport;
    @BindView(R.id.full_all_report_list)
    ListView fullAllReportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_all_reports);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.all_export)
    public void onClick() {
    }
}
