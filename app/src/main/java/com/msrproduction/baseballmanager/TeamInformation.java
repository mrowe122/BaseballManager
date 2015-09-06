package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.ListView;
import com.msrproduction.baseballmanager.plugins.OnDetectScrollListener;

public class TeamInformation extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	private String teamId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_information);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		teamId = getIntent().getExtras().getString("team_id");
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadTeamStats(databaseAdapter.select(teamId, Contract.TeamEntry.TABLE_NAME));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_team, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit_selected:
				/*startActivity(new Intent(TeamInformation.this, EditActivityTeam.class)
						.putExtra("edit_team", teamId));*/
				return true;
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void loadTeamStats(Cursor cursor) {
		cursor.moveToNext();
		//view for the teams name
		((TextView) findViewById(R.id.stats_team_name))
				.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_NAME)));

		//view for the coaches name
		((TextView) findViewById(R.id.stats_coach_name))
				.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_COACH)));

		//view for number of wins
		((TextView) findViewById(R.id.stats_team_win))
				.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_WINS)));

		//view for the number of losses
		((TextView) findViewById(R.id.stats_team_lose))
				.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_LOSE)));

		ListView listView = (ListView) findViewById(R.id.players_list);
		Cursor playersInTeams = databaseAdapter.loadPlayersInTeams(cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_NAME)));
		((TextView) findViewById(R.id.stats_team_total_players)).setText(playersInTeams.getCount() + " players");
		listView.setAdapter(new PlayerListAdapter(getApplicationContext(), playersInTeams, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(getApplicationContext(), PlayerInformation.class)
						.putExtra("player_id", id + ""));
			}
		});
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		listView.setOnDetectScrollListener(new OnDetectScrollListener() {
			@Override
			public void onUpScrolling() {
				fab.show();
			}

			@Override
			public void onDownScrolling() {
				fab.hide();
			}
		});
		cursor.close();
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
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_POSITION)));
		}
	}
}
