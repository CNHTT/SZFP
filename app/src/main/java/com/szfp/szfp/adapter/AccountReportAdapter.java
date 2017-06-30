package com.szfp.szfp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfplib.adapter.BaseListAdapter;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author：ct on 2017/6/29 18:35
 * email：cnhttt@163.com
 */

public class AccountReportAdapter extends BaseListAdapter<AccountReportBean> {

    public AccountReportAdapter(Context mContext, List<AccountReportBean> mDatas) {
        super(mContext, mDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ContextUtils.inflate(mContext, R.layout.full_report_item);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setData(getItem(position),position);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.full_item_no)
        TextView no;
        @BindView(R.id.tv_a_c_number)
        TextView tvACNumber;
        @BindView(R.id.tv_ac_deposits)
        TextView tvAcDeposits;
        @BindView(R.id.tv_ac_date_deposited)
        TextView tvAcDateDeposited;
        @BindView(R.id.tv_ac_fare_paid)
        TextView tvAcFarePaid;
        @BindView(R.id.tv_ac_date)
        TextView tvAcDate;
        @BindView(R.id.tv_ac_balance)
        TextView tvAcBalance;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        public void setData(AccountReportBean data, int position) {
            no.setText(String.valueOf(position+1));
            tvACNumber.setText(data.getACNumber());
             tvAcBalance.setText(DataUtils.format2Decimals(String.valueOf(data.getBalance())));
            if (data.getDeposits()==0){
                tvAcDeposits.setText("0");
                tvAcDateDeposited.setText("-");
            }else {

                tvAcDeposits.setText(DataUtils.format2Decimals(String.valueOf(data.getDeposits())));
                tvAcDateDeposited.setText(TimeUtils.milliseconds2String(data.getDepositsDate()));
            }

            if (data.getFarePaid()==0){
                tvAcFarePaid.setText("0");
                tvAcDate    .setText("-");
            }else {
                tvAcFarePaid.setText(DataUtils.format2Decimals(String.valueOf(data.getFarePaid())));
                tvAcDate    .setText(TimeUtils.milliseconds2String(data.getFarePaidDate()));
            }
        }
    }
}
