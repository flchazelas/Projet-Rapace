package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;

public class MainEnregistrement extends AppCompatActivity {

    private TextView textPseudo;
    private TextView textMdp;
    private EditText editPseudo;
    private EditText editMdp;
    private Button buttonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement);

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
                Intent intent = new Intent(MainEnregistrement.this, MainCompte.class);


                UtilisateurManagerDistant m = new UtilisateurManagerDistant(MainEnregistrement.this);
                //m.envoi("utilisateurs", new JSONArray());
                //int id = Integer.parseInt(editId.getText().toString());
                String pseudo = editPseudo.getText().toString();
                String mdp = editMdp.getText().toString();
                Utilisateur u = new Utilisateur(pseudo, mdp);
                m.envoi("ajout", u.convertionJSONArray());

                startActivity(intent);
            }
        });
    }

    public void retourneUtilisateur(Utilisateur u){
        if(u.getId_utilisateur() != -1){
            //textPseudo.setText(u.getPseudo_utilisateur());
            Log.d("ID :", String.valueOf(u.getId_utilisateur()));
            //textMdp.setText(u.getMdp_utilisateur());
        }
    }
}
