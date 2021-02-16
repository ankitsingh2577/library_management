package com.ankit.library_management;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class ContactStudent extends AppCompatActivity {

    TextView Name, Urn, Email, Phone, Book, Author, Publication, Date;
    String name, urn, email, phone, book, author, publication, date;
    Button Call, Eml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name = findViewById(R.id.name);
        Urn = findViewById(R.id.urn);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.phone);
        Book = findViewById(R.id.book_name);
        Author = findViewById(R.id.author_name);
        Publication = findViewById(R.id.publication);
        Date = findViewById(R.id.date);
        Call = findViewById(R.id.call);
        Eml = findViewById(R.id.eml);

        Intent in = getIntent();
        Bundle b = in.getExtras();

        if (b != null) {
            name = (String) b.get("name");
            urn = (String) b.get("urn");
            email = (String) b.get("email");
            phone = (String) b.get("phone");
            book = (String) b.get("book");
            author = (String) b.get("author");
            publication = (String) b.get("publication");
            date = (String) b.get("date");

            //Toast.makeText(this, table_name, Toast.LENGTH_SHORT).show();
            Name.setText(name);
            Urn.setText(urn);
            Email.setText(email);
            Phone.setText(phone);
            Book.setText(book);
            Author.setText(author);
            Publication.setText(publication);
            Date.setText(date);
        }

        Eml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });
    }

    private void sendEmail() {
        Intent intent = new Intent();
        String[] recipients = {email};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Library Management");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    private void makeCall() {
        String uri = "tel:" + phone.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
}
