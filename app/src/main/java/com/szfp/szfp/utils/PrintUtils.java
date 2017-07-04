package com.szfp.szfp.utils;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.bean.AgricultureFarmerCollection;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;

import java.util.ArrayList;

/**
 * author:  ct on 2017/6/29 17:05
 * email:  cnhttt@163.com
 */

public class PrintUtils {

    private static String print_ticket_line="--------------------------------";

    public static void printDepositsReceipt(CommuterAccountInfoBean bean, AccountReportBean reportBean) {
        BluetoothPrintDriver.Begin();
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte) 1);
        BluetoothPrintDriver.SetLineSpacing((byte)40);
        BluetoothPrintDriver.SetFontEnlarge((byte) 0x01);
        BluetoothPrintDriver.BT_Write("ACCOUNT REPORT_DEP0SIT");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte)0);//左对齐
        BluetoothPrintDriver.SetFontEnlarge((byte)0x00);//默认宽度、默认高度
        BluetoothPrintDriver.BT_Write("NAME:" +bean.getFullName()+"\r");
        BluetoothPrintDriver.BT_Write("NationalID:" +bean.getNationalID()+"\r");
        BluetoothPrintDriver.BT_Write("TIME:" + TimeUtils.getCurTimeString());
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("DEP0SIT:" +reportBean.getDeposits()+"\r");
        BluetoothPrintDriver.BT_Write("BALANCE:" +reportBean.getBalance()+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
    }
    public static void printFarePaidReceipt(CommuterAccountInfoBean bean, AccountReportBean reportBean) {
        BluetoothPrintDriver.Begin();
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte) 1);
        BluetoothPrintDriver.SetLineSpacing((byte)40);
        BluetoothPrintDriver.SetFontEnlarge((byte) 0x01);
        BluetoothPrintDriver.BT_Write("ACCOUNT REPORT_FAREPAID");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte)0);//左对齐
        BluetoothPrintDriver.SetFontEnlarge((byte)0x00);//默认宽度、默认高度
        BluetoothPrintDriver.BT_Write("NAME:" +bean.getFullName()+"\r");
        BluetoothPrintDriver.BT_Write("NationalID:" +bean.getNationalID()+"\r");
        BluetoothPrintDriver.BT_Write("TIME:" + TimeUtils.getCurTimeString());
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("FAREPAID:" +reportBean.getFarePaid()+"\r");
        BluetoothPrintDriver.BT_Write("BALANCE:" +reportBean.getBalance()+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
    }
    public static void printFarePaidReceiptREVERSE(CommuterAccountInfoBean bean, AccountReportBean reportBean) {
        BluetoothPrintDriver.Begin();
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte) 1);
        BluetoothPrintDriver.SetLineSpacing((byte)40);
        BluetoothPrintDriver.SetFontEnlarge((byte) 0x01);
        BluetoothPrintDriver.BT_Write("ACCOUNT REPORT_FAREPAID");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte)0);//左对齐
        BluetoothPrintDriver.SetFontEnlarge((byte)0x00);//默认宽度、默认高度
        BluetoothPrintDriver.BT_Write("REVERSE FARE"+"\r");
        BluetoothPrintDriver.BT_Write("NAME:" +bean.getFullName()+"\r");
        BluetoothPrintDriver.BT_Write("NationalID:" +bean.getNationalID()+"\r");
        BluetoothPrintDriver.BT_Write("TIME:" + TimeUtils.getCurTimeString());
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("FAREPAID:" +reportBean.getFarePaid()+"\r");
        BluetoothPrintDriver.BT_Write("BALANCE:" +reportBean.getBalance()+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
    }

    public static void printFareCash(String input) {
        BluetoothPrintDriver.Begin();
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte) 1);
        BluetoothPrintDriver.SetLineSpacing((byte)40);
        BluetoothPrintDriver.SetFontEnlarge((byte) 0x01);
        BluetoothPrintDriver.BT_Write("ACCOUNT REPORT_CASH FARE");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte)0);//左对齐
        BluetoothPrintDriver.SetFontEnlarge((byte)0x00);//默认宽度、默认高度
        BluetoothPrintDriver.BT_Write("CASH FARE"+"\r");
        BluetoothPrintDriver.BT_Write("NAME:  CASH FARE "+"\r");
        BluetoothPrintDriver.BT_Write("NationalID: 000000000"+"\r");
        BluetoothPrintDriver.BT_Write("TIME:" + TimeUtils.getCurTimeString());
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("FAREPAID:" +input+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        AccountReportBean bean = new AccountReportBean();
        bean.setBalance(0);
        bean.setDepositsDate(0);
        bean.setACNumber("CASH PARE");
        bean.setFarePaid(Float.valueOf(input));
        bean.setDepositsDate(0);
        bean.setFarePaidDate(TimeUtils.getCurTimeMills());
        DbHelper.insertAccountReport(bean);
    }

    public static void printAgricultureDailyCollection(AgricultureFarmerBean bean, AgricultureFarmerCollection result) {
        BluetoothPrintDriver.Begin();
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte) 1);
        BluetoothPrintDriver.SetLineSpacing((byte)40);
        BluetoothPrintDriver.SetFontEnlarge((byte) 0x01);
        BluetoothPrintDriver.BT_Write("Daily Collection");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte)0);//左对齐
        BluetoothPrintDriver.SetFontEnlarge((byte)0x00);//默认宽度、默认高度
        BluetoothPrintDriver.BT_Write("NAME:  "+bean.getName()+"\r");
        BluetoothPrintDriver.BT_Write("NationalID:"+bean.getIDNumber()+"\r");
        BluetoothPrintDriver.BT_Write("TIME:" + TimeUtils.getCurTimeString());
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("AmountCollected:" +result.getAmountCollected()+"\r");
        BluetoothPrintDriver.BT_Write("NumberOf Animals:" +bean.getNumberOfAnimals()+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");

    }

    public static void printAgriculturePayment(AgricultureFarmerBean bean, ArrayList<AgricultureFarmerCollection> list, String input) {
        BluetoothPrintDriver.Begin();
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte) 1);
        BluetoothPrintDriver.SetLineSpacing((byte)40);
        BluetoothPrintDriver.SetFontEnlarge((byte) 0x01);
        BluetoothPrintDriver.BT_Write("Payment");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.SetAlignMode((byte)0);//左对齐
        BluetoothPrintDriver.SetFontEnlarge((byte)0x00);//默认宽度、默认高度
        BluetoothPrintDriver.BT_Write("NAME:  "+bean.getName()+"\r");
        BluetoothPrintDriver.BT_Write("ID NUMBER:"+bean.getIDNumber()+"\r");
        BluetoothPrintDriver.BT_Write("Amount paid: " + input+"\r" );
        BluetoothPrintDriver.BT_Write("NumberOfAnimals " +bean.getNumberOfAnimals()+"\r");
        BluetoothPrintDriver.BT_Write("Should be paid: " + DataUtils.getAmountValue(bean.getNumberOfAnimals()*Integer.valueOf(input))+"\r");
        BluetoothPrintDriver.BT_Write("provide basic reports");
        BluetoothPrintDriver.LF();
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");
        BluetoothPrintDriver.BT_Write("Time              AmountCollected" +"\n");
        for (AgricultureFarmerCollection c:list) {
            BluetoothPrintDriver.BT_Write  (TimeUtils.milliseconds2String(c.getTime())+"     "+c.getAmountCollected()+"\n");
        }
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write("_"+"\r");
        BluetoothPrintDriver.BT_Write(print_ticket_line+"\r");

    }
}
