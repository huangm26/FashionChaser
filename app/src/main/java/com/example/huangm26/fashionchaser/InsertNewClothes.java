package com.example.huangm26.fashionchaser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class InsertNewClothes extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Spinner first_spinner;
    Spinner second_spinner;
    ArrayAdapter<CharSequence> first_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_clothes);
        first_spinner = (Spinner)findViewById(R.id.first_spinner);
        second_spinner = (Spinner)findViewById(R.id.second_spinner);
        first_adapter = ArrayAdapter.createFromResource(this,R.array.options,android.R.layout.simple_spinner_item);
        first_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        first_spinner.setAdapter(first_adapter);
        first_spinner.setOnItemSelectedListener(this);

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
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
