package com.airri.nemu.form;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airri.nemu.DetailNemuActivity;
import com.airri.nemu.DownloadImageAsync;
import com.airri.nemu.R;
import com.airri.nemu.model.NemuModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class formNemu extends AppCompatActivity implements View.OnClickListener {
    // deklarasi variabel
    private Spinner spCategory;
    private Button btnUpload, btnSubmit;
    private EditText etSubject, etDescription, etLocation, etPhone;
    private ImageView imgPhoto;
    private TextView tvTitle;
    private NemuModel nemuTemp, nemuModel;
    private String type, id;
    private Boolean isUpdate = false;
    private Boolean isDataSaved = false;
    private Boolean isImageSaved = false;
    private Boolean isImageChanged = false;

    // upload foto
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_nemu);

        // dropdown kategori
        spCategory = findViewById(R.id.nemu_sp_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        type = getIntent().getStringExtra("TYPE_EXTRA");
        getSupportActionBar().setTitle(type);

        if (getIntent().getStringExtra("KEY_EXTRA") != null) {
            isUpdate = true;
            id = getIntent().getStringExtra("KEY_EXTRA");
            getData();
        }

        // get instance
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();

        // Mendapatkan Instance dari Database
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        // inisialisasi
        tvTitle       = findViewById(R.id.tv_title);
        btnSubmit     = findViewById(R.id.nemu_btn_submit);
        btnUpload     = findViewById(R.id.nemu_btn_upload);
        etSubject     = findViewById(R.id.nemu_et_subject);
        etDescription = findViewById(R.id.nemu_et_description);
        etLocation    = findViewById(R.id.nemu_et_location);
        etPhone       = findViewById(R.id.nemu_et_phone);
        imgPhoto      = findViewById(R.id.nemu_img_photo);

        // event klik
        btnSubmit.setOnClickListener(this);
        btnUpload.setOnClickListener(this);


        if (type.equals("Golek")) {
            tvTitle.setText("Info Pencarian");
            etLocation.setHint("Tulis lokasi kehilangan");
        } else {
            tvTitle.setText("Info Penemuan");
            etLocation.setHint("Tulis lokasi penemuan");
        }
    }

    private void getData() {
        final ProgressDialog pb = new ProgressDialog(formNemu.this);
        pb.setTitle("Memuat...");
        pb.show();

        nemuTemp = new NemuModel();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(type);
        Query q = ref.orderByKey().equalTo(id);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    nemuTemp = snapshot.getValue(NemuModel.class);
                    nemuTemp.setId(snapshot.getKey());
                }

                // menampilkan data;
                String cat = nemuTemp.getCategory();
                etSubject.setText(nemuTemp.getSubject());
                etDescription.setText(nemuTemp.getDescription());
                etLocation.setText(nemuTemp.getLocation());
                etPhone.setText(nemuTemp.getPhone());

                if (cat.equals("Pakaian")) {
                    spCategory.setSelection(0);
                } else if (cat.equals("Barang")) {
                    spCategory.setSelection(1);
                } else if (cat.equals("Uang")) {
                    spCategory.setSelection(2);
                } else {
                    spCategory.setSelection(3);
                }

                if (nemuTemp.getPhoto() != "") {
                    // download image
                    DownloadImageAsync dl = new DownloadImageAsync(formNemu.this, nemuTemp.getPhoto(), imgPhoto);
                    dl.execute();
                }

                pb.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(formNemu.this,"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
                pb.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nemu_btn_submit :
                uploadNemu();
                break;
            case R.id.nemu_btn_upload :
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPhoto.setImageBitmap(bitmap);
                isImageChanged = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void uploadNemu() {
        if (TextUtils.isEmpty(etSubject.getText())) {
            etSubject.setError("Judul belum diisi");
        } else if (TextUtils.isEmpty(etDescription.getText())) {
            etDescription.setError("Deskripsi belum diisi");
        } else if (TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setError("Lokasi belum diisi");
        } else if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Kontak belum diisi");
        } else if (etPhone.getText().length() < 9) {
            etPhone.setError("Nomor telepon salah");
        } else {
            nemuModel = new NemuModel();

            nemuModel.setSubject(etSubject.getText().toString());
            nemuModel.setCategory(spCategory.getSelectedItem().toString());
            nemuModel.setDescription(etDescription.getText().toString());
            nemuModel.setLocation(etLocation.getText().toString());
            nemuModel.setPhone(etPhone.getText().toString());

            nemuModel.setGoogleid(auth.getCurrentUser().getUid());
            nemuModel.setFname(auth.getCurrentUser().getDisplayName());

            nemuModel.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            nemuModel.setPhoto("");
            nemuModel.setStatus("Belum");
            nemuModel.setFounderPhone("");
            nemuModel.setFounderName("");

            if (isUpdate) {
                nemuModel.setPhoto(nemuTemp.getPhoto());
                nemuModel.setStatus(nemuTemp.getStatus());
                nemuModel.setFounderPhone(nemuTemp.getFounderPhone());
                nemuModel.setFounderName(nemuTemp.getFounderName());
                nemuModel.setDate(nemuModel.getDate());
            }

            final ProgressDialog progressDialog = new ProgressDialog(formNemu.this);
            final ProgressDialog progress = new ProgressDialog(formNemu.this);

            if(filePath != null && isImageChanged) {
                    progressDialog.setTitle("Mengunggah foto...");
                    progressDialog.show();

                    nemuModel.setPhoto(UUID.randomUUID().toString());

                    StorageReference ref = storageReference.child("images/"+ nemuModel.getPhoto());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(formNemu.this, "Foto terunggah", Toast.LENGTH_SHORT).show();
                                    isImageSaved = true;
                                    if (isDataSaved) {
                                        finish();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(formNemu.this, "Gagal "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Sedang mengunggah "+(int)progress+"%");
                                }
                            });
            } else {
                isImageSaved = true;
            }

            if (!isUpdate) {
                //insert
                progress.setTitle("Menambahkan Data...");
                progress.show();

                dbRef.child(type).push()
                        .setValue(nemuModel)
                        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progress.dismiss();
                                Toast.makeText(formNemu.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                                isDataSaved = true;
                                if (isImageSaved) {
                                    finish();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText(formNemu.this, "Gagal! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                //update
                progress.setTitle("Memperbarui Data...");
                progress.show();

                dbRef.child(type)
                        .child(nemuTemp.getId())
                        .setValue(nemuModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progress.dismiss();
                                Toast.makeText(formNemu.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                                isDataSaved = true;
                                if (isImageSaved) {
                                    finish();
                                }
                            }
                        });
            }
        }
    }
}
