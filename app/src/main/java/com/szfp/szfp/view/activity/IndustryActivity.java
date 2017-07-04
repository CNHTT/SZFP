package com.szfp.szfp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.szfp.szfp.R;
import com.szfp.szfp.adapter.ListAdapter;
import com.szfp.szfplib.weight.StateButton;

import java.util.Arrays;

public class IndustryActivity extends AppCompatActivity {


    private StateButton button;
    private ListView listView;
    private String[] keys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_industry);
        listView = (ListView) findViewById(R.id.indusry_list);


        keys = getResources().getStringArray(R.array.industrys);
        listView.setAdapter(new ListAdapter(this, Arrays.asList(keys)));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity( new Intent(IndustryActivity.this,TransportActivity.class));
                        break;
                    case 1:
                        startActivity( new Intent(IndustryActivity.this,BankingActivity.class));
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        startActivity( new Intent(IndustryActivity.this,StudentManagementActivity.class));
                        break;
                    case 5:
                        break;
                    case 6:
                        startActivity( new Intent(IndustryActivity.this,AgricultureActivity.class));
                        break;
                    case 7:
                        startActivity( new Intent(IndustryActivity.this,ParkingActivity.class));
                        break;
                }
            }
        });
    }
}
