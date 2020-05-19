package com.example.projetRapace;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetRapace.Camera.Camera;
import com.example.projetRapace.Camera.CameraDBManager;
import com.example.projetRapace.ImageVideo.Image;
import com.example.projetRapace.ImageVideo.ImageDBManager;
import com.example.projetRapace.ImageVideo.Video;
import com.example.projetRapace.ImageVideo.VideoDBManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class VueCamera extends AppCompatActivity {
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
    private String addr_serv = "http://vps814672.ovh.net/";
    private boolean doneGetCamera;
    private Camera c;
    private ProgressDialog mProgressDialog;

    private boolean removedone = false;
    private boolean removeresult = false;

    private boolean CameraLoading = false;
    private boolean VideosLoading = false;
    private boolean ImagesLoading = false;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();

        if(intent != null) {
            setContentView(R.layout.maquette_gestion_camera);

            int id = intent.getIntExtra("id",-1);
            if(id != -1){
                loadPage(id);
            }
        }
    }

    public void cleanVideoScrollview(){
        ((LinearLayout) findViewById(R.id.scrollVideo)).removeAllViews();
    }

    public void cleanImageScrollview(){
        ((LinearLayout) findViewById(R.id.scrollImage)).removeAllViews();
    }

    private void fillVideoScrollview(ArrayList<Video> videoList){
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollVideo);
        for (Video v : videoList) {
            String name = v.getChemin().split("/")[2];
            final Activity context = this;
            final Video video = v;
            layout.addView(buildLine(name, "", v.getId(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, VideoView.class);

                            intent.putExtra("ip", addr_serv + video.getChemin());
                            startActivity(intent);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mProgressDialog = ProgressDialog.show(context, "Removing", "Removing Selected Data ...");
                            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                            VideoDBManager.VideoDBCallbackInterface callbackVideo = new VideoDBManager.VideoDBCallbackInterface() {
                                @Override
                                public void onQueryFinished(String operation, String output) {
                                    Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                                    if(operation.equals(CameraDBManager.CAMERA_DB_REMOVE)){
                                        try {
                                            Log.d("VueCamera", "(remove Camera) -> "+ output);
                                            if (mProgressDialog != null)
                                                mProgressDialog.dismiss();

                                            if(output.equals("REMOVE_SUCCESSFUL"))
                                                loadPage(c.getId());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            VideoDBManager.removeVideo(callbackVideo,video);
                        }
                    }
            ));
        }
    }

    private void fillImageScrollview(ArrayList<Image> imageList){
        LinearLayout layout = (LinearLayout) findViewById(R.id.scrollImage);
        for (Image i : imageList) {
            //Log.d("VueCamera",i.getChemin().split("/")[2]);
            String name = i.getChemin().split("/")[2];
            final Activity context = this;
            final Image image = i;
            layout.addView(buildLine(name, "", i.getId(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ImageViewActivity.class);

                            intent.putExtra("ip", addr_serv + image.getChemin());
                            startActivity(intent);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mProgressDialog = ProgressDialog.show(context, "Removing", "Removing Selected Data ...");
                            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside

                            ImageDBManager.ImageDBCallbackInterface callbackVideo = new ImageDBManager.ImageDBCallbackInterface() {
                                @Override
                                public void onQueryFinished(String operation, String output) {
                                    Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                                    if(operation.equals(ImageDBManager.IMAGE_DB_REMOVE)){
                                        try {
                                            Log.d("VueCamera", "(remove Camera) -> "+ output);
                                            if (mProgressDialog != null)
                                                mProgressDialog.dismiss();

                                            if(output.equals("REMOVE_SUCCESSFUL"))
                                                loadPage(c.getId());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            ImageDBManager.removeImage(callbackVideo,image);
                        }
                    }
            ));
        }
    }

    private LinearLayout buildLine(String name, String author, int id, ImageButton.OnClickListener viewListener, ImageButton.OnClickListener deleteListener){
        LinearLayout ly = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(5,5,5,5);
        ly.setBackgroundColor(Color.rgb(150, 150, 150));
        ly.setOrientation(LinearLayout.HORIZONTAL);
        ly.setLayoutParams(layoutParams);

        TextView nameTv = new TextView(this);
        nameTv.setText(name);
        nameTv.setPadding(10,10,0,0);
        nameTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        ly.addView(nameTv);

        if(author != ""){
            TextView authorTv = new TextView(this);
            authorTv.setText("capturée par "+ author);
            authorTv.setPadding(0,0,75,0);
            authorTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            authorTv.setTextSize(12);
            authorTv.setTextAppearance(Typeface.ITALIC);
            ly.addView(authorTv);
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        ImageButton viewImgBut = new ImageButton(this);
        viewImgBut.setLayoutParams(new LinearLayout.LayoutParams((int) metrics.density*15, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        viewImgBut.setImageResource(R.drawable.view_icon);
        viewImgBut.setColorFilter(Color.rgb(0, 0, 0), PorterDuff.Mode.SRC_ATOP);
        viewImgBut.setOnClickListener(viewListener);
        ly.addView(viewImgBut);

        ImageButton deleteImgBut = new ImageButton(this);
        deleteImgBut.setLayoutParams(new LinearLayout.LayoutParams((int) metrics.density*15, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        deleteImgBut.setImageResource(R.drawable.delete_icon);
        deleteImgBut.setColorFilter(Color.rgb(0, 0, 0), PorterDuff.Mode.SRC_ATOP);
        deleteImgBut.setOnClickListener(deleteListener);
        ly.addView(deleteImgBut);

        return ly;
    }

    private void loadPage(int id){
        final Activity context = this;
        c = null;

        mProgressDialog = ProgressDialog.show(this, "Downloading", "Downloading Data ...");
        mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
        CameraLoading = false;
        VideosLoading = false;
        ImagesLoading = false;


        CameraDBManager.CameraDBCallbackInterface callbackCamera = new CameraDBManager.CameraDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(CameraDBManager.CAMERA_DB_GETBYID)){
                    try {
                        Log.d("VueCamera", "(retour CAMERA_DB_GETBYID) -> "+ output);
                        JSONObject jsonResult = new JSONObject(output);
                        c = Camera.cameraFromJSON(jsonResult);
                        Log.d("VueCamera", "(retour CAMERA_DB_GETBYID) -> "+ c);

                        TextView tw = (TextView) findViewById(R.id.NomCamera);
                        tw.setText(c.getName());


                        Picasso.get().load(c.getIp()).centerCrop().fit().into((ImageView) findViewById(R.id.previewDirect));

                        ((Button) findViewById(R.id.boutonSupprimer)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removedone = false;
                                removeresult = false;

                                final Camera cam = c;

                                CameraDBManager.CameraDBCallbackInterface callbackImage = new CameraDBManager.CameraDBCallbackInterface() {
                                    @Override
                                    public void onQueryFinished(String operation, String output) {
                                        Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                                        if(operation.equals(CameraDBManager.CAMERA_DB_REMOVE)){
                                            try {
                                                Log.d("VueCamera", "(retour IMAGE_DB_GETBYCAMERA) -> " + output);
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
                                CameraDBManager.removeCamera(callbackImage,cam);

                                Thread checkLoading = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        while(!(removedone))
                                            Log.d("VueCamera", "(Waiting for loading to endd) -> (CameraLoading : " + CameraLoading + "); (VideosLoading : " + VideosLoading + "); (ImagesLoading : " +ImagesLoading+")");

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


                        ((Button) findViewById(R.id.boutonDirect)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, CameraView.class);

                                intent.putExtra("id", c.getId());
                                intent.putExtra("ip", c.getIp());
                                startActivity(intent);
                            }
                        });

                        ((Button) findViewById(R.id.boutonRetour)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });

                        CameraLoading = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        CameraDBManager.getById(callbackCamera,id);

        final ArrayList<Video> videoList = new ArrayList<Video>();
        final ArrayList<Image> imageList = new ArrayList<Image>();;

        VideoDBManager.VideoDBCallbackInterface callbackVideo = new VideoDBManager.VideoDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(VideoDBManager.VIDEO_DB_GETBYCAMERA)){
                    try {
                        Log.d("VueCamera", "(retour VIDEO_DB_GETBYCAMERA) -> " + output);
                        videoList.addAll(Video.videosFromJSON(new JSONArray(output)));

                        VideosLoading = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        VideoDBManager.getByCamera(callbackVideo,id);

        ImageDBManager.ImageDBCallbackInterface callbackImage = new ImageDBManager.ImageDBCallbackInterface() {
            @Override
            public void onQueryFinished(String operation, String output) {
                Log.d("VueCamera", "(onQueryFinished) -> "+ operation);
                if(operation.equals(ImageDBManager.IMAGE_DB_GETBYCAMERA)){
                    try {
                        Log.d("VueCamera", "(retour IMAGE_DB_GETBYCAMERA) -> " + output);
                        imageList.addAll(Image.imagesFromJSON(new JSONArray(output)));

                        ImagesLoading = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ImageDBManager.getByCamera(callbackImage,id);

        cleanVideoScrollview();
        cleanImageScrollview();

        Thread checkLoading = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!(CameraLoading && VideosLoading && ImagesLoading))
                    Log.d("VueCamera", "(Waiting for loading to endd) -> (CameraLoading : " + CameraLoading + "); (VideosLoading : " + VideosLoading + "); (ImagesLoading : " +ImagesLoading+")");

                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                        fillVideoScrollview(videoList);
                        fillImageScrollview(imageList);
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                    }
                });
            }
        });

        checkLoading.start();
    }
}
