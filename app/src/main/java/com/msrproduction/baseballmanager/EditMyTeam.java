package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class EditMyTeam extends AppCompatActivity {

	SharedPreferences sharedpreferences;
	private DatabaseAdapter databaseAdapter;
	private PlayerListAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		setContentView(R.layout.list_view_parallax);
		initSetup();
	}

	@Override
	public void onResume() {
		super.onResume();
		cursorAdapter.notifyDataSetChanged();
	}

	private void initSetup() {
		ListView listView = (ListView) findViewById(R.id.my_players_list);
		ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_coach_form, listView, false);
		listView.addHeaderView(header, null, false);
		registerForContextMenu(listView);

		SharedPreferences coachInfo = getSharedPreferences("coach_info", MODE_PRIVATE);
		((EditText) header.findViewById(R.id.form_coach_name)).setText(coachInfo.getString("coach_name", ""));
		((EditText) header.findViewById(R.id.form_coach_team)).setText(coachInfo.getString("team_name", ""));
		((EditText) header.findViewById(R.id.form_coach_email)).setText(coachInfo.getString("coach_email", ""));
		((EditText) header.findViewById(R.id.form_coach_phone)).setText(coachInfo.getString("coach_phone", ""));

		//hide/show appropiate views
		header.findViewById(R.id.buttons).setVisibility(View.GONE);
		header.findViewById(R.id.edit_roster).setVisibility(View.VISIBLE);
		findViewById(R.id.parallax_buttons).setVisibility(View.VISIBLE);

		//set edit roster to visible
		header.findViewById(R.id.edit_roster).setVisibility(View.VISIBLE);

		//add coach button
		findViewById(R.id.parallax_add_new_coach).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editCoach();
			}
		});

		//cancel button
		findViewById(R.id.parallax_cancel_coach).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		cursorAdapter = new PlayerListAdapter(getApplicationContext(), databaseAdapter.loadPlayers(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		//noinspection ConstantConditions
		listView.setAdapter(cursorAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(getApplicationContext(), PlayerInformation.class)
						.putExtra("player_id", id + ""));
			}
		});
	}

	private void editCoach() {
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
		finish();
	}

	private class PlayerListAdapter extends CursorAdapter {

		public PlayerListAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			String playerInfo = "#" + cursor.getInt(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)) + " " +
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME));
			((TextView) view.findViewById(R.id.list_item_name)).setText(playerInfo);
			playerInfo = "(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_POSITION)) + ")";
			((TextView) view.findViewById(R.id.list_item_sub_text)).setText(playerInfo);
		}
	}
}
