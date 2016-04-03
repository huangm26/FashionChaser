package aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;

import java.util.List;

/**
 * Created by huangm26 on 3/29/16.
 */
public class Cognito_credential {

    private static final String IDENTITY_POOL_ID = "us-east-1:80e2c3cd-6ea0-49ad-b604-80fa1a588605";
    private static final Regions COGNITO_REGION = Regions.US_EAST_1;
    private static final String ACCOUNT_ID = "434223312340";
    private static final String UNAUTHROLEARN = "arn:aws:iam::434223312340:role/Cognito_EdisonAppUnauth_Role";
    private static final String AUTHROLEARN = "arn:aws:iam::434223312340:role/lambda_kinesis_role";

    private static Context context = null;

    public Cognito_credential(Context context)
    {
        this.context = context;
    }

    public CognitoCachingCredentialsProvider getCognitoCredential(){
        if(this.context != null)
        {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    this.context,
                    ACCOUNT_ID,
                    IDENTITY_POOL_ID, // Identity Pool ID
                    UNAUTHROLEARN,
                    AUTHROLEARN,
                    COGNITO_REGION // Region
            );

            // Initialize the Cognito Sync client
            CognitoSyncManager syncClient = new CognitoSyncManager(
                    this.context,
                    Regions.US_EAST_1, // Region
                    credentialsProvider);
            // Create a record in a dataset and synchronize with the server
            Dataset dataset = syncClient.openOrCreateDataset("myDataset");
            dataset.put("myKey", "myValue");
            dataset.synchronize(new DefaultSyncCallback() {
                @Override
                public void onSuccess(Dataset dataset, List newRecords) {
                    //Your handler code here
                    Log.d("Connection status", "successful");
                }
            });
            return credentialsProvider;
        }
        else
        {
            Log.d("Invalid context: ", "Invalid context, please use the correct constructor");
            return null;
        }
    }
}
