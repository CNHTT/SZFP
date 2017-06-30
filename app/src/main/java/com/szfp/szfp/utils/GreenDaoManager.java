package com.szfp.szfp.utils;

import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.greendao.DaoMaster;
import com.szfp.szfp.greendao.DaoSession;

import static com.szfp.szfplib.utils.Utils.getContext;

/**
 * 作者：ct on 2017/6/22 10:05
 * 邮箱：cnhttt@163.com
 */

public class GreenDaoManager {
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private  static volatile GreenDaoManager mInstance=null;
    private GreenDaoManager(){
        if (mInstance == null){
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getContext(), ConstantValue.DB_NAME);
            mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
        }
    }


    public static  GreenDaoManager getInstance(){
        if (mInstance ==null)
        {
            synchronized (GreenDaoManager.class){
                if (mInstance==null)
                    mInstance = new GreenDaoManager();
            }
        }
        return  mInstance;
    }
    public DaoMaster getMaster() {
        return mDaoMaster;
    }
    public DaoSession getSession() {
        return mDaoSession;
    }
    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

}
