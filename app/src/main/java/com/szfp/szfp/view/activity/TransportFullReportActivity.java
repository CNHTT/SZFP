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
import com.szfp.szfp.ConstantValue;
import com.szfp.szfp.R;
import com.szfp.szfp.SzfpApplication;
import com.szfp.szfp.adapter.AccountReportAdapter;
import com.szfp.szfp.bean.AccountReportBean;
import com.szfp.szfp.bean.CommuterAccountInfoBean;
import com.szfp.szfp.utils.DbHelper;
import com.szfp.szfplib.utils.DataUtils;
import com.szfp.szfplib.utils.TimeUtils;
import com.szfp.szfplib.utils.ToastUtils;
import com.szfp.szfplib.weight.StateButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.szfp.szfp.utils.PdfUtils.addMetaData;

public class TransportFullReportActivity extends BaseActivity {

    private static final String TAG = "TransportFullReportActivity";
    @BindView(R.id.export)
    StateButton export;
    @BindView(R.id.full_report_list)
    ListView fullReportList;
    private CommuterAccountInfoBean bean;

    private ArrayList<AccountReportBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_report);
        ButterKnife.bind(this);
        bean = (CommuterAccountInfoBean) getIntent().getSerializableExtra(ConstantValue.INFO);
        showLoadingDialog();
        list = DbHelper.getListAccountReport(bean);
        dismissLoadingDialog();

        if (list!=null){

            fullReportList.setAdapter(new AccountReportAdapter(this,list));
        }else ToastUtils.error("NO DATA");



    }


    public int totalnoofpages = 1;
    public boolean counterFlag;
    String FILE_PATH;
    Document document;
    ProgressDialog progressDialog;
    @OnClick(R.id.export)
    public void onClick() {
        if (!DataUtils.isEmpty(list)) {
            totalnoofpages = 0;
            //set Output path
            FILE_PATH = Environment.getExternalStorageDirectory().getPath() + SzfpApplication.DISK_CACHE_PATH+SzfpApplication.PDF + bean.getNationalID()+".pdf";
            document = new Document(PageSize.A4);
            //Call Pdf RendererAsynTask to write into a document

            progressDialog = new ProgressDialog(TransportFullReportActivity.this);
            progressDialog.setMessage("Rendering the File...!! \n Please wait....");
            progressDialog.show();

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

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.error("Generate failure");
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
                    Log.d(TAG, "complete");
                }
            });


        } else {
            export.setError("Please Enter the Record Size");
        }

    }


    public void addContent(Document document, int no_of_rows) {
            Log.d("RESULT", "----------------------------------------------addContent: ");
            //Define Fonts
            Font font_title = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD | Font.UNDERLINE, BaseColor.GRAY);
            Font font_cat = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Font font_smllBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font_small = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            //Heading Sections
            Paragraph heading = new Paragraph();
            heading.setFont(font_title);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.add("FULL REPORT ("+bean.getFullName()+") \n");
            //Creating Table
            int noofcolumns = 7;
            PdfPTable table = new PdfPTable(noofcolumns);

            // 100.0f mean width of table is same as Document size
            table.setWidthPercentage(100.0f);

            //Add Column Headings
            Paragraph columnText_0 = new Paragraph();
            columnText_0.setFont(font_smllBold);
            columnText_0.setAlignment(Element.ALIGN_CENTER);
            columnText_0.add("No");

            Paragraph columnText_1 = new Paragraph();
            columnText_1.setFont(font_smllBold);
            columnText_1.setAlignment(Element.ALIGN_CENTER);
            columnText_1.add("A/C NUMBER");

            Paragraph columnText_2 = new Paragraph();
            columnText_2.setFont(font_smllBold);
            columnText_2.setAlignment(Element.ALIGN_CENTER);
            columnText_2.add("DEPOSITS");

            Paragraph columnText_3 = new Paragraph();
            columnText_3.setFont(font_smllBold);
            columnText_3.setAlignment(Element.ALIGN_CENTER);
            columnText_3.add("DATA");

            Paragraph columnText_4 = new Paragraph();
            columnText_4.setFont(font_smllBold);
            columnText_4.setAlignment(Element.ALIGN_CENTER);
            columnText_4.add("FARE PAID");

            Paragraph columnText_5 = new Paragraph();
            columnText_5.setFont(font_smllBold);
            columnText_5.setAlignment(Element.ALIGN_CENTER);
            columnText_5.add("DATA");

            Paragraph columnText_6= new Paragraph();
            columnText_6.setFont(font_smllBold);
            columnText_6.setAlignment(Element.ALIGN_CENTER);
            columnText_6.add("BALANCE");

            PdfPCell column_Title_0 = new PdfPCell(columnText_0);
            PdfPCell column_Title_1 = new PdfPCell(columnText_1);
            PdfPCell column_Title_2 = new PdfPCell(columnText_2);
            PdfPCell column_Title_3 = new PdfPCell(columnText_3);
            PdfPCell column_Title_4 = new PdfPCell(columnText_4);
            PdfPCell column_Title_5 = new PdfPCell(columnText_5);
            PdfPCell column_Title_6 = new PdfPCell(columnText_6);
            //Add table Rows
            table.addCell(column_Title_0);
            table.addCell(column_Title_1);
            table.addCell(column_Title_2);
            table.addCell(column_Title_3);
            table.addCell(column_Title_4);
            table.addCell(column_Title_5);
            table.addCell(column_Title_6);
            table.setHeaderRows(1);
            int i = 0, row;
            //Generate Random Table Contents
            while (i < no_of_rows) {
                row = i + 1;
                table.addCell(row + "");
                table.addCell(list.get(i).getACNumber());

                if (list.get(i).getDeposits()==0){
                    table.addCell("0");
                    table.addCell("-");
                }else {

                    table.addCell(DataUtils.format2Decimals(String.valueOf(list.get(i).getDeposits())));
                    table.addCell(TimeUtils.milliseconds2String(list.get(i).getDepositsDate()));
                }

                if (list.get(i).getFarePaid()==0){
                    table.addCell("0");
                    table.addCell("-");
                }else {
                    table.addCell(DataUtils.format2Decimals(String.valueOf(list.get(i).getFarePaid())));
                    table.addCell(TimeUtils.milliseconds2String(list.get(i).getFarePaidDate()));
                }

                table.addCell(DataUtils.format2Decimals(String.valueOf(list.get(i).getBalance())));

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
            }}

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
