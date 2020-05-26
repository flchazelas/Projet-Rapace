package com.example.projetRapace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainConnexion extends BaseActivity {

    private static final int CODE_ACTIVITY = 1;

    private TextView textPseudo;
    private TextView textMdp;
    private EditText editPseudo;
    private EditText editMdp;
    private Button buttonConnexion;
    //private Button buttonEnregistrement;
    private Intent intent;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        checkPermission();

        // Service de vérification de connexion
        intent = new Intent(MainConnexion.this, RapaceService.class);
        startService(intent);

        // Session Manager
        session = SessionManager.getInstance(this);
        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        //textPseudo = (TextView)findViewById(R.id.textRenduPseudo);
        //textMdp = (TextView)findViewById(R.id.textRenduMdp);
        editPseudo = (EditText)findViewById(R.id.pseudo);
        editMdp = (EditText)findViewById(R.id.password);
        buttonConnexion = (Button)findViewById(R.id.buttonConnexion);
        //buttonEnregistrement = (Button)findViewById(R.id.buttonRegister);

        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UtilisateurManagerDistant m = new UtilisateurManagerDistant(MainConnexion.this);
                //m.envoi("utilisateurs", new JSONArray());
                //int id = Integer.parseInt(editId.getText().toString());
                String pseudo = editPseudo.getText().toString();
                String mdp = editMdp.getText().toString();
                Utilisateur u = new Utilisateur(pseudo, mdp);
                m.envoi("connexion", u.convertionJSONArray());

                //startActivity(intent);
            }
        });
/*
        buttonEnregistrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainConnexion.this, MainEnregistrement.class);
                startActivity(intent);
            }
        });
*/
    }

    public void verifConnexion(Utilisateur u){
        if(u.getIsActif() == 1) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatee = format.format(now);

            if(u.getIsAdmin() == 1) {
                session.creationLoginSession(u.getPseudo_utilisateur(), dateFormatee, u.getId_utilisateur(), true);
            }
            else{
                session.creationLoginSession(u.getPseudo_utilisateur(), dateFormatee, u.getId_utilisateur(), false);
            }

            Intent intentVueCamera = new Intent(this, MainCardViewLocal.class);
            startActivity(intentVueCamera);
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainConnexion.this);

            alertDialog.setTitle(R.string.connection_failed);
            alertDialog.setMessage(R.string.account_disabled);

            alertDialog.setNegativeButton(R.string.return_, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), R.string.return_, Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
        }
    }

    public void echecConnexion(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainConnexion.this);

        alertDialog.setTitle(R.string.connection_failed);
        alertDialog.setMessage(R.string.password_or_username_wrong);

        alertDialog.setNegativeButton(R.string.return_, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), R.string.return_, Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }



    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!(checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            checkPermission();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.deconnexionSession();
    }
}
