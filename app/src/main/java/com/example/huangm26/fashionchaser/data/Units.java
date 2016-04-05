package com.example.huangm26.fashionchaser.data;

import org.json.JSONObject;
/**
 * Created by lyluy on 2016/4/4.
 */
public class Units implements JSONPopulator {
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
    }
}
