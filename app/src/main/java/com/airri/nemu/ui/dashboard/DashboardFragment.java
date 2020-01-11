package com.airri.nemu.ui.dashboard;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import java.util.zip.Inflater;

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
    private SearchView searchView;
    private TextView tvMylist;

    //Deklarasi Variable untuk RecyclerView
    private RecyclerView rvNemu;

    //deklarasi adapter
    private NemuAdapter nemuAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setHasOptionsMenu(true);

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
        tvMylist = root.findViewById(R.id.tv_mylist);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getSelectedItem().toString();

                // mendapatkan data dari firebase
                getData(type);

                // mengatur recycler view
                setRV(type);

                // mengubah judul
                if (type.equals("Nemu")) {
                    tvMylist.setText("Daftar penemuan saya");
                } else {
                    tvMylist.setText("Daftar pencarian saya");
                }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                nemuAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                nemuAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}