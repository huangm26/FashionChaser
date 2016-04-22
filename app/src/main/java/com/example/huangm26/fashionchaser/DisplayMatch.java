package com.example.huangm26.fashionchaser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.huangm26.fashionchaser.data.Channel;
import com.example.huangm26.fashionchaser.data.Item;
import com.example.huangm26.fashionchaser.service.WeatherServiceCallback;
import com.example.huangm26.fashionchaser.service.YahooWeatherService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DisplayMatch extends AppCompatActivity implements WeatherServiceCallback, View.OnClickListener{

    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;

    private YahooWeatherService service;
    private ProgressDialog dialog;

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
        exitButton.setOnClickListener(this);
        Uri fileUri = Uri.fromFile(new File("/storage/emulated/0/Pictures/MyCameraApp/IMG_20160422_110352.jpg"));
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
            ImageView topView = (ImageView) findViewById(R.id.displayTop);
            topView.setImageBitmap(bitmap);
            ImageView bottomView = (ImageView) findViewById(R.id.displayBottom);
            bottomView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();
        Item item = channel.getItem();
        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(service.getLocation());
    }

    public void sendMessage(View view) {

        TextView txtView = (TextView) findViewById(R.id.textView1);

        new CallMashapeAsync().execute();
    }

    private class CallMashapeAsync extends AsyncTask<String, Integer, HttpResponse<JsonNode>> {

        protected HttpResponse<JsonNode> doInBackground(String... msg) {
            HttpResponse<JsonNode> response = null;
            try {
                 response = Unirest.post("https://apicloud-colortag.p.mashape.com/tag-file.json")
                        .header("X-Mashape-Key", "Pn2jrLshIhmshRO1hb2wa5Fc7ZSxp1NFGQqjsnm3HlMlNVayTl")
                        .field("image", new File("18-04-2016_visvim_104cmykjacket_navy_sh_1x.jpg"))
                        .field("palette", "w3c")
                        .field("sort", "relevance")
                        .asJson();
            } catch (UnirestException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;
        }

        protected void onProgressUpdate(Integer...integers) {
        }

        protected void onPostExecute(HttpResponse<JsonNode> response) {
            String answer = response.getBody().toString();
            TextView txtView = (TextView) findViewById(R.id.textView1);
            txtView.setText(answer);
        }
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit:
                finish();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bp = (Bitmap) data.getExtras().get("data");
        ImageView iv = (ImageView) findViewById(R.id.displayPicture);
        iv.setImageBitmap(bp);
    }
}
