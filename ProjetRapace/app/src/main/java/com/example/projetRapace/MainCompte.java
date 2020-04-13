package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainCompte extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);

        Intent intent = getIntent();

        //Récupération des champs de données
        if(intent != null) {
            textView = (TextView)findViewById(R.id.bonjour);
            if (intent.hasExtra("PSEUDO")) {
                textView.setText(intent.getStringExtra("PSEUDO"));
            }
            if (intent.hasExtra("MDP")) {
                textView.setText(intent.getStringExtra("MDP"));
            }
        }
    }
}
