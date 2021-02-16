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
import com.ankit.library_management.helper.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnblockStudentFragment extends Fragment {

    private ListView DetailsListView;
    private ProgressBar progressBar;
    private TextView Result_Lable;
    private static final String TAG = UnblockStudentFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> JsonList;
    private Spinner Course,Year,Sem;
    private ProgressDialog pDialog;
    private String course,yr,sem,status="1",surname,name,email,urn,Fullname,first,full;
    private Button Search;

    public UnblockStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unblock_student, container, false);

        DetailsListView = view.findViewById(R.id.listView_search_result);
        progressBar = view.findViewById(R.id.progressbar);
        Search= view.findViewById(R.id.search);
        Result_Lable= view.findViewById(R.id.res_lable);
        Course = view.findViewById(R.id.course);
        Year = view.findViewById(R.id.year);
        Sem = view.findViewById(R.id.sem);

        JsonList = new ArrayList<>();

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                course = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Table_name=lec+"_"+bat+"_"+semi;
                if (course.contains("Select Lecture")){
                    Toast.makeText(getActivity(), "Please select lecture !", Toast.LENGTH_SHORT).show();
                }
                else if (yr.contains("Select Batch")){
                    Toast.makeText(getActivity(), "Please select batch !", Toast.LENGTH_SHORT).show();
                }
                else if (sem.contains("Select Semester")){
                    Toast.makeText(getActivity(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else {
                    JsonList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    ViewStudent(course,yr,sem);
                }
            }
        });

        List<String> Cour = new ArrayList<String>();
        Cour.add("Select Course");
        Cour.add("B.Tech CTIS");
        Cour.add("B.Tech MACT");
        Cour.add("B.Tech ITDS");
        Cour.add("B.C.A CTIS");
        Cour.add("B.C.A MACT");
        Cour.add("B.C.A ITDS");

        List<String> year = new ArrayList<String>();
        year.add("Select Year");
        year.add("1st Year");
        year.add("2nd Year");
        year.add("3rd Year");
        year.add("4th Year");

        List<String> seme = new ArrayList<String>();
        seme.add("Select Semester");
        seme.add("I");
        seme.add("II");
        seme.add("III");
        seme.add("IV");
        seme.add("V");
        seme.add("VI");
        seme.add("VII");
        seme.add("VIII");

        Course.setSelection(0);
        Year.setSelection(0);
        Sem.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> LectureAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Cour);
        ArrayAdapter<String> BatchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, year);
        ArrayAdapter<String> SemAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, seme);

        // Drop down layout style - list view with radio button
        LectureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BatchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Course.setAdapter(LectureAdapter);
        Year.setAdapter(BatchAdapter);
        Sem.setAdapter(SemAdapter);
        return view;
    }
    private void ViewStudent(final String course, final String yr, final String sem) {
        progressBar.setVisibility(View.GONE);
        Result_Lable.setVisibility(View.GONE);
        JsonList.clear();
        pDialog.setMessage("Please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VIEW_UNBLOCK_STUDNETS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d(TAG, "Search user Response: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONArray jsonObject = new JSONArray(response);

                    if(response !=null) {
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);

                            name = c.getString("name");
                            surname = c.getString("surname");
                            urn = c.getString("urn_no");
                            email = c.getString("email");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("surname", surname);
                            fetchOrders.put("urn_no", urn);
                            fetchOrders.put("email",email);
                            Fullname=name+" "+surname;
                            JsonList.add(fetchOrders);
                        }

                    }

                } catch (final Exception e) {
                    JsonList.clear();
                    progressBar.setVisibility(View.GONE);
                    Result_Lable.setText("No block user found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        getActivity(), JsonList,
                        R.layout.search_students_list, new String[]{"name", "surname", "urn_no", "email"}, new int[]{R.id.name,R.id.surname,R.id.urn,R.id.email});

                DetailsListView.setAdapter(adapter);

                DetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final TextView Email = (TextView) view.findViewById(R.id.email);
                        final TextView First = (TextView) view.findViewById(R.id.name);
                        final TextView Surname = (TextView) view.findViewById(R.id.surname);
                        email = Email.getText().toString();
                        first = First.getText().toString();
                        surname = Surname.getText().toString();
                        full=first+" "+surname;
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("course", course);
                params.put("year", yr);
                params.put("semester", sem);
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void UnblockUser(final String email, final String status) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.UNBLOCK_STUDNETS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Unblock user Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getActivity(),"User unblocked successfully !", Toast.LENGTH_LONG).show();
                        ViewStudent(course,yr,sem);
                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unblock user Error: " + error.getMessage(), error);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("status", status);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Block User");
        dialogBuilder.setMessage("Name: "+full+"\n"+"Email: "+email);
        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Unblock", new DialogInterface.OnClickListener() {
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
                        UnblockUser(email,status);
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