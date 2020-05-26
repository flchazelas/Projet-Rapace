package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainModificationUtilisateur extends AppCompatActivity {
    private Intent intentSession;

    private EditText textPseudo;
    private EditText textMdp;
    private EditText textNum;
    private Button validate;
    private Intent intent;
    private Utilisateur u;
    private UtilisateurManagerDistant m;

    private static final int CODE_ACTIVITY = 1;
    private static final int MENU_QUIT = 1;
    private static final int MENU_ADMIN = 2;
    private static final int MENU_PROFIL = 3;

    /**
     * Création d'un menu d'Items dans la Barre du Haut de l'application
     * Ajout de l'option de déconnexion
     * */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, R.string.logout);
        if(SessionManager.getInstance(this).isAdmin()) {
            menu.add(0, MENU_ADMIN, 0, R.string.user_administration);
        }
        menu.add(0, MENU_PROFIL, 0, R.string.user_profile);
        return true;
    }

    /**
     * Gère le menu d'Items
     * Pour l'appuie de Déconnexion appel finish() qui ferme l'activité courante
     * */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_QUIT:

                //ferme l'activité courante
                SessionManager.getInstance(this).deconnexionSession();
                finish();
                return true;

            case MENU_ADMIN:

                //redirection vers MainAdministrationUtilisateur
                intent = new Intent(MainModificationUtilisateur.this, MainAdministrationUtilisateur.class);
                startActivity(intent);
                SessionManager.getInstance(this).checkLogin();
                return true;

            case MENU_PROFIL:

                //redirection vers MainModificationUtilisateur
                intent = new Intent(MainModificationUtilisateur.this, MainModificationUtilisateur.class);
                intent.putExtra("PSEUDO", SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO));
                startActivityForResult(intent, CODE_ACTIVITY);
                return true;
        }
        return false;
    }

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

        intentSession = new Intent(MainModificationUtilisateur.this, RapaceService.class);
        startService(intentSession);
        SessionManager.getInstance(this).checkLogin();

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
