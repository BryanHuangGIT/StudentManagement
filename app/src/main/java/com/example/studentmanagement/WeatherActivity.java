
package com.example.studentmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    private static final String API_KEY = "YOUR_API_KEY";

    private EditText editTextCity;
    private ProgressBar progressBarWeather;
    private RecyclerView recyclerViewWeather;

    private final List<Weather> weatherList = new ArrayList<>();
    private WeatherAdapter weatherAdapter;
    private RequestQueue requestQueue;

    private int pendingRequests = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        editTextCity = findViewById(R.id.editTextCity);
        Button buttonSearchWeather = findViewById(R.id.buttonSearchWeather);
        progressBarWeather = findViewById(R.id.progressBarWeather);
        recyclerViewWeather = findViewById(R.id.recyclerViewWeather);

        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(this));
        weatherAdapter = new WeatherAdapter(weatherList);
        recyclerViewWeather.setAdapter(weatherAdapter);

        requestQueue = Volley.newRequestQueue(this);

        buttonSearchWeather.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();

            if (city.isEmpty()) {
                editTextCity.setError("Please enter a city");
                return;
            }

            fetchWeather(city);
        });

        // Default cities
        fetchWeather("Auckland");
        fetchWeather("Wellington");
        fetchWeather("Christchurch");
    }

    private void fetchWeather(String city) {

        if (API_KEY.equals("YOUR_API_KEY")) {
            Toast.makeText(this, "Please configure your OpenWeatherMap API key",
                    Toast.LENGTH_LONG).show();
            return;
        }

        pendingRequests++;
        progressBarWeather.setVisibility(View.VISIBLE);

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + encodedCity + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String cityName = response.getString("name");

                        JSONObject system = response.getJSONObject("sys");
                        String country = system.optString("country", "");

                        JSONObject main = response.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        int humidity = main.getInt("humidity");

                        JSONArray conditions = response.getJSONArray("weather");
                        String description = conditions.getJSONObject(0)
                                .getString("description");

                        JSONObject wind = response.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");

                        Weather weather = new Weather(
                                cityName, country, description,
                                temperature, humidity, windSpeed);

                        weatherList.add(weather);
                        weatherAdapter.notifyItemInserted(weatherList.size() - 1);

                    } catch (Exception e) {
                        Toast.makeText(this, "Unable to parse weather data",
                                Toast.LENGTH_SHORT).show();
                    }

                    finishRequest();
                },
                error -> {
                    String message = "Unable to retrieve weather for " + city;

                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;

                        if (statusCode == 401) {
                            message = "Invalid or inactive API key";
                        } else if (statusCode == 404) {
                            message = "City not found: " + city;
                        } else if (statusCode == 429) {
                            message = "API request limit reached";
                        }
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    finishRequest();
                }
        );

        requestQueue.add(request);
    }

    private void finishRequest() {
        pendingRequests--;

        if (pendingRequests <= 0) {
            pendingRequests = 0;
            progressBarWeather.setVisibility(View.GONE);
        }
    }
}
