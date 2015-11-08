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
		String name = ((EditText) findViewById(R.id.form_coach_name)).getText().toString();
		String teamName = ((EditText) findViewById(R.id.form_coach_team)).getText().toString();
		String email = ((EditText) findViewById(R.id.form_coach_email)).getText().toString();
		String phone = ((EditText) findViewById(R.id.form_coach_phone)).getText().toString();

		sharedpreferences = getSharedPreferences("coachInfo", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();

		editor.putString("coach_name", name);
		editor.putString("team_name", teamName);
		editor.putString("coach_email", email);
		editor.putString("coach_phone", phone);
		editor.apply();
		finish();
	}
}