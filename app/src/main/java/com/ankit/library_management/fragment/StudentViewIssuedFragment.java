package com.ankit.library_management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.MyApplication;
import com.ankit.library_management.R;
import com.ankit.library_management.helper.DatabaseHandler;
import com.ankit.library_management.helper.Functions;
import com.ankit.library_management.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StudentViewIssuedFragment extends Fragment {

    public ListView Book_details;
    public TextView Hint_res;
    private ProgressBar progressBar;
    private static final String TAG = StudentViewReturnedFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> JsonList;
    private String Email, Urn,urn_no, name, date, book, author, publication,category,course,year,sem,phone,code;
    private SessionManager session;
    int min = 0, max = 999999999, random=0;
    private DatabaseHandler db;
    private HashMap<String, String> user = new HashMap<>();


    public StudentViewIssuedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_view_issued_returned, container, false);

        Book_details = view.findViewById(R.id.res_list);
        Hint_res = view.findViewById(R.id.text_res);
        progressBar = view.findViewById(R.id.ProgressBar1);

        JsonList = new ArrayList<>();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        db = new DatabaseHandler(getActivity());
        user = db.getUserDetails();

        random = new Random().nextInt((max - min) - 1) + min;

        code = Integer.toString(random);

        // session manager
        session = new SessionManager(getActivity());

        Email = user.get("email");
        Urn = user.get("urn_no");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        ViewIssuedBooks(Email,Urn);

        return view;
    }
    public void ViewIssuedBooks(final String email, final String urn) {
        progressBar.setVisibility(View.GONE);
        Hint_res.setVisibility(View.GONE);
        JsonList.clear();
        pDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.STUDENT_VIEW_ISSUED_BOOKS_URL, new Response.Listener<String>() {

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
                            Urn = c.getString("urn");
                            Email = c.getString("email");
                            course = c.getString("course");
                            year = c.getString("year");
                            sem = c.getString("semester");
                            phone = c.getString("phone");
                            book = c.getString("book_name");
                            author = c.getString("author_name");
                            publication = c.getString("publication");
                            category = c.getString("category");
                            date = c.getString("date");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("urn", Urn);
                            fetchOrders.put("email",Email);
                            fetchOrders.put("course",course);
                            fetchOrders.put("year",year);
                            fetchOrders.put("semester",sem);
                            fetchOrders.put("phone",phone);
                            fetchOrders.put("book_name",book);
                            fetchOrders.put("author_name",author);
                            fetchOrders.put("publication",publication);
                            fetchOrders.put("category",category);
                            fetchOrders.put("date",date);

                            JsonList.add(fetchOrders);
                        }
                        progressBar.setVisibility(View.GONE);
                        Hint_res.setVisibility(View.GONE);
                    }
                } catch (final Exception e ) {
                    JsonList.clear();
                    progressBar.setVisibility(View.GONE);
                    Hint_res.setVisibility(View.VISIBLE);
                    Hint_res.setText("No data found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        getActivity(), JsonList,
                        R.layout.studnet_view_issued_books_list, new String[]{"name", "urn", "email", "phone","book_name", "author_name", "publication", "date"}, new int[]{R.id.name,R.id.urn,R.id.email,R.id.phone,R.id.book_name,R.id.author_name,R.id.publication,R.id.date});

                Book_details.setAdapter(adapter);

                Book_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final TextView Name = (TextView) view.findViewById(R.id.name);
                        final TextView Urn = (TextView) view.findViewById(R.id.urn);
                        final TextView Eml = (TextView) view.findViewById(R.id.email);
                        final TextView Phone = (TextView) view.findViewById(R.id.phone);
                        final TextView BookName = (TextView) view.findViewById(R.id.book_name);
                        final TextView AuthorName = (TextView) view.findViewById(R.id.author_name);
                        final TextView Publication = (TextView) view.findViewById(R.id.publication);

                        name = Name.getText().toString();
                        urn_no = Urn.getText().toString();
                        Email = Eml.getText().toString();
                        phone = Phone.getText().toString();
                        book = BookName.getText().toString();
                        author = AuthorName.getText().toString();
                        publication = Publication.getText().toString();

                        Conformation();
                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("urn", urn);
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
    private void ReturnBook(final String Name, final String Urn, final String Email, final String Course, final String Year, final String Semester, final String Phone, final String Book, final String Author,final String Publication, final String Category,final String Code) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Initiating return please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.STUDNET_RETURN_BOOK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Return book Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        ViewIssuedBooks(Email,Urn);
                        String Msg = jObj.getString("message");
                        Toast.makeText(getActivity(),Msg, Toast.LENGTH_LONG).show();

                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Return book Error: " + error.getMessage(), error);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("name", Name);
                params.put("urn", Urn);
                params.put("email", Email);
                params.put("course", Course);
                params.put("year", Year);
                params.put("sem", Semester);
                params.put("phone", Phone);
                params.put("book_name", Book);
                params.put("author", Author);
                params.put("publication", Publication);
                params.put("category", Category);
                params.put("code", Code);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Return Book");
        dialogBuilder.setMessage("Book Name: "+book+"\n"+"Author: "+author);
        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Conform Return Action Method
                        ReturnBook(name,Urn,Email,course,year,sem,phone,book,author,publication,category,code);
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
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