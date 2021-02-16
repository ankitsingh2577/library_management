package com.ankit.library_management.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Map;

public class StudentViewReturnedFragment extends Fragment {

    public ListView Book_details;
    public TextView Hint_res;
    private ProgressBar progressBar;
    private static final String TAG = StudentViewReturnedFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> JsonList;
    private String Email,Urn,name,date,book,author,publication,phone;
    private SessionManager session;
    private DatabaseHandler db;
    private HashMap<String, String> user = new HashMap<>();

    public StudentViewReturnedFragment() {
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

        // session manager
        session = new SessionManager(getActivity());

        Email = user.get("email");
        Urn = user.get("urn_no");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        ViewReturnedBooks(Email,Urn);
        return view;
    }
    public void ViewReturnedBooks(final String email, final String urn) {
        progressBar.setVisibility(View.GONE);
        Hint_res.setVisibility(View.GONE);
        pDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.STUDENT_VIEW_RETURNED_BOOKS_URL, new Response.Listener<String>() {

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
                            phone = c.getString("phone");
                            book = c.getString("book_name");
                            author = c.getString("author_name");
                            publication = c.getString("publication");
                            date = c.getString("date");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("urn", Urn);
                            fetchOrders.put("email",Email);
                            fetchOrders.put("phone",phone);
                            fetchOrders.put("book_name",book);
                            fetchOrders.put("author_name",author);
                            fetchOrders.put("publication",publication);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
