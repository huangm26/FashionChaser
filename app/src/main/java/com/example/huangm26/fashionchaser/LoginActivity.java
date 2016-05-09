package com.example.huangm26.fashionchaser;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import aws.DynamoDB_util;


public class LoginActivity extends AppCompatActivity {

    Button login_button,signup_button;
    EditText usernameText,passwordText;

    String my_username;
    String my_password;
    TextView tx1;
    int counter = 3;


    private static final String MY_BUCKET = "fashionchaser";
    private static final String OBJECT_KEY = "test.txt";
    private static final File MY_FILE = new File("test.txt");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button=(Button)findViewById(R.id.login);
        usernameText=(EditText)findViewById(R.id.usernameText);
        passwordText=(EditText)findViewById(R.id.passwordText);

        signup_button=(Button)findViewById(R.id.signup);
        tx1=(TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);


        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String hash_password = hashPassword(passwordText.getText().toString());
                if (verifyUser(usernameText.getText().toString(),hash_password)) {
                    Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                    Intent goToOption = new Intent(getApplicationContext(), OptionMenuActivity.class);
                    goToOption.putExtra("username",usernameText.getText().toString());
                    startActivity(goToOption);

                } else {
                    //incorrect password
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();

                    tx1.setVisibility(View.VISIBLE);
                    tx1.setBackgroundColor(Color.RED);
                    counter--;
                    tx1.setText(Integer.toString(counter));

                    if (counter == 0) {
                        login_button.setEnabled(false);
                    }
                }
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_username = usernameText.getText().toString();
                if (temp_username.equals("")) {
                    Toast.makeText(getApplicationContext(), "Plese enter a valid username", Toast.LENGTH_SHORT).show();
                } else {
                    String temp_password = passwordText.getText().toString();
                    String hash_password = hashPassword(temp_password);
                    if (temp_password.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
                    } else {
                        my_username = temp_username;
                        my_password = hash_password;
                        signUpNewUser(my_username,my_password);
                    }
                }
            }
        });


    }

    private void signUpNewUser(String username, String password)
    {
        DynamoDB_util myDB_util = new DynamoDB_util(getApplicationContext());
        myDB_util.uploadUser(username, password);
    }

    private boolean verifyUser(String username, String password)
    {
        DynamoDB_util myDB_util = new DynamoDB_util(getApplicationContext());
        Boolean result = myDB_util.verifyUser(username, password);
        if(result) {
//            Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
//            Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void testAws(String username, String password){
//        S3_util myS3_util = new S3_util(getApplicationContext());
//        myS3_util.uploadToS3("fashionchaser","test.txt","test.txt");
        DynamoDB_util myDB_util = new DynamoDB_util(getApplicationContext());
//        myDB_util.uploadUser("aaa", "aaa");
        Boolean result = myDB_util.verifyUser(username, password);
        if(result)
            Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
    }


    private String hashPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            md.update(password.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();
        String hash = new String(digest);
        return hash;
    }

}
