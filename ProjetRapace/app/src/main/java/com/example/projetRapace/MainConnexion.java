package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainConnexion extends AppCompatActivity {

    private static final int CODE_ACTIVITY = 1;

    private TextView textPseudo;
    private TextView textMdp;
    private EditText editPseudo;
    private EditText editMdp;
    private Button buttonValider;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        //textPseudo = (TextView)findViewById(R.id.textRenduPseudo);
        //textMdp = (TextView)findViewById(R.id.textRenduMdp);
        editPseudo = (EditText)findViewById(R.id.pseudo);
        editMdp = (EditText)findViewById(R.id.password);
        buttonValider = (Button)findViewById(R.id.buttonValidate);

        /*
        UtilisateurManager m = new UtilisateurManager(this); // gestionnaire de la table "utilisateur"
        m.open(); // ouverture de la table en lecture/écriture


        // insertion. L'id sera attribué automatiquement par incrément
        m.addUtilisateur(new Utilisateur(0,"maya"));

    /*
        // modification du nom de l'utilisateur dont l'id est 1
        Utilisateur a=m.getUtilisateur(1);
        a.setNom_utilisateur("toto");
        m.modUtilisateur(a);

        // suppression
        m.supUtilisateur(a);
        */

        /*
        // Listing des enregistrements de la table
        Cursor c = m.getUtilisateurs();
        if (c.moveToFirst())
        {
            do {
                Log.d("test",
                        c.getInt(c.getColumnIndex(UtilisateurManager.KEY_ID_UTILISATEUR)) + "," +
                                c.getString(c.getColumnIndex(UtilisateurManager.KEY_NOM_UTILISATEUR))
                );
            }
            while (c.moveToNext());
        }
        c.close(); // fermeture du curseur

        // fermeture du gestionnaire
        m.close();

         */

        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainConnexion.this, MainCompte.class);


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
    }

    public void verifConnexion(Utilisateur u){
        intent.putExtra("PSEUDO", u.getPseudo_utilisateur());
        intent.putExtra("MDP", u.getMdp_utilisateur());

        startActivityForResult(intent, CODE_ACTIVITY);
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

    public void retourneUtilisateur(Utilisateur u){
        if(u.getId_utilisateur() != -1){
            //textPseudo.setText(u.getPseudo_utilisateur());
            Log.d("ID :", String.valueOf(u.getId_utilisateur()));
            //textMdp.setText(u.getMdp_utilisateur());
        }
    }
}
