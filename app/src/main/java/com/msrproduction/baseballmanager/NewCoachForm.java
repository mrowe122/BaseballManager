package com.msrproduction.baseballmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.msrproduction.baseballmanager.plugins.UsPhoneNumberFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

		AutoCompleteTextView editTextLogin = (AutoCompleteTextView) findViewById(R.id.form_coach_email);
		Account[] accounts = AccountManager.get(this).getAccounts();
		Set<String> emailSet = new HashSet<>();
		for (Account account : accounts) {
			emailSet.add(account.name);
		}
		editTextLogin.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(emailSet)));
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
/*
		String formattedPhone = phone.getText().toString();
		if(!phoneNumberFormatter.getError()) {
			if (formattedPhone.equals(""))
				formattedPhone = "n/a";
		} else {
			Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show();
			return;
		}
*/
		SharedPreferences sharedpreferences = getSharedPreferences("coach_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString("coach_name", name);
		editor.putString("team_name", teamName);
		editor.apply();
		getSharedPreferences("FirstRunPreference", MODE_PRIVATE).edit().putBoolean("isCoachSetup", true).apply();
		setResult(1);
		finish();
	}
}
