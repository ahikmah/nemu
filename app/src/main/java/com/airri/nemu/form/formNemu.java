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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airri.nemu.R;
import com.airri.nemu.model.NemuModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private String type, id;
    private Boolean isUpdate = false;
    private Boolean isDataSaved = false;
    private Boolean isImageSaved = false;

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
        tvTitle = findViewById(R.id.tv_title);

        if (getIntent().getStringExtra("KEY_EXTRA") != null) {
            isUpdate = true;
            id = getIntent().getStringExtra("KEY_EXTRA");
        }

        if (type.equals("Golek")) {
            tvTitle.setText("Info Pencarian");
        } else {
            tvTitle.setText("Info Penemuan");
        }

        // get instance
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();

        // Mendapatkan Instance dari Database
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        // inisialisasi
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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void uploadNemu() {
        final String subject, category, description, location, phone, filename;

        subject      = etSubject.getText().toString();
        category     = spCategory.getSelectedItem().toString();
        description  = etDescription.getText().toString();
        location     = etLocation.getText().toString();
        phone        = etPhone.getText().toString();

        //Mendapatkan UserID dari pengguna yang Terautentikasi
        String userID = auth.getCurrentUser().getUid();
        String fname  = auth.getCurrentUser().getDisplayName();
        String date   = DateFormat.getDateTimeInstance().format(new Date());

        if (TextUtils.isEmpty(subject)) {
            etSubject.setError("Judul belum diisi");
        } else if (TextUtils.isEmpty(description)) {
            etDescription.setError("Deskripsi belum diisi");
        } else if (TextUtils.isEmpty(location)) {
            etLocation.setError("Lokasi belum diisi");
        } else if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Kontak belum diisi");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(formNemu.this);
            final ProgressDialog progress = new ProgressDialog(formNemu.this);

            if(filePath != null) {
                    progressDialog.setTitle("Mengunggah foto...");
                    progressDialog.show();

                    filename = UUID.randomUUID().toString();

                    StorageReference ref = storageReference.child("images/"+ filename);
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
                filename = "";
                isImageSaved = true;
            }

            if (!isUpdate) {
                progress.setTitle("Menambahkan Data...");
                progress.show();

                dbRef.child(type).push()
                        .setValue(new NemuModel(userID, fname, subject, category, description, location, phone, "Belum", date, filename))
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
                progress.setTitle("Memperbarui Data...");
                progress.show();

                dbRef.child(type).push()
                        .setValue(new NemuModel(userID, fname, subject, category, description, location, phone, "Belum", date, filename))
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
            }
        }
    }
}
