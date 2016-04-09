package aws;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;

/**
 * Created by huangm26 on 3/29/16.
 */
public class S3_util {

    Context context = null;
    CognitoCachingCredentialsProvider credentialsProvider = null;
    AmazonS3 s3 = null;
    public S3_util(Context context)
    {
        this.context = context;
        Cognito_credential new_credential = new Cognito_credential(context);
        credentialsProvider = new_credential.getCognitoCredential();
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.US_WEST_1));
    }

    public void createBucket(String bucket_name)
    {
        //error check
        if (context == null) {
            Log.d("Invalid context: ", "Invalid context, please use the correct constructor");
            return;
        }
        CreateBucketJob job = new CreateBucketJob(s3, bucket_name);
        job.execute();


    }


    public void uploadToS3(String bucket, String filename, String object_key)
    {
        //error check
        if (context == null) {
            Log.d("Invalid context: ", "Invalid context, please use the correct constructor");
            return;
        }

        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + filename);
        UploadJob job = new UploadJob(context, file, s3);
        job.execute();
    }

    private class CreateBucketJob extends AsyncTask<Void, Void, Void> {

        AmazonS3 s3 = null;
        String bucketName;
        public CreateBucketJob(AmazonS3 s3, String bucketname)
        {
            this.s3 = s3;
            this.bucketName = bucketname;
        }
        @Override
        protected Void doInBackground(Void... params) {
            // do above Server call here

            Bucket bucket = s3.createBucket(bucketName);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // execution of result of Long time consuming operation
            Log.d("Finished execution", "Create bucket Fished");
        }
    }

    private class UploadJob extends AsyncTask<Void, Void, Void> {

        Context context = null;
        AmazonS3 s3 = null;
        File file = null;
        public UploadJob(Context context, File file, AmazonS3 s3)
        {
            this.context = context;
            this.file = file;
            this.s3 = s3;
        }
        @Override
        protected Void doInBackground(Void... params) {
            // do above Server call here
            TransferUtility transferUtility = new TransferUtility(s3, context);

//            s3.putObject(new PutObjectRequest("fashionchaser", "test.txt", file));
            TransferObserver observer = transferUtility.upload(
                    "fashionchaser",     /* The bucket to upload to */
                    "test.txt",    /* The key for the uploaded object */
                    file        /* The file where the data to upload exists */
            );

            observer.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    // do something
                    Log.d("State changed", "state changed");
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    //Display percentage transfered to user
                    Log.d("The current percentage", Integer.toString(percentage));
                }

                @Override
                public void onError(int id, Exception ex) {
                    // do something
                    Log.d("Error", "Error");
                }

            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // execution of result of Long time consuming operation
            Log.d("Finished execution", "Upload to S3 Fished");
        }
    }

    private class Download extends AsyncTask<Void, Void, Void> {

        Context context = null;
        CognitoCachingCredentialsProvider credentialsProvider = null;
        AmazonS3 s3 = null;
        File file = null;
        public Download(Context context, File file)
        {
            this.context = context;
            this.file = file;
        }
        @Override
        protected Void doInBackground(Void... params) {
            // do above Server call here
            Cognito_credential new_credential = new Cognito_credential(context);
            credentialsProvider = new_credential.getCognitoCredential();
            s3 = new AmazonS3Client(credentialsProvider);
            s3.setRegion(Region.getRegion(Regions.US_WEST_1));
            TransferUtility transferUtility = new TransferUtility(s3, context);


//            s3.putObject(new PutObjectRequest("fashionchaser", "test.txt", file));
            TransferObserver observer = transferUtility.download(
                    "fashionchaser",     /* The bucket to download from*/
                    "test.txt",    /* The key for the download object */
                    file        /* The file where the data to download  */
            );

            observer.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    // do something
                    Log.d("State changed", "state changed");
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    //Display percentage transfered to user
                    Log.d("The current percentage", Integer.toString(percentage));
                }

                @Override
                public void onError(int id, Exception ex) {
                    // do something
                    Log.d("Error", "Error");
                }

            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // execution of result of Long time consuming operation
            Log.d("Finished execution", "Download from S3 Fished");
        }
    }
}
