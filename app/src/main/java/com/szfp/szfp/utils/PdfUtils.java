package com.szfp.szfp.utils;

import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;

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
    Font font_smllBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    Font font_title = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD | Font.UNDERLINE, BaseColor.GRAY);

}
