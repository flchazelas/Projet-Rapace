package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonConnexion;
    private Button buttonEnregistrer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonEnregistrer = (Button) findViewById(R.id.buttonRegister);
        buttonConnexion = (Button) findViewById(R.id.buttonLogin);

        buttonEnregistrer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainEnregistrement.class);

                startActivity(intent);
            }
        });

        buttonConnexion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainConnexion.class);

                startActivity(intent);
            }
        });
    }
}
