package com.szfp.szfp.view.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.szfp.szfp.R;
import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.adapter.AgricultureFramerAdapter;
import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.szfp.szfp.utils.PdfUtils.addMetaData;
import static com.szfp.szfplib.utils.DataUtils.isEmpty;

public class AgricultureReportFramerActivity extends BaseNoActivity {

    @BindView(R.id.bt_framer_export)
    StateButton btFramerExport;
    @BindView(R.id.list)
    ListView listView;


    private List<AgricultureFarmerBean> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture_report_framer);
        ButterKnife.bind(this);
        list = DbHelper.getAllListAgricultureFramer();

        if (isEmpty(list)){
         showErrorToast("No REPORT");
            btFramerExport.setEnabled(false);
        }else listView.setAdapter(new AgricultureFramerAdapter(this,list));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Rendering the File...!! \n Please wait....");
    }
    public int totalnoofpages = 1;
    public boolean counterFlag;
    String FILE_PATH;
    Document document;
    ProgressDialog progressDialog;

    @OnClick(R.id.bt_framer_export)
    public void onViewClicked() {
        progressDialog.show();
        totalnoofpages = 0;
        FILE_PATH =  Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.PDF + "Agriculture Framer Report.pdf";
        document = new Document(PageSize.A4);
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(list.size());
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                try {
                    PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH));
                    pdfWriter.setPageEvent(new HeaderFooter());
                    counterFlag = true;
                    Log.d("RESULT", "-------------------------------------FILE_PATH: " + FILE_PATH);
                    //Open Document
                    document.open();
                    //User Defined Methods
                    addMetaData(document);
                    addContent(document,value);
                    document.close();

                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(FILE_PATH)), "application/pdf");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtils.error(getResources().getString(R.string.pdf_error));

                }
                ToastUtils.success("Successfully generated");
            }
        });

    }

    private void addContent(Document document, Integer value) {
        Font font_smllBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font font_title = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD | Font.UNDERLINE, BaseColor.GRAY);

        Paragraph heading = new Paragraph();
        heading.setFont(font_title);
        heading.setAlignment(Element.ALIGN_CENTER);
        heading.add("FARMER REPORT \n");

        int noofcolumns = 6;
        PdfPTable table = new PdfPTable(noofcolumns);

        // 100.0f mean width of table is same as Document size
        table.setWidthPercentage(100.0f);

        Paragraph
                p1=new Paragraph();
        p1.setFont(font_smllBold);
        p1.setAlignment(Element.ALIGN_CENTER);
        p1.add("No");

        Paragraph
                p2=new Paragraph();
        p2.setFont(font_smllBold);
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.add("NAME");

        Paragraph
                p3=new Paragraph();
        p3.setFont(font_smllBold);
        p3.setAlignment(Element.ALIGN_CENTER);
        p3.add("ID NUMBER");

        Paragraph
                p4=new Paragraph();
        p4.setFont(font_smllBold);
        p4.setAlignment(Element.ALIGN_CENTER);
        p4.add("A/C NUMBER");

        Paragraph
                p5=new Paragraph();
        p5.setFont(font_smllBold);
        p5.setAlignment(Element.ALIGN_CENTER);
        p5.add("Amount collected \n(liters)");

        Paragraph
                p6=new Paragraph();
        p6.setFont(font_smllBold);
        p6.setAlignment(Element.ALIGN_CENTER);
        p6.add("AMOUNT");

        PdfPCell c1 =new PdfPCell(p1);
        PdfPCell c2 =new PdfPCell(p2);
        PdfPCell c3 =new PdfPCell(p3);
        PdfPCell c4 =new PdfPCell(p4);
        PdfPCell c5 =new PdfPCell(p5);
        PdfPCell c6 =new PdfPCell(p6);
        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
        table.addCell(c4);
        table.addCell(c5);
        table.addCell(c6);
        table.setHeaderRows(1);
        int i = 0, row;
        while (i < value) {
            row = i + 1;
            table.addCell(row + "");
            table.addCell(list.get(i).getName());
            table.addCell(list.get(i).getIDNumber());
            table.addCell(list.get(i).getRegistrationNumber());
            table.addCell(String.valueOf(list.get(i).getNumberOfAnimals()));
            table.addCell(DataUtils.format2Decimals(String.valueOf(list.get(i).getAmount())));

            i++;
        }

        try {
          /*  document.add(document.getPageSize());*/
            document.add(addEmptyLine(2));
            document.add(heading);
            document.add(addEmptyLine(2));
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    //Empty Line Generator
    public Paragraph addEmptyLine(int lines) {
        int i;
        //Add Empty Line
        Paragraph emptyLines = new Paragraph();
        for (i = 0; i < lines; i++) {
            emptyLines.add(new Paragraph(" "));
        }
        return emptyLines;
    }


    //HEADER AND FOOTER SECTION
    class HeaderFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC);
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            counterFlag = false;
            super.onCloseDocument(writer, document);
        }
        public void onEndPage(PdfWriter writer, Document document) {
            if (counterFlag)
                totalnoofpages++;
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase("system should also have pdf reports for all the information", ffont);
            Phrase footer = new Phrase(document.getPageNumber() + " of " + totalnoofpages + " Pages ", ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}
