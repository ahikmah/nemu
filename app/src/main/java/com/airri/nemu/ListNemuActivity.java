package com.airri.nemu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.airri.nemu.adapter.NemuAdapter;
import com.airri.nemu.model.NemuModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListNemuActivity extends AppCompatActivity  {

    // deklarasi variabel firebase
    private DatabaseReference nemuRef;
    private FirebaseAuth auth;
    private ArrayList<NemuModel> dataNemu;

    //Deklarasi Variable
    private RecyclerView rvNemu;
    private ProgressBar pbList;
    private ScrollView svList;
    private String category;

    //deklarasi adapter
    private NemuAdapter nemuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nemu);
        category = getIntent().getStringExtra("CATEGORY_EXTRA");
        getSupportActionBar().setTitle(category);

        // inisialisasi firebase auth
        auth = FirebaseAuth.getInstance();

        //inisialisasi
        rvNemu = findViewById(R.id.rv_list_nemu);
        pbList = findViewById(R.id.pb_list_nemu);
        svList = findViewById(R.id.sv_list_nemu);

        // mengatur recycler view
        setRV();

        // mendapatkan data dari firebase
        getData();
    }

    private void getData() {

        pbList.setVisibility(View.VISIBLE);
        nemuRef = FirebaseDatabase.getInstance().getReference().child("Nemu");

        Query q = nemuRef.orderByChild("category").equalTo(category);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataNemu = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    NemuModel temp = snapshot.getValue(NemuModel.class);
                    temp.setId(snapshot.getKey());
                    dataNemu.add(temp);
                }

                //set data ke adapter
                nemuAdapter.setData(dataNemu);
                pbList.setVisibility(View.GONE);
                //Toast.makeText(ListNemuActivity.this,"Data berhasil dimuat", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListNemuActivity.this,"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRV() {
        // inisialisasi adapter
        nemuAdapter = new NemuAdapter(ListNemuActivity.this);
        rvNemu.setAdapter(nemuAdapter);
        rvNemu.setLayoutManager(new LinearLayoutManager(ListNemuActivity.this));
    }
}
