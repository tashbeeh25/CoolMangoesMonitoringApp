package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.coolmangoesmonitoringapp.Containers.selectedQrCode;
import static com.example.coolmangoesmonitoringapp.Dashboard.selectedValue;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TemperatureInfo extends AppCompatActivity {

    private LineChart lineChart;

    private List<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_info);


        Button logout_button = (Button) findViewById(R.id.logoutBtn);
        Button setting_button = (Button) findViewById(R.id.setting);
        Button home = (Button) findViewById(R.id.dashboardBtn);

        lineChart = findViewById(R.id.chart);

        Description description = new Description();
        description.setText("Temperature Overtime");
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        xValues = Arrays.asList("15", "30", "45", "60");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);


        //List<Entry> entries1 = new ArrayList<>();
        //entries1.add((Entry) new Entry(0, 10f));
        //entries1.add((Entry) new Entry(1, 8f));
        //entries1.add((Entry) new Entry(2, 34f));
        //entries1.add((Entry) new Entry(3, 15f));

        //Log.d("selectedqr" , selectedQrCode);

        //selectedValue = selectedQrCode;
        showTempGraph();

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemperatureInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemperatureInfo.this, Dashboard.class);
                startActivity(intent);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemperatureInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemperatureInfo.this, Setting.class);
                startActivity(intent);
            }
        });


    }



    private void showTempGraph() {
        OkHttpClient client = new OkHttpClient();


        String apiUrl = "https://api2.charlie-iot.com/api/temperature-list/" + selectedQrCode;

        //String apiUrl = "https://api2.charlie-iot.com/api/temperature-list/" + selectedQrCode;

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure( Call call,  IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string(); // Store the response body as a string
                    try {
                        List<Entry> entries = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(responseData); // Parse the stored response data
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String temperature = jsonObject.getString("temperature");
                            String timestamp = jsonObject.getString("timestamp");

                            float temp = Float.parseFloat(temperature);

                            entries.add(new Entry(i, temp)); // No need to cast to Entry

                            LineDataSet dataSet1 = new LineDataSet(entries, "Temperature");
                            dataSet1.setColor(Color.RED);

                            LineData lineData = new LineData(dataSet1);

                            lineChart.setData(lineData);

                            lineChart.invalidate();

                            // Now you can use id, temperature, and timestamp as needed
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
                }
            }

        });

    }
}