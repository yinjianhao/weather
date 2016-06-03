package com.demo.weather.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.weather.R;
import com.demo.weather.db.WeatherDB;
import com.demo.weather.model.City;
import com.demo.weather.model.County;
import com.demo.weather.model.Province;
import com.demo.weather.util.HttpCallbackListener;
import com.demo.weather.util.HttpUtil;
import com.demo.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    public static final int HTTP_ERROR = 3;

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
                case HTTP_ERROR:
                    closeProgressDialog();
                    Toast.makeText(MainActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
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

        listView = (ListView) findViewById(R.id.lv_list);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
//        queryProvinces();
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
            queryFromServer(null, "province");
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
            queryFromServer(selectedProvince.getProvinceCode(), "city");
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
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;

        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("province".equals(type) && Utility.handleProvincesResponse(weatherDB, response)) {
                    handler.sendEmptyMessage(LEVEL_PROVINCE);
                } else if ("city".equals(type) && Utility.handleCitiesResponse(weatherDB, response, selectedProvince.getId())) {
                    handler.sendEmptyMessage(LEVEL_CITY);
                } else if ("county".equals(type) && Utility.handleCountyResponse(weatherDB, response, selectedCity.getId())) {
                    handler.sendEmptyMessage(LEVEL_COUNTY);
                }
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(HTTP_ERROR);
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    Log.d("asd","sadas");
//                }
//            });
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
















