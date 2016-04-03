package aws;

import android.content.Context;
import android.os.AsyncTask;
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

import java.io.File;

/**
 * Created by huangm26 on 3/29/16.
 */
public class S3_util  extends AsyncTask<Void, Void, Void> {

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
//        Bucket bucket = s3.createBucket(bucket_name);


    }




    public void uploadToS3(String bucket, File file, String object_key)
    {
        //error check
        if (context == null) {
            Log.d("Invalid context: ", "Invalid context, please use the correct constructor");
            return;
        }

        TransferUtility transferUtility = new TransferUtility(s3, context);


        TransferObserver observer = transferUtility.upload(
                bucket,     /* The bucket to upload to */
                object_key,    /* The key for the uploaded object */
                file        /* The file where the data to upload exists */
        );

        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                // do something
                Log.d("State changed", "state changed");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                //Display percentage transfered to user
                Log.d("The current percentage", Integer.toString(percentage));
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
            }

        });
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String bucketLocation = s3.getBucketLocation(new GetBucketLocationRequest("fashionchaser"));
            Log.d("location","bucket location = " + bucketLocation);
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // execution of result of Long time consuming operation
        Log.d("Finished execution", "Fished");
    }
}
