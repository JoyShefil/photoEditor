package com.example.photoeditor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Editing extends AppCompatActivity {

    ImageView image;
    Uri uri;
    Switch mSwitch,kSwitch;
    Boolean toStart = true;
    Button saveBtn;
    EditText editTextCaption;
    FileOutputStream outputStream=null;






    public Drawable drawable;
    Bitmap bitmap, newBitmap,finalImage,processedBitmap,k;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        image = findViewById(R.id.image);
        String s = getIntent().getStringExtra("gallery");
        mSwitch = findViewById(R.id.switch1);
        saveBtn = findViewById(R.id.savebtn);
        editTextCaption = findViewById(R.id.textFromUser);
       // processing = findViewById(R.id.processing);
        kSwitch = findViewById(R.id.kSwitch);
        uri = Uri.parse(s);
        Log.v("resting",s);


        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            drawable = Drawable.createFromStream(inputStream,uri.toString());
        } catch (FileNotFoundException e){
            drawable = getResources().getDrawable(R.drawable.image);
        }

        bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (toStart){
            image.setImageBitmap(bitmap);
            toStart = false;

        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (kSwitch.isChecked()){
                        newBitmap = convertImage(processedBitmap);

                    } else{
                        newBitmap = convertImage(bitmap);
                    }
                    image.setImageBitmap(newBitmap);


                } else {
                    if (kSwitch.isChecked()){
                        newBitmap = ProcessingBitmap(bitmap);
                        image.setImageBitmap(newBitmap);
                    } else {
                        image.setImageBitmap(bitmap);
                    }

                }
            }
        });

        kSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (mSwitch.isChecked()){
                        processedBitmap = ProcessingBitmap(newBitmap);
                        k = processedBitmap;
                        // Bitmap processedBitmap1 = ProcessingBitmap1();
                        if (processedBitmap != null) {
                            image.setImageBitmap(processedBitmap);
                            Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(),"Something Went Wrong in  processing",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Bitmap processedBitmap = ProcessingBitmap(bitmap);
                        // Bitmap processedBitmap1 = ProcessingBitmap1();
                        if (processedBitmap != null) {
                            image.setImageBitmap(processedBitmap);
                            Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(),"Something Went Wrong in  processing",Toast.LENGTH_SHORT).show();
                        }

                    }


                } else {
                    if (mSwitch.isChecked()){
                        newBitmap = convertImage(bitmap);
                        image.setImageBitmap(newBitmap);
                    } else {
                        image.setImageBitmap(bitmap);
                    }
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                File filePath = Environment.getExternalStorageDirectory();
                File dir = new File(filePath.getAbsolutePath() + "/Photo Editor");
                dir.mkdir();
                String fileName = String.format("%d.jpg",System.currentTimeMillis());

                File file = new File(dir,fileName);
                Toast.makeText(getApplicationContext(),"Image Saved To Internal Storage!"
                        ,Toast.LENGTH_SHORT).show();
                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

                    outputStream.flush();
                    outputStream.close();


                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    sendBroadcast(intent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_option,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
             BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
             Bitmap bitmap = drawable.getBitmap();

             String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),
                     bitmap,"title",null);

             Uri uri1 = Uri.parse(bitmapPath);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, uri1);
                startActivity(Intent.createChooser(intent,"Share"));

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap convertImage(Bitmap original){

        int width;
        int height;
        height = original.getHeight();
        width = original.getWidth();


        finalImage = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);


        Canvas c = new Canvas(finalImage);
        Paint paint = new Paint();
        ColorMatrix cm =new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(original,0,0,paint);
        return finalImage;

    }

    private Bitmap ProcessingBitmap(Bitmap original){
        Bitmap newBitmap;



            newBitmap = Bitmap.createBitmap(original.getWidth(),original.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(original,0,0, null);

            String captionString = editTextCaption.getText().toString();

            if (captionString != null){
                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLUE);
                paintText.setTextSize(200);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(10f,10f,10f,Color.BLACK);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString,0,captionString.length(),rectText);

                newCanvas.drawText(captionString,0,rectText.height(),paintText);

                Toast.makeText(getApplicationContext(),"drawText: " + captionString,Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"Caption Empty",Toast.LENGTH_SHORT).show();
            }
        return newBitmap;


        }



}