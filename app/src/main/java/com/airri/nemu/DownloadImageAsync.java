package com.airri.nemu;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DownloadImageAsync extends AsyncTask<String, Integer, String> {
    private StorageReference imgRef;
    private ImageView imgDownload;
    private String url;
    private Context context;

    public DownloadImageAsync(Context context, String url, ImageView imgDownload) {
        this.context = context;
        this.url = url;
        this.imgDownload = imgDownload;
        imgRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            imgRef.child("images").child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(imgDownload);
                }
            });
        } catch (Exception ex) {

        }
        return "Downloaded";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}
