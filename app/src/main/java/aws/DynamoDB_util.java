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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Created by huangm26 on 4/5/16.
 */
public class DynamoDB_util {
    Context context = null;
    CognitoCachingCredentialsProvider credentialsProvider = null;
    AmazonDynamoDBClient client = null;
    DynamoDBMapper mapper = null;
    public  DynamoDB_util(Context context)
    {
        this.context = context;
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
        UploadUserJob job = new UploadUserJob(user, mapper);
        job.execute();
    }

    public void uploadClothes(Clothes_items clothes) {
        UploadClothesJob job = new UploadClothesJob(clothes, mapper);
        job.execute();
    }

    public boolean verifyUser(String username, String password)
    {
        DownloadUserJob job = new DownloadUserJob(username, mapper);
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


    public List<Clothes_items> downloadClothes(String style, String username) {
        DownloadClothesJob job = new DownloadClothesJob(style, username, mapper);
        List<Clothes_items> outputs = null;
        try {
            outputs = job.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return outputs;

    }


    private class UploadUserJob extends AsyncTask<Void, Void, Void>
    {
        Users user = null;
        DynamoDBMapper mapper = null;
        public UploadUserJob(Users user, DynamoDBMapper mapper)
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

    private class UploadClothesJob extends AsyncTask<Void, Void, Void>
    {
        Clothes_items clothes = null;
        DynamoDBMapper mapper = null;
        public UploadClothesJob(Clothes_items clothes, DynamoDBMapper mapper) {
            this.clothes = clothes;
            this.mapper = mapper;
        }
        @Override
        protected Void doInBackground(Void... params) {
            mapper.save(clothes);
            return null;
        }
    }

    private class DownloadUserJob extends AsyncTask<Void, Void, Users>
    {
        String username;
        DynamoDBMapper mapper = null;
        public DownloadUserJob(String username, DynamoDBMapper mapper)
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


    private class DownloadClothesJob extends AsyncTask<Void, Void, List<Clothes_items>>
    {
        String style;
        String username;
        DynamoDBMapper mapper;
        public DownloadClothesJob(String style, String username, DynamoDBMapper mapper) {
            this.style = style;
            this.username = username;
            this.mapper = mapper;
        }

        @Override
        protected List<Clothes_items> doInBackground(Void... params) {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(style));
            eav.put(":val2", new AttributeValue().withS(username));
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("clothesStyle = :val1 and username = :val2")
                    .withExpressionAttributeValues(eav);
            List<Clothes_items> scanResult = mapper.scan(Clothes_items.class, scanExpression);
            for(Clothes_items clothes: scanResult) {
                Log.d("received clothes", clothes.getFileuri());
            }
            return scanResult;
        }
    }

}
