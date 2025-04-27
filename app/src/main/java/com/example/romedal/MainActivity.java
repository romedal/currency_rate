package com.example.romedal;

import static com.example.romedal.rUtils.getDayOfMonth;
import static com.example.romedal.rUtils.getCurrentYear;
import static com.example.romedal.rUtils.getCurrentMonth;
import static com.example.romedal.rUtils.fillChart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    private static String monthReq = Integer.toString(getCurrentMonth());
    private static String yearReq = Integer.toString(getCurrentYear());
    private static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeSpinners();
        findViewById(R.id.button).performClick();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //TODO Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, getString(R.string.orient_alert_landscape), Toast.LENGTH_LONG).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, getString(R.string.orient_alert_portrait), Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.button).performClick();
    }

    public void getPeriodReqExchange(android.view.View d) {

        final TextView textView = findViewById(R.id.txtv);
        queue = Volley.newRequestQueue(this);
        String reqFinal = String.format(getString(R.string.JSON_req_final), getString(R.string.accessKey), yearReq, monthReq, yearReq, monthReq, getDayOfMonth(monthReq, yearReq));
        Log.d(getString(R.string.TAG), "reqFinal => " + reqFinal);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, reqFinal, response -> {
            String result = "";
            Map<PointValue, String> points = new LinkedHashMap<>();
            Log.d(getString(R.string.TAG), "response => " + response);

            try {

                JSONObject ResponseObject = new JSONObject(response.trim());

                if (ResponseObject.getString("success").equals("false")) {

                    String errInfo = ResponseObject.getJSONObject(ResponseObject.names().getString(1)).getString(getString(R.string.JSON_resp_err_info));
                    String errCode = ResponseObject.getJSONObject(ResponseObject.names().getString(1)).getString(getString(R.string.JSON_resp_err_code));
                    String err = getString(R.string.bad_response);

                    if (!errInfo.isEmpty() || !errCode.isEmpty()) {
                        //TODO add additional control for representing of err code
                        err = errInfo + " (err " + errCode + " )";
                    }

                    textView.setText(err);
                    textView.setTextColor(getColor(R.color.design_default_color_error));

                    LineChartView chart = findViewById(R.id.chart);
                    chart.clearAnimation();
                    chart.setLineChartData(null);

                    return;
                }

                JSONObject quotes = ResponseObject.getJSONObject("quotes");
                Map<Integer, Integer> treeMap = new TreeMap<>();

                int dayOfMonth;
                float EurUsd;

                for (int i = 0; i < quotes.names().length(); i++) {
                    Log.v(getString(R.string.TAG), "" + Integer.parseInt(quotes.names().getString(i).substring(8)));
                    dayOfMonth = Integer.parseInt(quotes.names().getString(i).substring(8));
                    EurUsd = Float.parseFloat(quotes.getJSONObject(quotes.names().getString(i)).getString("EURUSD"));
                    //TODO check whether we need treeMap at all
                    treeMap.put(Integer.parseInt(quotes.names().getString(i).substring(8)), i);
                    points.put(new PointValue(dayOfMonth, EurUsd), "");
                }

            } catch (JSONException e) {
                Log.e(getString(R.string.TAG), getString(R.string.exc_msg));
                e.printStackTrace();
            }

            float r = 1.0F;

            for (int i = 0; i < 31; i++) {
                r += 0.1F;
                points.put(new PointValue(i, r), "");
            }

            setChart(points);
            textView.setText(result);
            textView.setTextColor(getColor(R.color.black));

        }, error -> {
            textView.setText(getString(R.string.err_text));
            textView.setTextColor(getColor(R.color.design_default_color_error));
        });

        stringRequest.setTag(getString(R.string.TAG));
        queue.add(stringRequest);
    }

    public void makeSpinners() {


        String[] monthNames = getResources().getStringArray(R.array.month_names);
        String[] monthValues = getResources().getStringArray(R.array.month_values);

        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        Spinner spinnerMonth = findViewById(R.id.spinnerMonth);


        //TODO 3 paremeter for error to be updated ( Throwable: An exception to log. This value may be null.)
        if (monthNames.length != monthValues.length)
            Log.e(getString(R.string.TAG), getString(R.string.err_month_arrays), null);

        Map<String, String> monthMap = new HashMap<>();

        for (int i = 0; i < getResources().getInteger(R.integer.MAX_MONTH_VALUE); i++)
            monthMap.put(monthNames[i], monthValues[i]);

        List<String> yearList = new ArrayList<>();

        int currentYear = getCurrentYear();

        for (int i = currentYear; i >= getResources().getInteger(R.integer.MIN_YEAR_VALUE); i--)
            yearList.add((String.valueOf(i)));

        List<String> monthList = new ArrayList<>();

        for (String month : monthNames) {
            Log.d(getString(R.string.TAG), month);
            monthList.add(month);
        }

        int currentMonth = getCurrentMonth();
        List<String> monthList2021 = new ArrayList<>();

        for (int i = currentMonth - 1; i >= 0; i--)
            monthList2021.add(monthNames[i]);


        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, monthList);
        ArrayAdapter<String> adapterMonth2021 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, monthList2021);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, yearList);

        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearReq = yearList.get(position);
                //TODO check Integer.toString(getCurrentYear()))
                if (!yearReq.contentEquals(Integer.toString(getCurrentYear()))) {
                    Spinner spinner2 = findViewById(R.id.spinnerMonth);
                    spinner2.setAdapter(adapterMonth);
                } else {
                    Spinner spinner2 = findViewById(R.id.spinnerMonth);
                    spinner2.setAdapter(adapterMonth2021);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthReq = monthMap.get(monthList.get(position));
                Log.i(getString(R.string.TAG), monthList.get(position) + " " + monthReq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //TODO Decide whether function is needed for chart scalability
    private float calculate10K_eur(String usd) {

        float usd10000eur = Float.parseFloat(usd);
        Log.d(getString(R.string.TAG), "converted => " + usd10000eur);
        usd10000eur *= 10000;
        Log.d(getString(R.string.TAG), "after multiply => " + usd10000eur);
        return usd10000eur;
    }

    private void setChart(Map<PointValue, String> hp) {
        LineChartView chart = findViewById(R.id.chart);

        //TODO check objectives
        chart.setTransitionName(getString(R.string.chart_transition_name));
        chart.setContentDescription(getString(R.string.chart_content_desc));

        Axis axisX = new Axis();
        axisX.setName(getString(R.string.Axis_X_label));
        axisX.setHasLines(true);
        axisX.setAutoGenerated(true);
        axisX.setHasSeparationLine(true);
        axisX.setHasTiltedLabels(true);

        Axis axisY = new Axis();
        axisY.setName(getString(R.string.Axis_Y_label));
        axisY.setHasLines(true);
        axisY.setInside(true);

        List<AxisValue> listX = new ArrayList<>();
        List<AxisValue> listY = new ArrayList<>();

        for (int i = 0; i <= getResources().getInteger(R.integer.MAX_DAYS_OF_MONTH); i++) {
            listX.add(new AxisValue(i));
        }

        for (float i = 0.1F; i < 1.9F; i += 0.01F) {
            listY.add(new AxisValue(i));
        }

        axisX.setValues(listX);
        axisY.setValues(listY);
        chart.setInteractive(true);
        List<PointValue> values = new ArrayList<>();

        for (Map.Entry<PointValue, String> set : hp.entrySet()) {
            Log.d(getString(R.string.TAG), set.getKey().getX() + " => " + set.getKey().getY());
            values.add(set.getKey());
        }

        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setCubic(true);
        line.setHasPoints(true);
        line.setFilled(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setStrokeWidth(2);
        line.setPointRadius(5);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setAxisYRight(axisY);
        data.setAxisXBottom(axisX);
        data.setLines(lines);
        chart.setLineChartData(data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(getString(R.string.TAG));
        }

    }
}