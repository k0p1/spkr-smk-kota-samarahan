package com.example.spkr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.google.zxing.common.StringUtils;

import java.security.Key;
import java.util.HashMap;
import java.util.Scanner;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, CustomDialogFragment.CustomDialogListener {
    private ZXingScannerView mScannerView;
    public Context context;
    private String stripInfo = "";
    protected LaptopInfo li = new LaptopInfo();
    private DatabaseOp dbop;
    private DatabaseReference dreff = FirebaseDatabase.getInstance().getReference().child("Laptop");
    private CustomDialogFragment customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_frame);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        context = getApplicationContext();

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        //once get result do something - db
        if (rawResult.getText() != null) {
            setObjectData(rawResult.getText(), li);
//            if(dreff != null) {
//                //show alert dialog "Record Exist, go to update page"
//            }
//
//            else {
//                //show alert dialog "New record, adding to db"
//            }

            Toast.makeText(ScannerActivity.this, "Verifying...",Toast.LENGTH_SHORT).show();
            //dialog.showAlertDialog(R.layout.activity_scanner_result, this, "Is the information correct?");
            showMessageDialog("Is the information correct?"+rawResult.getText().trim());
        }

        //else
            //showAlertDialog(li); //customise view for invalid qr code?
    }

    public void showMessageDialog(String message) {
        DialogFragment dialog = CustomDialogFragment.newInstance("Scan Results", message, this);
        dialog.show(this.getSupportFragmentManager(), "scan_results");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //mScannerView.resumeCameraPreview(this);
        Intent intent = new Intent(this, ScannerResult.class);
        intent.putExtra("laptop_info", li); //return the object li as a serialized object
        startActivity(intent);
    }

    //can move to other activity
    private void showAlertDialog(final LaptopInfo li) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //create a new layout xml for this?
        final View customLayout = getLayoutInflater().inflate(R.layout.activity_scanner_result, null);
        alertDialog.setView(customLayout);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                //EditText editText = customLayout.findViewById(R.id.edit_studentName);
                Toast.makeText(ScannerActivity.this,"Redirecting...",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ScannerActivity.this, ScannerResult.class);
                intent.putExtra("laptop_info", li); //return the object li as a serialized object
                startActivity(intent);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void setObjectData(String raw, LaptopInfo li) {
        String a = "";
        int index = 0;

        while(!raw.isEmpty()) {
            if (raw.indexOf(",") >= 0) {
                a = raw.substring(index, raw.indexOf(","));
                li.setData(a);
                raw = raw.replace(a + ",", "");
                continue;
            } else {
                a = raw;
                li.setData(a);
                break;
            }
        }
    }


}
