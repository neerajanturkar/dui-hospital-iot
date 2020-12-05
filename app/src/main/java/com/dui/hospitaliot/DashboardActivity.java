package com.dui.hospitaliot;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dui.hospitaliot.helper.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class DashboardActivity extends AppCompatActivity {
    TextView medicineLevel, heartRate, pulseRate;
    String sessionId;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuShowDashboard:

                break;
            case R.id.menuShowEditPatientDetails :
                Intent editPatientDetailIntent = new Intent(DashboardActivity.this, EditPatientDetailsActivity.class);
                editPatientDetailIntent.putExtra("showEditPatientDetails", true);
                startActivity(editPatientDetailIntent);
                finish();
                break;

            case R.id.menuShowQrCode:
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        medicineLevel = findViewById(R.id.tvMedicineLevel);
        heartRate = findViewById(R.id.tvheartRate);
        pulseRate = findViewById(R.id.tvPulseRate);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
        sessionId = sharedPreferences.getString(AppConstants.SESSION_ID, null);

        medicineLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Integer.parseInt(editable.toString()) > 50){
                    medicineLevel.setTextColor(getResources().getColor(R.color.info));
                } else if ((Integer.parseInt(editable.toString()) > 30)) {
                    medicineLevel.setTextColor(getResources().getColor(R.color.warning));
                } else {
                    medicineLevel.setTextColor(getResources().getColor(R.color.error));
                }
                RequestQueue requestQueue = Volley.newRequestQueue(DashboardActivity.this);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("action", "INTEGER::tvMedicineLevel::" + editable.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, AppConstants.DUI_CORE_URL + "api/v1/session/" + sessionId + "/publish", postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       System.out.println(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);
            }
        });

        pulseRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                RequestQueue requestQueue = Volley.newRequestQueue(DashboardActivity.this);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("action", "INTEGER::tvPulseRate::" + editable.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, AppConstants.DUI_CORE_URL + "api/v1/session/" + sessionId + "/publish", postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);
            }
        });
        heartRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Integer.parseInt(editable.toString()) > 95){
                    heartRate.setTextColor(getResources().getColor(R.color.info));
                } else if ((Integer.parseInt(editable.toString()) > 90)) {
                    heartRate.setTextColor(getResources().getColor(R.color.warning));
                } else {
                    heartRate.setTextColor(getResources().getColor(R.color.error));
                }
                RequestQueue requestQueue = Volley.newRequestQueue(DashboardActivity.this);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("action", "INTEGER::tvHeartRate::" + editable.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, AppConstants.DUI_CORE_URL + "api/v1/session/" + sessionId + "/publish", postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);
            }
        });
        new MockChange(pulseRate).execute();
        new MockChange(medicineLevel).execute();
        new MockChange(heartRate).execute();
    }
    class MockChange extends AsyncTask<Void, Void, String> {
        private TextView textView;
        public MockChange(TextView textView) {
            this.textView = textView;
        }
        @Override
        protected void onPostExecute(String s) {
            switch (textView.getId()) {
                case R.id.tvMedicineLevel:
                    if(Integer.parseInt(textView.getText().toString()) > 0)
                        new MockChange(textView).execute();
                    break;
                case R.id.tvheartRate:
                case R.id.tvPulseRate:
                    new MockChange(textView).execute();
                    break;
            }

        }
        @Override
        protected String doInBackground(Void... voids) {
            switch (textView.getId()) {
                case R.id.tvMedicineLevel:
                    Integer currentValue = Integer.parseInt(textView.getText().toString());
                    if(currentValue > 0) {
                        currentValue -= 10;

                        final Integer finalCurrentValue = currentValue;
                        DashboardActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(finalCurrentValue.toString());


                            }
                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                case R.id.tvPulseRate:
                    int max = 162, min = 95;
                    Random random = new Random();
                    final Integer value = random.nextInt(max - min) + min;
                    DashboardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(value.toString());


                        }
                    });
                    try {
                        Thread.sleep(4000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.tvheartRate:
                    int maxOxygen = 100, minOxygen = 85;
                    Random randomOxy = new Random();
                    final Integer valueOxy = randomOxy.nextInt(maxOxygen - minOxygen) + minOxygen;
                    DashboardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(valueOxy.toString());
                        }
                    });
                    try {
                        Thread.sleep(4000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;



            }
            return null;
        }
    }


}
