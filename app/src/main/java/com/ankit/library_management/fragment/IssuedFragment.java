package com.ankit.library_management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.MyApplication;
import com.ankit.library_management.QrView;
import com.ankit.library_management.R;
import com.ankit.library_management.helper.DatabaseHandler;
import com.ankit.library_management.helper.Functions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IssuedFragment extends Fragment {

    private static final String TAG = IssuedFragment.class.getSimpleName();
    private TextView BookName, AuthorName, StudentName, URN, Emial, Batch, Hint_Res;
    private Button Cancel;
    private ImageView QrImage;
    private Bitmap bitmap ;
    private QrView qrView;
    private String urn;
    private final static int QRcodeWidth = 500 ;
    private ProgressDialog pDialog;
    private DatabaseHandler db;
    private HashMap<String, String> user = new HashMap<>();

    public IssuedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issued, container, false);

        QrImage = view.findViewById(R.id.qr_image);
        qrView = view.findViewById(R.id.layout_ticket);
        Hint_Res = view.findViewById(R.id.hint_res);
        Cancel = view.findViewById(R.id.cancel);
        AuthorName = view.findViewById(R.id.author_name);
        BookName = view.findViewById(R.id.Book_name);
        StudentName = view.findViewById(R.id.name);
        URN = view.findViewById(R.id.urn);
        Emial = view.findViewById(R.id.email);
        Batch = view.findViewById(R.id.batch);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        db = new DatabaseHandler(getActivity());
        user = db.getUserDetails();

        urn = user.get("urn_no");

        FetchQR(urn);

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_Conformation();
            }
        });
        return view;
    }

    private void cancel(final String urn) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Cancelling request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.Cancel_Issue_BOOK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cancel Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        final String resp = jObj.getString("message");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), resp, Toast.LENGTH_SHORT).show();
                            }
                        });
                        FetchQR(urn);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e(TAG, "Cancel Error: " + error.getMessage(), error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("urn", urn);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void FetchQR(final String urn) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        qrView.setVisibility(View.GONE);
        Cancel.setVisibility(View.GONE);
        Hint_Res.setVisibility(View.GONE);
        pDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.Search_Pending_Issue_BOOK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cancel Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Cancel.setVisibility(View.VISIBLE);
                        qrView.setVisibility(View.VISIBLE);
                        final String resp = jObj.getString("message");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
                            }
                        });
                        String name = jObj.getString("name");
                        String urn = jObj.getString("urn");
                        String email = jObj.getString("email");
                        String course = jObj.getString("course");
                        String year = jObj.getString("year");
                        String semester = jObj.getString("semester");
                        String author = jObj.getString("author_name");
                        String book_name = jObj.getString("book_name");
                        String code = jObj.getString("approval_code");
                        String batch=course+"_"+year+"_"+semester;
                        bitmap = TextToImageEncode(code);
                        QrImage.setImageBitmap(bitmap);

                        StudentName.setText(name);
                        URN.setText(urn);
                        Emial.setText(email);
                        Batch.setText(batch);
                        AuthorName.setText(author);
                        BookName.setText(book_name);

                    } else {
                        String resp = jObj.getString("message");
                        //Toast.makeText(Student_Cancel_Issue_Book.this, resp, Toast.LENGTH_SHORT).show();
                        Hint_Res.setVisibility(View.VISIBLE);
                        Hint_Res.setText("No pending approval found!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e(TAG, "Cancel Error: " + error.getMessage(), error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "error"+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("urn", urn);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void cancel_Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Cancellation");
        dialogBuilder.setMessage("Do you want to cancel request?");
        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                        //Conform Delete Action Method
                        cancel(urn);
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }
}