package com.home.dz.adapter;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.dz.R;
import com.home.dz.bean.Home;
import com.home.dz.home.HomeActivity;
import com.home.dz.home.annonce.AfficherAnnonceActivity;
import com.home.dz.home.annonce.MesFavorisFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MesFavorisAdapter extends RecyclerView.Adapter<MesFavorisAdapter.MyViewHolder>{

    private List<Home> homeList;
    private HomeActivity activity;
    private MesFavorisFragment fragment;

    public MesFavorisAdapter(List<Home> homeList, HomeActivity activity, MesFavorisFragment fragment) {
        this.homeList = homeList;
        this.activity = activity;
        this.fragment = fragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView titre,prix;
        public ImageView photo, show, delete;


        public MyViewHolder(View view) {
            super(view);
            titre = view.findViewById(R.id.anno_titre_item_tv);
            prix = view.findViewById(R.id.anno_prix_item_tv);
            photo = view.findViewById(R.id.anno_photo_item_img);
            show = view.findViewById(R.id.anno_show_item_img);
            delete = view.findViewById(R.id.anno_delete_item_img);


            delete.setOnClickListener(this);
            show.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int id = view.getId();

            if(id == R.id.anno_delete_item_img){
                fragment.deleteFavori(getAdapterPosition());
            }

            if(id == R.id.anno_show_item_img){
                //TODO
                Home home = homeList.get(getAdapterPosition());
                Intent intent = new Intent(activity, AfficherAnnonceActivity.class);
                intent.putExtra("home",home);
                activity.startActivity(intent);
            }

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mesfavorisitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Home home = homeList.get(position);

        holder.titre.setText(home.getTitre());
        holder.prix.setText(home.getPrix() + " DA");
        Picasso.get().load(home.getUrlPhotos().get(0)).into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }
}
