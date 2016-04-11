package com.example.huangm26.fashionchaser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangm26.fashionchaser.data.Channel;
import com.example.huangm26.fashionchaser.data.Item;
import com.example.huangm26.fashionchaser.service.WeatherServiceCallback;
import com.example.huangm26.fashionchaser.service.YahooWeatherService;

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
        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
    }

    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();
        Item item = channel.getItem();
        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(service.getLocation());
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cameraButton:
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
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
