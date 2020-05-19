package com.example.projetRapace;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Local.Local;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Object> list;

    /**
     * Ajouter un constructeur prenant en entrée une liste d'Objects
     * L'adaptateur fait le lien entre le RecyclerView et Les CardViews
     * */
    public Adapter(List<Object> list) {
        this.list = list;
    }

    /**
     * Cette méthode permet de créer les viewHolder
     * Et par la même occasion indiquer la vue à inflater (à partir des layout xml)
     * */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_card_view, viewGroup,false);
        int type = 0;
        if(list.get(0).getClass().toString().equals(Camera.class.toString())) {
            type = 1;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_card_view, viewGroup, false);
        }
        return new MyViewHolder(view, type);
    }

    /**
     * Remplissage de nos cellules avec le texte/image de chaque Object
     * */
    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Object myObject = list.get(position);
        if(myObject.getClass().toString().equals(Local.class.toString())){
            Log.d("LOCAL ADAPTATEUR : ", myObject.getClass().toString());
            myViewHolder.bind((Local) myObject);
        }
        else if(myObject.getClass().toString().equals(Camera.class.toString())){
            Log.d("LOCAL ADAPTATEUR : ", myObject.getClass().toString());
            myViewHolder.bind((Camera) myObject);
        }
    }

    /**
     * Renvoie la taille de la liste d'Objects
     * */
    @Override
    public int getItemCount() {
        return list.size();
    }

}
