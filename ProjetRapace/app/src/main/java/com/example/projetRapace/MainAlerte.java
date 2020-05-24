package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainAlerte extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alerte);
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

            case R.id.appellerNumUtilisateur:
                //Donner le numéro à appeller ==>
                uri = Uri.parse("tel:"+"0625418526");
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
