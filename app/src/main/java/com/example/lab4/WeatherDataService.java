package com.example.lab4;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    private static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    private static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";
    Context context;
    String cityId;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface GetCityIdListener {
        void onError(String message);

        void onResponse(String cityId);
    }

    public interface ForeCastByIdListener {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModelList);
    }

    public interface ForeCastByCityListener{
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModelList);
    }

    public void getCityId(String cityName, GetCityIdListener getCityIdListener) {
        String url = QUERY_FOR_CITY_ID + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cityId = "";
                        try {
                            JSONObject cityInfo = response.getJSONObject(0);
                            cityId = cityInfo.getString("woeid");
                        } catch (JSONException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        getCityIdListener.onResponse(cityId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getCityIdListener.onError(error.toString());
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getCityForecastById(String cityId, ForeCastByIdListener foreCastByIdListener) {

        List<WeatherReportModel> weatherReportModels = new ArrayList<>();

        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                            for (int i = 0; i < consolidated_weather_list.length(); i++) {
                                WeatherReportModel forecastDay = new WeatherReportModel();
                                JSONObject first_day_from_api = (JSONObject) consolidated_weather_list.get(i);
                                forecastDay.setId(first_day_from_api.getInt("id"));
                                forecastDay.setWeather_state_name(first_day_from_api.getString("weather_state_name"));
                                forecastDay.setWeather_state_abbr(first_day_from_api.getString("weather_state_abbr"));
                                forecastDay.setWind_direction_compass(first_day_from_api.getString("wind_direction_compass"));
                                forecastDay.setCreated(first_day_from_api.getString("created"));
                                forecastDay.setApplicable_date(first_day_from_api.getString("applicable_date"));
                                forecastDay.setMin_temp(first_day_from_api.getLong("min_temp"));
                                forecastDay.setMax_temp(first_day_from_api.getLong("max_temp"));
                                forecastDay.setThe_temp(first_day_from_api.getLong("the_temp"));
                                forecastDay.setWind_speed(first_day_from_api.getLong("wind_speed"));
                                forecastDay.setWind_direction(first_day_from_api.getLong("wind_direction"));
                                forecastDay.setAir_pressure(first_day_from_api.getInt("air_pressure"));
                                forecastDay.setHumidity(first_day_from_api.getInt("humidity"));
                                forecastDay.setVisibility(first_day_from_api.getLong("visibility"));
                                forecastDay.setPredictability(first_day_from_api.getInt("predictability"));
                                weatherReportModels.add(forecastDay);
                            }

                            foreCastByIdListener.onResponse(weatherReportModels);
                        } catch (JSONException e) {
                            foreCastByIdListener.onError(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getCityForecastByName(String cityName, ForeCastByCityListener foreCastByCityListener) {
        getCityId(cityName, new GetCityIdListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String cityId) {
                getCityForecastById(cityId, new ForeCastByIdListener() {
                    @Override
                    public void onError(String message) {
                        foreCastByCityListener.onError(message);
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModelList) {
                        foreCastByCityListener.onResponse(weatherReportModelList);
                    }
                });
            }
        });
    }
}
