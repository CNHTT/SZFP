package com.szfp.szfp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfplib.adapter.BaseListAdapter;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名称：SZFP.
 * 创建人： CT.
 * 创建时间: 2017/7/10.
 * GitHub:https://github.com/CNHTT
 */

public class AgricultureFramerAdapter extends BaseListAdapter<AgricultureFarmerBean> {
    public AgricultureFramerAdapter(Context mContext, List<AgricultureFarmerBean> mDatas) {
        super(mContext, mDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView ==null){ 
            convertView = ContextUtils.inflate(mContext, R.layout.framer_item_layout);
            viewHolder  = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else viewHolder = (ViewHolder) convertView.getTag();
       viewHolder.setData(getItem(position),position);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.no)
        TextView no;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.id_number)
        TextView idNumber;
        @BindView(R.id.ac_number)
        TextView acNumber;
        @BindView(R.id.litres)
        TextView litres;
        @BindView(R.id.amount)
        TextView amount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(AgricultureFarmerBean item, int position) {
            name.setText(item.getName());
            no.setText(String.valueOf(position+1));
            acNumber.setText(item.getRegistrationNumber());
            idNumber.setText(item.getIDNumber());
            litres.setText(String.valueOf(item.getNumberOfAnimals()));
            amount.setText(DataUtils.format2Decimals(String.valueOf(item.getAmount())));
        }
    }
}
