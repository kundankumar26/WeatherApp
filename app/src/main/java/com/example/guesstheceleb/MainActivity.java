package com.example.guesstheceleb;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    TextView weatherInfoTextview;
    EditText weatherInfoEditText;
    Button weatherInfoButton;

    @SuppressLint("SetTextI18n")
    public void getWeatherInfo(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(weatherInfoEditText.getWindowToken(), 0);

        String query = null;
        try {
            query = URLEncoder.encode(weatherInfoEditText.getText().toString(), "utf-8");
            String apiLink = "https://api.openweathermap.org/data/2.5/weather?q=" + query + "&appid=c09cc76ce07a101c32ed4b20f06c721f";
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(apiLink);

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            weatherInfoTextview.setText("Firse kosis kejeye Chacha");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherInfoTextview = findViewById(R.id.weatherinfo_textview);
        weatherInfoEditText = findViewById(R.id.weatherinfo_edittext);
        weatherInfoButton = findViewById(R.id.weatherinfo_button);

    }

    class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection httpURLConnection = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                //httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = bufferedReader.readLine();
                while(line != null){
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }

                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        String netProblem = "Net nahi chal raha hai Chacha";
//                        final Toast toast = Toast.makeText(getApplicationContext(), netProblem, Toast.LENGTH_SHORT);
//                        toast.show();
//                    }
//                });
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            String weatherInfoString = "";
            try {
                weatherInfoString = parseJSON(result);
                if(!weatherInfoString.equals("")){
                    weatherInfoTextview.setText(weatherInfoString);
                } else {
                    Toast.makeText(getBaseContext(), "Kuch galat ho gya firse Try kejeye", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                //e.printStackTrace();
                Toast.makeText(getBaseContext(), "Jagah galat hai chacha", Toast.LENGTH_SHORT).show();
            }
        }

        private String parseJSON(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);

                //Get Place Name
                String placeName = jsonObject.getString("name");

                //Get Temperature of the Place
                JSONObject main = jsonObject.getJSONObject("main");
                String tempInKelvin = main.getString("temp");
                String temp = convertToFarenheit(Double.parseDouble(tempInKelvin));

                //Get Description of the weather
                JSONArray weatherDescriptionArray = jsonObject.getJSONArray("weather");
                JSONObject weatherDescriptionObject = weatherDescriptionArray.getJSONObject(0);
                String shortDescription = weatherDescriptionObject.getString("description");


                //Get the Country Name of the Place
                JSONObject weatherCountryObject = jsonObject.getJSONObject("sys");
                String country = weatherCountryObject.getString("country");

                //Get the Wind Speed of the Place
                JSONObject weatherWindSpeedObject = jsonObject.getJSONObject("wind");
                String windSpeed = weatherWindSpeedObject.getString("speed");

                AtomicReference<String> weatherInfo = new AtomicReference<>(
                        "City Name: " + placeName + "\n"
                                + "Country: " + country + "\n"
                                + "Tempearture: " + temp + "\u00B0" + "F " + "\n"
                                + "Wind Speed: " + windSpeed + " m/s \n"
                                + "Description: " + shortDescription);

                return weatherInfo.get();

            } catch (JSONException e) {

                //Toast.makeText(getApplicationContext(), "Could not find Weather Info", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return null;
        }


        private String convertToFarenheit(Double tempInKelvin) {
            DecimalFormat formater = new DecimalFormat("0.00");
            double tempInDouble = (( tempInKelvin - 273.15) * 9/5) + 32;
            return formater.format(tempInDouble);
        }
    }

    private void makeToast() {
        Toast.makeText(getApplicationContext(), "Net gaya Chacha", Toast.LENGTH_SHORT).show();
    }
}