package com.example.projetRapace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Local.Local;
import com.example.projetRapace.streamlib.MjpegInputStream;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
        if(local.getImage() == null){
            Picasso.get().load(Local.defaultImage).centerCrop().fit().into(imageView);
            return;
        }
        final String camIP = local.getImage();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.d("bitmap", "(start)");
                MjpegInputStream inputStream = MjpegInputStream.read(camIP);
                try {
                    final Bitmap b = inputStream.readMjpegFrame();
                    Activity context = (Activity) itemView.getContext();
                    context.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            imageView.setImageBitmap(b);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("bitmap", "(done)");
            }
        });
        thread.start();
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

        final Camera cam = camera;
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.d("bitmap", "(start)");
                MjpegInputStream inputStream = MjpegInputStream.read(cam.getIp());
                try {
                    final Bitmap b = inputStream.readMjpegFrame();
                    Activity context = (Activity) itemView.getContext();
                    context.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            imageView.setImageBitmap(b);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("bitmap", "(done)");
            }
        });
        thread.start();

        //Picasso.get().load(camera.getIp()).centerCrop().fit().into(imageView);
    }
}
