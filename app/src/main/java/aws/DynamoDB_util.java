package aws;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;


/**
 * Created by huangm26 on 4/5/16.
 */
public class DynamoDB_util {
    Context context = null;
    CognitoCachingCredentialsProvider credentialsProvider = null;
    AmazonDynamoDBClient client = null;
    DynamoDBMapper mapper = null;
    String tablename = null;
    public  DynamoDB_util(Context context, String tablename)
    {
        this.context = context;
        this.tablename = tablename;
        Cognito_credential new_credential = new Cognito_credential(context);
        this.credentialsProvider = new_credential.getCognitoCredential();
        this.client = new AmazonDynamoDBClient(credentialsProvider);
        this.mapper = new DynamoDBMapper(client);
    }

    public void uploadUser(String username, String password)
    {
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        UploadJob job = new UploadJob(user, mapper);
        job.execute();
    }


    public boolean verifyUser(String username, String password)
    {
        DownloadJob job = new DownloadJob(username, mapper);
        Users user = null;
        try {
            user = job.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(user == null)
            return false;
        if(user.getPassword().equals(password))
            return true;
        else
            return false;
    }



    private class UploadJob extends AsyncTask<Void, Void, Void>
    {
        Users user = null;
        DynamoDBMapper mapper = null;
        public UploadJob(Users user, DynamoDBMapper mapper)
        {
            this.user = user;
            this.mapper = mapper;
        }
        @Override
        protected Void doInBackground(Void... params) {
            mapper.save(user);
            return null;
        }
    }


    private class DownloadJob extends AsyncTask<Void, Void, Users>
    {
        String username;
        DynamoDBMapper mapper = null;
        public DownloadJob(String username, DynamoDBMapper mapper)
        {
            this.username = username;
            this.mapper = mapper;
        }
        @Override
        protected Users doInBackground(Void... params) {
            Users user = mapper.load(Users.class, username);
            return user;
        }
    }



}
