package com.airri.nemu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airri.nemu.form.formNemu;
import com.airri.nemu.model.NemuModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DetailNemuActivity extends AppCompatActivity implements View.OnClickListener {

    // deklarasi variabel firebase
    private DatabaseReference nemuRef;

    // deklarasi variabel
    private String id, type;
    private NemuModel nemuDetail;
    private ProgressBar pbDetail;
    private TextView tvName, tvSubject, tvCategory, tvDescription, tvLocation, tvPhone, tvStatus, tvDate, tvFoundTitle, tvFoundName, tvFoundPhone;
    private ImageView imgPhoto;
    private Button btnFound, btnCall, btnFoundCall;
    //private LinearLayout foundInfo;
    private EditText etContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nemu);
        id = getIntent().getStringExtra("KEY_EXTRA");
        type = getIntent().getStringExtra("TYPE_EXTRA");
        getSupportActionBar().setTitle("Loading...");

        // inisialisasi
        pbDetail = findViewById(R.id.pb_detail_nemu);
        tvName = findViewById(R.id.tv_nemu_fname);
        tvSubject = findViewById(R.id.tv_nemu_subject);
        tvCategory = findViewById(R.id.tv_nemu_category);
        tvDescription = findViewById(R.id.tv_nemu_description);
        tvLocation = findViewById(R.id.tv_nemu_location);
        tvPhone = findViewById(R.id.tv_nemu_phone);
        tvStatus = findViewById(R.id.tv_nemu_status);
        tvDate = findViewById(R.id.tv_nemu_date);
        imgPhoto = findViewById(R.id.img_detail_nemu);
        tvFoundTitle = findViewById(R.id.tv_found_title);
        btnFound = findViewById(R.id.btn_found);
        //foundInfo = findViewById(R.id.found_info);
        tvFoundName = findViewById(R.id.found_info_name);
        tvFoundPhone = findViewById(R.id.found_info_phone);
        btnCall = findViewById(R.id.btn_call);
        btnFoundCall = findViewById(R.id.btn_founder_call);
        nemuDetail = new NemuModel();


        if (type.equals("Nemu")){
            tvFoundTitle.setText("Info Pencarian");
        }

        // mendapatkan data dari firebase
        getData();

        // tambah listener
        btnFound.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnFoundCall.setOnClickListener(this);
    }

    private void getData() {
        pbDetail.setVisibility(View.VISIBLE);
        nemuRef = FirebaseDatabase.getInstance().getReference().child(type);

        Query q = nemuRef.orderByKey().equalTo(id);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
                Toast.makeText(DetailNemuActivity.this, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
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

        if (nemuDetail.getStatus().equals("Belum")) {
            btnFound.setVisibility(View.VISIBLE);
            tvFoundName.setVisibility(View.GONE);
            tvFoundPhone.setVisibility(View.GONE);
            btnFoundCall.setVisibility(View.GONE);
            tvFoundTitle.setVisibility(View.GONE);
            if (type.equals("Nemu")) {
                btnFound.setText("Ini milik saya");
                btnCall.setText("Hubungi Penemu");
            }
        } else {
            btnFound.setVisibility(View.GONE);
            tvFoundName.setVisibility(View.VISIBLE);
            tvFoundPhone.setVisibility(View.VISIBLE);
            btnFoundCall.setVisibility(View.VISIBLE);
            tvFoundTitle.setVisibility(View.VISIBLE);

            if (type.equals("Nemu")) {
                btnFoundCall.setText("Hubungi Pencari");
                btnCall.setText("Hubungi Penemu");
            }
            tvFoundName.setText(nemuDetail.getFounderName());
            tvFoundPhone.setText(nemuDetail.getFounderPhone());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_found:
                Intent intent = new Intent(DetailNemuActivity.this, FounderFormActivity.class);
                intent.putExtra("TYPE_EXTRA", type);
                intent.putExtra("KEY_EXTRA", nemuDetail.getId());
                finish();
                startActivity(getIntent());
                startActivity(intent);
                break;
            case R.id.btn_call:
                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nemuDetail.getPhone()));
                startActivity(call);
                break;
            case R.id.btn_founder_call:
                Intent callFounder = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nemuDetail.getFounderPhone()));
                startActivity(callFounder);
                break;
        }
    }
}
