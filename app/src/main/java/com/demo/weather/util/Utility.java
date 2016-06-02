package com.demo.weather.util;

import android.text.TextUtils;

import com.demo.weather.db.WeatherDB;
import com.demo.weather.model.City;
import com.demo.weather.model.County;
import com.demo.weather.model.Province;

public class Utility {
    /**
     * 解析省级数据
     */
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response) {
        if(!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if(allProvinces.length > 0) {
                for(String p : allProvinces) {
                    String[] array = p.split("|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return  false;
    }

    /**
     * 解析市级数据
     */
    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB, String response, int provinceId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if(allCities.length > 0) {
                for(String a : allCities) {
                    String[] array = a.split("|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return  false;
    }

    /**
     * 解析区县数据
     */
    public synchronized static boolean handleCountyResponse(WeatherDB weatherDB, String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if(allCounties.length > 0) {
                for(String a : allCounties) {
                    String[] array = a.split("|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return  false;
    }
}
