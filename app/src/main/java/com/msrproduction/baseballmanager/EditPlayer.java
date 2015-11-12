package com.msrproduction.baseballmanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.PlayerSchema;

public class EditPlayer extends Activity {

	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_player);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		editFields(databaseAdapter.select(getIntent().getExtras().getString("edit_player"), Contract.PlayerEntry.TABLE_NAME));
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
		//edit player button
		(findViewById(R.id.edit_player)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editPlayer();
			}
		});

		//cancel button
		findViewById(R.id.cancel_edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	String name;
	String oldNumber;

	private void editFields(Cursor cursor) {
		cursor.moveToNext();
		name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME));
		oldNumber = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER));
		((TextView) findViewById(R.id.player_name)).setText(name);
		((EditText) findViewById(R.id.form_player_number)).setText(oldNumber);
	}

	private void editPlayer() {
		String number = ((EditText)findViewById(R.id.form_player_number)).getText().toString();
		databaseAdapter.updatePlayer(name, number, "n/a");
		finish();
	}
}
