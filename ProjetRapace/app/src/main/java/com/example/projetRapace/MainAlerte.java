package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainAlerte extends AppCompatActivity {

    private Intent intent;
    private ListView listView;
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alerte);

        // Lancement du Service de vérification de connexion
        intentService = new Intent(MainAlerte.this, RapaceService.class);
        startService(intentService);

        listView = findViewById(R.id.list);
/*
        final String[] strings = new String[listeU.size()];
        for (int i=0; i<listeU.size(); i++){
            strings[i] = listeU.get(i).getPseudo_utilisateur();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                if(listeU.get(position).getIsAdmin() == 1){
                    textView.setText(textView.getText().toString()+"     (Admin) ");
                    textView.setTextColor(Color.RED);
                }

                else if(listeU.get(position).getIsActif() == 0){
                    textView.setText(textView.getText().toString()+"     (Désactivé) ");
                    textView.setTextColor(Color.GRAY);
                }
                else{
                    textView.setTextColor(Color.BLACK);
                }

                return textView;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Donner le numéro à appeller ==>
                Uri uri = Uri.parse("tel:"+"0625468526");
                intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;
            }
        });*/
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.visuReel:


                break;

            case R.id.appellerNumLocal:

                //Donner le numéro à appeller ==>
                Uri uri = Uri.parse("tel:"+"0625468526");
                intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;

            case R.id.declencherAlarme:


                break;

            case R.id.ignorerAlerte:


                break;

            case R.id.historiqueActions:


                break;
        }
    }
}
