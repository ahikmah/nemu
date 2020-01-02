package com.airri.nemu.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DashboardFragment extends Fragment {

    // ga ke pake
    private DashboardViewModel dashboardViewModel;

    // deklarasi variabel
    private DatabaseReference nemuRef;
    private FirebaseAuth auth;
    private ArrayList<NemuModel> dataNemu;

    // deklarasi
    private Spinner spType;
    private String type;
    private ProgressBar pbList;

    //Deklarasi Variable untuk RecyclerView
    private RecyclerView rvNemu;

    //deklarasi adapter
    private NemuAdapter nemuAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // dropdown kategori
        spType = root.findViewById(R.id.sp_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);


        // inisialisasi firebase auth
        auth = FirebaseAuth.getInstance();

        //inisialiassi
        rvNemu = root.findViewById(R.id.rv_item_nemu);
        pbList = root.findViewById(R.id.pb_list_dashboard);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getSelectedItem().toString();

                // mendapatkan data dari firebase
                getData(type);

                // mengatur recycler view
                setRV(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing to do
            }
        });

        return root;
    }

    private void getData(String type) {

        pbList.setVisibility(View.VISIBLE);

        Toast.makeText(getContext(), "Tunggu sebentar...", Toast.LENGTH_SHORT);

        nemuRef = FirebaseDatabase.getInstance().getReference().child(type);

        Query q = nemuRef.orderByChild("googleid").equalTo(auth.getUid());
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
                //Toast.makeText(getContext(),"Data berhasil dimuat", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(),"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRV(String type) {
        // inisialisasi adapter
        nemuAdapter = new NemuAdapter(getContext(), type);
        rvNemu.setAdapter(nemuAdapter);
        rvNemu.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}