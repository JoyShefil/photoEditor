package com.example.photoeditor;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoeditor.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    int IMAGE_REQUEST_CODE = 11 , CAMERA_REQUEST_CODE= 50;
    public Uri gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Pick an image"),IMAGE_REQUEST_CODE);
            }
        });

        binding.camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.CAMERA},33);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            gallery = data.getData();
            Log.v("gallery",gallery.toString());
            Intent intent = new Intent(this,Editing.class);
            //intent.setData(gallery);
            intent.putExtra("gallery",gallery.toString());
            startActivity(intent);

        }
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            Bitmap cam = (Bitmap) data.getExtras().get("data");
            Uri uri = getCamUri(cam);
            Intent intent = new Intent(this,Editing.class);
            //intent.setData(gallery);
            intent.putExtra("gallery",uri.toString());
            startActivity(intent);
        }

    }

    public Uri getCamUri (Bitmap bitmap) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,arrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
        return Uri.parse(path);
    }
}