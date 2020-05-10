package com.example.projetRapace;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetRapace.Camera.Camera;
import com.squareup.picasso.Picasso;

import static androidx.core.content.ContextCompat.startActivity;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private TextView textViewView;
    private ImageView imageView;
    private ImageButton buttonModif;

    /**
     * itemView est la vue correspondante à 1 cellule
     * */
    public MyViewHolder(final View itemView, int type) {
        super(itemView);

        //Si le type est 0, on gère des Locaux
        if(type == 0){
            //Action de click sur l'item, lancement de l'activité MainCardViewCamera
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), MainCardViewCamera.class));
                }
            });

            //c'est ici que l'on fait nos findView
            textViewView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            buttonModif = (ImageButton) itemView.findViewById(R.id.buttonModif);

            //Gestion de l'appuie du Bouton de Modif d'un Local
            buttonModif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), MainCardViewCamera.class));
                }
            });
        }
        //Sinon on gère des Caméras
        else if(type == 1){
            //Action de click sur l'item, lancement de l'activité CameraListView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), CameraListView.class));
                }
            });

            //c'est ici que l'on fait nos findView
            textViewView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            buttonModif = (ImageButton) itemView.findViewById(R.id.buttonModif);

            //Gestion de l'appuie du Bouton de Modif d'une Caméra
            buttonModif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), CameraListView.class));
                }
            });
        }
    }

    /**
     * Ajout une fonction pour remplir la cellule en fonction d'un Object Local
     * */
    public void bind(Local local){

        textViewView.setText(local.getNom());
        Picasso.with(imageView.getContext()).load(local.getImage()).centerCrop().fit().into(imageView);
    }

    /**
     * Ajout une fonction pour remplir la cellule en fonction d'un Object Caméra
     * */
    public void bind(Camera camera){

        textViewView.setText(camera.getName());
        Picasso.with(imageView.getContext()).load(camera.getIp()).centerCrop().fit().into(imageView);
    }
}
