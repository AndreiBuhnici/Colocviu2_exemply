package ro.pub.cs.systems.eim.practicaltest02;

import androidx.annotation.NonNull;

public class WeatherForecastInformation {
    final String temperature;
    final String windSpeed;
    final String condition;
    final String pressure;
    final String humidity;
    public WeatherForecastInformation(String temperature, String windSpeed, String condition, String pressure, String humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    @NonNull
    @Override
    public String toString() {
        return "WeatherForecastInformation{" +
                "temperature='" + temperature + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", condition='" + condition + '\'' +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }
}
