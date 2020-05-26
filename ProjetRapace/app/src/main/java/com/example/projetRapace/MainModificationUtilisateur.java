package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainModificationUtilisateur extends AppCompatActivity {

    private EditText textPseudo;
    private EditText textMdp;
    private EditText textNum;
    private Button validate;
    private Utilisateur u;
    private UtilisateurManagerDistant m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_modification_utilisateur);

        textPseudo = findViewById(R.id.pseudo);
        textMdp = findViewById(R.id.password);
        textNum = findViewById(R.id.numTel);
        validate = findViewById(R.id.buttonValidate);

        m = new UtilisateurManagerDistant(MainModificationUtilisateur.this);

        Intent intent = getIntent();

        //Récupération des champs de données
        if(intent != null) {
            if (intent.hasExtra("PSEUDO")) {
                Utilisateur u = new Utilisateur(intent.getStringExtra("PSEUDO"), "password");
                m.envoi("recupUtilisateur", u.convertionJSONArray());
            }
        }
    }

    public void affichage(Utilisateur utilisateur){
        u = utilisateur;
        textPseudo.setText(u.getPseudo_utilisateur());
        textMdp.setText(u.getMdp_utilisateur());
        textNum.setText(u.getPhone());
        Log.d("UTILISATEUR", String.valueOf(u.getId_utilisateur()));
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilisateur uti = new Utilisateur(u.getId_utilisateur() , textPseudo.getText().toString(), textMdp.getText().toString(), textNum.getText().toString());
                m.envoi("updateProfil", uti.convertionJSONArray());
            }
        });
    }

    public void changements(Utilisateur utilisateur){
        SessionManager session = SessionManager.getInstance(this);
        if(Integer.valueOf(session.getDonneesSession().get(SessionManager.KEY_ID)) == utilisateur.getId_utilisateur()){
            session.setPseudo(utilisateur.getPseudo_utilisateur());
        }
        finish();
        /*Intent intent = new Intent(MainModificationUtilisateur.this, MainCardViewLocal.class);
        startActivity(intent);*/
    }
}
