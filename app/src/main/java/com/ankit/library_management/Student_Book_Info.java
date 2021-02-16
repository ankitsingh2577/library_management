package com.ankit.library_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.helper.DatabaseHandler;
import com.ankit.library_management.helper.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Student_Book_Info extends AppCompatActivity {

    TextView Book_Name,Author,Publication,Category,Number;
    String Name,Surname,Fullname,Email,Urn,Course,Year,Sem,book_name,author,publication,category,quanity,code,date,time;
    Button Book_Now;
    private ProgressDialog pDialog;
    private DatabaseHandler db;
    int min = 0, max = 999999999, random=0,num=0;
    private HashMap<String, String> user = new HashMap<>();
    private static final String TAG = Student_Book_Info.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_book_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Book_Name=findViewById(R.id.book_name);
        Author=findViewById(R.id.author);
        Publication=findViewById(R.id.publication);
        Category=findViewById(R.id.category);
        Number=findViewById(R.id.available);
        Book_Now=findViewById(R.id.book_now);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            book_name =(String) b.get("book_name");
            author =(String) b.get("author");
            publication =(String) b.get("publication");
            category =(String) b.get("category");
            quanity =(String) b.get("quantity");
            //Toast.makeText(this, table_name, Toast.LENGTH_SHORT).show();
        }

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        // Fetching user details from database
        Name = user.get("name");
        Surname = user.get("surname");
        Email = user.get("email");
        Urn = user.get("urn_no");
        Course = user.get("course");
        Year = user.get("year");
        Sem = user.get("semester");

        Fullname=Name+" "+Surname;

        random = new Random().nextInt((max - min) - 1) + min;

        code = Integer.toString(random);

        if (quanity.equals(Integer.toString(num))){
            Book_Now.setText("Out Of Stock");
            Book_Name.setText(book_name);
            Author.setText(author);
            Publication.setText(publication);
            Category.setText(category);
            Number.setText(quanity);
            Book_Now.setEnabled(false);
        }
        else {
            Book_Name.setText(book_name);
            Author.setText(author);
            Publication.setText(publication);
            Category.setText(category);
            Number.setText(quanity);
            Book_Now.setEnabled(true);
        }
        Book_Now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               VerifyUser(Email);
            }
        });
    }

    public void VerifyUser(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Initiating your booking...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VERIFY_USER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Verify User Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String Msg = jObj.getString("verify");
                        if (Msg.equals("0")){
                            Toast.makeText(Student_Book_Info.this, "Sorry... your account is blocked \nContact admin", Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                        else if (Msg.equals("1")){
                            BookNow(Fullname, Urn, Email, Course, Year, Sem, book_name, author, publication, category, quanity, code);
                        }
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
                        hideDialog();
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
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
                params.put("email", email);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

     public void BookNow(final String name,final String urn, final String email, final String course, final String year, final String sem, final String bookname, final String Author, final String publication, final String category, final String quantity,final String cod) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.Initiate_BOOK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Booking Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        hideDialog();
                        String resp = jObj.getString("message");
                        Toast.makeText(Student_Book_Info.this, resp, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                        finish();
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
                        hideDialog();
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Booking Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("urn", urn);
                params.put("email", email);
                params.put("course", course);
                params.put("year", year);
                params.put("sem", sem);
                params.put("book_name", bookname);
                params.put("author", Author);
                params.put("publication", publication);
                params.put("category", category);
                params.put("quantity", quantity);
                params.put("code", cod);
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
