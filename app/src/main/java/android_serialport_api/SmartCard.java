/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package android_serialport_api;

//import android.content.Context;

// Wrapper for native library

public class SmartCard {
    //……
	/*open the serial port*/
    int mFd = 0;
    //final int baudrate = 115200;
    //String path = "/dev/ttys0";

    //mFd = open(path, baudrate);//device.getAbsolutePath()
    // JNI
    //public native int open(String path, int baudrate);
    //public native int write(int fd, byte[] buf, int count);
    //public native int read(int fd, byte[] buf, int count);
    //public native int close(int fd);
    static
    {
        System.loadLibrary("SmartCard");
    }
    public SmartCard() {
        //	final int mFd = open(path, baudrate);//device.getAbsolutePath()
        //	InitClassName(className.getBytes(),MethodSend.getBytes(),MethodRecv.getBytes());
    }

    public static native int InitClassName(byte[] ClassName,byte[] MethodSend,byte[] MethodRecv);

    public static native int DeInitClassName();
    //mFd = open(path, baudrate);//device.getAbsolutePath()
    public static native int UartOpen();

    public static native int UartClose();

    //public native int Set_Bluetooth_Channel();

    public static native int InitMF(byte TransCode,byte Authority,byte FileId,int NameLen,byte[] MFName);

    public static native int InitADF(char FileId,byte Authority,int NameLen,byte[] ADFName);

    public static native int InitBEF(char FileId,byte FileType,char Authority,char Len);

    public static native int InitREF(char FileId,byte FileType,char Authority,char Len);
    //public native int Write_KEY();

    public static native int ReadBinary(byte FileId,byte Offset,int Count,byte[] RecBuf);//ReadBinary byte FileId,byte Offset,int Count,

    public static native int UpdateBinary(byte FileId,byte Offset,int level,byte[] RecBuf,int Count);

    public static native int ReadRecord(byte FileId,int Index,byte[] RecBuf,int Count);

    public static native int AppendRecord(byte FileId,int level,byte[] RecBuf,int Count);

    public static native int UpdateRecord(byte FileId,int level,int Index,byte[] RecBuf,int Count);

    public static native int SelectFile(int FileType,int FileIndex,int SFDatalen,byte[] FileId);

    public static native int GetChallenge(int Count,byte[] RecBuf);

    public static native int GetResponse(int Count,byte[] RecBuf);

    public static native int DeleteFile();

    public static native int ReadIBAN(int Count,byte[] IBANCode);

}

