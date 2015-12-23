package com.example.accounting;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class OperDescriptionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oper_description);
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            String billFrom = extras.getString(getString(R.string.billfrom));
            String billTo = extras.getString(getString(R.string.billto));
            String category = extras.getString(getString(R.string.category));
            String descrpition = extras.getString(getString(R.string.description));
            TextView bf = (TextView) findViewById(R.id.billfromT);
            TextView bt = (TextView) findViewById(R.id.billtoT);
            TextView cat = (TextView) findViewById(R.id.categoryT);
            TextView des = (TextView) findViewById(R.id.descriptionT);
            bf.setText(billFrom);
            bt.setText(billTo);
            cat.setText(category);
            des.setText(descrpition);
        }
    }


}
