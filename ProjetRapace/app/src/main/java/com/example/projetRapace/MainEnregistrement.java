package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainEnregistrement extends BaseActivity {

    private TextView textPseudo;
    private TextView textMdp;
    private TextView textNum;
    private EditText editPseudo;
    private EditText editMdp;
    private EditText editMdpConfirm;
    private EditText editNum;
    private Button buttonValider;
    private SessionManager session;
    private Utilisateur utilisateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement);

        // Session Manager
        session = new SessionManager(getApplicationContext());
        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        //textPseudo = (TextView)findViewById(R.id.textRenduPseudo);
        //textMdp = (TextView)findViewById(R.id.textRenduMdp);
        editPseudo = (EditText)findViewById(R.id.pseudo);
        editMdp = (EditText)findViewById(R.id.password);
        editNum = (EditText)findViewById(R.id.numTel);
        editMdpConfirm = (EditText)findViewById(R.id.password_confirm);
        buttonValider = (Button)findViewById(R.id.buttonValidate);

        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UtilisateurManagerDistant m = new UtilisateurManagerDistant(MainEnregistrement.this);
                //m.envoi("utilisateurs", new JSONArray());
                //int id = Integer.parseInt(editId.getText().toString());
                String pseudo = editPseudo.getText().toString();
                String mdp = editMdp.getText().toString();
                String mdp_confirm = editMdpConfirm.getText().toString();
                int num = Integer.parseInt(editNum.getText().toString());
                if(!mdp.equals(mdp_confirm)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainEnregistrement.this);

                    alertDialog.setTitle(R.string.register_failed);
                    alertDialog.setMessage(R.string.username_already_used);

                    alertDialog.setNegativeButton(R.string.return_, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), R.string.return_, Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.show();
                }
                else{
                    utilisateur = new Utilisateur(0, pseudo, mdp, num);
                    m.envoi("ajout", utilisateur.convertionJSONArray());
                }
            }
        });
    }

    public void verifConnexion(){
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatee = format.format(now);
        session.creationLoginSession(utilisateur.getPseudo_utilisateur(), dateFormatee,utilisateur.getId_utilisateur(), false);

        Intent intentVueCamera = new Intent(this, MainCardViewLocal.class);
        startActivity(intentVueCamera);
    }

    public void echecConnexion(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainEnregistrement.this);

        alertDialog.setTitle(R.string.register_failed);
        alertDialog.setMessage(R.string.username_already_used);

        alertDialog.setNegativeButton(R.string.return_, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), R.string.return_, Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }
}
