package com.example.spkr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, CustomDialogFragment.CustomDialogListener {
    private ZXingScannerView mScannerView;
    public Context context;
    public Boolean format = false;
    private String hint = "NXMZDSM0156320FCXXXXXX, YEA9999/9999/9999, 999. Replace the leading X and 9 with characters and numbers respectively.";
    protected List<LaptopInfo> laptopInfoList =  new ArrayList<>();
    protected List<LaptopCheckOutInfo> laptopCheckOutInfoList = new ArrayList<>();
    protected LaptopInfo li = new LaptopInfo();
    private LaptopCheckOutInfo laptopCheckOutInfo;
    private DatabaseOp dbop = new DatabaseOp();
    private DatabaseReference dreff = FirebaseDatabase.getInstance().getReference().child("Laptop");
    private DatabaseReference dreff1 = FirebaseDatabase.getInstance().getReference().child("Laptop Record");
    private CustomDialogFragment customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_frame);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        context = getApplicationContext();
        initLaptopList();
        initLaptopRecordList();

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
        Toast.makeText(ScannerActivity.this, "Verifying...", Toast.LENGTH_SHORT).show();
        //spinning loader

        onPause();
        //once get result do something - db
        if (rawResult.getText() != null) {
            //showScanResultDialog("Is the information/format correct?", rawResult.getText().trim());
            if (checkFormat(rawResult.getText().trim())) {
                setObjectData(rawResult.getText(), li);

                if (isLaptopExist(laptopInfoList, li.getSerialNo())) {
                    showAlertDialog(li.getSerialNo());
                    //show exist dialog?
                    //back main page?
                    //this.finish();
                }
                //customDialog.showAlertDialog(R.layout.activity_scanner_result, this, "Is the information correct?");

                else {
                    Toast.makeText(ScannerActivity.this, "Record not exist, redirecting to add laptop...", Toast.LENGTH_SHORT).show();
                    li.setStatus("Vacant");
                    Intent intent = new Intent(ScannerActivity.this, ScannerResult.class);
                    intent.putExtra("laptop_info", li); //return the object li as a serialized object
                    this.finish();
                    startActivity(intent);
                }
            }

            else {
                showScanResultDialog("Incorrect Format", "The QR code data format is invalid. Input is \""+rawResult.getText().trim()+"\".\nHint: It should contain "+hint);
                Toast.makeText(ScannerActivity.this, "Warning", Toast.LENGTH_SHORT).show();
                mScannerView.resumeCameraPreview(this);
            }
        }
    }


    public void showMessageDialog(String message) {
        DialogFragment dialog = CustomDialogFragment.newInstance("Scan Results", message, this);
        dialog.show(this.getSupportFragmentManager(), "scan_results");
        onDialogPositiveClick(dialog);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //mScannerView.resumeCameraPreview(this);
//        Intent intent = new Intent(this, ScannerResult.class);
//        intent.putExtra("laptop_info", li); //return the object li as a serialized object
//        startActivity(intent);
        this.format = true;
        Toast.makeText(ScannerActivity.this, "Dialog Click OK...", Toast.LENGTH_LONG).show();
    }

    public void testAlertDialog(String message) {
        DialogFragment dialogFragment = CustomDialogFragment.newInstance("Testing", message, this);


    }

    private void showScanResultDialog(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //create a new layout xml for this?
        //final View customLayout = getLayoutInflater().inflate(R.layout.activity_scanner_result, null);
        //alertDialog.setView(customLayout);
        alertDialog.setTitle(title).setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            private boolean format = false;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onResume();
                // send data from the AlertDialog to the Activity
                //EditText editText = customLayout.findViewById(R.id.edit_studentName);
                //Toast.makeText(ScannerActivity.this,"Format correct, please proceed...",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ScannerActivity.this, ScannerResult.class);
//                intent.putExtra("laptop_info", li); //return the object li as a serialized object
//                startActivity(intent);
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
    //record exist, go to show details
    private void showAlertDialog(String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //create a new layout xml for this?
        //final View customLayout = getLayoutInflater().inflate(R.layout.activity_scanner_result, null);
        //alertDialog.setView(customLayout);
        alertDialog.setTitle("Alert Dialog - Duplicate Check").setMessage("Oh No!! Record "+key+" is found in database..");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                //EditText editText = customLayout.findViewById(R.id.edit_studentName);
                Toast.makeText(ScannerActivity.this,"Redirecting...",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScannerActivity.this, ScannerResult.class);
                assignObjValue(laptopCheckOutInfoList, key);
                intent.putExtra("laptop_record_info", laptopCheckOutInfo); //return the object li as a serialized object
                startActivity(intent);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private boolean checkFormat(String raw) {
        boolean result = true;
        String [] split = raw.trim().split(",");
        if (split.length != 3) {
            result = false;
        }

        else {
            for (int i = 0; i < split.length; i++) {
                if (split[i].startsWith("NXMZDSM0156320FC")) {
                    continue;
                } else if (split[i].startsWith("YEA")) {
                    continue;
                } else if (split[i].matches("\\d*")) {
                    continue;
                }
                else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    private void setObjectData(String raw, LaptopInfo li) {
        String [] split = raw.trim().split(",");
        li.setData(split);
    }

    private void initLaptopList () {
        Query query = dreff;
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
                //mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isLaptopExist (List<LaptopInfo> list, String key) {
        boolean result = false;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getSerialNo().equalsIgnoreCase(key)){
                result = true;
                break;
            }
        }

        return result;
    }

    private void initLaptopRecordList () {
        Query query = dreff1;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void assignObjValue (List<LaptopCheckOutInfo> laptopCheckOutInfoList, String key) {
        for (int i = 0; i< laptopCheckOutInfoList.size(); i++) {
            if (laptopCheckOutInfoList.get(i).getSerialNo().equalsIgnoreCase(key)) {
                laptopCheckOutInfo = laptopCheckOutInfoList.get(i);
                break;
            }
        }
    }
}