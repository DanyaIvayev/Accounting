package com.example.accounting;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DescriptionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String description = extras.getString(getString(R.string.description));
            TextView descTV = (TextView) findViewById(R.id.descriptionTV);
            descTV.setText(description);
        }
    }


}
