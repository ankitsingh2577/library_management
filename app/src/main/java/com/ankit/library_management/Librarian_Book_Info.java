package com.ankit.library_management;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class Librarian_Book_Info extends AppCompatActivity {

    TextView Book_Name,Author,Publication,Category,Number;
    String book_name,author,publication,category,quanity;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_book_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Book_Name=findViewById(R.id.book_name);
        Author=findViewById(R.id.author);
        Publication=findViewById(R.id.publication);
        Category=findViewById(R.id.category);
        Number=findViewById(R.id.available);

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
            Book_Name.setText(book_name);
            Author.setText(author);
            Publication.setText(publication);
            Category.setText(category);
            Number.setText(quanity);
        }
    }
}
