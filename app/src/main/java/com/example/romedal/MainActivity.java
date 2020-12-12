package com.example.romedal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "r_TAG";
    private static final String URL_CUR_SYMBOL = "https://api.exchangeratesapi.io/latest?symbols=USD";
    private static final String URL_CUR_PERIOD = "https://api.exchangeratesapi.io/history?start_at=2020-12-01&end_at=2020-12-11&symbols=USD&&base=EUR";

    StringRequest stringRequest;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getReqExchange(android.view.View d) {
        final TextView textView = (TextView) findViewById(R.id.txtv);

        queue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, URL_CUR_SYMBOL,
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

        stringRequest = new StringRequest(Request.Method.GET, URL_CUR_PERIOD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        String result = "";
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

                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "exception is caught by romedal");
                            e.printStackTrace();
                        }

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

    private float calculate10K_eur(String usd) {
        float usd10000eur = Float.parseFloat(usd);
        Log.d(TAG, "converted => " + usd10000eur);
        usd10000eur *= 10000;
        Log.d(TAG, "after multiplay => " + usd10000eur);
        return usd10000eur;
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }

    }
}