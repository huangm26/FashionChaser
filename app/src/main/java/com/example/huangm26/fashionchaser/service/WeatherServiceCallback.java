package com.example.huangm26.fashionchaser.service;

import com.example.huangm26.fashionchaser.data.Channel;

/**
 * Created by lyluy on 2016/4/4.
 */
public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
