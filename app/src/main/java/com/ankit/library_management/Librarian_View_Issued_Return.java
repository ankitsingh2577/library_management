package com.ankit.library_management;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ankit.library_management.fragment.LibrarianViewIssuedFragment;
import com.ankit.library_management.fragment.LibrarianViewReturnedFragment;
import com.ankit.library_management.helper.DatabaseHandler;
import com.ankit.library_management.helper.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Librarian_View_Issued_Return extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SessionManager session;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_view_issued_return_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!NetConnectivityCheck.isNetworkAvailable(Librarian_View_Issued_Return.this)) {
            ViewDialogNet alert = new Librarian_View_Issued_Return.ViewDialogNet();
            alert.showDialog(Librarian_View_Issued_Return.this, "Please, enable internet connection before using this app !!");
        }
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        db = new DatabaseHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }
    public class ViewDialogNet {

        void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(Librarian_View_Issued_Return.this)) {
                        ViewDialogNet alert = new Librarian_View_Issued_Return.ViewDialogNet();
                        alert.showDialog(Librarian_View_Issued_Return.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });

            dialog.show();
        }
    }
    private void setupTabIcons() {

        // create tab with text and icon

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        // set text
        tabOne.setText("Issued Books");
        // set icon
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_refresh_white, 0, 0);
        // set postion
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Returned Books");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_return_white, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new Librarian_View_Issued_Return.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LibrarianViewIssuedFragment(), "Issued Books");
        adapter.addFrag(new LibrarianViewReturnedFragment(), "Returned Books");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(Librarian_View_Issued_Return.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
