package com.example.huangm26.fashionchaser;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class OptionMenuActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_menu);
        Button insertButton = (Button) findViewById(R.id.insertNewItem);
        Button findMatchButton = (Button) findViewById(R.id.findMatch);
        Button exitButton = (Button) findViewById(R.id.exit);

        insertButton.setOnClickListener(this);
        findMatchButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.insertNewItem:
                insertNewItem();
                break;
            case R.id.findMatch:
                findMatch();
                break;
            case R.id.exit:
                exitProgram();
                break;
            default:
                break;
        }
    }

    private void insertNewItem()
    {
        Intent insertIntent = new Intent(getApplicationContext(),InsertNewClothes.class);
        startActivity(insertIntent);
    }

    private void findMatch()
    {
        Intent findMatchIntent = new Intent(getApplicationContext(), DisplayMatch.class);
        startActivity(findMatchIntent);
    }

    private void exitProgram()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
