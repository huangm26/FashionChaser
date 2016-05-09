package com.example.huangm26.fashionchaser.data;

import android.content.Context;
import android.net.Uri;

/**
 * Created by huangm26 on 4/26/16.
 */
public class Clothes {
    String color = null;
    String colorValue = null;
    String style = null;
    String uri = null;
    String username;
    public Clothes(Context context){

    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public void uploadDB() {

    }

    public void setUsername (String username) {
        this.username = username;
    }

}
