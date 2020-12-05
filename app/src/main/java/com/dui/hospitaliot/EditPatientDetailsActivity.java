package com.dui.hospitaliot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dui.hospitaliot.helper.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class EditPatientDetailsActivity extends AppCompatActivity {
    public Button connect;
    public EditText mPatientName, mPatientId;
    Boolean showEditPatientDetails = false;
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

                break;

            case R.id.menuShowQrCode:
                Intent intent = new Intent(EditPatientDetailsActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_details);
        connect = findViewById(R.id.btnConnect);
        mPatientId = findViewById(R.id.edPatientId);
        mPatientName = findViewById(R.id.edPatientName);
        Bundle bundle = getIntent().getExtras();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditPatientDetailsActivity.this);
        sessionId = sharedPreferences.getString(AppConstants.SESSION_ID, null);
        if (bundle!=null)
            showEditPatientDetails = bundle.getBoolean("showEditPatientDetails");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPref.edit();
        String patientId = sharedPref.getString("patientId", null);
        String patientName = sharedPref.getString("patientName", null);
        if (patientId != null && patientName !=null) {
            if (showEditPatientDetails == true) {
                mPatientId.setText(patientId);
                mPatientName.setText(patientName);
            } else {
                Intent intent = new Intent(EditPatientDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("patientId", mPatientId.getText().toString());
                editor.putString("patientName", mPatientName.getText().toString());
                editor.apply();
                RequestQueue requestQueue = Volley.newRequestQueue(EditPatientDetailsActivity.this);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("action", "TEXT::tvPatientName::" + mPatientName.getText().toString());

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
                requestQueue = Volley.newRequestQueue(EditPatientDetailsActivity.this);
                postData = new JSONObject();
                try {
                    postData.put("action", "TEXT::tvPatientId::" + mPatientId.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, AppConstants.DUI_CORE_URL + "api/v1/session/" + sessionId + "/publish", postData, new Response.Listener<JSONObject>() {
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
                Intent intent = new Intent(EditPatientDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}