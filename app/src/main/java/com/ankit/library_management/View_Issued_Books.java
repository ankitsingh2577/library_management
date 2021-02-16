package com.ankit.library_management;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.helper.Functions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class View_Issued_Books extends AppCompatActivity {

    public ListView Book_details;
    public TextView Hint_res;
    private ProgressBar progressBar;
    private static final String TAG = View_Issued_Books.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> JsonList;
    private String name,urn,email,date,book,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issued__books);
        Book_details = findViewById(R.id.res_list);
        Hint_res = findViewById(R.id.text_res);
        progressBar = findViewById(R.id.ProgressBar1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        JsonList = new ArrayList<>();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (!NetConnectivityCheck.isNetworkAvailable(View_Issued_Books.this)) {
            ViewDialogNet alert = new View_Issued_Books.ViewDialogNet();
            alert.showDialog(View_Issued_Books.this, "Please, enable internet connection before using this app !!");
        }
        ViewIssuedBooks();
    }

    public void ViewIssuedBooks() {
        progressBar.setVisibility(View.GONE);
        Hint_res.setVisibility(View.GONE);
        pDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.LIBRARIAN_VIEW_ISSUED_BOOKS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Response: " + response);
                hideDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONArray jsonObject = new JSONArray(response);

                    if(response !=null) {
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);

                            name = c.getString("name");
                            urn = c.getString("urn");
                            email = c.getString("email");
                            phone = c.getString("phone");
                            book = c.getString("book_name");
                            date = c.getString("date");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("urn", urn);
                            fetchOrders.put("email",email);
                            fetchOrders.put("phone",phone);
                            fetchOrders.put("book_name",book);
                            fetchOrders.put("date",date);

                            JsonList.add(fetchOrders);
                        }

                    }
                } catch (final Exception e ) {
                    JsonList.clear();
                    progressBar.setVisibility(View.GONE);
                    Hint_res.setVisibility(View.VISIBLE);
                    Hint_res.setText("No data found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        View_Issued_Books.this, JsonList,
                        R.layout.view_issued_books_list, new String[]{"name", "urn", "email", "phone","book_name", "date"}, new int[]{R.id.name,R.id.urn,R.id.email,R.id.phone,R.id.book_name,R.id.date});

                Book_details.setAdapter(adapter);

                Book_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public class ViewDialogNet {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(View_Issued_Books.this)) {
                        ViewDialogNet alert = new View_Issued_Books.ViewDialogNet();
                        alert.showDialog(View_Issued_Books.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }
}
