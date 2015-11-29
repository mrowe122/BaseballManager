package com.msrproduction.baseballmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.msrproduction.baseballmanager.plugins.UsPhoneNumberFormatter;

import java.lang.ref.WeakReference;

public class NewCoachForm extends AppCompatActivity {

	private EditText phone;
	private UsPhoneNumberFormatter phoneNumberFormatter;

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

		phone = (EditText) findViewById(R.id.form_coach_phone);
		phoneNumberFormatter = new UsPhoneNumberFormatter(new WeakReference<>(phone), (TextInputLayout) findViewById(R.id.input_layout_phone), this);
		phone.addTextChangedListener(phoneNumberFormatter);
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

		String formattedPhone = phone.getText().toString();
		if(!phoneNumberFormatter.getError()) {
			if (formattedPhone.equals(""))
				formattedPhone = "n/a";
		} else {
			Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show();
			return;
		}

		SharedPreferences sharedpreferences = getSharedPreferences("coach_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString("coach_name", name);
		editor.putString("team_name", teamName);
		editor.putString("coach_email", email);
		editor.putString("coach_phone", formattedPhone);
		editor.apply();
		getSharedPreferences("FirstRunPreference", MODE_PRIVATE).edit().putBoolean("isCoachSetup", true).apply();
		setResult(1);
		finish();
	}
}
