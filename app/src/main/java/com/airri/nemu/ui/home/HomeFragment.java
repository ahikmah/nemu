package com.airri.nemu.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airri.nemu.ListNemuActivity;
import com.airri.nemu.R;
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

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    //deklarasi
    private CardView cvPakaian, cvBarang, cvUang, cvLainnya;

    // deklarasi variabel firebase
    private DatabaseReference nemuRef;
    private FirebaseAuth auth;
    private ArrayList<NemuModel> dataNemu;

    //Deklarasi Variable
    private RecyclerView rvNemu;
    private ProgressBar pbList;
    private ScrollView svList;
    private String category;
    private TextView tvMore;

    //deklarasi adapter
    private NemuAdapter nemuAdapter;

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
        tvMore    = root.findViewById(R.id.tv_golek_more);

        cvPakaian.setOnClickListener(this);
        cvBarang.setOnClickListener(this);
        cvUang.setOnClickListener(this);
        cvLainnya.setOnClickListener(this);

        // click golek more
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ListNemuActivity.class);
                intent.putExtra("TYPE_EXTRA","Golek");
                startActivity(intent);
            }
        });

        // inisialisasi firebase auth
        auth = FirebaseAuth.getInstance();

        //inisialisasi
        rvNemu = root.findViewById(R.id.rv_list_golek_home);
        pbList = root.findViewById(R.id.pb_list_golek_home);

        if (auth.getCurrentUser() != null) {
            // mengatur recycler view
            setRV();

            // mendapatkan data dari firebase
            getData();
        }

        return root;
    }

    private void getData() {
        pbList.setVisibility(View.VISIBLE);
        nemuRef = FirebaseDatabase.getInstance().getReference().child("Golek");

        Query q = nemuRef.orderByChild("status").limitToFirst(5);

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
                if (auth.getCurrentUser() != null) {
                    Toast.makeText(getActivity(),"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRV() {
        // inisialisasi adapter
        nemuAdapter = new NemuAdapter(getContext(), "Golek");
        rvNemu.setAdapter(nemuAdapter);
        rvNemu.setLayoutManager(new LinearLayoutManager(getContext()));
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