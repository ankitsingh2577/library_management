package com.ankit.library_management.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.ankit.library_management.ContactStudent;
import com.ankit.library_management.MyApplication;
import com.ankit.library_management.NetConnectivityCheck;
import com.ankit.library_management.R;
import com.ankit.library_management.helper.DatabaseHandler;
import com.ankit.library_management.helper.Functions;
import com.ankit.library_management.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibrarianViewIssuedFragment extends Fragment {

    public ListView Book_details;
    public TextView Hint_res;
    private ProgressBar progressBar;
    private static final String TAG = LibrarianViewIssuedFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> JsonList;
    private String Email,Urn,name,urn,email,date,book,author,publication,phone;
    private SessionManager session;
    private DatabaseHandler db;

    public LibrarianViewIssuedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_librarian_view_issued, container, false);

        Book_details = view.findViewById(R.id.res_list);
        Hint_res = view.findViewById(R.id.text_res);
        progressBar = view.findViewById(R.id.ProgressBar1);
        JsonList = new ArrayList<>();
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // session manager
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        ViewIssuedBooks();
        return view;
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
                            author = c.getString("author_name");
                            publication = c.getString("publication");
                            date = c.getString("date");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("urn", urn);
                            fetchOrders.put("email",email);
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
                        Intent i=new Intent(getActivity(), ContactStudent.class);
                        i.putExtra("name",name);
                        i.putExtra("urn",urn);
                        i.putExtra("email",email);
                        i.putExtra("phone",phone);
                        i.putExtra("book",book);
                        i.putExtra("author",author);
                        i.putExtra("publication",publication);
                        i.putExtra("date",date);
                        startActivity(i);
                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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
                    if (!NetConnectivityCheck.isNetworkAvailable(getActivity())) {
                        ViewDialogNet alert = new LibrarianViewIssuedFragment.ViewDialogNet();
                        alert.showDialog(getActivity(), "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }
}
