package com.ankit.library_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.helper.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Sucessfully_Issued extends AppCompatActivity {

    String Qr_Result;
    private ProgressDialog pDialog;
    private static final String TAG = Sucessfully_Issued.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucessfully__issued);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent in = getIntent();
        Bundle b = in.getExtras();

        if (b != null) {
            Qr_Result = (String) b.get("code");
            Toast.makeText(this, Qr_Result, Toast.LENGTH_SHORT).show();
        }
        IssueBook(Qr_Result);
    }

        private void IssueBook(final String code) {

            // Tag used to cancel the request
            String tag_string_req = "req_register";

            pDialog.setMessage("Please Wait ...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    Functions.Issue_Book_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Register Response: " + response);
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                        boolean error = jObj.getBoolean("error");
                        if (!error) {

                        } else {
                            // Error occurred during attendance. Get the error
                            // message
                            String errorMsg = jObj.getString("message");
                            Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Registration Error: " + error.getMessage(), error);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<>();
                    params.put("code", code);
                    return params;
                }
            };

            // Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        private void showDialog() {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        private void hideDialog() {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LibrarianHomeActivity.class));
        finish();
    }
}
