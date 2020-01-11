package com.airri.nemu.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airri.nemu.DetailNemuActivity;
import com.airri.nemu.DownloadImageAsync;
import com.airri.nemu.MainActivity;
import com.airri.nemu.R;
import com.airri.nemu.form.formNemu;
import com.airri.nemu.model.NemuModel;
import com.airri.nemu.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class NemuAdapter extends RecyclerView.Adapter<NemuAdapter.ViewHolder> implements Filterable {

    //Deklarasi Variable
    private List<NemuModel> listNemu;
    private List<NemuModel> listNemuFiltered;
    private Context context;
    private String type, guid;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;


    public NemuAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
        listNemu = new ArrayList<>();
        listNemuFiltered = new ArrayList<>();

        // Mendapatkan Instance dari Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        guid = auth.getUid();
    }

    public void setData(List<NemuModel> data) {
        listNemu.clear();
        listNemu.addAll(data);
        listNemuFiltered = listNemu;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // R.layout.namaLayout
        View v = LayoutInflater.from(context).inflate(R.layout.item_nemu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindView(listNemuFiltered.get(position));
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailNemuActivity.class);
                intent.putExtra("KEY_EXTRA", listNemuFiltered.get(position).getId());
                intent.putExtra("TYPE_EXTRA", type);
                context.startActivity(intent);
            }
        });

        String uid = listNemuFiltered.get(position).getGoogleid();
        if (uid.equals(guid)) {
            holder.listItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final String[] action = {"Update", "Delete"};
                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    alert.setItems(action,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i){
                                case 0:
                                    Intent intent = new Intent(context, formNemu.class);
                                    intent.putExtra("KEY_EXTRA", listNemuFiltered.get(position).getId());
                                    intent.putExtra("TYPE_EXTRA", type);
                                    context.startActivity(intent);
                                    break;
                                case 1:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Hapus");
                                    builder.setMessage("Apakah anda yakin akan menghapus "+listNemuFiltered.get(position).getSubject()+"?");
                                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(dbRef != null){
                                                dbRef.child(type).child(listNemuFiltered.get(position).getId()).removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener() {
                                                            @Override
                                                            public void onSuccess(Object o) {
                                                                Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirm = builder.create();
                                    confirm.show();
                                    break;
                            }
                        }
                    });
                    alert.create();
                    alert.show();
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listNemuFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listNemuFiltered = listNemu;
                } else {
                    List<NemuModel> filteredList = new ArrayList<>();
                    for (NemuModel row : listNemu) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDescription().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    listNemuFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listNemuFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listNemuFiltered = (ArrayList<NemuModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvSubject, tvCategory, tvStatus;
        private CircularImageView imgPhoto;
        private LinearLayout listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName      = itemView.findViewById(R.id.tv_fname);
            tvSubject   = itemView.findViewById(R.id.tv_subject);
            tvCategory  = itemView.findViewById(R.id.tv_category);
            tvStatus    = itemView.findViewById(R.id.tv_status);
            listItem    = itemView.findViewById(R.id.list_item);
            imgPhoto    = itemView.findViewById(R.id.img_thumb);
            setIsRecyclable(false);
        }

        public void bindView(NemuModel nemu) {
            tvName.setText(nemu.getFname());
            tvSubject.setText(nemu.getSubject());
            tvCategory.setText(nemu.getCategory());
            tvStatus.setText(nemu.getStatus());

            if (nemu.getStatus().equals("Belum")) {
                tvStatus.setBackgroundResource(R.drawable.badge_red);
            } else {
                tvStatus.setBackgroundResource(R.drawable.badge_tosca);
            }

            String url = nemu.getPhoto();
            //imgPhoto.setImageResource(R.drawable.lainnya);
            DownloadImageAsync dl = new DownloadImageAsync(context, url, imgPhoto);
            dl.execute();
        }
    }
}
