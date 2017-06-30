package com.szfp.szfp.utils;

import android.content.Context;

import com.szfp.szfp.R;
import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfp.bean.BankCustomerBean;
import com.szfp.szfp.bean.BankRegistrationBean;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.bean.VehicleInfoBean;
import com.szfp.szfp.greendao.AccountReportBeanDao;
import com.szfp.szfp.greendao.BankCustomerBeanDao;
import com.szfp.szfp.greendao.CommuterAccountInfoBeanDao;
import com.szfp.szfp.inter.OnSaveListener;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;

import static com.szfp.szfp.ConstantValue.FINGERPRINT;
import static com.szfp.szfp.ConstantValue.FINGERPRINT_END;
import static com.szfp.szfp.ConstantValue.PAH;
import static com.szfp.szfplib.utils.Utils.getContext;

/**
 * 作者：ct on 2017/6/22 10:29
 * 邮箱：cnhttt@163.com
 */

public class DbHelper {

    long result;
    public boolean insertVehicleInfo(VehicleInfoBean vehicleInfoBean, Context context) {


        try {

         result =     GreenDaoManager.getInstance().getSession().getVehicleInfoBeanDao().insert(vehicleInfoBean);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public String getVehiclesNum() {

        long  num = GreenDaoManager.getInstance().getSession().getVehicleInfoBeanDao().queryBuilder().count();
        return String.valueOf(num);
    }

    public boolean insertCommuter(CommuterAccountInfoBean bean) {
        try {
            result =     GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().insert(bean);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public String getCommuterNum() {
        return String.valueOf(GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder().count());
    }

    private CommuterAccountInfoBean commBean;
    /**
     *  <item>ID Number</item>
        <item>Name</item>
        <item>Registration number</item>
     * @param type
     * @param input
     * @return
     */
    public CommuterAccountInfoBean getCommuterInfo(int type, String input) {
        try {
            switch (type){
                case 0:
                    commBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                            .where(CommuterAccountInfoBeanDao.Properties.NationalID.eq(input)).build().unique();
                    break;
                case 1:
                    commBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                                        .where(CommuterAccountInfoBeanDao.Properties.FullName.eq(input)).build().unique();
                    break;
                case 2:
                    commBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                                .where(CommuterAccountInfoBeanDao.Properties.CommuterAccount.eq(input)).build().unique();
                    break;
            }

            }catch (Exception e){

            return null;
        }
        return commBean;
    }
    public static CommuterAccountInfoBean getCommuterInfo(String input) {
        CommuterAccountInfoBean commuterAccountInfoBean;
        try {

            commuterAccountInfoBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                                .where(CommuterAccountInfoBeanDao.Properties.CommuterAccount.eq(input)).build().unique();
            }catch (Exception e){

            return null;
        }
        return commuterAccountInfoBean;
    }


    public static CommuterAccountInfoBean getCommuterInfo(String fingerPrintId, String input, OnSaveListener listener) {
        CommuterAccountInfoBean commuterAccountInfoBean;
        try {

            commuterAccountInfoBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                                .where(CommuterAccountInfoBeanDao.Properties.FingerPrintFileUrl.like(PAH+FINGERPRINT+fingerPrintId+FINGERPRINT_END+PAH)).build().unique();
            if (DataUtils.isEmpty(commuterAccountInfoBean)){
                listener.error(getContext().getResources().getString(R.string.no_find));
            }else{
                if (commuterAccountInfoBean.getBalance()>Float.valueOf(input)){

                    commuterAccountInfoBean.setFarePaid(commuterAccountInfoBean.getFarePaid()+Float.valueOf(input));
                    commuterAccountInfoBean.setBalance(commuterAccountInfoBean.getBalance()-Float.valueOf(input));
                    GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().update(commuterAccountInfoBean);

                    //生成消费记录
                    AccountReportBean reportBean = new AccountReportBean();

                    reportBean.setDeposits(0);
                    reportBean.setDepositsDate(0);

                    reportBean.setBalance(commuterAccountInfoBean.getBalance());
                    reportBean.setFarePaid(Float.valueOf(input));
                    reportBean.setACNumber(commuterAccountInfoBean.getCommuterAccount());
                    reportBean.setFarePaidDate(TimeUtils.getCurTimeMills());
                    insertAccountReport(reportBean);
                    PrintUtils.printFarePaidReceipt(commuterAccountInfoBean,reportBean);

                    listener.success();

                }else {
                    listener.error(getContext().getResources().getString(R.string.not_sufficient_funds));
                }
            }
            }catch (Exception e){

            listener.error(getContext().getResources().getString(R.string.no_find));

            return null;
        }
        return commuterAccountInfoBean;
    }

    public static CommuterAccountInfoBean getCommuterInfo(String fingerPrintId, String input, OnSaveListener listener ,boolean isPrint) {
        CommuterAccountInfoBean commuterAccountInfoBean;
        try {

            commuterAccountInfoBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                                .where(CommuterAccountInfoBeanDao.Properties.FingerPrintFileUrl.like(PAH+FINGERPRINT+fingerPrintId+FINGERPRINT_END+PAH)).build().unique();
            if (DataUtils.isEmpty(commuterAccountInfoBean)){
                listener.error(getContext().getResources().getString(R.string.no_find));
            }else{
                if (commuterAccountInfoBean.getBalance()>Float.valueOf(input)){

                    commuterAccountInfoBean.setFarePaid(commuterAccountInfoBean.getFarePaid()+Float.valueOf(input));
                    commuterAccountInfoBean.setBalance(commuterAccountInfoBean.getBalance()-Float.valueOf(input));
                    GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().update(commuterAccountInfoBean);

                    //生成消费记录
                    AccountReportBean reportBean = new AccountReportBean();

                    reportBean.setDeposits(0);
                    reportBean.setDepositsDate(0);

                    reportBean.setBalance(commuterAccountInfoBean.getBalance());
                    reportBean.setFarePaid(Float.valueOf(input));
                    reportBean.setACNumber(commuterAccountInfoBean.getCommuterAccount());
                    reportBean.setFarePaidDate(TimeUtils.getCurTimeMills());
                    insertAccountReport(reportBean);
                   if (isPrint)PrintUtils.printFarePaidReceipt(commuterAccountInfoBean,reportBean);

                    listener.success();

                }else {
                    listener.error(getContext().getResources().getString(R.string.not_sufficient_funds));
                }
            }
            }catch (Exception e){

            listener.error(getContext().getResources().getString(R.string.no_find));

            return null;
        }
        return commuterAccountInfoBean;
    }

    public static CommuterAccountInfoBean getCommuterInfo(String fingerPrintId, String input, OnSaveListener listener,String type) {
        CommuterAccountInfoBean commuterAccountInfoBean;
        try {

            commuterAccountInfoBean = GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().queryBuilder()
                                .where(CommuterAccountInfoBeanDao.Properties.FingerPrintFileUrl.like(PAH+FINGERPRINT+fingerPrintId+FINGERPRINT_END+PAH)).build().unique();
            if (DataUtils.isEmpty(commuterAccountInfoBean)){
                listener.error(getContext().getResources().getString(R.string.no_find));
            }else{
                if (commuterAccountInfoBean.getBalance()>Float.valueOf(input)){

                    commuterAccountInfoBean.setFarePaid(commuterAccountInfoBean.getFarePaid()+Float.valueOf(input));
                    commuterAccountInfoBean.setBalance(commuterAccountInfoBean.getBalance()-Float.valueOf(input));
                    GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().update(commuterAccountInfoBean);

                    //生成消费记录
                    AccountReportBean reportBean = new AccountReportBean();

                    reportBean.setDeposits(0);
                    reportBean.setDepositsDate(0);

                    reportBean.setBalance(commuterAccountInfoBean.getBalance());
                    reportBean.setFarePaid(Float.valueOf(input));
                    reportBean.setACNumber(commuterAccountInfoBean.getCommuterAccount());
                    reportBean.setFarePaidDate(TimeUtils.getCurTimeMills());
                    insertAccountReport(reportBean);
                    PrintUtils.printFarePaidReceiptREVERSE(commuterAccountInfoBean,reportBean);
                    listener.success();

                }else {
                    listener.error(getContext().getResources().getString(R.string.not_sufficient_funds));
                }
            }
            }catch (Exception e){

            listener.error(getContext().getResources().getString(R.string.no_find));

            return null;
        }
        return commuterAccountInfoBean;
    }

    public boolean insertBankInfo(BankRegistrationBean bean) {
        try {
            GreenDaoManager.getInstance().getSession().getBankRegistrationBeanDao().insert(bean);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static boolean insertBankCustomer(BankCustomerBean bean) {
        try {
            GreenDaoManager.getInstance().getSession().getBankCustomerBeanDao().insert(bean);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static BankCustomerBean getBankCustomerBean(String depositAcc) {
        BankCustomerBean bean;
        try {
            bean = GreenDaoManager.getInstance().getSession().getBankCustomerBeanDao().queryBuilder().where(BankCustomerBeanDao.Properties.FosaAccount.eq(depositAcc)).build().unique();
        }catch (Exception e){
         return null;
        }
        return bean;
    }

    public static boolean isUpdateCommuterAccountInfoPhoto(CommuterAccountInfoBean bean) {

        try {
            GreenDaoManager.getInstance().getSession().getCommuterAccountInfoBeanDao().update(bean);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static boolean insertAccountReport(AccountReportBean reportBean) {
        try {
            GreenDaoManager.getInstance().getSession().getAccountReportBeanDao().insert(reportBean);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static ArrayList<AccountReportBean> getListAccountReport(CommuterAccountInfoBean bean) {


        try {
            Query<AccountReportBean> query = null;
            ArrayList count = null;
            query = GreenDaoManager.getInstance().getSession().getAccountReportBeanDao().queryBuilder().
                    where(AccountReportBeanDao.Properties.ACNumber.eq(bean.getCommuterAccount()))
                    .orderDesc(AccountReportBeanDao.Properties.Id).build();

            if (query == null) {
                return null;
            } else {
                count = (ArrayList) query.list();
                return count;
            }
        }catch (Exception e){
            return null;
        }




    }
}
