package com.example.spkr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListDetails extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Date co, ret;
    private List<LaptopCheckOutInfo> laptopCheckOutInfoList;
    private List<LaptopInfo> laptopInfoList;
    private LaptopInfo laptopInfo;
    private LaptopCheckOutInfo laptopCheckOutInfo;
    private DatabaseOp dbop;
    private Spinner status;
    private Button btn_edit, btn_save;
    private EditText serialNo, regNo, laptopID, studentName, studentIC, studentClass, checkoutDate, returnDate;
    private Boolean newRecord, save;
    private DatePickerDialog datePicker;
    final private String errBlank = "This field cannot be blank";
    final private String errSymbol = "This field should not contain symbols and special characters!";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laptop_record_details);

        laptopInfoList = new ArrayList<>();
        laptopCheckOutInfoList = new ArrayList<>();
        dbop = new DatabaseOp();
        laptopCheckOutInfoList = dbop.setLaptopCheckoutInfoList();

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_edit = (Button) findViewById(R.id.btn_edit);

        serialNo = (EditText) findViewById(R.id.edit_laptopSerialNo);
        regNo = (EditText) findViewById(R.id.edit_laptopRegistrationNo);
        laptopID = (EditText) findViewById(R.id.edit_laptopID);

        studentName = (EditText) findViewById(R.id.edit_studentName);
        studentClass = (EditText) findViewById(R.id.edit_studentClass);
        studentIC = (EditText) findViewById(R.id.edit_studentIC);
        checkoutDate = (EditText) findViewById(R.id.edit_checkoutDate);
        returnDate = (EditText) findViewById(R.id.edit_returnDate);
        status = (Spinner) findViewById(R.id.sp_statusDropdown);

        disableEditAll();

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("laptop_info")) {
                laptopInfo = (LaptopInfo) getIntent().getSerializableExtra("laptop_info");
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initPage(exist());
            }
        },1000);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                try {
                    showSaveConfirmationDialog();
                    //ok button clicked on confirmation
                    if (save) {
                        Toast.makeText(getApplicationContext(), "Saving...",Toast.LENGTH_SHORT).show();
                        //load spinner

                        //set condition, if student name == null || date != null, save record, else just laptop?
                            presetData(laptopCheckOutInfo);
                            presetData(laptopInfo);
                            dbop.updateLaptopInfo("Laptop", laptopCheckOutInfo.getSerialNo(), getApplicationContext(), laptopInfo);
                            dbop.updateLaptopCheckoutInfo("Laptop Record", laptopCheckOutInfo.getSerialNo(), getApplicationContext(), laptopCheckOutInfo);
                            showSuccessDialog(laptopCheckOutInfo.getSerialNo(), "updated");
                        }


//                    Intent intent = new Intent(ScannerResult.this, MainActivity.class);
//                    startActivity(intent);
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

                    //cancel button clicked on confirmation
                    else {

                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception occurred: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                try {
                    enableEditAll();
                    dateClickListener(checkoutDate, returnDate);
                    dateComparison(checkoutDate, returnDate);
                    //dateInputHandling(checkoutDate, returnDate);
                    numberInputHandling(laptopID, errSymbol);
                    numberInputHandling(studentIC, errSymbol);
                    stringInputHandling(serialNo, errSymbol);
                    stringInputHandling(regNo, errSymbol);
                    stringInputHandling(studentName, errSymbol);
                    stringInputHandling(studentClass, errSymbol);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean exist(){
        Boolean recordExist = false;
        for(int i=0; i<laptopCheckOutInfoList.size(); i++) {
            if (laptopCheckOutInfoList.get(i).getSerialNo().equalsIgnoreCase(laptopInfo.getSerialNo())) {
                laptopCheckOutInfo = laptopCheckOutInfoList.get(i);
                Toast.makeText(getApplicationContext(), "Laptop has Record Information, loading...", Toast.LENGTH_LONG).show();
                recordExist = true;
                break;
            }
        }
        return recordExist;
    }

    private void initPage(Boolean exist) {
        if(!exist){
            //Toast.makeText(getApplicationContext(), "Laptop has Record Information, loading...", Toast.LENGTH_LONG).show();
            //initDisplay(laptopCheckOutInfo);
            Toast.makeText(getApplicationContext(), "Laptop does not have Record Information, loading...", Toast.LENGTH_LONG).show();
            initDisplay(laptopInfo);
        }
        else {
            initDisplay(laptopCheckOutInfo);
        }
    }

    private void initDisplay (LaptopInfo laptopInfo) {
        serialNo.setText(laptopInfo.getSerialNo());
        regNo.setText(laptopInfo.getRegistrationNo());
        laptopID.setText(laptopInfo.getLaptopID());
        laptopInfo.setStatus(status.getItemAtPosition(0).toString());
    }

    private void initDisplay (LaptopCheckOutInfo laptopCheckOutInfo) {
        serialNo.setText(laptopCheckOutInfo.getSerialNo());
        regNo.setText(laptopCheckOutInfo.getRegistrationNo());
        laptopID.setText(laptopCheckOutInfo.getLaptopID());
        studentName.setText(laptopCheckOutInfo.getStudentName());
        studentClass.setText(laptopCheckOutInfo.getStudentClass());
        studentIC.setText(laptopCheckOutInfo.getStudentIC());
        checkoutDate.setText(laptopCheckOutInfo.getCheckoutDate());
        initDropdown(status, laptopCheckOutInfo.getStatus());
    }

    private void initDropdown (Spinner dropdown, String selection) {
        for (int i = 0; i < dropdown.getCount(); i++) {
            if(dropdown.getItemAtPosition(i).toString().equalsIgnoreCase(selection)) {
                dropdown.setSelection(i);
                break;
            }
        }
    }

    private boolean validateNumber (String text) {
        Pattern p = Pattern.compile("\\d*"); //all digit 0-9
        Matcher m = p.matcher(text);
        return m.matches();
    }

    private boolean validateText (String text) {
        Pattern p = Pattern.compile("[\\w\\d\\s]*"); //all character a-zA-Z0-9 ?
        Matcher m = p.matcher(text);
        return m.matches();
    }

    private void presetData(LaptopCheckOutInfo laptopCheckOutInfo) {
        laptopCheckOutInfo.setSerialNo(serialNo.getText().toString().trim());
        laptopCheckOutInfo.setLaptopID(laptopID.getText().toString().trim());
        laptopCheckOutInfo.setRegistrationNo(regNo.getText().toString().trim());
        laptopCheckOutInfo.setStudentName(studentName.getText().toString().trim());
        laptopCheckOutInfo.setStudentClass(studentClass.getText().toString().trim());
        laptopCheckOutInfo.setStudentIC(studentIC.getText().toString().trim());
        laptopCheckOutInfo.setCheckoutDate(checkoutDate.getText().toString().trim());
        laptopCheckOutInfo.setReturnDate(returnDate.getText().toString().trim());
        laptopCheckOutInfo.setStatus(status.getSelectedItem().toString().trim());
    }

    private void presetData(LaptopInfo laptopInfo) {
        laptopInfo.setSerialNo(serialNo.getText().toString().trim());
        laptopInfo.setLaptopID(laptopID.getText().toString().trim());
        laptopInfo.setRegistrationNo(regNo.getText().toString().trim());
        laptopInfo.setStatus(status.getSelectedItem().toString().trim());
    }

    public void dateClickListener (EditText checkout, EditText ret) throws ParseException {
        checkout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                long currentTimestamp = c.getTimeInMillis();

                datePicker = new DatePickerDialog(ListDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //validation
                        checkout.setText(new StringBuilder().append(dayOfMonth).append("-").append(month+1).append("-").append(year).toString());
                    }
                }, year, month, day);
                datePicker.create();
                datePicker.show();
            }
        });

        ret.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                long currentTimestamp = c.getTimeInMillis();

                datePicker = new DatePickerDialog(ListDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //validation
                        ret.setText(new StringBuilder().append(dayOfMonth).append("-").append(month+1).append("-").append(year).toString());
                    }
                }, year, month, day);
                datePicker.create();
                datePicker.show();
            }
        });
    }

    private void disableEditAll () {
        disableEdit(serialNo);
        disableEdit(regNo);
        disableEdit(laptopID);
        disableEdit(studentName);
        disableEdit(studentClass);
        disableEdit(studentIC);
        disableEdit(checkoutDate);
        disableEdit(returnDate);
        status.setEnabled(false);
        status.setClickable(false);
    }

    private void disableEdit (EditText editText) {
        editText.setFocusable(false);
        editText.setTextIsSelectable(false);
    }

    private void enableEditAll () {
        enableEdit(serialNo);
        enableEdit(regNo);
        enableEdit(laptopID);
        enableEdit(studentName);
        enableEdit(studentClass);
        enableEdit(studentIC);
        status.setClickable(true);
    }

    private void enableEdit (EditText editText) {
        editText.setFocusable(true);
        editText.setTextIsSelectable(true);
    }

    private void showSuccessDialog(String key, String op) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //create a new layout xml for this?
        //final View customLayout = getLayoutInflater().inflate(R.layout.activity_scanner_result, null);
        //alertDialog.setView(customLayout);
        alertDialog.setTitle("Record Status").setMessage("Record "+key+" is "+op+" successfully!");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Back to home page...",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),  Home.class);
                startActivity(intent);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void showSaveConfirmationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //create a new layout xml for this?
        //final View customLayout = getLayoutInflater().inflate(R.layout.activity_scanner_result, null);
        //alertDialog.setView(customLayout);
        alertDialog.setTitle("Are you sure?").setMessage("Do you want to save the changes to this record?");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //link to save button? continue from there?
                save = true;
                // send data from the AlertDialog to the Activity
//                //EditText editText = customLayout.findViewById(R.id.edit_studentName);
//                Toast.makeText(ScannerResult.this,"Back to home page...",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ScannerResult.this,  MainActivity.class);
//                intent.putExtra("laptop_info", li); //return the object li as a serialized object
//                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
                save = false;
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void numberInputHandling (EditText editText, String onTextErrMsg) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateNumber(s.toString())) {
                    editText.requestFocus();
                    editText.setError(onTextErrMsg);
                } else {
                    editText.setError(null);
                }

                if(editText.getText().toString().length() <= 0) {
                    editText.requestFocus();
                    editText.setError(errBlank);
                }

                else {
                    editText.setError(null);
                }
            }
        });
    }

    private void stringInputHandling (EditText editText, String onTextErrMsg) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateText(s.toString())) {
                    editText.requestFocus();
                    editText.setError(onTextErrMsg);
                } else {
                    editText.setError(null);
                }

                if(editText.getText().toString().length() <= 0) {
                    editText.requestFocus();
                    editText.setError(errBlank);
                }

                else {
                    editText.setError(null);
                }
            }
        });
    }

//    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//
//    Date date1 = format.parse(dateString1);
//    Date date2 = format.parse(dateString2);
//
//    if (date1.compareTo(date2) <= 0) {
//        System.out.println("dateString1 is an earlier date than dateString2");
//    }

    private void dateComparison (EditText checkoutDate, EditText returnDate){
        if (!checkoutDate.getText().toString().isEmpty() && !returnDate.getText().toString().isEmpty()) {
            try {
                co = simpleDateFormat.parse(checkoutDate.getText().toString());
                ret = simpleDateFormat.parse(returnDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (co.after(ret)) {
            checkoutDate.setError("Checkout Date must not be later than Return Date");
        }

        else if (ret.before(co)) {
            returnDate.setError("Return Date must not be earlier than Checkout Date");
        }

        else {
            checkoutDate.setError(null);
            returnDate.setError(null);
        }
    }
    private void dateInputHandling (EditText checkout, EditText returnDate) {
        checkout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    checkout.requestFocus();
                } else {
                    checkout.setError(null);
                }
            }
        });

        returnDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateText(s.toString())) {
                    returnDate.requestFocus();
                    returnDate.setError("");
                } else {
                    returnDate.setError(null);
                }

                if(returnDate.getText().toString().length() <= 0) {
                    returnDate.requestFocus();
                    returnDate.setError(errBlank);
                }

                else {
                    returnDate.setError(null);
                }
            }
        });
    }
}
