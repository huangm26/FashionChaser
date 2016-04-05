package com.example.huangm26.fashionchaser.data;

import org.json.JSONObject;
/**
 * Created by lyluy on 2016/4/4.
 */
public class Item implements JSONPopulator{
    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void populate(JSONObject data){
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));
    }
}
