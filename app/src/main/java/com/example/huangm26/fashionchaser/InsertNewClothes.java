package com.example.huangm26.fashionchaser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertNewClothes extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Spinner first_spinner;
    Spinner second_spinner;
    Spinner third_spinner;
    ArrayAdapter<CharSequence> first_adapter;
    ArrayAdapter third_adapter;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_clothes);
        first_spinner = (Spinner)findViewById(R.id.first_spinner);
        second_spinner = (Spinner)findViewById(R.id.second_spinner);
        third_spinner = (Spinner) findViewById(R.id.third_spinner);
        first_adapter = ArrayAdapter.createFromResource(this,R.array.options,android.R.layout.simple_spinner_item);
        first_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        first_spinner.setAdapter(first_adapter);
        first_spinner.setOnItemSelectedListener(this);
        third_adapter = ArrayAdapter.createFromResource(this,R.array.colorOptions, android.R.layout.simple_spinner_item);
        third_spinner.setAdapter(third_adapter);
        third_spinner.setOnItemSelectedListener(this);

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);

        Button exitButton = (Button) findViewById(R.id.exit);
        exitButton.setOnClickListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (first_spinner.getSelectedItem().equals("Top")) {
            Toast.makeText(getApplicationContext(), "Top is choosen",
                    Toast.LENGTH_SHORT).show();

            ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.top, android.R.layout.simple_spinner_item);
            second_spinner.setAdapter(adapter2);
        } else if (first_spinner.getSelectedItem().equals("Bottom")){
            Toast.makeText(getApplicationContext(), "Bottom is choosen",
                    Toast.LENGTH_SHORT).show();
            ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.bottom, android.R.layout.simple_spinner_item);
            second_spinner.setAdapter(adapter2);
        } else
        {
            Toast.makeText(getApplicationContext(), "Footwear is choosen",
                Toast.LENGTH_SHORT).show();
            ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.footwear, android.R.layout.simple_spinner_item);
            second_spinner.setAdapter(adapter2);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cameraButton:
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//                Log.d("File uri", fileUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.exit:
                finish();
                break;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        fileUri.toString(), Toast.LENGTH_LONG).show();
                Log.d("file URI",fileUri.toString());
//                Bitmap bp = (Bitmap) data.getExtras().get("data");
//                ImageView iv = (ImageView) findViewById(R.id.displayPicture);
//                iv.setImageBitmap(bp);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                    ImageView iv = (ImageView) findViewById(R.id.displayPicture);
                    iv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }

    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }


    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
