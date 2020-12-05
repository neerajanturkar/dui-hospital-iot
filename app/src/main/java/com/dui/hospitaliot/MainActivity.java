package com.dui.hospitaliot;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_INTERNET = 1;
    private static final int PERMISSION_ACCESS_NETWORK_STATE = 2;
    public ImageView qrImage;
    public String responseMessage = new String();
    public Activity self = this;
    public Jedis jedis = new Jedis(AppConstants.DUI_CORE_HOST);
    public JedisPubSub jedisPubSub;
    String sessionId = new String();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuShowDashboard:
                Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
                finish();
                break;
            case R.id.menuShowEditPatientDetails :
                Intent editPatientDetailIntent = new Intent(MainActivity.this, EditPatientDetailsActivity.class);
                editPatientDetailIntent.putExtra("showEditPatientDetails", true);
                startActivity(editPatientDetailIntent);
                finish();
                break;

            case R.id.menuShowQrCode:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrImage = findViewById(R.id.ivQrImage);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final SharedPreferences.Editor editor = sharedPref.edit();

        requestPermission(Manifest.permission.INTERNET, PERMISSION_INTERNET);
        requestPermission(Manifest.permission.ACCESS_NETWORK_STATE, PERMISSION_ACCESS_NETWORK_STATE);
        ApplicationInfo ai = null;
        sessionId = sharedPref.getString(AppConstants.SESSION_ID, null);
        if (sessionId == null) {
            String applicationId = new String();
            String applicationSecret = new String();
            String uiProfileId = new String();
            try {
                ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
                applicationId = ai.metaData.get("applicationId").toString();
                applicationSecret = ai.metaData.get("applicationSecret").toString();
                uiProfileId = ai.metaData.get("uiProfileId").toString();
                if (applicationId.isEmpty() || applicationSecret.isEmpty() || uiProfileId.isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error!");
                    alertDialog.setMessage("Missing configuration");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("applicationId", applicationId);
                        postData.put("secret", applicationSecret);
                        postData.put("uiProfileId", uiProfileId);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AppConstants.DUI_CORE_URL + "api/v1/session/", postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject data = (JSONObject) response.get("data");
                                data.getString("_id");
                                String uuid = data.getString("_id");
                                if (uuid.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Value is required", Toast.LENGTH_LONG).show();
                                } else {
                                    QRGEncoder qrgEncoder = new QRGEncoder(uuid, null, QRGContents.Type.TEXT, 500);
                                    try {
                                        Bitmap qrBits = qrgEncoder.getBitmap();
                                        qrImage.setImageBitmap(qrBits);
                                        editor.putString(AppConstants.SESSION_ID, uuid);
                                        editor.apply();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_LONG);
                                    }
//                            new RedisHelper().execute();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

                    requestQueue.add(jsonObjectRequest);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error!");
                alertDialog.setMessage("Missing configuration");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        } else {
            QRGEncoder qrgEncoder = new QRGEncoder(sessionId, null, QRGContents.Type.TEXT, 500);
            try {
                Bitmap qrBits = qrgEncoder.getBitmap();
                qrImage.setImageBitmap(qrBits);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_LONG);
            }

        }
    }
    private void requestPermission(String permission, int requestId) {
        if (ContextCompat.checkSelfPermission(self,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(self,
                    new String[]{permission},
                    requestId);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_INTERNET: {
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(Manifest.permission.INTERNET, PERMISSION_INTERNET);
                }
                return;
            }
            case PERMISSION_ACCESS_NETWORK_STATE: {
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(Manifest.permission.ACCESS_NETWORK_STATE, PERMISSION_ACCESS_NETWORK_STATE);
                }
                return;
            }
        }
    }
}