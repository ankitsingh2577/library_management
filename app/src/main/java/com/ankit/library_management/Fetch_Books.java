package com.ankit.library_management;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class Fetch_Books extends AppCompatActivity {

    ListView DetailsListView;
    ProgressBar progressBar;
    TextView Result_Lable;
    public Spinner Book_Category;
    public String book_category;
    private static final String TAG = Fetch_Books.class.getSimpleName();
    ArrayList<HashMap<String, String>> contactJsonList;
    String book_name,author,publication,category,quantity;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch__books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DetailsListView = findViewById(R.id.res_list);
        progressBar = findViewById(R.id.ProgressBar1);
        Result_Lable=findViewById(R.id.text_res);
        Book_Category = findViewById(R.id.category);

        contactJsonList = new ArrayList<>();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (!NetConnectivityCheck.isNetworkAvailable(Fetch_Books.this)) {
            ViewDialogNet alert = new ViewDialogNet();
            alert.showDialog(Fetch_Books.this, "Please, enable internet connection before using this app !!");
        }

        Book_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                book_category= parent.getItemAtPosition(position).toString();
                contactJsonList.clear();
                Result_Lable.setVisibility(View.VISIBLE);
                SearchStudents(book_category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner Drop down elements
        List<String> category = new ArrayList<String>();
        category.add("Select Category");
        category.add("Storage");
        category.add("Architecture");
        category.add("Cloud Computing");
        category.add("Data Science");
        category.add("Machine Learning");
        category.add("Artificial Intelligence");
        category.add("Soft Skills");
        category.add("Novels");

        Book_Category.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Book_Category.setAdapter(dataAdapter);

    }
    public void SearchStudents(final String book_category) {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.FETCH_BOOKS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetch Response: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONArray jsonObject = new JSONArray(response);

                    if(response !=null) {
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);

                            book_name = c.getString("book_name");
                            author = c.getString("author");
                            publication = c.getString("publication");
                            category = c.getString("category");
                            quantity = c.getString("quantity");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("book_name", book_name);
                            fetchOrders.put("author", author);
                            fetchOrders.put("publication",publication);
                            fetchOrders.put("category",category);
                            fetchOrders.put("quantity",quantity);

                            contactJsonList.add(fetchOrders);
                        }
                        progressBar.setVisibility(View.GONE);
                        Result_Lable.setVisibility(View.GONE);
                    }

                } catch (final Exception e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        Fetch_Books.this, contactJsonList,
                        R.layout.fetch_books_list, new String[]{"book_name", "author", "publication", "category","quantity"}, new int[]{R.id.book_name,R.id.author,R.id.publication,R.id.category,R.id.available});

                DetailsListView.setAdapter(adapter);
                DetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final TextView BookName = (TextView) view.findViewById(R.id.book_name);
                        final TextView AuthorName = (TextView) view.findViewById(R.id.author);
                        final TextView Publication = (TextView) view.findViewById(R.id.publication);
                        final TextView Category = (TextView) view.findViewById(R.id.category);

                        book_name = BookName.getText().toString();
                        category = Category.getText().toString();
                        author = AuthorName.getText().toString();
                        publication = Publication.getText().toString();

                        Intent intent = new Intent(getApplication(), Student_Book_Info.class);
                        intent.putExtra("book_name", book_name);
                        intent.putExtra("author", author);
                        intent.putExtra("publication", publication);
                        intent.putExtra("category", category);
                        intent.putExtra("quantity", quantity);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                contactJsonList.clear();
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("book_category", book_category);
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
                    System.exit(0);
                }
            });
            dialog.show();
        }
    }

}