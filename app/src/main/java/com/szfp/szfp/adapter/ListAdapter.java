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
 * 作者：ct on 2017/6/19 12:17
 * 邮箱：cnhttt@163.com
 */

public class ListAdapter extends BaseListAdapter<String> {
    public ListAdapter(Context context) {
        super(context);
    }

    private int type=0;
    public ListAdapter(Context mContext, List<String> mDatas) {
        super(mContext, mDatas);
    }
    public ListAdapter(Context mContext, List<String> mDatas,int type) {
        super(mContext, mDatas);
        this.type=type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = ContextUtils.inflate(mContext,R.layout.adapter_list);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (type!=0){
            viewHolder.tvContent.setTextSize(13);
            viewHolder.tvContent.setPadding(10,10,10,10);
        }

        viewHolder.tvContent.setText(mDatas.get(position));
        return convertView;
    }

    static class  ViewHolder{
        public   TextView tvContent;

        public ViewHolder(View convertView) {
            tvContent = (TextView) convertView.findViewById(R.id.tv_content);
        }
    }
}
