package com.ngandroidaja.examplefirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ngandroidaja.examplefirebase.R;
import com.ngandroidaja.examplefirebase.UpdateDelActivity;
import com.ngandroidaja.examplefirebase.model.Biodata;

import java.util.ArrayList;

public class BioAdapter extends RecyclerView.Adapter<BioAdapter.ViewHolder> {

    private ArrayList<Biodata> list;
    private Context context;

    public BioAdapter(ArrayList<Biodata> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_biodata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Biodata bio = list.get(position);
        holder.EdtNama.setText(bio.getNama());
        holder.EdtKampus.setText(bio.getNotelp());

        Glide.with(context)
                .asBitmap()
                .load(bio.getUrl())
                .apply(new RequestOptions().override(100, 100))
                .error(R.mipmap.ic_launcher)
                .into(holder.imageViewUrl);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BioDetail = new Intent(context, UpdateDelActivity.class);
                BioDetail.putExtra("id", bio.getKey());
                BioDetail.putExtra("nama", bio.getNama());
                BioDetail.putExtra("notelp", bio.getNotelp());
                BioDetail.putExtra("alamat", bio.getAlamat());
                BioDetail.putExtra("kampus", bio.getKampus());
                BioDetail.putExtra("url", bio.getUrl());

                context.startActivity(BioDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<Biodata> getList() {
        return list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView EdtNama, EdtKampus;
        private ImageView imageViewUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            EdtNama = itemView.findViewById(R.id.show_nama);
            EdtKampus = itemView.findViewById(R.id.show_kampus);
            imageViewUrl = itemView.findViewById(R.id.image_url);
        }
    }
}
