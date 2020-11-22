package com.example.spkr;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
