package com.szfp.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.szfp.szfplib.adapter.BaseListAdapter;
import com.szfp.szfplib.inter.OnItemClickListener;
import com.szfp.szfplib.utils.ContextUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.AlertView;

import java.util.List;

/**
 * author：ct on 2017/7/31 15:01
 * email：cnhttt@163.com
 */

public class ApkAdapter extends BaseListAdapter<AppInfo> {


    public ApkAdapter(Context context) {
        super(context);
    }

    public ApkAdapter(Context mContext, List<AppInfo> mDatas) {
        super(mContext, mDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ContextUtils.inflate(mContext, R.layout.item);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setData(getItem(position), position,mContext);

        return convertView;
    }

    static class ViewHolder {
        ImageView idImgSrc;
        TextView idName;
        ImageView idImvDele;
        FrameLayout idImgContent;

        ViewHolder(View view) {
            idImgSrc = (ImageView) view.findViewById(R.id.id_img_src);
            idName = (TextView) view.findViewById(R.id.id_name);
            idImvDele = (ImageView) view.findViewById(R.id.id_imv_dele);
            idImgContent = (FrameLayout) view.findViewById(R.id.id_img_content);
        }

        public void setData(final AppInfo item, int position, final Context mContext) {
            idImgSrc.setImageDrawable(item.getImage());
            idName.setText(item.getAppName());
            idImgContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent =mContext.getPackageManager().getLaunchIntentForPackage(item.getPageName());
                        mContext.startActivity(intent);
                    }catch(Exception e) {
                        ToastUtils.error("未发现对应APP");
                    }

                }
            });

            idImgContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertView(null, null, null, new String[]{"uninstallApp"}, new String[]{"Cancel","Please Select"}
                            ,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                           if (position ==0){
                                try{
                                    Uri packageURI = Uri.parse("package:"+ item.getPageName());
                                    Intent uninstallIntent =new Intent(Intent.ACTION_DELETE,packageURI);
                                    mContext.startActivity(uninstallIntent);
                                }catch(Exception E) {
                                    ToastUtils.error("未发现对应APP");
                                }

                            }
                        }
                    }).show();
                    return true;
                }
            });
        }
    }
}
