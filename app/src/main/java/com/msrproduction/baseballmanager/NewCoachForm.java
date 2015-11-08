package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.NewPlayerModel;

import java.util.ArrayList;

public class NewCoachForm extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	SharedPreferences sharedpreferences;
	NewPlayerModel newPlayers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_coach_form);
		newPlayers = new NewPlayerModel(this);
		initSetup();
	}

	private void initSetup() {
		//add team button
		(findViewById(R.id.add_new_coach)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addCoach();
			}
		});

		//cancel button
		findViewById(R.id.cancel_coach).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(0);
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

	private void addCoach() {
		String name = ((EditText) findViewById(R.id.form_coach_name)).getText().toString();
		if (name.equals("")) {
			Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
			return;
		}
		String teamName = ((EditText) findViewById(R.id.form_coach_team)).getText().toString();
		if (teamName.equals("")) {
			Toast.makeText(this, R.string.error_empty_team, Toast.LENGTH_SHORT).show();
			return;
		}
		String email = ((EditText) findViewById(R.id.form_coach_email)).getText().toString();
		if (email.equals(""))
			email = "n/a";
		String phone = ((EditText) findViewById(R.id.form_coach_phone)).getText().toString();
		if (phone.equals(""))
			phone = "n/a";

		sharedpreferences = getSharedPreferences("coach_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();

		editor.putString("coach_name", name);
		editor.putString("team_name", teamName);
		editor.putString("coach_email", email);
		editor.putString("coach_phone", phone);
		editor.apply();
		getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isCoachSetup", true).apply();
		setResult(1);
		finish();
	}
}
