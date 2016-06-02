package com.demo.weather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.weather.R;
import com.demo.weather.db.WeatherDB;
import com.demo.weather.model.City;
import com.demo.weather.model.County;
import com.demo.weather.model.Province;
import com.demo.weather.util.HttpCallbackListener;
import com.demo.weather.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView tvTitle;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LEVEL_PROVINCE:
                    queryProvinces();
                    break;
                case LEVEL_CITY:
                    queryCities();
                    break;
                case LEVEL_COUNTY:
                    queryCounties();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);

                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);

                }
            }
        });
    }

    private void queryProvinces() {
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tvTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {

        }
    }

    private void queryCities() {
        cityList = weatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tvTitle.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {

        }
    }

    private void queryCounties() {
        countyList = weatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            tvTitle.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {

        }
    }

    private void queryFromServer(final String code, final String type) {
        String address = "www.aaa";

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
















