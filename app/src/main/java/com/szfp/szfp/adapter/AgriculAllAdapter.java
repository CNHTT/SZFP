package com.szfp.szfp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfp.bean.AgricultureFarmerCollection;
import com.szfp.szfplib.adapter.BaseListAdapter;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名称：SZFP.
 * 创建人： CT.
 * 创建时间: 2017/7/10.
 * GitHub:https://github.com/CNHTT
 */

public class AgriculAllAdapter extends BaseListAdapter<AgricultureFarmerCollection> {
    public AgriculAllAdapter(Context mContext, List<AgricultureFarmerCollection> mDatas) {
        super(mContext, mDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ContextUtils.inflate(mContext, R.layout.agri_all_layout);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setData(getItem(position), position);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.no)
        TextView no;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.idNumber)
        TextView idNumber;
        @BindView(R.id.acNumber)
        TextView acNumber;
        @BindView(R.id.litres)
        TextView litres;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.data)
        TextView data;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(AgricultureFarmerCollection item, int position) {
            no.setText(String.valueOf(position++));
            name.setText(item.getName());
            idNumber.setText(item.getIdNumber());
            acNumber.setText(item.getRegistrationNumber());
            litres.setText(String.valueOf(item.getAmountCollected()));
            amount.setText(DataUtils.format2Decimals(String.valueOf(item.getAmount())));
            data.setText(TimeUtils.milliseconds2String(item.getTime()));
        }
    }
}
