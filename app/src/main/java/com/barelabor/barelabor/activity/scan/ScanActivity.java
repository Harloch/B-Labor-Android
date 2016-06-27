package com.barelabor.barelabor.activity.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener{

    private final int REQUEST_CAMERA = 1001;
    private final int REQUEST_GALLARY = 1002;

    ImageButton btnClose, btnGallery, btnTake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();
    }

    private void initView(){

        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnTake = (ImageButton) findViewById(R.id.btnTakePhoto);
        btnGallery = (ImageButton) findViewById(R.id.btnGallary);

        btnClose.setOnClickListener(this);
        btnTake.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == btnClose){
            finish();
        }else if(v == btnTake){

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);

//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            File f = new File(android.os.Environment
//                    .getExternalStorageDirectory(), "temp.jpg");
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//            startActivityForResult(intent, REQUEST_CAMERA);

        }else if(v == btnGallery){

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    REQUEST_GALLARY);
        }
    }

    /*
	 * On activity result to pick an image from the user gallery
	 */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bm = null;
        Constants.bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                bm = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                bm.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
//                File destination = new File(Environment.getExternalStorageDirectory(),
//                        System.currentTimeMillis() + ".jpg");
//                FileOutputStream fo;
//                try {
//                    destination.createNewFile();
//                    fo = new FileOutputStream(destination);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            } else if (requestCode == REQUEST_GALLARY) {

                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Constants.bitmap = bm;
        startActivity(new Intent(ScanActivity.this, PreviewActivity.class));
    }
}
