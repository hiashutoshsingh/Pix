package com.ashu.pix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int RESULT_LOAD_IMAGE = 101;
    private ImageView imageView;
    private Button btPreview, btFlipH, btFlipV, btOpacity, btAddText, btSave, btChoose;
    private Bitmap bitmap, alteredBitmap;
    RelativeLayout rlImageVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPreview = findViewById(R.id.btPreview);
        imageView = findViewById(R.id.image);
        btFlipH = findViewById(R.id.btFlipH);
        btFlipV = findViewById(R.id.btFlipV);
        btOpacity = findViewById(R.id.btOpacity);
        btAddText = findViewById(R.id.btAddText);
        btSave = findViewById(R.id.btSave);
        btChoose = findViewById(R.id.btChoose);
        rlImageVisible = findViewById(R.id.rlImageVisible);


        btFlipH.setOnClickListener(this);
        btFlipV.setOnClickListener(this);
        btOpacity.setOnClickListener(this);
        btAddText.setOnClickListener(this);
        btSave.setOnClickListener(this);
        btChoose.setOnClickListener(this);
        btPreview.setOnClickListener(this);


    }

    private void flipHorizontally() {
        if (imageView.getScaleX() == 1) {
            btFlipH.setText("Revert Change");
            imageView.setScaleX(-1);
        } else {
            btFlipH.setText("Flip Horizontally");
            imageView.setScaleX(1);
        }

    }

    private void flipVertically() {
        if (imageView.getScaleY() == 1) {
            btFlipV.setText("Revert Change");
            imageView.setScaleY(-1);
        } else {
            btFlipV.setText("Flip Horizontally");
            imageView.setScaleY(1);
        }

    }

    private void setOpacity() {
        if (imageView.getAlpha() != 0.5f) {
            btOpacity.setText("Revert Change");
            imageView.setAlpha(0.5f);
        } else {
            btOpacity.setText("Flip Horizontally");
            imageView.setAlpha(1f);
        }
    }

    private void setText() {
        if (bitmap != null) {
            alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
            Canvas canvas = new Canvas(alteredBitmap);
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, 0, 0, paint);
            paint.setColor(Color.GREEN);
            paint.setTypeface(tf);
            paint.setTextSize(50);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GreedyGames", bitmap.getWidth() / 2, bitmap.getHeight() / 2, paint);
            Glide.with(this).load(alteredBitmap).into(imageView);
        } else {

        }
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.e("pix", "Error creating media file, check storage permissions");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(this, "Image is Saved", Toast.LENGTH_SHORT).show();
            btChoose.setVisibility(View.VISIBLE);
            rlImageVisible.setVisibility(View.GONE);

        } catch (FileNotFoundException e) {
            Log.e("pix", "File not found " + e.getMessage());
        } catch (IOException e) {
            Log.e("pix", "Error accessing file " + e.getMessage());
        }
    }

    private void seePreview() {
        if (alteredBitmap != null) {
            View view = View.inflate(MainActivity.this, R.layout.dialog_layout, null);
            ImageView imgRefInflated = view.findViewById(R.id.imgRefInflated);
            Glide.with(MainActivity.this).load(alteredBitmap).into(imgRefInflated);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setView(view);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "Pix" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                showMessageOKCancel("You need to allow access permissions",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission();
                                }
                            }
                        });
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            btChoose.setVisibility(View.GONE);
            rlImageVisible.setVisibility(View.VISIBLE);
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(this).load(bitmap).into(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btFlipH:
                flipHorizontally();
                break;
            case R.id.btFlipV:
                flipVertically();
                break;
            case R.id.btOpacity:
                setOpacity();
                break;
            case R.id.btAddText:
                setText();
                break;
            case R.id.btSave:
                if (alteredBitmap != null)
                    storeImage(alteredBitmap);
                break;
            case R.id.btChoose:
                if (checkPermission()) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else requestPermission();
                break;
            case R.id.btPreview:
                seePreview();
                break;
        }
    }
}
