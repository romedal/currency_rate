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

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "romedalTAG";
    StringRequest stringRequest; // Assume this exists.
    RequestQueue queue;  // Assume this exists.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getReqExchange(android.view.View d) {
        final TextView textView = (TextView) findViewById(R.id.txtv);

        queue = Volley.newRequestQueue(this);
        String url = "https://api.exchangeratesapi.io/latest?symbols=USD";

        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    float usd10000eur = 0;
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject ResponseObject = new JSONObject(response);
                            String usd = ResponseObject.getJSONObject("rates").getString("USD");
                            Log.d(TAG, usd);
                            usd10000eur = Float.parseFloat(usd);
                            Log.d(TAG, " converted " + usd10000eur);
                            usd10000eur *= 10000;
                            Log.d(TAG, "after multiplay " + usd10000eur);

                        } catch (JSONException e) {
                            Log.e(TAG, "exception romedal");
                            e.printStackTrace();
                        }
                        int usd10000eurInt = (int)usd10000eur;
                        textView.setText("10K EUR is: " + usd10000eurInt + " $");
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
    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }

    }
}