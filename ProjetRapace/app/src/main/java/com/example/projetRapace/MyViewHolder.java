package com.example.projetRapace;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Local.Local;
import com.squareup.picasso.Picasso;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private TextView textViewView;
    private ImageView imageView;
    private ImageButton buttonModif;
    private View itemView;

    /**
     * itemView est la vue correspondante à 1 cellule
     * */
    public MyViewHolder(final View itemView, int type) {
        super(itemView);
        this.itemView=itemView;
        //Si le type est 0, on gère des Locaux
        if(type == 0){
            //c'est ici que l'on fait nos findView
            textViewView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            buttonModif = (ImageButton) itemView.findViewById(R.id.buttonModif);
            //buttonModif.setVisibility(View.GONE);
        }
        //Sinon on gère des Caméras
        else if(type == 1){
            //Action de click sur l'item, lancement de l'activité CameraListView

            //c'est ici que l'on fait nos findView
            textViewView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            buttonModif = (ImageButton) itemView.findViewById(R.id.buttonModif);
        }
    }

    /**
     * Ajout une fonction pour remplir la cellule en fonction d'un Object Caméra
     * */
    public void bind(Local local){
        final Local l = local;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), MainCardViewCamera.class);

                intent.putExtra("id", l.getId());
                itemView.getContext().startActivity(intent);
            }
        });

        buttonModif = (ImageButton) itemView.findViewById(R.id.buttonModif);

        //Gestion de l'appuie du Bouton de Modif d'un Local
        buttonModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), VueLocal.class);

                Log.d("bindLocal", "(id) -> " + l.getId());
                intent.putExtra("id", l.getId());
                itemView.getContext().startActivity(intent);
            }
        });

        textViewView.setText(local.getName());
        if(local.getImage() != null)
            Picasso.get().load(local.getImage()).centerCrop().fit().into(imageView);
    }

    /**
     * Ajout une fonction pour remplir la cellule en fonction d'un Object Caméra
     * */
    public void bind(Camera camera){
        final Camera c = camera;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(itemView.getContext(), CameraView.class);

                intent.putExtra("id", c.getId());
                intent.putExtra("ip", c.getIp());
                itemView.getContext().startActivity(intent);
            }
        });

        buttonModif = (ImageButton) itemView.findViewById(R.id.buttonModif);
        buttonModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), VueCamera.class);
                intent.putExtra("id", c.getId());

                itemView.getContext().startActivity(intent);
            }
        });

        textViewView.setText(camera.getName());
        Picasso.get().load(camera.getIp()).centerCrop().fit().into(imageView);
    }
}
