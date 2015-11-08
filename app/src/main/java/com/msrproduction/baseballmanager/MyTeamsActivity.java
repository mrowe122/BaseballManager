package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class MyTeamsActivity extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_team);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		initSetup();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_new_player:
				startActivity(new Intent(MyTeamsActivity.this, NewPlayerForm.class));
				break;
			case R.id.action_settings:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_my_team, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		readPlayers();
	}

	private void readPlayers() {
		//noinspection ConstantConditions
		final ListView listView = (ListView) findViewById(R.id.my_players_list);
		registerForContextMenu(listView);
		listView.setAdapter(new PlayerListAdapter(getApplicationContext(), databaseAdapter.loadPlayers(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(getApplicationContext(), PlayerInformation.class)
						.putExtra("player_id", id + ""));
			}
		});
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
			((TextView) view.findViewById(R.id.list_item_name)).setText("#" +
					cursor.getInt(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)) + " " +
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME)));
			((TextView) view.findViewById(R.id.list_item_sub_text)).setText(
					"(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME)) + ")");
		}
	}

	private void initSetup() {
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		SharedPreferences coachInfo = getSharedPreferences("coachInfo", MODE_PRIVATE);
		((TextView) findViewById(R.id.my_coach_name)).setText(coachInfo.getString("coach-name", ""));
		((TextView) findViewById(R.id.my_team_name)).setText(coachInfo.getString("team-name", ""));
		if (coachInfo.getString("coach-email", "").equals("")) {
			((TextView) findViewById(R.id.my_email)).setText("n/a");
		}

		if (coachInfo.getString("coach-phone", "").equals("")) {
			((TextView) findViewById(R.id.my_phone)).setText("n/a");
		}
	}
}
