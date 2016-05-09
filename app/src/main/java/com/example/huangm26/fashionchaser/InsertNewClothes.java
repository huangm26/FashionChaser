package com.example.huangm26.fashionchaser;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.huangm26.fashionchaser.data.Clothes;
import com.example.huangm26.fashionchaser.data.DetectColor;
import com.example.huangm26.fashionchaser.data.DetectColorComplete;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import aws.Clothes_items;
import aws.DynamoDB_util;

public class InsertNewClothes extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, DetectColorComplete {
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
    private static File imagePath = null;
    String color;
    String colorValue;
    String style;
    String username;
    String first_spinner_select = "nothing";

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

        Button insertButton = (Button) findViewById(R.id.insertNew);
        insertButton.setOnClickListener(this);

        Button exitButton = (Button) findViewById(R.id.exit);
        exitButton.setOnClickListener(this);
        setUsername();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(first_spinner.getSelectedItem().equals(first_spinner_select)) {
            if(second_spinner != null)
                this.style = second_spinner.getSelectedItem().toString();
            if(third_spinner != null)
                this.color = third_spinner.getSelectedItem().toString();
            return;
        }
        else
            first_spinner_select = (String) first_spinner.getSelectedItem();

        if (first_spinner.getSelectedItem().equals("Top")) {
            ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.top, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            second_spinner.setAdapter(adapter2);
            second_spinner.setOnItemSelectedListener(this);
        } else if (first_spinner.getSelectedItem().equals("Bottom")){
            ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.bottom, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            second_spinner.setAdapter(adapter2);
            second_spinner.setOnItemSelectedListener(this);
        } else
        {
            ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.footwear, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            second_spinner.setAdapter(adapter2);
            second_spinner.setOnItemSelectedListener(this);
        }
        this.style = second_spinner.getSelectedItem().toString();
        this.color = third_spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cameraButton:
                startCamera();
                break;
            case R.id.insertNew:
                insertClothes();
                break;
            case R.id.exit:
                finish();
                break;
        }

    }

    private void startCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /** Save the image from camera, and display it on the image view **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        fileUri.toString(), Toast.LENGTH_LONG).show();
                Log.d("file URI",fileUri.toString());

                DisplayPicture job = new DisplayPicture(fileUri, this.getContentResolver(), (ImageView) findViewById(R.id.displayPicture));
                job.execute();
                detectColor();
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
        imagePath = getOutputMediaFile(type);
        return Uri.fromFile(imagePath);
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


    /** Auto-adjust the spinner according to the color detected **/
    private void setSpinner(Spinner spinner, String value)
    {
        for(int i=0; i < spinner.getCount(); i++) {
            if(spinner.getItemAtPosition(i).equals(value))
            {
                spinner.setSelection(i);
                break;
            }
        }
    }


    /** Detect the dominant color in the image, use for auto-color recognization **/
    private void detectColor()
    {
        DetectColor colorDetector = new DetectColor(imagePath);
        colorDetector.delegate = this;
        colorDetector.execute();
//        String color = colorDetector.returnColor();
//        Log.d("Color is :", color);
    }

    @Override
    public void processFinish(String color, String colorValue) {
        this.color = color;
        this.colorValue = colorValue;
        Log.d("Color is ", color);
        if(color.toLowerCase().contains("Red".toLowerCase()))
            this.color = "Red";
        else if(color.toLowerCase().contains("Green".toLowerCase()))
            this.color = "Green";
        else if(color.toLowerCase().contains("Blue".toLowerCase()))
            this.color = "Blue";
        else if(color.toLowerCase().contains("Black".toLowerCase()))
            this.color = "Black";
        else if(color.toLowerCase().contains("White".toLowerCase()))
            this.color = "White";
        else if(color.toLowerCase().contains("Yellow".toLowerCase()))
            this.color = "Yellow";
        else if(color.toLowerCase().contains("Purple".toLowerCase()))
            this.color = "Purple";
        else if(color.toLowerCase().contains("Brown".toLowerCase()))
            this.color = "Brown";
        else if(color.toLowerCase().contains("Gray".toLowerCase()))
            this.color = "Gray";
        else
            this.color = "Orange";
        setSpinner(third_spinner,this.color);

    }

    /** get the username from the intent **/
    private void setUsername() {
        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            username = extra.getString("username");
        }
    }

    /** Insert the new clothes to the dynamodb **/
    private void insertClothes() {
        if(fileUri == null)
            return;
        Clothes_items clothes = new Clothes_items();
        clothes.setColor(color);
        clothes.setColorValue(colorValue);
        clothes.setUsername(username);
        clothes.setClothesStyle(style);
        clothes.setFileuri(fileUri.toString());
        DynamoDB_util myDB_util = new DynamoDB_util(getApplicationContext());
        myDB_util.uploadClothes(clothes);
    }

    /** set the image view with the corresponding picture from the uri **/
    private class DisplayPicture extends AsyncTask<Void, Void, Bitmap> {
        Uri fileUri;
        ContentResolver resolver;
        ImageView view;
        public DisplayPicture(Uri fileUri, ContentResolver resolver, ImageView view) {
            this.fileUri = fileUri;
            this.resolver = resolver;
            this.view = view;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            view.setImageBitmap(result);
        }
    }
}
