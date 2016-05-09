package com.example.huangm26.fashionchaser;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.EntityStatus;
import com.amazonaws.services.machinelearning.model.GetMLModelRequest;
import com.amazonaws.services.machinelearning.model.GetMLModelResult;
import com.amazonaws.services.machinelearning.model.PredictRequest;
import com.amazonaws.services.machinelearning.model.PredictResult;
import com.amazonaws.services.machinelearning.model.RealtimeEndpointStatus;
import com.example.huangm26.fashionchaser.data.Channel;
import com.example.huangm26.fashionchaser.data.Item;
import com.example.huangm26.fashionchaser.service.WeatherServiceCallback;
import com.example.huangm26.fashionchaser.service.YahooWeatherService;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import aws.Clothes_items;
import aws.Cognito_credential;
import aws.DynamoDB_util;
import aws.MachineLearning_util;
import google.Predict_util;


public class DisplayMatch extends AppCompatActivity implements WeatherServiceCallback, View.OnClickListener{

    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;

    private YahooWeatherService service;
    private ProgressDialog dialog;

    String username;
    int temperature = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_match);

        temperatureTextView = (TextView)findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView)findViewById(R.id.conditionTextView);
        locationTextView = (TextView)findViewById(R.id.locationTextView);

        service = new YahooWeatherService(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        service.refreshWeather("New York City, NY");

        Button exitButton = (Button) findViewById(R.id.exit);
        Button displayButton = (Button) findViewById(R.id.display);
        exitButton.setOnClickListener(this);
        displayButton.setOnClickListener(this);
        setUsername();

    }


    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();
        Item item = channel.getItem();
        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(service.getLocation());
        temperature = item.getCondition().getTemperature();
    }


    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.display:
                displayClothes();
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    /** Fetch the certain style clothes from dynamodb, and input the color to machine learning
     *  to determine if it's fashion
     *  If fashion, display on the image view, otherwise try another combination
     */
    private void displayClothes() {
        DynamoDB_util dynamoDB_util = new DynamoDB_util(getApplicationContext());
        Predict_util predict_ml = new Predict_util();
        List<String> tops = pickTop();
        List<String> bottoms = pickBottom();
        List<Clothes_items> topOutputs = new ArrayList<Clothes_items>();
        List<Clothes_items> bottomOutputs = new ArrayList<Clothes_items>();
        for(String top : tops) {
            List<Clothes_items> outputs = dynamoDB_util.downloadClothes(top, username);
            for(Clothes_items output : outputs)
                topOutputs.add(output);
        }
        for(String bottom : bottoms) {
            List<Clothes_items> outputs = dynamoDB_util.downloadClothes(bottom, username);
            for(Clothes_items output : outputs)
                bottomOutputs.add(output);
        }
//        List<Clothes_items> outputs = dynamoDB_util.downloadClothes("Blazers", username);
//        for(Clothes_items output : outputs) {
//            String uriString = output.getFileuri();
//            Uri fileUri = Uri.parse(uriString);
////        MachineLearning_util predict_ml = new MachineLearning_util(getApplicationContext());
////        predict_ml.makeRequest();
//            try {
//                String prediction_output = predict_ml.run("0x123456", "0x987654");
//                if(prediction_output.equals("FALSE"))
//                    break;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        Boolean findMatch = false;
        Uri topUri = null;
        Uri bottomUri = null;
        for(Clothes_items topOutput : topOutputs) {
            for(Clothes_items bottomOutput : bottomOutputs) {
                try {
                    String prediction_output = predict_ml.run(topOutput.getColorValue(), bottomOutput.getColorValue());
                    if(prediction_output.equals("TRUE")) {
                        Uri temptopUri = Uri.parse(topOutput.getFileuri());
                        Uri tempbottomUri = Uri.parse(bottomOutput.getFileuri());
                        if(!findMatch) {
                            topUri = temptopUri;
                            bottomUri = tempbottomUri;
                            findMatch = true;
                        } else {
                            Random randomno = new Random();
                            if(randomno.nextInt(3) == 1) {
                                topUri = temptopUri;
                                bottomUri = tempbottomUri;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(!findMatch) {
            Toast.makeText(this, "Sorry, you don't have enough clothes to output a match", Toast.LENGTH_LONG).show();
        } else {
            DisplayPicture displayTop = new DisplayPicture(topUri, this.getContentResolver(),(ImageView) findViewById(R.id.displayTop));
            displayTop.execute();
            DisplayPicture displayBottom = new DisplayPicture(bottomUri, this.getContentResolver(), (ImageView) findViewById(R.id.displayBottom));
            displayBottom.execute();
        }
    }

    /** Choose the top style based on the temperature **/
    private List<String> pickTop() {
        List<String> tops = new ArrayList<String>();
        if(temperature > 75) {
            tops.add("T-shirts");
            tops.add("Polos");
        } else if(temperature > 55) {
            tops.add("Hoodies");
            tops.add("Blazers");
            tops.add("Suits");
            tops.add("Shirt");
        } else if(temperature > 35) {
            tops.add("Sweaters");
            tops.add("Coats");
            tops.add("Jackets");
            tops.add("Suits");
        } else {
            tops.add("Coats");
            tops.add("Jackets");
        }
        return tops;
    }

    /** Choose the bottom style based on the temperature **/
    private List<String> pickBottom() {
        List<String> bottoms = new ArrayList<String>();
        if(temperature > 75) {
            bottoms.add("Shorts");
            bottoms.add("Pants");
        } else {
            bottoms.add("Pants");
            bottoms.add("Jeans");
            bottoms.add("Trousers");
        }
        return bottoms;
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

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Bitmap bp = (Bitmap) data.getExtras().get("data");
//        ImageView iv = (ImageView) findViewById(R.id.displayPicture);
//        iv.setImageBitmap(bp);
//    }

    /** get the username from the intent **/
    private void setUsername() {
        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            username = extra.getString("username");
        }
    }
}
