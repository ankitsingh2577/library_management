package com.ankit.library_management;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ankit.library_management.Navigation.DrawerAdapter;
import com.ankit.library_management.Navigation.DrawerItem;
import com.ankit.library_management.Navigation.SimpleItem;
import com.ankit.library_management.Navigation.SpaceItem;
import com.ankit.library_management.helper.DatabaseHandler;
import com.ankit.library_management.helper.Functions;
import com.ankit.library_management.helper.SessionManager;
import com.ankit.library_management.imageSliderFragment.CustomViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentHomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener {

    private SlidingRootNav slidingRootNav;
    private static final int Home = 0;
    private static final int My_Account = 1;
    private static final int SearchBook = 2;
    private static final int CancelReqest = 3;
    private static final int IssuedReturnBook = 4;
    private static final int Share = 6;
    private static final int About = 7;
    private static final int Contact_Dev = 8;
    private static final int Exit = 10;

    private String[] screenTitles;
    private Drawable[] screenIcons;
    private Toast toast;
    private long lastBackPressTime = 0;
    private ImageView Logout_btn;
    CardView card;
    ListView DetailsListView;
    ProgressDialog pDialog;
    TextView Result_Lable;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    public Spinner Book_Category;
    public String book_category;
    private DatabaseHandler db;
    private SessionManager session;
    private static final String TAG = Fetch_Books.class.getSimpleName();
    ArrayList<HashMap<String, String>> JsonList;
    String book_name,author,publication,category,quantity;
    private Handler handler;
    private static final long SLIDER_TIMER = 2000;
    private int currentPage = 0;
    private boolean isCountDownTimerActive = false;
    private TextView[] dots;
    int imageIds[] = {R.drawable.image_a, R.drawable.image_b, R.drawable.image_c};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Logout_btn = findViewById(R.id.logout_icon);
        card=findViewById(R.id.card);
        DetailsListView = findViewById(R.id.res_list);
        Result_Lable=findViewById(R.id.text_res);
        Book_Category = findViewById(R.id.category);
        viewPager = findViewById(R.id.view_pager_slider);
        dotsLayout = findViewById(R.id.layoutDots);

        if (!NetConnectivityCheck.isNetworkAvailable(StudentHomeActivity.this)) {
            ViewDialogNet alert = new ViewDialogNet();
            alert.showDialog(StudentHomeActivity.this, "Please, enable internet connection before using this app !!");
        }

        db = new DatabaseHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        JsonList = new ArrayList<>();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter drawadapter = new DrawerAdapter(Arrays.asList(
                createItemFor(Home).setChecked(true),
                createItemFor(My_Account),
                createItemFor(SearchBook),
                createItemFor(CancelReqest),
                createItemFor(IssuedReturnBook),
                new SpaceItem(48),
                createItemFor(Share),
                createItemFor(About),
                createItemFor(Contact_Dev),
                new SpaceItem(48),
                createItemFor(Exit)));

        drawadapter.setListener(this);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);

        drawadapter.setSelected(Home);
        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        handler = new Handler();

        handler.postDelayed(runnable, 4000);
        runnable.run();

        CustomViewPagerAdapter viewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position == 0) {
                    currentPage = 0;
                } else if (position == 1) {
                    currentPage = 1;
                } else {
                    currentPage = 2;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_Conformation();
            }
        });

        Book_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                book_category= parent.getItemAtPosition(position).toString();
                JsonList.clear();
                Result_Lable.setVisibility(View.VISIBLE);
                if (book_category.equals("Select Category")){

                }
                else{
                    SearchBooks(book_category);
                }
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

    public void SearchBooks(final String book_category) {

        pDialog.setMessage("Please wait ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.FETCH_BOOKS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d(TAG, "Fetch Books Response: " + response);
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

                            JsonList.add(fetchOrders);
                        }
                        Result_Lable.setVisibility(View.GONE);
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        StudentHomeActivity.this, JsonList,
                        R.layout.fetch_books_list, new String[]{"book_name", "author", "publication", "category","quantity"}, new int[]{R.id.book_name,R.id.author,R.id.publication,R.id.category,R.id.available});
                DetailsListView.setAdapter(adapter);
                DetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final TextView BookName = (TextView) view.findViewById(R.id.book_name);
                        final TextView AuthorName = (TextView) view.findViewById(R.id.author);
                        final TextView Publication = (TextView) view.findViewById(R.id.publication);
                        final TextView Category = (TextView) view.findViewById(R.id.category);
                        final TextView Availablity = (TextView) view.findViewById(R.id.available);

                        book_name = BookName.getText().toString();
                        category = Category.getText().toString();
                        author = AuthorName.getText().toString();
                        publication = Publication.getText().toString();
                        quantity = Availablity.getText().toString();

                        Intent intent = new Intent(getApplication(), Student_Book_Info.class);
                        intent.putExtra("book_name", book_name);
                        intent.putExtra("author", author);
                        intent.putExtra("publication", publication);
                        intent.putExtra("category", category);
                        intent.putExtra("quantity", quantity);
                        startActivity(intent);
                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                JsonList.clear();
                Log.e(TAG, "Fetch Books Error: " + error.getMessage());
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

        void showDialog(Activity activity, String msg) {
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
                    if (!NetConnectivityCheck.isNetworkAvailable(StudentHomeActivity.this)) {
                        ViewDialogNet alert = new ViewDialogNet();
                        alert.showDialog(StudentHomeActivity.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });

            dialog.show();
        }
    }

    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(StudentHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(int position) {
        if (position == Share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT," Hey! guys I just found an app on play store name "+"\"Smart Packet Analyser\""+" Click the link below to download this application. ");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT,"G E E N  B O X");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        if (position== My_Account)
        {
            startActivity(new Intent(this, StudentAccount.class));
            finish();
        }if (position== CancelReqest)
        {
            startActivity(new Intent(this, Student_Cancel_Book_Request.class));
        }if (position== IssuedReturnBook)
        {
            startActivity(new Intent(this, Student_View_Issued_Return.class));
        }
        if (position== Exit)
        {
            exit_Conformation();
        }
        slidingRootNav.closeMenu();
    }



    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.black))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.navfun))
                .withSelectedTextTint(color(R.color.navfun));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.StudentTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.StudentIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }
    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!isCountDownTimerActive) {
                automateSlider();
            }
            handler.postDelayed(runnable, 1000);

        }
    };

    private void automateSlider() {
        isCountDownTimerActive = true;
        new CountDownTimer(SLIDER_TIMER, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                int nextSlider = currentPage + 1;
                if (nextSlider == 3) {
                    nextSlider = 0;
                }

                viewPager.setCurrentItem(nextSlider);
                isCountDownTimerActive = false;
            }
        }.start();
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[3];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void logout_Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(StudentHomeActivity.this);

        dialogBuilder.setTitle("Logout");
        dialogBuilder.setMessage("Do you want to logout?");
        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
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
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Conform Delete Action Method
                        logoutUser();
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void exit_Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(StudentHomeActivity.this);

        dialogBuilder.setTitle("Exit");
        dialogBuilder.setMessage("Do you want to exit application?");

        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
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
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Conform Delete Action Method
                        System.exit(0);
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast=Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        }
        else {
            if (toast != null) {
                toast.cancel();
                finish();
            }
            super.onBackPressed();
        }
    }
}


