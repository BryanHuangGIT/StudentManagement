
package com.example.studentmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private final List<Weather> weatherList;

    public WeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        Weather weather = weatherList.get(position);

        holder.textViewCity.setText(weather.getCity() + ", " + weather.getCountry());
        holder.textViewTemperature.setText(
                String.format(Locale.getDefault(), "%.1f°C", weather.getTemperature()));
        holder.textViewDescription.setText(weather.getDescription());
        holder.textViewHumidity.setText("Humidity: " + weather.getHumidity() + "%");
        holder.textViewWind.setText(
                String.format(Locale.getDefault(), "Wind speed: %.1f m/s", weather.getWindSpeed()));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCity, textViewTemperature, textViewDescription;
        TextView textViewHumidity, textViewWind;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCity = itemView.findViewById(R.id.textViewCity);
            textViewTemperature = itemView.findViewById(R.id.textViewTemperature);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewHumidity = itemView.findViewById(R.id.textViewHumidity);
            textViewWind = itemView.findViewById(R.id.textViewWind);
        }
    }
}
