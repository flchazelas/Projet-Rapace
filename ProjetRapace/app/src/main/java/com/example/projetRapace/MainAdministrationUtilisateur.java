package com.example.projetRapace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainAdministrationUtilisateur extends AppCompatActivity {
    private Intent intentSession;

    private  UtilisateurManagerDistant m;
    private ListView listView;
    private Button boutonDesactiver;
    private Button boutonSupprimer;
    private Button boutonAdmin;
    private Button boutonModification;
    private Button boutonAjout;
    private Intent intent;
    private String pseudo;
    private int isActif;
    private int isAdmin;
    private int id;

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
                intent = new Intent(MainAdministrationUtilisateur.this, MainAdministrationUtilisateur.class);
                startActivity(intent);
                SessionManager.getInstance(this).checkLogin();
                return true;

            case MENU_PROFIL:

                //redirection vers MainModificationUtilisateur
                intent = new Intent(MainAdministrationUtilisateur.this, MainModificationUtilisateur.class);
                intent.putExtra("PSEUDO", SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO));
                startActivityForResult(intent, CODE_ACTIVITY);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_administration_utilisateur);

        intentSession = new Intent(MainAdministrationUtilisateur.this, RapaceService.class);
        startService(intentSession);
        SessionManager.getInstance(this).checkLogin();

        m = new UtilisateurManagerDistant(MainAdministrationUtilisateur.this);
        String pseudo = SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO);
        Utilisateur u = new Utilisateur(pseudo, "password");
        m.envoi("recupUtilisateurs", u.convertionJSONArray());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(intentSession);
        SessionManager.getInstance(this).checkLogin();

        m = new UtilisateurManagerDistant(MainAdministrationUtilisateur.this);
        String pseudo = SessionManager.getInstance(this).getDonneesSession().get(SessionManager.KEY_PSEUDO);
        Utilisateur u = new Utilisateur(pseudo, "password");
        m.envoi("recupUtilisateurs", u.convertionJSONArray());
    }

    public void affichage(final List<Utilisateur> listeU){
        listView = findViewById(R.id.list);
        boutonDesactiver = findViewById(R.id.desactiver);
        boutonSupprimer = findViewById(R.id.supprimer);
        boutonAdmin = findViewById(R.id.admin);
        boutonModification = findViewById(R.id.modif);
        boutonAjout = findViewById(R.id.add_user);

        boutonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainAdministrationUtilisateur.this, MainEnregistrement.class);
                startActivity(intent);
            }
        });


        final String[] strings = new String[listeU.size()];
        for (int i=0; i<listeU.size(); i++){
            strings[i] = listeU.get(i).getPseudo_utilisateur();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings){
            /*
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null)
                {
                    LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //le layout représentant la ligne dans le listView
                    view = li.inflate(R.layout.ligne_liste_num, null);
                }
                TextView text = (TextView) view.findViewById(R.id.ligne);

                // maintenant tu peux travailler ta view qui correspond à une ligne
                // si la ligne correspond à l'élément sélectionnée préalablement tu change son fond .
                if(listeU.get(position).getIsAdmin() == 1){
                    text.setText(listeU.get(position).getPseudo_utilisateur().concat("     ("+R.string.admin+") "));
                    text.setTextColor(Color.RED);
                }
                else if(listeU.get(position).getIsActif() == 0){
                    text.setText(listeU.get(position).getPseudo_utilisateur().concat("     ("+R.string.disable+") "));
                    text.setBackgroundColor(Color.GRAY);
                }
                else{
                    text.setText(listeU.get(position).getPseudo_utilisateur());
                }


                return view;

            }*/

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
                pseudo = adapterView.getItemAtPosition(position).toString();
                isActif = listeU.get(position).getIsActif();
                isAdmin = listeU.get(position).getIsAdmin();
                id = listeU.get(position).getId_utilisateur();

                boutonDesactiver.setClickable(true);
                boutonDesactiver.setBackgroundTintList(getColorStateList(R.color.colorFullWhite));
                boutonDesactiver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        m = new UtilisateurManagerDistant(MainAdministrationUtilisateur.this);
                        Utilisateur u = new Utilisateur(0, pseudo, "test", isAdmin, isActif);
                        m.envoi("changeIsActif", u.convertionJSONArray());
                    }
                });

                boutonSupprimer.setClickable(true);
                boutonSupprimer.setBackgroundTintList(getColorStateList(R.color.colorFullWhite));
                boutonSupprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        m = new UtilisateurManagerDistant(MainAdministrationUtilisateur.this);
                        Utilisateur u = new Utilisateur(id, pseudo, "test", isAdmin, isActif);
                        m.envoi("supprimerUtilisateur", u.convertionJSONArray());
                    }
                });

                boutonAdmin.setClickable(true);
                boutonAdmin.setBackgroundTintList(getColorStateList(R.color.colorFullWhite));
                boutonAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        m = new UtilisateurManagerDistant(MainAdministrationUtilisateur.this);
                        Utilisateur u = new Utilisateur(0, pseudo, "test", isAdmin, isActif);
                        m.envoi("changeIsAdmin", u.convertionJSONArray());
                    }
                });

                boutonModification.setClickable(true);
                boutonModification.setBackgroundTintList(getColorStateList(R.color.colorFullWhite));
                boutonModification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent = new Intent(MainAdministrationUtilisateur.this, MainModificationUtilisateur.class);
                        intent.putExtra("PSEUDO", pseudo);
                        startActivityForResult(intent, CODE_ACTIVITY);
                    }
                });

            }
        });
    }
}
