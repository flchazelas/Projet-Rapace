package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.projetRapace.ImageVideo.Image;
import com.example.projetRapace.ImageVideo.Video;
import com.example.projetRapace.Local.Local;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainAlerteDesactive extends AppCompatActivity {

    private Intent intent;
    private ListView listViewUser;
    private ListView listViewActions;
    private ListView listViewIV;
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alerte_active);

        // Lancement du Service de vérification de connexion
        intentService = new Intent(MainAlerteDesactive.this, RapaceService.class);
        startService(intentService);

        listViewActions = findViewById(R.id.listActions);
        listViewIV = findViewById(R.id.listIV);

        //A enlever quand tu auras récup la liste des actions
        List<Action> listeA = new ArrayList<>();
        listeA.add(new Action("Alarme", "15/04/2020", "Fred", "Lancement d'une alarme"));
        listeA.add(new Action("Ignorer", "23/04/2020", "Daniel", "Ignorer l'alerte"));
        listeA.add(new Action("Capture", "07/05/2019", "Gile", "Capture d'une image"));
        this.recupListeAction(listeA);

        //A enlever quand tu auras récup la liste des actions
        List<Object> listeI = new ArrayList<>();
        listeI.add(new Image(0, "https://dsrhsjyd/sdb/sdbt.com"));
        listeI.add(new Video(1, "https://sgbdb/sqsgsbtsdb/srgrsdbt.com"));
        this.recupListeIV(listeI);
    }


    public void recupListeAction(final List<Action> listeA){

        final String[] strings = new String[listeA.size()];
        for (int i=0; i<listeA.size(); i++){
            strings[i] = listeA.get(i).getType()+"%"+listeA.get(i).getDateEffectuee()+"%"+listeA.get(i).getUtilisateur()+"%"+listeA.get(i).getDescription();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null)
                {
                    LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //le layout représentant la ligne dans le listView
                    view = li.inflate(R.layout.ligne_liste_actions, null);
                }

                final String[] s = strings[position].split("%");

                TextView text = (TextView) view.findViewById(R.id.textType);
                text.setText(s[0]);

                TextView text1 = (TextView) view.findViewById(R.id.textDate);
                text1.setText(s[1]);

                TextView text2 = (TextView) view.findViewById(R.id.textUser);
                text2.setText(s[2]);

                TextView text3 = (TextView) view.findViewById(R.id.textDescription);
                text3.setText(s[3]);

                return view;
            }};

        listViewActions.setAdapter(adapter);
    }


    public void recupListeIV(final List<Object> listeI){

        final String[] strings = new String[listeI.size()];
        for (int i=0; i<listeI.size(); i++){
            Log.d("LIST : ", String.valueOf(listeI.get(i).getClass().toString().equals(Video.class.toString())));
            if(listeI.get(i).getClass().toString().equals(Image.class.toString())){
                Image l = (Image) listeI.get(i);
                strings[i] = l.getId()+"%"+l.getChemin();
            }
            else if(listeI.get(i).getClass().toString().equals(Video.class.toString())){
                Video l = (Video) listeI.get(i);
                strings[i] = l.getId()+"%"+l.getChemin();
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null)
                {
                    LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //le layout représentant la ligne dans le listView
                    view = li.inflate(R.layout.ligne_liste_iv, null);
                }

                final String[] s = strings[position].split("%");

                TextView text = (TextView) view.findViewById(R.id.textType);
                text.setText(s[0]);

                TextView text1 = (TextView) view.findViewById(R.id.textDate);
                text1.setText(s[1]);

                return view;
            }};

        listViewIV.setAdapter(adapter);
    }
}