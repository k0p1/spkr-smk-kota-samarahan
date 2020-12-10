package com.example.spkr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SummaryActivity extends AppCompatActivity {

    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private String folderName = "Reports";
    private String fileName = "";
    private String rootPath = "";
    private Button btn_export;
    private EditText laptopCount, recordCount;
    private ProgressBar loadingBar;
    private List<LaptopInfo> laptopInfoList = new ArrayList<>();
    private List<LaptopCheckOutInfo> laptopCheckOutInfoList = new ArrayList<>();
    private DatabaseReference dreff, dreff1;
    private String [] headerColumns = {"Serial No.", "Reg No.", "Laptop ID", "Student Name", "Student Class", "Student IC", "Checkout Date", "Return Date", "Status"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        btn_export = (Button) findViewById(R.id.btn_export);
        laptopCount = (EditText) findViewById(R.id.edit_laptopCount);
        recordCount = (EditText) findViewById(R.id.edit_recordCount);
        loadingBar = (ProgressBar) findViewById(R.id.progressBar_export);
        loadingBar.setVisibility(View.INVISIBLE);

        laptopCount.setClickable(false);
        recordCount.setClickable(false);

        dreff = FirebaseDatabase.getInstance().getReference().child("Laptop Record");
        dreff1 = FirebaseDatabase.getInstance().getReference().child("Laptop");

        fileName = new StringBuilder().append("Report_").append(year).append(".xlsx").toString(); //Name of the file


        initLaptopRecordList();
        initLaptopList();

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setVisibility(View.VISIBLE);
                //setSize(laptopCheckOutInfoList);
                try {
                    createFolder();
                    createWorkbook();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //createFolder();
                //exportCheckoutRecord();
                //openFileManager();
            }
        });
    }

    public void createFolder () {
        File reports = new File("/storage/emulated/0/SPKR", folderName);
        if (reports.isDirectory()) {
        }
        else {
            if (reports.mkdirs()) {
            }
        }
    }

    public void createWorkbook () throws IOException {
        //creating an instance of Workbook class
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Checkout Record"); //Creating a sheet

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK1.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //create header row
        Row headerRow = sheet.createRow(0);
        headerRow.setRowStyle(headerCellStyle);

        //create header row's column
        for (int i = 0; i < headerColumns.length; i++) {
            headerRow.createCell(i).setCellValue(headerColumns[i]);
        }

        //{"Serial No.", "Reg No.", "Laptop ID", "Student Name", "Student Class", "Student IC", "Checkout Date", "Return Date", "Status"};
        //create data row and insert
        int rowNum = 1;
        for (LaptopCheckOutInfo lcoi : laptopCheckOutInfoList) {
            Row dataRow = sheet.createRow(rowNum++);

            dataRow.createCell(0).setCellValue(lcoi.getSerialNo());
            dataRow.createCell(1).setCellValue(lcoi.getRegistrationNo());
            dataRow.createCell(2).setCellValue(lcoi.getLaptopID());
            dataRow.createCell(3).setCellValue(lcoi.getStudentName());
            dataRow.createCell(4).setCellValue(lcoi.getStudentClass());
            dataRow.createCell(5).setCellValue(lcoi.getStudentIC());
            dataRow.createCell(6).setCellValue(lcoi.getCheckoutDate());
            dataRow.createCell(7).setCellValue(lcoi.getReturnDate());
            dataRow.createCell(8).setCellValue(lcoi.getStatus());
        }

        showProgressBar(laptopCheckOutInfoList);

        //create the file to be written
        File excel = new File("/storage/emulated/0/SPKR/"+folderName+"/"+fileName);
        if (!excel.exists()) {
            if (excel.createNewFile()){
                Toast.makeText(this, "File not found, creating new one...", Toast.LENGTH_LONG).show();
                laptopCount.setText("createNewFile: "+excel.exists()+"\n isReadble: "+excel.canRead()+"\n isWritable: "+excel.canWrite());
            }
        }

        FileOutputStream fileOut = new FileOutputStream(excel);
        workbook.write(fileOut);

        fileOut.close();
        workbook.close();
    }

    public void showProgressBar (List list) {
        for(int i=1; i<=loadingBar.getMax(); i++){
            loadingBar.setProgress(i);
        }

        if(loadingBar.getProgress() == 100) {
            loadingBar.setVisibility(View.INVISIBLE);
            loadingBar.setProgress(0);
            Toast.makeText(this, "Export done(hopefully)", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSize(List list, EditText editText){
        if(list.isEmpty())
            editText.setText("No record found");

        else {
            editText.setText(String.valueOf(list.size()));
        }
    }

    public void openFileManager(){
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().toString());
        startActivity(new Intent(Intent.ACTION_GET_CONTENT).setDataAndType(uri, "*/*"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length>0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            exportCheckoutRecord();
        }
        else {
            Toast.makeText(SummaryActivity.this,"Write permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLaptopRecordList () {
        Query query = this.dreff;
        query.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!laptopCheckOutInfoList.isEmpty()) {
                    laptopCheckOutInfoList.clear();
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LaptopCheckOutInfo recordRow = postSnapshot.getValue(LaptopCheckOutInfo.class);
                    laptopCheckOutInfoList.add(recordRow);
                }

                setSize(laptopCheckOutInfoList, recordCount);
                loadingBar.setMax(laptopCheckOutInfoList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initLaptopList () {
        Query query = this.dreff1;
        query.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!laptopInfoList.isEmpty()) {
                    laptopInfoList.clear();
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LaptopInfo recordRow = postSnapshot.getValue(LaptopInfo.class);
                    laptopInfoList.add(recordRow);
                }

                setSize(laptopInfoList, laptopCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void exportCheckoutRecord () {

//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Checkout Record"); //Creating a sheet
//
//        // Create a Font for styling header cells
//        Font headerFont = workbook.createFont();
//        headerFont.setBold(true);
//        headerFont.setFontHeightInPoints((short) 14);
//        headerFont.setColor(IndexedColors.BLACK1.getIndex());
//
//        // Create a CellStyle with the font
//        CellStyle headerCellStyle = workbook.createCellStyle();
//        headerCellStyle.setFont(headerFont);
//
//        //create header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.setRowStyle(headerCellStyle);
//
//        //create header row's column
//        for (int i = 0; i < headerColumns.length; i++) {
//            headerRow.createCell(i).setCellValue(headerColumns[i]);
//        }
//
//        //{"Serial No.", "Reg No.", "Laptop ID", "Student Name", "Student Class", "Student IC", "Checkout Date", "Return Date", "Status"};
//        //create data row and insert
//        int rowNum = 1;
//        for (LaptopCheckOutInfo lcoi : laptopCheckOutInfoList) {
//            Row dataRow = sheet.createRow(rowNum++);
//
//            dataRow.createCell(0).setCellValue(lcoi.getSerialNo());
//            dataRow.createCell(1).setCellValue(lcoi.getRegistrationNo());
//            dataRow.createCell(2).setCellValue(lcoi.getLaptopID());
//            dataRow.createCell(3).setCellValue(lcoi.getStudentName());
//            dataRow.createCell(4).setCellValue(lcoi.getStudentClass());
//            dataRow.createCell(5).setCellValue(lcoi.getStudentIC());
//            dataRow.createCell(6).setCellValue(lcoi.getCheckoutDate());
//            dataRow.createCell(7).setCellValue(lcoi.getReturnDate());
//            dataRow.createCell(8).setCellValue(lcoi.getStatus());
//        }

        Toast.makeText(this, "Exporting...", Toast.LENGTH_SHORT).show();
//        for(int i=0; i<laptopCheckOutInfoList.size(); i++){
//            loadingBar.setVisibility(View.VISIBLE);
//            int progress = (i+1)/laptopCheckOutInfoList.size();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                loadingBar.setProgress(progress*100, true);
//            }
//        }
//
//        if(loadingBar.getProgress() == 100) {
//            loadingBar.setVisibility(View.INVISIBLE);
//            loadingBar.setProgress(0);
//            Toast.makeText(this, "Export done(hopefully)", Toast.LENGTH_SHORT).show();
//        }

        // Resize all columns to fit the content size
        for(int i = 0; i < headerColumns.length; i++) {
            //sheet.setColumnWidth(i, 15);
        }



            File folder = new File(getApplicationContext().getFilesDir(), folderName);// Name of the folder you want to keep your file in the local storage.
            if (folder.exists()) {
                Toast.makeText(this, "Folder: " + folder.getAbsolutePath() + " is exists...", Toast.LENGTH_SHORT).show();
            }
            else {
                //creating the folder to save the excel file if not exists
                folder.mkdir();
                if (folder.isDirectory()){
                    Toast.makeText(this, "Folder not found, created successfully! "+folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(this, "Folder not created .... T.T "+folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    String content = "Message: Failed to create directory at \nPath: "+getApplicationContext().getFilesDir()+"\nmkdir: "+folder.mkdir();
                    builder.setMessage(content);
                    builder.show();
                }

            }

            File file = new File(folder, fileName);
            try {
                if (!file.exists()){
                    // creating the file inside the folder
                    if (file.createNewFile()) {
                        if(file.exists()){
                            Toast.makeText(this, "File not found, file created successfully at "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(this, "File exist, create failed T.T", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e1) {
                Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
            }

        //file created successfully but content corrupted
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/xlsx");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);


        Uri uri =  Uri.parse(getApplicationContext().getFilesDir().toString());
        Uri.Builder builder = new Uri.Builder();
        builder.appendPath(uri.toString()).appendPath(fileName);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(getApplicationContext().getFilesDir().toString()));
        startActivityForResult(intent, 1);

            try {
                if (file.canWrite()){
                    //FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
                    //workbook.write(fileOut); //Writing all your row column inside the file
                    ParcelFileDescriptor pfd = getApplicationContext().getContentResolver().openFileDescriptor(builder.build(), "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    //workbook.write(fileOutputStream);
                    Toast.makeText(this, "Report generated successfully! Please check your internal storage", Toast.LENGTH_LONG).show();
                    //workbook.close();
                    fileOutputStream.close();
                    pfd.close();
                    //fileOut.close(); //closing the file and done
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }


    }
}
