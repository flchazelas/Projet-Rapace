package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainCardView extends AppCompatActivity {

    private RecyclerView recyclerView;

    private List<Local> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        //remplir la ville
        ajouterVilles();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //définit l'agencement des cellules, ici de façon verticale, comme une ListView
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //pour adapter en grille comme une RecyclerView, avec 2 cellules par ligne
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //puis créer un MyAdapter, lui fournir notre liste de villes.
        //cet adapter servira à remplir notre recyclerview
        recyclerView.setAdapter(new Adapter(cities));
    }

    private void ajouterVilles() {
        cities.add(new Local("Local 1","https://telesurveillance.securitas.fr/medium/W1siZiIsIjIwMTgvMDgvMjkvNDVhcmFnbTExa19jYW1lcmFfdmlkZW9zdXJ2ZWlsbGFuY2VfOTYwLmpwZyJdLFsicCIsInRodW1iIiwiOTYweDY0MD4iXV0/camera-videosurveillance-960.jpg?sha=ffb4cb9a3b8ac9ea"));
        cities.add(new Local("Local 2","https://www.cnil.fr/sites/default/files/styles/contenu-generique-visuel/public/thumbnails/image/video-commerces-828584278.jpg?itok=SsIbhMcB"));
    }
}
