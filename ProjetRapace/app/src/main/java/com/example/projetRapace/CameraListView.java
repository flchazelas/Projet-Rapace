package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Camera.CameraDBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CameraListView extends BaseActivity {
    public Activity context;
    public TableLayout mainTable;
    public CameraDBManager cameraAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list_view);
        context = this;
        mainTable = findViewById(R.id.mainTable);
        CameraDBManager.CameraDBCallbackInterface callback = new CameraDBManager.CameraDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("CameraListView", "(onQueryFinished) -> "+ operation);
                if(operation.equals(CameraDBManager.CAMERA_DB_GETALL)){
                    try {
                        Log.d("CameraListView", "(retour CAMERA_DB_GETALL) -> "+ output);
                        JSONArray jsonResult = new JSONArray(output);
                        Log.d("CameraListView", "(retour CAMERA_DB_GETALL) -> "+ Camera.camerasFromJSON(jsonResult));
                        for(Camera c : Camera.camerasFromJSON(jsonResult))
                            addCameraToList(c);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        CameraDBManager.getAll(callback);
    }

    public void addCameraToList(Camera c){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 5);

        TableRow tbRow = new TableRow(this);
        tbRow.setLayoutParams(lp);

        Button button  = new Button(this);
        button.setText(c.getName());

        final int idCamera = c.getId();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VueCamera.class);

                intent.putExtra("id", idCamera);
                startActivity(intent);
            }
        });
        tbRow.addView(button);
        mainTable.addView(tbRow);
    }
}
