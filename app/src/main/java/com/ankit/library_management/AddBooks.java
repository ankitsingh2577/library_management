package com.ankit.library_management;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.helper.Functions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBooks extends AppCompatActivity {

    private Spinner Category,Quantity;
    private String cate,quant;
    private Button Submit;
    private ProgressDialog pDialog;
    private TextInputEditText Name,Author,Publication;
    private static final String TAG = AddBooks.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Submit=findViewById(R.id.submit);
        Category=findViewById(R.id.categ);
        Quantity=findViewById(R.id.quantt);
        Name=findViewById(R.id.lEditBook);
        Author=findViewById(R.id.lEditAuthor);
        Publication=findViewById(R.id.lEditPublication);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Spinner click listener
        Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cate = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quant = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final List<String> category = new ArrayList<String>();
        category.add("Select Category");
        category.add("Storage");
        category.add("Architecture");
        category.add("Cloud Computing");
        category.add("Data Science");
        category.add("Machine Learning");
        category.add("Artificial Intelligence");
        category.add("Soft Skills");
        category.add("Novels");

        final List<String> quantity = new ArrayList<String>();
        quantity.add("Select Quantity");
        quantity.add("1");
        quantity.add("2");
        quantity.add("3");
        quantity.add("4");
        quantity.add("5");
        quantity.add("6");
        quantity.add("7");
        quantity.add("8");
        quantity.add("9");
        quantity.add("10");
        quantity.add("11");
        quantity.add("12");
        quantity.add("13");
        quantity.add("14");
        quantity.add("15");
        quantity.add("16");
        quantity.add("17");
        quantity.add("18");
        quantity.add("19");
        quantity.add("20");

        Category.setSelection(0);
        Quantity.setSelection(0);

        ArrayAdapter<String> CategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);
        ArrayAdapter<String> QuantityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quantity);

        CategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        QuantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Category.setAdapter(CategoryAdapter);
        Quantity.setAdapter(QuantityAdapter);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Name.getText().toString();
                String author = Author.getText().toString();
                String publication = Publication.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter book name !", Toast.LENGTH_SHORT).show();
                }
                else if (author.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter author name !", Toast.LENGTH_SHORT).show();
                }
                else if (publication.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter publication name !", Toast.LENGTH_SHORT).show();
                }
                else if (cate.contains("Select Category")){
                    Toast.makeText(getApplicationContext(), "Please select quantity !", Toast.LENGTH_SHORT).show();
                }
                else if (quant.contains("Select Quantity")){
                    Toast.makeText(getApplicationContext(), "Please select quantity !", Toast.LENGTH_SHORT).show();
                }
                else {
                    AddBook(name, author, publication, cate, quant);
                    getCurrentFocus().clearFocus();
                }
            }
        });
    }

    private void AddBook(final String name, final String author, final String publication, final String cate, final String quant) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("Uploading Book...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.Add_BOOK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String resp = jObj.getString("message");
                        Toast.makeText(AddBooks.this, resp, Toast.LENGTH_SHORT).show();
                        Name.setText("");
                        Author.setText("");
                        Publication.setText("");
                        Category.setSelection(0);
                        Quantity.setSelection(0);
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
                params.put("name", name);
                params.put("author", author);
                params.put("publication", publication);
                params.put("cate", cate);
                params.put("qunat", quant);
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

}
