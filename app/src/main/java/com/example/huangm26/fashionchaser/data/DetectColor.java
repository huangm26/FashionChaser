package com.example.huangm26.fashionchaser.data;

import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by huangm26 on 4/22/16.
 */
public class DetectColor extends AsyncTask<String, Integer, HttpResponse<JsonNode>> {

    File imagePath = null;
    String color = null;
    public DetectColorComplete delegate = null;
    public DetectColor(File imagePath)
    {
        this.imagePath = imagePath;
    }

    protected HttpResponse<JsonNode> doInBackground(String... msg) {
        HttpResponse<JsonNode> response = null;
        if(imagePath == null) {
            Log.d("Invalid image file path", "Please try a valid file path");
            return response;
        }
        try {
            response = Unirest.post("https://apicloud-colortag.p.mashape.com/tag-file.json")
                    .header("X-Mashape-Key", "Pn2jrLshIhmshRO1hb2wa5Fc7ZSxp1NFGQqjsnm3HlMlNVayTl")
                    .field("image", imagePath)
                    .field("palette", "w3c")
                    .field("sort", "relevance")
                    .asJson();
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    protected void onProgressUpdate(Integer...integers) {
    }

    protected void onPostExecute(HttpResponse<JsonNode> response) {
        String answer = response.getBody().toString();
//            TextView txtView = (TextView) findViewById(R.id.textView1);
//            txtView.setText(answer);
//            Log.d("Color response", answer);
        try {
            JSONObject jsonobj = new JSONObject(answer);
            JSONArray jsonarray = jsonobj.getJSONArray("tags");
            if(jsonarray.length()>0){
                JSONObject jsonObject = jsonarray.getJSONObject(1);
                color = jsonObject.getString("label");
//                Log.d("Color is ", color);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        delegate.processFinish(color);
    }

}
