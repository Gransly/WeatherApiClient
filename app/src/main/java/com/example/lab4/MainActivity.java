package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.lab4.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    final WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.btnGetCityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherDataService.getCityId(binding.etDataInput.getText().toString(), new WeatherDataService.GetCityIdListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String cityId) {
                        Toast.makeText(MainActivity.this, "City id = " + cityId, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.btnGetWeatherByCityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherDataService.getCityForecastById(binding.etDataInput.getText().toString(), new WeatherDataService.ForeCastByIdListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModelList) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModelList);
                        binding.lvWeatherReports.setAdapter(arrayAdapter);
                    }
                });
            }
        });

        binding.btnGetWeatherByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherDataService.getCityForecastByName(binding.etDataInput.getText().toString(), new WeatherDataService.ForeCastByCityListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModelList) {

                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, weatherReportModelList);
                        binding.lvWeatherReports.setAdapter(arrayAdapter);
                    }
                });
            }
        });
    }
}