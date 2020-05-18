package com.example.projetRapace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        setContentView(R.layout.activity_image_view);

        ((Button) findViewById(R.id.boutonRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(intent != null) {
            final String ip = intent.getStringExtra("ip");
            if (ip != null) {


                final android.widget.ImageView imageView;
                imageView = (android.widget.ImageView) findViewById(R.id.imageView);
                Picasso.get().load(ip).into(imageView);
            }
        }
    }
}
