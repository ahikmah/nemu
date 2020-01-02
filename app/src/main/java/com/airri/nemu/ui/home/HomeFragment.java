package com.airri.nemu.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.airri.nemu.ListNemuActivity;
import com.airri.nemu.R;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    //deklarasi
    private CardView cvPakaian, cvBarang, cvUang, cvLainnya;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //inisialisasi
        cvPakaian = root.findViewById(R.id.cv_pakaian);
        cvBarang  = root.findViewById(R.id.cv_barang);
        cvUang    = root.findViewById(R.id.cv_uang);
        cvLainnya = root.findViewById(R.id.cv_lainnya);

        cvPakaian.setOnClickListener(this);
        cvBarang.setOnClickListener(this);
        cvUang.setOnClickListener(this);
        cvLainnya.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        String category = "Lainnya";
        switch (view.getId()) {
            case R.id.cv_pakaian :
                category = "Pakaian";
                break;
            case R.id.cv_barang :
                category = "Barang";
                break;
            case R.id.cv_uang :
                category = "Uang";
                break;
            case R.id.cv_lainnya :
                category = "Lainnya";
                break;
        }
        Intent intent = new Intent(getActivity(), ListNemuActivity.class);
        intent.putExtra("CATEGORY_EXTRA", category);
        startActivity(intent);
    }
}