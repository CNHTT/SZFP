package com.szfp.szfp.utils;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;
import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfplib.utils.TimeUtils;

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
}
