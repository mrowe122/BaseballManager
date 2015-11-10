package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.NewPlayerModel;

import java.util.ArrayList;
import java.util.List;

public class NewPlayerForm extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	NewPlayerModel newPlayers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_form);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		newPlayers = new NewPlayerModel(this);
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
		newPlayers.addField();

		//add player button
		(findViewById(R.id.add_new_player)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addPlayer();
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
		findViewById(R.id.add_new_field).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newPlayers.addField();
			}
		});
	}

	private void addPlayer() {
		//if adding players is true, finish
		if (newPlayers.addPlayers()) {
			finish();
		}
	}
}
