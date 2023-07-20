package com.example.coolmangoesmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HumidityInfo extends AppCompatActivity {


    private LineChart lineChart;

    private List<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity_info);

        lineChart = findViewById(R.id.chart);

        Description description = new Description();
        description.setText("Humidity Overtime");
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

        List<Entry> entries1 = new ArrayList<>();
        entries1.add((Entry) new Entry(0, 10f));
        entries1.add((Entry) new Entry(1, 10f));
        entries1.add((Entry) new Entry(2, 15f));
        entries1.add((Entry) new Entry(3, 45f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "Humidity");
        dataSet1.setColor(Color.BLUE);

        LineData lineData = new LineData(dataSet1);

        lineChart.setData(lineData);

        lineChart.invalidate();
    }
}