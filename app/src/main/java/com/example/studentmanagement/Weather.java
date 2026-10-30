package com.example.studentmanagement;

public class Weather {

    private final String city;
    private final String country;
    private final String description;
    private final double temperature;
    private final int humidity;
    private final double windSpeed;

    public Weather(String city, String country, String description,
                   double temperature, int humidity, double windSpeed) {
        this.city = city;
        this.country = country;
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getDescription() { return description; }
    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
}
