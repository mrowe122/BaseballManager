package com.msrproduction.baseballmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.NewPlayerModel;

public class NewTeamForm extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	SharedPreferences sharedpreferences;
	NewPlayerModel newPlayers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_form);
		newPlayers = new NewPlayerModel(this);
		initSetup();
	}

	private void initSetup() {
		//add team button
		(findViewById(R.id.add_new_team)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createTeam();
			}
		});

		//cancel button
		findViewById(R.id.cancel_team).setOnClickListener(new View.OnClickListener() {
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

	private void createTeam() {
	}
}