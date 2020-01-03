package com.airri.nemu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airri.nemu.model.NemuModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FounderFormActivity extends AppCompatActivity implements View.OnClickListener {

    private String id, type;
    private EditText etName, etPhone;
    private Button btnSubmit;
    private FirebaseAuth auth;

    private Boolean isStatus = false;
    private Boolean isName = false;
    private Boolean isPhone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_founder_form);

        id   = getIntent().getStringExtra("KEY_EXTRA");
        type = getIntent().getStringExtra("TYPE_EXTRA");

        auth = FirebaseAuth.getInstance();

        if (type.equals("Nemu")) {
            getSupportActionBar().setTitle("Informasi Pencarian");
        } else {
            getSupportActionBar().setTitle("Informasi Penemuan");
        }

        etName    = findViewById(R.id.et_founder_name);
        etPhone   = findViewById(R.id.et_founder_phone);
        btnSubmit = findViewById(R.id.btn_founder_submit);

        btnSubmit.setOnClickListener(this);

        etName.setText(auth.getCurrentUser().getDisplayName());
        etName.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Kontak belum diisi");
        } else if (etPhone.getText().length() < 9) {
            etPhone.setError("Nomor telepon salah");
        } else {
            updateFound(etPhone.getText().toString());
        }
    }

    private void updateFound(String phone) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference();

        String status = "Sudah";
        String name = auth.getCurrentUser().getDisplayName();

        dbRef.child(type)
                .child(id)
                .child("status")
                .setValue(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isStatus = true;
                        if (isPhone && isName && isStatus) {
                            finish();
                        }
                    }
                });
        dbRef.child(type)
                .child(id)
                .child("founderName")
                .setValue(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isName = true;
                        if (isPhone && isName && isStatus) {
                            finish();
                        }
                    }
                });
        dbRef.child(type)
                .child(id)
                .child("founderPhone")
                .setValue(phone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isPhone = true;
                        if (isPhone && isName && isStatus) {
                            finish();
                        }
                    }
                });


    }
}
