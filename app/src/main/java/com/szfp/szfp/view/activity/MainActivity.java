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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private StateButton mainButton;
    private ListView listView;

    private String[] keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);

        mainButton = (StateButton) findViewById(R.id.mainButton);
        listView = (ListView) findViewById(R.id.main_list);
        mainButton .setOnClickListener(this);


        keys  = getResources().getStringArray(R.array.meaus);

        listView.setAdapter( new ListAdapter(this, Arrays.asList(keys)));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity( new Intent(MainActivity.this,IndustryActivity.class));
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainButton:

                break;
        }
    }
}
