package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetRapace.Local.Local;
import com.example.projetRapace.Local.LocalDBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VueLocal extends AppCompatActivity {
    private static final int MENU_QUIT = 1;
    private SessionManager session;

    /**
     * Création d'un menu d'Items dans la Barre du Haut de l'application
     * Ajout de l'option de déconnexion
     * */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, R.string.logout);
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
                finish();
                return true;
        }
        return false;
    }
    private ProgressDialog mProgressDialog;

    private boolean removedone = false;
    private boolean removeresult = false;

    private int id_local;
    private Local local;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.maquette_gestion_local);
        if(intent != null) {

            int id = intent.getIntExtra("id",-1);
            if(id != -1){
                id_local = id;
                loadPage(id_local);
            }
        }
    }

    public void cleanVideoScrollview(){
        ((LinearLayout) findViewById(R.id.userContainer)).removeAllViews();
    }

    private void fillUserList(int id){
        final LinearLayout layout = (LinearLayout) findViewById(R.id.userContainer);
        final Activity context = this;
        final int id_local = id;

        LocalDBManager.LocalDBCallbackInterface callbackVideo = new LocalDBManager.LocalDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("fillUserList", "(onQueryFinished) -> "+ operation);
                if(operation.equals(LocalDBManager.LOCAL_DB_GET_USERS_PERMISSIONS)){
                    try {
                        Log.d("fillUserList", "(get users permissions) -> "+ output);
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();

                        if(output.equals("QUERY_ERROR")){
                            Log.d("fillUserList", "ERROR");
                            return;
                        }


                        JSONArray jsonResult = new JSONArray(output);
                        JSONObject jsonObj;
                        Log.d("CameraListView", "(retour CAMERA_DB_GETALL) -> "+ jsonResult);

                        for (int i = 0; i < jsonResult.length(); ++i) {
                            try {
                                jsonObj = jsonResult.getJSONObject(i);
                                final int id_user = jsonObj.getInt("id");
                                String name =  jsonObj.getString("name");
                                boolean permission = (jsonObj.getInt("permission") == 1);

                                layout.addView(buildLine(name, permission,  id_user,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mProgressDialog = ProgressDialog.show(context, "Changing Permission", "Changing User Permission");
                                            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                                            LocalDBManager.LocalDBCallbackInterface callbackVideo = new LocalDBManager.LocalDBCallbackInterface() {
                                                @Override
                                                public void onQueryFinished(String operation, String output) {
                                                    Log.d("fillUserList", "(onQueryFinished) -> "+ operation);
                                                    if(operation.equals(LocalDBManager.LOCAL_DB_CHANGE_USERS_PERMISSIONS)){
                                                        try {
                                                            Log.d("fillUserList", "(retour LOCAL_DB_CHANGE_USERS_PERMISSIONS) -> " + output);

                                                            if (mProgressDialog != null)
                                                                mProgressDialog.dismiss();

                                                            if(output.equals("CHANGE_SUCCESS"))
                                                                loadPage(id_local);
                                                            else
                                                                Toast.makeText(context, "Erreur lors du changement de permission.\nVeuillez réessayer ou contacter un administrateur.",Toast.LENGTH_LONG).show();

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            };
                                            LocalDBManager.changeUserPermission(callbackVideo,id_local,id_user);
                                        }
                                    }
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        LocalDBManager.getUsersPermissions(callbackVideo,id_local);

    }

    private RelativeLayout buildLine(String name, boolean permission, int id, ImageView.OnClickListener viewListener) {
        RelativeLayout ly = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(5,5,5,5);
        ly.setBackgroundColor(Color.rgb(255, 255, 255));
        ly.setLayoutParams(layoutParams);

        TextView nameTv = new TextView(this);
        nameTv.setText(name);
        nameTv.setPadding(5,5,5,5);
        nameTv.setTypeface(Typeface.DEFAULT_BOLD);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        nameTv.setLayoutParams(layoutParams);
        ly.addView(nameTv);

        TextView autorise = new TextView(this);
        autorise.setText("autorisé : ");
        autorise.setPadding(5,5,5,5);
        autorise.setTypeface(null, Typeface.ITALIC);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0,0,15,0);
        autorise.setLayoutParams(layoutParams);
        ly.addView(nameTv);

        ImageView viewImgBut = new ImageButton(this);
        if (permission)
            viewImgBut.setImageResource(android.R.drawable.checkbox_on_background);
        else
            viewImgBut.setImageResource(android.R.drawable.checkbox_off_background);
        viewImgBut.setOnClickListener(viewListener);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0,0,15,0);
        viewImgBut.setLayoutParams(layoutParams);
        viewImgBut.setAdjustViewBounds(true);
        viewImgBut.setScaleType(ImageView.ScaleType.FIT_XY);
        ly.addView(viewImgBut);

        return ly;
    }

    private void loadPage(int id) {
        final Activity context = this;

        mProgressDialog = ProgressDialog.show(this, "Downloading", "Downloading Data ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

        LocalDBManager.LocalDBCallbackInterface callbackVideo = new LocalDBManager.LocalDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("loadPage", "(onQueryFinished) -> "+ operation);
                if(operation.equals(LocalDBManager.LOCAL_DB_GETBYID)){
                    try {
                        Log.d("loadPage", "(retour LOCAL_DB_GETBYID) -> " + output);
                        JSONObject jsonResult = new JSONObject(output);
                        local = Local.localFromJSON(jsonResult);
                        Log.d("VueCamera", "(retour LOCAL_DB_GETBYID) -> "+ local);
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        ((TextView) findViewById(R.id.textView6)).setText(local.getName());

                        ((Button) findViewById(R.id.boutonSupprimer)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removedone = false;
                                removeresult = false;

                                final Local l = local;

                                LocalDBManager.LocalDBCallbackInterface callbackImage = new LocalDBManager.LocalDBCallbackInterface() {
                                    @Override
                                    public void onQueryFinished(String operation, String output) {
                                        Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                                        if(operation.equals(LocalDBManager.LOCAL_DB_REMOVE)){
                                            try {
                                                Log.d("VueCamera", "(retour LOCAL_DB_REMOVE) -> " + output);
                                                if(output.equals("REMOVE_SUCCESSFUL"))
                                                    removeresult = true;
                                                else
                                                    removeresult = false;
                                                removedone = true;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };
                                LocalDBManager.removeLocal(callbackImage,l);

                                Thread checkLoading = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        while(!(removedone))
                                            Log.d("VueCamera", "(Waiting for loading to endd) -> (removedone : " + removedone);

                                        context.runOnUiThread(new Runnable(){
                                            @Override
                                            public void run() {
                                                if(removeresult)
                                                    finish();
                                                if (mProgressDialog != null)
                                                    mProgressDialog.dismiss();
                                            }
                                        });
                                    }
                                });

                                checkLoading.start();
                            }
                        });

                        fillUserList(local.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        LocalDBManager.getById(callbackVideo,id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.deconnexionSession();
    }
}
