package com.example.personalfinanceapp2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalfinanceapp2.data.database.DatabaseClient;
import com.example.personalfinanceapp2.data.model.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        setupPieChart();
        setupBarChart();

        loadChartData();
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(58f);
        pieChart.setHoleRadius(45f);
        pieChart.setCenterText("Expense\nReport");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1200);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(13f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1200);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
    }

    private void loadChartData() {
        DatabaseClient.getInstance(this)
                .getAppDatabase()
                .transactionDao()
                .getAllTransactions()
                .observe(this, transactions -> {
                    loadPieChart(transactions);
                    loadBarChart(transactions);
                });
    }

    private void loadPieChart(List<Transaction> transactions) {
        Map<String, Float> categoryTotals = new HashMap<>();

        for (Transaction transaction : transactions) {
            if ("Expense".equals(transaction.getType())) {
                String category = transaction.getCategory();
                float amount = (float) transaction.getAmount();

                if (categoryTotals.containsKey(category)) {
                    categoryTotals.put(category, categoryTotals.get(category) + amount);
                } else {
                    categoryTotals.put(category, amount);
                }
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50"));
        colors.add(Color.parseColor("#2ECC71"));
        colors.add(Color.parseColor("#3498DB"));
        colors.add(Color.parseColor("#9B59B6"));
        colors.add(Color.parseColor("#F39C12"));
        colors.add(Color.parseColor("#E74C3C"));
        colors.add(Color.parseColor("#1ABC9C"));

        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void loadBarChart(List<Transaction> transactions) {
        float[] monthTotals = new float[12];

        for (Transaction transaction : transactions) {
            if ("Expense".equals(transaction.getType())) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(transaction.getDate());
                int month = calendar.get(Calendar.MONTH);
                monthTotals[month] += (float) transaction.getAmount();
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, monthTotals[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");
        dataSet.setColor(Color.parseColor("#3498DB"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setLabelCount(months.length);
        xAxis.setGranularity(1f);

        barChart.invalidate();
    }
}