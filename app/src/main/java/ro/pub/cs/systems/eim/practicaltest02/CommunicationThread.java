package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.util.Printer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    private Socket socket;
    private ServerThread serverThread;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.d("PracticalTest02", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        Log.d("PracticalTest02", "[COMMUNICATION THREAD] Started!");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            String city = bufferedReader.readLine();
            String informationType = bufferedReader.readLine();

            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
                return;
            }

            HashMap<String, WeatherForecastInformation> data = serverThread.getData();
            WeatherForecastInformation weatherForecastInformation = null;
            if (data.get(city) != null) {
                weatherForecastInformation = data.get(city);
            }
            else {
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=" + city +"&appid=e03c3b32cfb5a6f7069f2ef29237d87e");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
                if (pageSourceCode == null) {
                    Log.e("PracticalTest02", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                JSONObject content = new JSONObject(pageSourceCode);
                JSONArray weatherArray = content.getJSONArray("weather");
                JSONObject weather;
                StringBuilder condition = new StringBuilder();
                for (int i = 0; i < weatherArray.length(); i++) {
                    weather = weatherArray.getJSONObject(i);
                    condition.append(weather.getString("main")).append(" : ").append(weather.getString("description"));

                    if (i < weatherArray.length() - 1) {
                        condition.append(";");
                    }
                }
                JSONObject main = content.getJSONObject("main");
                String temperature = main.getString("temp");
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");
                JSONObject wind = content.getJSONObject("wind");
                String windSpeed = wind.getString("speed");

                weatherForecastInformation = new WeatherForecastInformation(
                        condition.toString(),
                        temperature,
                        pressure,
                        humidity,
                        windSpeed
                );

                serverThread.setData(city, weatherForecastInformation);
            }
            if (weatherForecastInformation == null) {
                Log.e("PracticalTest02", "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result;
            switch (informationType) {
                case "all":
                    result = weatherForecastInformation.toString();
                    break;
                case "condition":
                    result = weatherForecastInformation.condition;
                    break;
                case "temperature":
                    result = weatherForecastInformation.temperature;
                    break;
                case "humidity":
                    result = weatherForecastInformation.humidity;
                    break;
                case "pressure":
                    result = weatherForecastInformation.pressure;
                    break;
                case "wind_speed":
                    result = weatherForecastInformation.windSpeed;
                    break;
                default:
                    result = "Wrong information type (all / temperature / humidity / pressure / wind_speed)!";
                    break;
            }

            printWriter.println(result);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
