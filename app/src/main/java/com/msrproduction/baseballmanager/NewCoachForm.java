package com.msrproduction.baseballmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewCoachForm extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coach_form);
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

		SharedPreferences sharedpreferences = getSharedPreferences("team_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString("coach_name", name);
		editor.putString("team_name", teamName);
		editor.apply();
		getSharedPreferences("AppPreferences", MODE_PRIVATE).edit().putBoolean("isTeamSetup", true).apply();
		setResult(1);
		finish();
	}
}
