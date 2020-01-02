package com.airri.nemu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airri.nemu.DetailNemuActivity;
import com.airri.nemu.DownloadImageAsync;
import com.airri.nemu.R;
import com.airri.nemu.model.NemuModel;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class NemuAdapter extends RecyclerView.Adapter<NemuAdapter.ViewHolder> {

    //Deklarasi Variable
    private List<NemuModel> listNemu;
    private Context context;

    public NemuAdapter(Context context) {
        this.context = context;
        listNemu = new ArrayList<>();
    }

    public void setData(List<NemuModel> data) {
        listNemu.clear();
        listNemu.addAll(data);
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
        holder.bindView(listNemu.get(position));
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailNemuActivity.class);
                intent.putExtra("KEY_EXTRA", listNemu.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNemu.size();
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
        }

        public void bindView(NemuModel nemu) {
            tvName.setText(nemu.getFname());
            tvSubject.setText(nemu.getSubject());
            tvCategory.setText(nemu.getCategory());
            tvStatus.setText(nemu.getStatus());

            String url = nemu.getPhoto();
            DownloadImageAsync dl = new DownloadImageAsync(context, url, imgPhoto);
            dl.execute();
        }
    }
}
