package com.airri.nemu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airri.nemu.model.NemuModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DetailNemuActivity extends AppCompatActivity {

    // deklarasi variabel firebase
    private DatabaseReference nemuRef;

    // deklarasi variabel
    private String id, type;
    private NemuModel nemuDetail;
    private ProgressBar pbDetail;
    private TextView tvName, tvSubject, tvCategory, tvDescription, tvLocation, tvPhone, tvStatus, tvDate;
    private ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nemu);
        id   = getIntent().getStringExtra("KEY_EXTRA");
        type = getIntent().getStringExtra("TYPE_EXTRA");
        getSupportActionBar().setTitle("Loading...");

        // inisialisasi
        pbDetail         = findViewById(R.id.pb_detail_nemu);
        tvName           = findViewById(R.id.tv_nemu_fname);
        tvSubject        = findViewById(R.id.tv_nemu_subject);
        tvCategory       = findViewById(R.id.tv_nemu_category);
        tvDescription    = findViewById(R.id.tv_nemu_description);
        tvLocation       = findViewById(R.id.tv_nemu_location);
        tvPhone          = findViewById(R.id.tv_nemu_phone);
        tvStatus         = findViewById(R.id.tv_nemu_status);
        tvDate           = findViewById(R.id.tv_nemu_date);
        imgPhoto         = findViewById(R.id.img_detail_nemu);
        nemuDetail       = new NemuModel();

        // mendapatkan data dari firebase
        getData();
    }

    private void getData() {
        pbDetail.setVisibility(View.VISIBLE);
        nemuRef = FirebaseDatabase.getInstance().getReference().child(type);

        Query q = nemuRef.orderByKey().equalTo(id);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    nemuDetail = snapshot.getValue(NemuModel.class);
                    nemuDetail.setId(snapshot.getKey());
                }

                // menampilkan data;
                setData();
                getSupportActionBar().setTitle(nemuDetail.getCategory());
                // download image
                DownloadImageAsync dl = new DownloadImageAsync(DetailNemuActivity.this, nemuDetail.getPhoto(), imgPhoto);
                dl.execute();
                pbDetail.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailNemuActivity.this,"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        tvName.setText(nemuDetail.getFname());
        tvSubject.setText(nemuDetail.getSubject());
        tvCategory.setText(nemuDetail.getCategory());
        tvDescription.setText(nemuDetail.getDescription());
        tvLocation.setText(nemuDetail.getLocation());
        tvPhone.setText(nemuDetail.getPhone());
        tvStatus.setText(nemuDetail.getStatus());
        tvDate.setText(nemuDetail.getDate());
    }
}
