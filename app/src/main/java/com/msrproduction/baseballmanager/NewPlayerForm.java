package com.msrproduction.baseballmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class NewPlayerForm extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_form);
        initSetup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSetup() {
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //add player button
        (findViewById(R.id.add_new_player)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addTeam();
                finish();
            }
        });

        //cancel button
        findViewById(R.id.cancel_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //add field button
        /*findViewById(R.id.add_new_field).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addField();
            }
        });*/
    }
}
