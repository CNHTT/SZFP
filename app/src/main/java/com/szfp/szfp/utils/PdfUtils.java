package com.szfp.szfp.utils;

import android.util.Log;

import com.itextpdf.text.Document;

/**
 * author：ct on 2017/6/30 10:28
 * email：cnhttt@163.com
 */

public class PdfUtils {
    public static void addMetaData(Document document) {
        Log.d("RESULT", "-------------------------------------addMetaData: ");
        document.addTitle("Title");
        document.addSubject("Subject");
        document.addKeywords("KEYWORD,KEYWORD2,KEYWORD3");
        document.addAuthor("");
        document.addCreator("");
    }
}
