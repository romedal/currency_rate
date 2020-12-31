package com.example.romedal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "r_TAG";
    private static final String URL_CUR_SYMBOL = "https://api.exchangeratesapi.io/latest?symbols=USD";
    private static final String URL_CUR_PERIOD = "https://api.exchangeratesapi.io/history?start_at=2020-12-01&end_at=2020-12-30&symbols=USD&&base=EUR";
    private static String monthReq= "01";
    private static String yearReq= "2020";

    StringRequest stringRequest;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeSpinners();
    }

    public void getReqExchange(android.view.View d) {
        final TextView textView = (TextView) findViewById(R.id.txtv);
        String reqFinal = "https://api.exchangeratesapi.io/history?start_at=2020-" + monthReq + "-01&end_at=2020-" + monthReq + "-31&symbols=USD&&base=EUR";
        queue = Volley.newRequestQueue(this);

        stringRequest = new StringRequest(Request.Method.GET, reqFinal,
                new Response.Listener<String>() {
                    float usd10000eur = 0;
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        try {
                            JSONObject ResponseObject = new JSONObject(response);
                            String usd = ResponseObject.getJSONObject("rates").getString("USD");
                            usd10000eur = Float.parseFloat(usd);
                            usd10000eur *= 10000;
                        } catch (JSONException e) {
                            Log.e(TAG, getString(R.string.exc_msg));
                            e.printStackTrace();
                        }

                        int usd10000eurInt = (int)usd10000eur;
                        textView.setText(String.format(getString(R.string.first_part_msg) + "%d $", usd10000eurInt));
                    }
                }, error -> textView.setText(getString(R.string.err_text)));

        stringRequest.setTag(TAG);
        queue.add(stringRequest);

    }

    public void getPeriodReqExchange(android.view.View d) {

        final TextView textView = (TextView) findViewById(R.id.txtv);
        queue = Volley.newRequestQueue(this);
        int c = 1;
        String reqFinal = String.format("https://api.exchangeratesapi.io/history?start_at=%s-%s-01&end_at=%s-%s-%s&symbols=USD&&base=EUR", yearReq, monthReq, yearReq, monthReq, getDayOfMonth(monthReq, yearReq));
        Log.i(TAG, reqFinal);
        stringRequest = new StringRequest(Request.Method.GET, reqFinal,
                (Response.Listener<String>) response -> {
                    String result = "";
                    Map points = new LinkedHashMap<PointValue, String>();
                    Log.i(TAG, "respone =>" + response);
                    try {
                        JSONObject ResponseObject = new JSONObject(response.trim());
                        JSONObject rates = ResponseObject.getJSONObject("rates");
                        Iterator<String> keys = rates.keys();
                        Map treeMap = new TreeMap<Integer, Integer>();
                        for (int i = 0; i < rates.names().length(); i++) {
                            Log.v(TAG, "" + Integer.parseInt(rates.names().getString(i).substring(8)));
                            treeMap.put(Integer.parseInt(rates.names().getString(i).substring(8)), i);
                        }

                        Set keyss = treeMap.keySet();
                        for (Object obj : keyss) {
                            Integer key = (Integer) obj;
                            Integer value = (Integer) treeMap.get(key);
                            Log.v(TAG, key + " =>>>>>>> " + value);
                            JSONObject ResponseObjectusd = new JSONObject(rates.get(rates.names().getString(value)).toString());
                            NumberFormat formatter = new DecimalFormat(getString(R.string.DecFormPattern));
                            String formattedNumber = formatter.format((double) calculate10K_eur(ResponseObjectusd.get("USD").toString()));
                            result += String.format("%s %s $ %s %s \r\n", getResources().getString(R.string.first_part_msg),
                                    formattedNumber, getResources().getString(R.string.second_part_msg), rates.names().getString(value));
                            points.put(new PointValue(key, (int) (double) calculate10K_eur(ResponseObjectusd.get("USD").toString())), "");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, getString(R.string.exc_msg));
                        e.printStackTrace();
                    }
                    setChart(points);
                    textView.setText(result);
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(getString(R.string.err_text));
            }
        });
        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    public void makeSpinners() {

        Spinner spinnerYear  = findViewById(R.id.spinnerYear);
        Spinner spinnerMonth = findViewById(R.id.spinnerMonth);

        Map<String, String> monthMap = new HashMap<>();
        monthMap.put(getString(R.string.Jan), getString(R.string.d1));
        monthMap.put(getString(R.string.Fab), getString(R.string.d2));
        monthMap.put(getString(R.string.Mar), getString(R.string.d3));
        monthMap.put(getString(R.string.Apr), getString(R.string.d4));
        monthMap.put(getString(R.string.May), getString(R.string.d5));
        monthMap.put(getString(R.string.Jun), getString(R.string.d6));
        monthMap.put(getString(R.string.Jul), getString(R.string.d7));
        monthMap.put(getString(R.string.Aug), getString(R.string.d8));
        monthMap.put(getString(R.string.Sep), getString(R.string.d9));
        monthMap.put(getString(R.string.Oct), getString(R.string.d10));
        monthMap.put(getString(R.string.Nov), getString(R.string.d11));
        monthMap.put(getString(R.string.Dec), getString(R.string.d12));

        List<String> yearList = new ArrayList<>();

        for (int i = 2021; i >= 1999; i--){
            yearList.add((String.valueOf(i)));
        }

        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);

        List<String> monthList = new ArrayList<>();
        monthList.add(getString(R.string.Jan));
        monthList.add(getString(R.string.Fab));
        monthList.add(getString(R.string.Mar));
        monthList.add(getString(R.string.Apr));
        monthList.add(getString(R.string.May));
        monthList.add(getString(R.string.Jun));
        monthList.add(getString(R.string.Jul));
        monthList.add(getString(R.string.Aug));
        monthList.add(getString(R.string.Sep));
        monthList.add(getString(R.string.Oct));
        monthList.add(getString(R.string.Nov));
        monthList.add(getString(R.string.Dec));

        List<String> monthList2021 = new ArrayList<>();
        monthList2021.add(getString(R.string.Jan));
//        monthList.add(getString(R.string.Fab));
//        monthList.add(getString(R.string.Mar));
//        monthList.add(getString(R.string.Apr));
//        monthList.add(getString(R.string.May));
//        monthList.add(getString(R.string.Jun));
//        monthList.add(getString(R.string.Jul));
//        monthList.add(getString(R.string.Aug));
//        monthList.add(getString(R.string.Sep));
//        monthList.add(getString(R.string.Oct));
//        monthList.add(getString(R.string.Nov));
//        monthList.add(getString(R.string.Dec));


        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, monthList);
        ArrayAdapter<String> adapterMonth2021 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, monthList2021);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, yearList);

        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearReq = yearList.get(position);
                if (!yearReq.contentEquals("2021"))
                {
                    Spinner spinner2 = findViewById(R.id.spinnerMonth);
                    spinner2.setAdapter(adapterMonth);
                }
                else
                {
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
                Log.i(TAG, monthList.get(position) + " " + monthReq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public String getDayOfMonth(String month, String year)
    {
        int daysCount, date = 1;
        DecimalFormat twodigits = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int yearCalendar = Integer.parseInt(year);
        int monthCalendar = Integer.parseInt(month);
        calendar.set(yearCalendar, monthCalendar - 1, date);
        daysCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.i(TAG, "number of days in month: monthCalendar yearCalendar daysCount" + monthCalendar + " " + yearCalendar + " " + daysCount);
        Log.i(TAG, "number of days in month: " + twodigits.format(daysCount));
        return twodigits.format(daysCount);
    }

    private float calculate10K_eur(String usd) {

        float usd10000eur = Float.parseFloat(usd);
        Log.i(TAG, "converted => " + usd10000eur);
        usd10000eur *= 10000;
        Log.i(TAG, "after multiplay => " + usd10000eur);
        return usd10000eur;

    }

    private void setChart(Map<PointValue, String> hp){
        LineChartView chart = findViewById(R.id.chart);

        chart.setTransitionName("romedal trans name");
        chart.setContentDescription("romedal content desc");

        Axis axisX = new Axis();
        axisX.setName("Day of month");
        axisX.setHasLines(true);
        axisX.setAutoGenerated(true);
        axisX.setHasSeparationLine(true);
        axisX.setHasTiltedLabels(true);

        Axis axisY = new Axis();
        axisY.setName("10K EUR = $");
        axisY.setHasLines(true);
        axisY.setInside(true);

        List listX = new ArrayList<AxisValue>();
        List listY = new ArrayList<AxisValue>();

        for (int i = 0; i < 32; i++)
        {
            listX.add(new AxisValue(i));
        }

        for (int i = 8000; i < 12500; i+=50)
        {
            listY.add(new AxisValue(i));
        }

        axisX.setValues(listX);
        axisY.setValues(listY);
        chart.setInteractive(true);
        List<PointValue> values = new ArrayList<>();

        for (Map.Entry<PointValue, String> set : hp.entrySet()) {
            Log.d(TAG,set.getKey().getX() + " =77= " + set.getKey().getY());
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
        line.setPointRadius(10);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setAxisYRight(axisY);
        data.setAxisXBottom(axisX);
        data.setLines(lines);
        chart.setLineChartData(data);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }

    }
}