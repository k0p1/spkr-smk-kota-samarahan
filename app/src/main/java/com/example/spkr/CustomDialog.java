package com.example.spkr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class CustomDialog {

    public void showAlertDialog(int layout, Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        //create a new layout xml for individual dialog
        final View customLayout = new Activity().getLayoutInflater().inflate(layout, null);
        alertDialog.setView(customLayout);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                //EditText editText = customLayout.findViewById(R.id.edit_studentName);
                Toast.makeText(context,"Redirecting...",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ScannerActivity.this, ScannerResult.class);
//                intent.putExtra("laptop_info", li); //return the object li as a serialized object
//                startActivity(intent);
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
