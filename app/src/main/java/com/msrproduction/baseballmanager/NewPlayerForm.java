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
		/*
		List<String> nameList = new ArrayList<>(newPlayers.playerName.size());
		List<String> numberList = new ArrayList<>(newPlayers.playerNumber.size());
		List<String> positionList = new ArrayList<>(newPlayers.playerPositions.size());
		for(int i = 0; i < newPlayers.numPlayers; i++) {
			if (!newPlayers.playerName.get(i).getText().toString().equals("") && !newPlayers.playerNumber.get(i).getText().toString().equals("")) {
				nameList.add(newPlayers.playerName.get(i).getText().toString());
				numberList.add(newPlayers.playerNumber.get(i).getText().toString());
				if (newPlayers.playerPositions.get(i).getText().toString().equals("")) {
					positionList.add("n/a");
				} else {
					positionList.add(newPlayers.playerPositions.get(i).getText().toString());
				}
			} else {
				//toast message to a field is empty or team wasn't selected
				Toast.makeText(getApplicationContext(),
						R.string.error_player_form_empty,
						Toast.LENGTH_SHORT).show();
				nameList.clear();
				numberList.clear();
				positionList.clear();
				return;
			}
		}
		databaseAdapter.bulkInsert(nameList, numberList, positionList, getSharedPreferences("coach_info", MODE_PRIVATE).getString("team_name", ""));
		*/
		newPlayers.addPlayers();
		finish();
	}
}
