package com.szfp.szfp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szfp.szfp.R;
import com.szfp.szfplib.adapter.BaseListAdapter;
import com.szfp.szfplib.utils.ContextUtils;

import java.util.List;

/**
 * 作者：ct on 2017/6/19 15:20
 * 邮箱：cnhttt@163.com
 */

public class TransportListAdapter extends BaseListAdapter<String> {
    public TransportListAdapter(Context mContext, List<String> mDatas) {
        super(mContext, mDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = ContextUtils.inflate(mContext, R.layout.trans_item);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvContent.setText(mDatas.get(position));
        return convertView;
    }

    static class  ViewHolder{
        public TextView tvContent;

        public ViewHolder(View convertView) {
            tvContent = (TextView) convertView.findViewById(R.id.trans_text);
        }
    }
}
