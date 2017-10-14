package com.ashraf.imageprocessing;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonSelect, buttonInvert,buttonSave,buttonShare;
    ImageView imageView;
    Bitmap myBitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSelect = (Button) findViewById(R.id.btn_select);
        buttonInvert = (Button) findViewById(R.id.btn_invart);
        buttonSave = (Button) findViewById(R.id.btn_save);
        buttonShare = (Button) findViewById(R.id.btn_share);
        imageView = (ImageView) findViewById(R.id.img_show);


        buttonSelect.setOnClickListener(this);
        buttonInvert.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v == buttonSelect){
            // Select Image From Gallery
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent,"Chose Img"),1);
        }else if (v == buttonInvert){
            if (myBitmap != null){
                 myBitmap = invertImg(myBitmap);
                imageView.setImageBitmap(myBitmap);
            }else
                Toast.makeText(MainActivity.this,"Plz Select Img First",Toast.LENGTH_SHORT);
        }else if (v == buttonShare){
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

            if (myBitmap != null){
                myBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteOutStream);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),myBitmap,"Title",null);

                Uri imgUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM,imgUri);
                startActivity(Intent.createChooser(share,"Select"));
            }
        }else if (v == buttonSave){
                if (myBitmap != null){
                    MediaStore.Images.Media.insertImage(getContentResolver(),myBitmap,"new Image","Nothing Special");
                    Toast.makeText(MainActivity.this,"Save Image Successfully",Toast.LENGTH_SHORT).show();
                }
        }
    }
    //invert Image
    private Bitmap invertImg(Bitmap orginal) {
        Bitmap invertedImg = Bitmap.createBitmap(orginal.getWidth(),orginal.getHeight(),orginal.getConfig());
        int red,green,blue,alpha;
        int height = orginal.getHeight();
        int width = orginal.getWidth();
        int pixelColor;

        for (int w = 0; w <width; w++){
            for (int h = 0; h<height; h++){
                pixelColor = orginal.getPixel(w,h);
                alpha = Color.alpha(pixelColor);
                red =255 - Color.red(pixelColor);
                green =255 - Color.green(pixelColor);
                blue = 255 - Color.blue(pixelColor);

                invertedImg.setPixel(w,h,Color.argb(alpha,red,green,blue));
            }
        }
        return invertedImg;
    }


    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {
                if (myBitmap != null) myBitmap.recycle();

                InputStream stream = getContentResolver().openInputStream(data.getData());
                myBitmap = BitmapFactory.decodeStream(stream);
                stream.close();


                imageView.setImageBitmap(myBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
