package com.example.huangm26.fashionchaser;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class OptionMenuActivity extends Activity implements View.OnClickListener{

    String username;
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
        setUsername();
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
        insertIntent.putExtra("username",username);
        startActivity(insertIntent);
    }

    private void findMatch()
    {
        Intent findMatchIntent = new Intent(getApplicationContext(), DisplayMatch.class);
        findMatchIntent.putExtra("username", username);
        startActivity(findMatchIntent);
    }

    private void exitProgram()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setUsername() {
        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            username = extra.getString("username");
        }
    }
}
