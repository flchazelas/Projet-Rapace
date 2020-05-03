package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

public class MainEnregistrement extends AppCompatActivity {

    private TextView textPseudo;
    private TextView textMdp;
    private EditText editPseudo;
    private EditText editMdp;
    private EditText editMdpConfirm;
    private Button buttonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement);

        //textPseudo = (TextView)findViewById(R.id.textRenduPseudo);
        //textMdp = (TextView)findViewById(R.id.textRenduMdp);
        editPseudo = (EditText)findViewById(R.id.pseudo);
        editMdp = (EditText)findViewById(R.id.password);
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
                    Utilisateur u = new Utilisateur(pseudo, mdp);
                    m.envoi("ajout", u.convertionJSONArray());
                }
            }
        });
    }

    public void verifConnexion(){
        Intent intentVueCamera = new Intent(this, CameraListView.class);
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


    public void retourneUtilisateur(Utilisateur u){
        if(u.getId_utilisateur() != -1){
            //textPseudo.setText(u.getPseudo_utilisateur());
            Log.d("ID :", String.valueOf(u.getId_utilisateur()));
            //textMdp.setText(u.getMdp_utilisateur());
        }
    }
}
