package com.example.nurilmibila;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DonasiBerhasil extends AppCompatActivity {

    ImageView iv_buktiDonasi;
    String nominal;
    Bitmap imgBukti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi_berhasil);
        iv_buktiDonasi = findViewById(R.id.iv_buktiDonasi);
        Intent intent = getIntent();
        nominal = intent.getStringExtra("nominal");
        imgBukti = getIntent().getParcelableExtra("imgBukti");
        iv_buktiDonasi.setImageBitmap(imgBukti);
        Toast.makeText(this, "Donasi dengan nilai "+nominal+"", Toast.LENGTH_LONG).show();
    }

    public void back(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
