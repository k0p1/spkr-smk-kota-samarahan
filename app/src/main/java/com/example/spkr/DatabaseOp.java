package com.example.spkr;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DatabaseOp extends AppCompatActivity {

    private DatabaseReference dreff;
    public DatabaseOp () {
        this.dreff = FirebaseDatabase.getInstance().getReference();
    }
    //private DatabaseReference dreff = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getChild (String table) {
        return dreff.child(table);
    }

    public void insertLaptopInfo (String table, String key, Context appContext, LaptopInfo obj) {
        dreff.child(table).child(key).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //hide spinner
                Toast.makeText(appContext, "Laptop Info added successfully!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(appContext, "Failed to add laptop info...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void insertLaptopRecord (String table, String key, Context appContext, LaptopCheckOutInfo obj) {
        dreff.child(table).child(key).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //hide spinner
                Toast.makeText(appContext, "Laptop Record added successfully!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(appContext, "Failed to add laptop record...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void updateLaptopInfo (String table, String key, Context appContext, LaptopInfo obj) {
        dreff.child(table).child(key).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //hide spinner
                Toast.makeText(appContext, "Laptop Info updated successfully!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(appContext, "Failed to update laptop info...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isLaptopExist (String key) {
        final boolean[] exist = {false};
        Query existRecord = FirebaseDatabase.getInstance().getReference().child("Laptop");
        existRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.hasChild(key)) {
                        exist[0] = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                exist[0] = false;
            }
        });

        return exist[0];
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
