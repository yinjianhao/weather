package com.demo.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.demo.weather.model.City;
import com.demo.weather.model.County;
import com.demo.weather.model.Province;

import java.util.ArrayList;
import java.util.List;

public class WeatherDB {
    /**
     * 数据库名称
     */
    public static final String DB_NAME = "db_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static WeatherDB weatherDB;

    private SQLiteDatabase db;

    /**
     * 构造方法私有化,单例
     */
    private WeatherDB(Context context) {
        WeatherOpenHelper weatherOpenHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = weatherOpenHelper.getWritableDatabase();
    }

    /**
     * 获取WeatherDB实例
     */
    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * 将province实例存储到数据库
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取所有省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Province province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }
        cursor.close();
        return list;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省份下所有市
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();

        Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
        while (cursor.moveToNext()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceId(provinceId);
            list.add(city);
        }
        cursor.close();
        return list;
    }

    /**
     * 将County实例存储到数据库
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", county.getCountyName());
            values.put("city_code", county.getCountyCode());
            values.put("province_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有区县
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<>();

        Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
        while (cursor.moveToNext()) {
            County county = new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCityId(cityId);
            list.add(county);
        }
        cursor.close();
        return list;
    }
}
