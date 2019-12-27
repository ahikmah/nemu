package com.airri.nemu.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airri.nemu.model.NemuModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    //Deklarasi Variable Database Reference dan ArrayList dengan Parameter Class Model kita.
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference nemuRef;

    private MutableLiveData<List> dataNemu;

    public DashboardViewModel() {
        dataNemu = new MutableLiveData<>();

        // inisialisasi
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        nemuRef = database.getReference().child("Nemu").child(auth.getUid());

        nemuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<NemuModel> tempNemu = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    NemuModel nemu = data.getValue(NemuModel.class);
                    tempNemu.add(nemu);
                }
                dataNemu.setValue(tempNemu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<List> getDataNemu() {
        return dataNemu;
    }
}