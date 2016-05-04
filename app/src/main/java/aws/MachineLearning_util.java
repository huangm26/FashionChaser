package aws;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.EntityStatus;
import com.amazonaws.services.machinelearning.model.GetMLModelRequest;
import com.amazonaws.services.machinelearning.model.GetMLModelResult;
import com.amazonaws.services.machinelearning.model.PredictRequest;
import com.amazonaws.services.machinelearning.model.PredictResult;
import com.amazonaws.services.machinelearning.model.RealtimeEndpointStatus;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangm26 on 4/25/16.
 */
public class MachineLearning_util {

    Context context = null;
    CognitoCachingCredentialsProvider credentialsProvider = null;
    AmazonMachineLearningClient client = null;
    public MachineLearning_util(Context context)
    {
        this.context = context;

    }



    public void makeRequest()
    {
        // Use a created model that has a created real-time endpoint
        String mlModelId = "ml-aElNe7N7KsM";
//        MakeRequest newRequest = new MakeRequest(context, mlModelId);
        MakeRequest newRequest = new MakeRequest(mlModelId);
        newRequest.execute();



    }


    private class MakeRequest extends AsyncTask<Void, Void, Void> {

//        AmazonMachineLearningClient client = null;
        String mlModelId = null;
//        public MakeRequest(Context context, String mlModelId)
//        {
//            Cognito_credential new_credential = new Cognito_credential(context);
//            credentialsProvider = new_credential.getCognitoCredential();
//            client = new AmazonMachineLearningClient(credentialsProvider);
//            this.mlModelId = mlModelId;
//        }
        public MakeRequest(String mlModelId) {
            this.mlModelId = mlModelId;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Cognito_credential new_credential = new Cognito_credential(context);
            credentialsProvider = new_credential.getCognitoCredential();
            client = new AmazonMachineLearningClient(credentialsProvider);
            // Call GetMLModel to get the realtime endpoint URL
            GetMLModelRequest getMLModelRequest = new GetMLModelRequest();
            getMLModelRequest.setMLModelId(mlModelId);
            GetMLModelResult mlModelResult = client.getMLModel(getMLModelRequest);
            // Validate that the ML model is completed
            if (!mlModelResult.getStatus().equals(EntityStatus.COMPLETED.toString())) {
                System.out.println("ML Model is not completed: + mlModelResult.getStatus()");
                return null;
            }

            // Validate that the realtime endpoint is ready
            if (!mlModelResult.getEndpointInfo().getEndpointStatus().equals(RealtimeEndpointStatus.READY.toString())){
                System.out.println("Realtime endpoint is not ready: " + mlModelResult.getEndpointInfo().getEndpointStatus());
                return null;
            }
            else {
                Log.d("End point", "Realtime endpoint is ready");
            }
            // Create a Predict request with your ML model ID and the appropriate Record mapping
            PredictRequest predictRequest = new PredictRequest();
            predictRequest.setMLModelId(mlModelId);
//
            Map<String, String> record = new HashMap();
            record.put("top_color", "");
            record.put("bottom_color", "");

            predictRequest.setPredictEndpoint(mlModelResult.getEndpointInfo().getEndpointUrl());
            predictRequest.setRecord(record);

//            PredictRequest predictRequest = new PredictRequest().withMLModelId(mlModelId).withPredictEndpoint(mlModelResult.getEndpointInfo().getEndpointUrl());
//            predictRequest.addRecordEntry("top_color", "0x123456");
//            predictRequest.addRecordEntry("bottom_color", "0x987654");
            Log.d("Prediction record", predictRequest.getRecord().toString());
            // Call Predict and print out your prediction
            PredictResult predictResult = client.predict(new PredictRequest());
//            Log.d("Prediction value ", predictResult.getPrediction().toString());
            Log.d("URL", mlModelResult.getEndpointInfo().getEndpointUrl());
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // execution of result of Long time consuming operation
            Log.d("Machine learning model ", "finish");
        }
    }
}
