package com.example.huangm26.fashionchaser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.List;

import aws.Cognito_credential;
import aws.S3_util;


public class LoginActivity extends AppCompatActivity {

    Button login_button,signup_button;
    EditText usernameText,passwordText;

    String my_username;
    String my_password;
    TextView tx1;
    int counter = 3;

    private static final String IDENTITY_POOL_ID = "us-east-1:80e2c3cd-6ea0-49ad-b604-80fa1a588605";
    private static final Regions COGNITO_REGION = Regions.US_EAST_1;
    private static final Regions S3_REGION = Regions.US_WEST_1;
    private static final String ACCOUNT_ID = "434223312340";
    private static final String UNAUTHROLEARN = "arn:aws:iam::434223312340:role/Cognito_EdisonAppUnauth_Role";
    private static final String AUTHROLEARN = "arn:aws:iam::434223312340:role/lambda_kinesis_role";

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


        
//        login_button.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                Intent intent = new Intent("com.example.huangm26.fashionchaser.FashionOptions");
//                startActivity(intent);
//            }
//        });





        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameText.getText().toString().equals(my_username) &&

                        passwordText.getText().toString().equals(my_password)) {
                    testAws();
                    Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                } else {
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
                    if (temp_password.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
                    } else {
                        my_username = temp_username;
                        my_password = temp_password;
                    }
                }
            }
        });


    }

    private void testAws(){
//        S3_util myS3_util = new S3_util(getApplicationContext());
//        myS3_util.createBucket("fashionchaser");
        SendfeedbackJob job = new SendfeedbackJob();
        job.execute();

    }

    private class SendfeedbackJob extends AsyncTask<Void, Void, Void> {

        Context context = getApplicationContext();
        CognitoCachingCredentialsProvider credentialsProvider = null;
        AmazonS3 s3 = null;
        @Override
        protected Void doInBackground(Void... params) {
            // do above Server call here
            Cognito_credential new_credential = new Cognito_credential(context);
            credentialsProvider = new_credential.getCognitoCredential();
            s3 = new AmazonS3Client(credentialsProvider);
            s3.setRegion(Region.getRegion(Regions.US_WEST_1));
            TransferUtility transferUtility = new TransferUtility(s3, context);

            File file = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/test.txt");
            s3.putObject(new PutObjectRequest("fashionchaser", "folder/"+"test.txt", file));
//            TransferObserver observer = transferUtility.download(
//                    "fashionchaser",     /* The bucket to upload to */
//                    "test.txt",    /* The key for the uploaded object */
//                    file        /* The file where the data to upload exists */
//            );
//
//            observer.setTransferListener(new TransferListener() {
//
//                @Override
//                public void onStateChanged(int id, TransferState state) {
//                    // do something
//                    Log.d("State changed", "state changed");
//                }
//
//                @Override
//                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
//                    //Display percentage transfered to user
//                    Log.d("The current percentage", Integer.toString(percentage));
//                }
//
//                @Override
//                public void onError(int id, Exception ex) {
//                    // do something
//                    Log.d("Error", "Error");
//                }
//
//            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // execution of result of Long time consuming operation
            Log.d("Finished execution", "Fished");
        }
    }


}
