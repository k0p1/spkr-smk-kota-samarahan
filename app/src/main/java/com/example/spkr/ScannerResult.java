package com.example.spkr;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ScannerResult extends AppCompatActivity {
    private Button btn_edit, btn_save;
    private EditText serialNo, regNo, laptopID;
    private TextInputLayout textInputLayout;
    private LaptopInfo li;
    private DatabaseOp dbop;
    private DatabaseReference dreff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);

        if (getIntent().getExtras() != null) {
            li = (LaptopInfo) getIntent().getSerializableExtra("laptop_info");
        }

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_edit = (Button) findViewById(R.id.btn_edit);

        serialNo = (EditText) findViewById(R.id.edit_laptopSerialNo);
        serialNo.setText(li.getSerialNo());
        disableEdit(serialNo);

        regNo = (EditText) findViewById(R.id.edit_laptopRegistrationNo);
        regNo.setText(li.getRegistrationNo());
        disableEdit(regNo);

        laptopID = (EditText) findViewById(R.id.edit_laptopID);
        laptopID.setText(li.getLaptopID());
        disableEdit(laptopID);

        dbop = new DatabaseOp();
        dreff = FirebaseDatabase.getInstance().getReference().child("Laptop");

        //click save button
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                try {
                    dbop.insertRecord("Laptop", li.getClass(), li.getSerialNo(), ScannerResult.this);
                    Intent intent = new Intent(ScannerResult.this, MainActivity.class);
                    startActivity(intent);
//                    dreff.child(li.getSerialNo()).setValue(li).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(ScannerResult.this, "Record updated",Toast.LENGTH_SHORT).show();
//                            //go back to home page
//                            Intent intent = new Intent(ScannerResult.this, MainActivity.class);
//                            startActivity(intent);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(ScannerResult.this, "Record update failed...",Toast.LENGTH_SHORT).show();
//                            //show a dialog?
//                        }
//                    });

                } catch (Exception e) {
                    Toast.makeText(ScannerResult.this, "Exception occurred: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        //click edit button
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                try {
                    enableEdit(serialNo);
                    enableEdit(regNo);
                    enableEdit(laptopID);

                    serialNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().equalsIgnoreCase(li.getSerialNo())) {
                                //ignore
                            }

                            else
                                li.setSerialNo(serialNo.getText().toString().trim());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    Toast.makeText(ScannerResult.this, "Saving...",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ScannerResult.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void disableEdit (EditText editText) {
        editText.setFocusable(false);
        editText.setTextIsSelectable(false);
    }

    private void enableEdit (EditText editText) {
        editText.setFocusable(true);
    }

}
