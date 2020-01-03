package com.airri.nemu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

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

public class ListNemuActivity extends AppCompatActivity  {

    // deklarasi variabel firebase
    private DatabaseReference nemuRef;
    private FirebaseAuth auth;
    private ArrayList<NemuModel> dataNemu;

    //Deklarasi Variable
    private RecyclerView rvNemu;
    private ProgressBar pbList;
    private ScrollView svList;
    private String category, type;
    private SearchView searchView;

    //deklarasi adapter
    private NemuAdapter nemuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nemu);

        // inisialisasi firebase auth
        auth = FirebaseAuth.getInstance();

        //inisialisasi
        rvNemu = findViewById(R.id.rv_list_nemu);
        pbList = findViewById(R.id.pb_list_nemu);
        svList = findViewById(R.id.sv_list_nemu);

        if (getIntent().getStringExtra("TYPE_EXTRA") != null) {
            type = getIntent().getStringExtra("TYPE_EXTRA");
            getSupportActionBar().setTitle(type);

            // mendapatkan data dari firebase
            getDataGolek();

            // mengatur recycler view
            setRV(type);
        } else {
            category = getIntent().getStringExtra("CATEGORY_EXTRA");
            getSupportActionBar().setTitle(category);

            // mendapatkan data dari firebase
            getData();

            // mengatur recycler view
            setRV("Nemu");
        }
    }

    private void getDataGolek() {
        pbList.setVisibility(View.VISIBLE);
        nemuRef = FirebaseDatabase.getInstance().getReference().child(type);

        Query q = nemuRef.orderByChild("status");

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
                Toast.makeText(ListNemuActivity.this,"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        pbList.setVisibility(View.VISIBLE);
        nemuRef = FirebaseDatabase.getInstance().getReference().child("Nemu");

        Query q = nemuRef.orderByChild("category").equalTo(category);

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
                Toast.makeText(ListNemuActivity.this,"Data Gagal Dimuat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRV(String t) {
        // inisialisasi adapter
        nemuAdapter = new NemuAdapter(ListNemuActivity.this, t);
        rvNemu.setAdapter(nemuAdapter);
        rvNemu.setLayoutManager(new LinearLayoutManager(ListNemuActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
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
        return true;
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

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
