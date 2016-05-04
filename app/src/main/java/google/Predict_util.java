package google;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Insert;
import com.google.api.services.prediction.model.Insert2;
import com.google.api.services.prediction.model.Output;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.services.storage.StorageScopes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangm26 on 5/3/16.
 */
public class Predict_util {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "fashioChaser";

    /** Specify the Cloud Storage location of the training data. */
    static final String STORAGE_DATA_LOCATION = "predictcolor/clothes_output_new.csv";
    static final String MODEL_ID = "colorPrediction";

    /**
     * Specify your Google Developers Console project ID, your service account's email address, and
     * the name of the P12 file you copied to src/main/resources/.
     */
    static final String PROJECT_ID = "iot-spring2016";
    static final String SERVICE_ACCT_EMAIL = "iot-spring2016@appspot.gserviceaccount.com";
    static final String SERVICE_ACCT_KEYFILE = "IoT-Spring2016-90a113dd52e2.p12";

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();



    private GoogleCredential authorize() throws Exception {
//        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        /** Authorizes the installed application to access user's protected data. */

//        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();;
//        GoogleCredential cred = GoogleCredential.fromStream(new FileInputStream(Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                + "/" + SERVICE_ACCT_KEYFILE));
//        Log.d("Credential", cred.getServiceAccountId());
//        return cred;

        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCT_EMAIL)
                .setServiceAccountPrivateKeyFromP12File(new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + "/"+SERVICE_ACCT_KEYFILE))
                .setServiceAccountScopes(Arrays.asList(PredictionScopes.PREDICTION,
                        StorageScopes.DEVSTORAGE_READ_ONLY))
                .build();
    }

    public void run() throws Exception {
        MakeRequest newRequest = new MakeRequest();
        newRequest.execute();
    }


    private class MakeRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            httpTransport =  AndroidHttp.newCompatibleTransport();
            // authorization
            GoogleCredential credential = null;
            try {
                credential = authorize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Prediction prediction = new Prediction.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
//            try {
//                train(prediction);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            List entry = new ArrayList();
            entry.add("0x345678");
            entry.add("0x987654");
            try {
                predict(prediction, entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void train(Prediction prediction) throws IOException {
        Insert trainingData = new Insert();
        trainingData.setId(MODEL_ID);
        trainingData.setStorageDataLocation(STORAGE_DATA_LOCATION);
        prediction.trainedmodels().insert(PROJECT_ID, trainingData).execute();
        System.out.println("Training started.");
        System.out.print("Waiting for training to complete");
        System.out.flush();

        int triesCounter = 0;
        Insert2 trainingModel;
        while (triesCounter < 100) {
            // NOTE: if model not found, it will throw an HttpResponseException with a 404 error
            try {
                HttpResponse response = prediction.trainedmodels().get(PROJECT_ID, MODEL_ID).executeUnparsed();
                if (response.getStatusCode() == 200) {
                    trainingModel = response.parseAs(Insert2.class);
                    String trainingStatus = trainingModel.getTrainingStatus();
                    if (trainingStatus.equals("DONE")) {
                        System.out.println();
                        System.out.println("Training completed.");
                        System.out.println(trainingModel.getModelInfo());
                        return;
                    }
                }
                response.ignore();
            } catch (HttpResponseException e) {
            }

            try {
                // 5 seconds times the tries counter
                Thread.sleep(5000 * (triesCounter + 1));
            } catch (InterruptedException e) {
                break;
            }
            System.out.print(".");
            System.out.flush();
            triesCounter++;
        }
        error("ERROR: training not completed.");
    }

    private static void error(String errorMessage) {
        System.err.println();
        System.err.println(errorMessage);
        System.exit(1);
    }

    private static void predict(Prediction prediction, List entry) throws IOException {
        Input input = new Input();
        Input.InputInput inputInput = new Input.InputInput();
        inputInput.setCsvInstance(entry);
        input.setInput(inputInput);
        Output output = prediction.trainedmodels().predict(PROJECT_ID, MODEL_ID, input).execute();
        Log.d("Predict output", output.getOutputLabel());
    }


}
