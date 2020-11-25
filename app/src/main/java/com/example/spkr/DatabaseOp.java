package com.example.spkr;

import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseOp extends AppCompatActivity {

    private DatabaseReference dreff = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getChild (String table) {
        return dreff.child(table);
    }

    public void insertRecord (String table, Class obj, String key, Context appContext) {
        dreff.child(table).child(key).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(appContext, "Record added successfully!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(appContext, "Failed to add record...", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public boolean checkIfDataExist (Class clss, String value, DatabaseReference dreff) {
//            boolean resp = false;
//            dreff.equalTo(li.getSerialNo()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot serialNolist: dataSnapshot.getChildren()) {
//                        if (dataSnapshot.getKey().contentEquals(li.getSerialNo())){
//                            resp = true;
//                            break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//    }
}
