package com.example.romedal;

import androidx.appcompat.app.AppCompatActivity;

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
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        try {
                            JSONObject ResponseObject = new JSONObject(response);
                            String usd = ResponseObject.getJSONObject("rates").getString("USD");
                            Log.d(TAG, usd);
                            usd10000eur = Float.parseFloat(usd);
                            Log.e(TAG, "romedal777" + reqFinal);
                            Log.d(TAG, " converted " + usd10000eur);
                            usd10000eur *= 10000;
                            Log.d(TAG, "after multiplay " + usd10000eur);

                        } catch (JSONException e) {
                            Log.e(TAG, "exception is caught by romedal");
                            e.printStackTrace();
                        }

                        int usd10000eurInt = (int)usd10000eur;
                        textView.setText(String.format("10K EUR is: %d $", usd10000eurInt));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });
        stringRequest.setTag(TAG);
        queue.add(stringRequest);

    }

    public void getPeriodReqExchange(android.view.View d) {

        final TextView textView = (TextView) findViewById(R.id.txtv);
        queue = Volley.newRequestQueue(this);
        String reqFinal = "https://api.exchangeratesapi.io/history?start_at="+yearReq+"-" + monthReq + "-01&end_at=" + yearReq + "-" + monthReq + "-28&symbols=USD&&base=EUR";
        Log.e(TAG, "romedal777" + reqFinal);
        stringRequest = new StringRequest(Request.Method.GET, reqFinal,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        String result = "";
                        Map points = new LinkedHashMap<PointValue, String>();
                        Log.d(TAG, "respone =>" + response);
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
                            for (Iterator i = keyss.iterator(); i.hasNext(); ) {
                                Integer key = (Integer) i.next();
                                Integer value = (Integer) treeMap.get(key);
                                Log.v(TAG, key + " =>>>>>>> " + value);
                                JSONObject ResponseObjectusd = new JSONObject(rates.get(rates.names().getString(value)).toString());
                                NumberFormat formatter = new DecimalFormat("##,###");
                                String formattedNumber = formatter.format( (double) calculate10K_eur(ResponseObjectusd.get("USD").toString()));
                                result += String.format("%s %s $ %s %s \r\n", getResources().getString(R.string.first_part_msg),
                                        formattedNumber, getResources().getString(R.string.second_part_msg), rates.names().getString(value));
                                points.put(new PointValue(key,  (int) (double) calculate10K_eur(ResponseObjectusd.get("USD").toString())), "");

                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "exception is caught by romedal");
                            e.printStackTrace();
                        }
                        setChart(points);
                        textView.setText(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });
        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    public void makeSpinners() {
        Spinner spinner;
        Spinner spinner2;
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("January", "01");
        monthMap.put("Fabuaru", "02");
        monthMap.put("April", "03");
        monthMap.put("March", "04");
        monthMap.put("May", "05");
        monthMap.put("June", "06");
        monthMap.put("July", "07");
        monthMap.put("August", "08");
        monthMap.put("September", "09");
        monthMap.put("October", "10");
        monthMap.put("November", "11");
        monthMap.put("December", "12");
        List<String> monthList;
        List<String> yearList = new ArrayList<>();
         for (int i = 2020; i > 1999; i--){
            yearList.add((String.valueOf(i)));
        }
        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        monthList = new ArrayList<>();
        monthList.add("January");
        monthList.add("Fabuaru");
        monthList.add("April");
        monthList.add("March");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, monthList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, yearList);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthReq = monthMap.get(monthList.get(position).toString());
                Log.i(TAG, "romedal777 " + monthList.get(position) + monthReq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearReq = yearList.get(position).toString();
                Log.i(TAG, "romedal777 " + yearList.get(position) + yearReq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private float calculate10K_eur(String usd) {
        float usd10000eur = Float.parseFloat(usd);
        Log.d(TAG, "converted => " + usd10000eur);
        usd10000eur *= 10000;
        Log.d(TAG, "after multiplay => " + usd10000eur);
        return usd10000eur;
    }
//HashMap<PointValue, String>
    private void setChart(Map<PointValue, String> hp){
        LineChartView chart = findViewById(R.id.chart);
        chart.setTransitionName("romedal trans name");
        chart.setTooltipText("romedal chart");
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
//        axisY.setHasSeparationLine(true);
//        axisY.setHasTiltedLabels(true);
//        axisY.setAutoGenerated(true);
//        axisY.setTextColor(0xFF000000);
        List listX = new ArrayList<AxisValue>();
        List listY = new ArrayList<AxisValue>();

        for (int i = 0; i < 32; i++)
        {
            listX.add(new AxisValue(i));
        }
        for (int i = 10000; i < 12500; i+=50)
        {
            listY.add(new AxisValue(i));
        }

//        listY.add(new AxisValue(12300));
//        listY.add(new AxisValue(12350));
        axisX.setValues(listX);
        axisY.setValues(listY);
        chart.setInteractive(true);
        List<PointValue> values = new ArrayList<PointValue>();
        for (Map.Entry<PointValue, String> set : hp.entrySet()) {
            Log.d(TAG,set.getKey().getX() + " =77= " + set.getKey().getY());
            values.add(set.getKey());
        }

        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        line.setHasPoints(true).setFilled(true);
        List<Line> lines = new ArrayList<Line>();
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