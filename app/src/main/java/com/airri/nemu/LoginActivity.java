package com.airri.nemu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Membuat Kode Permintaan
    private int RC_SIGN_IN = 1;

    // Deklarasi variabel
    Button btnLogin;
    ProgressBar pbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // inisialisasi variabel
        btnLogin = findViewById(R.id.btn_login);
        pbLogin = findViewById(R.id.pb_login);

        // menonaktifkan loading
        pbLogin.setVisibility(View.GONE);

        // memberi aksi klik
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            // Statement program untuk login/masuk
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            //Memilih Provider atau Method masuk yang akan kita gunakan
                            .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
            // menampilkan loading
            pbLogin.setVisibility(View.VISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN adalah kode permintaan yang Anda berikan ke startActivityForResult, saat memulai masuknya arus.
        if (requestCode == RC_SIGN_IN) {

            //Berhasil masuk
            if (resultCode == RESULT_OK) {
                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                //menghilangkan loading
                pbLogin.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Login Dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // mencegah aksi back
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
