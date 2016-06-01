package com.demo.weather.util;

import android.text.TextUtils;

import com.demo.weather.db.WeatherDB;

public class Utility {
    /**
     * 解析省级数据
     */
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response) {
        if(!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
        }
        return  false;
    }
}
