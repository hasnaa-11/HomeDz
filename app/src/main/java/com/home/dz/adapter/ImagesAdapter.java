package com.home.dz.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.home.dz.R;
import com.home.dz.home.annonce.AddHomeActivity;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder>{

    private List<Uri> listeURI;
    private AddHomeActivity activity;

    public ImagesAdapter(List<Uri> listeURI, AddHomeActivity activity) {
        this.listeURI = listeURI;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView delete;
        public ImageView photo;

        public MyViewHolder(View view) {
            super(view);

            delete = view.findViewById(R.id.delete_item_img);
            photo = view.findViewById(R.id.photo_item_img);

        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            if(id == R.id.photo_item_img){
                activity.setPhoto(getAdapterPosition());
            }

            if(id == R.id.delete_item_img){
                activity.deleteImage(getAdapterPosition());
            }

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imageitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.photo.setImageURI(listeURI.get(position));
    }

    @Override
    public int getItemCount() {
        return listeURI.size();
    }

}
