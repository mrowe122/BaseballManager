package com.msrproduction.baseballmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract.PlayerEntry;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class PlayerInformation extends AppCompatActivity {

	private final String LOG_TAG = PlayerInformation.class.getSimpleName();
	private DatabaseAdapter databaseAdapter;
	private String playerId;
	private boolean dataChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_information);
		databaseAdapter = new DatabaseAdapter(this).open();
		initSetup();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.context_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit_selected:
				startActivityForResult(new Intent(PlayerInformation.this, EditPlayer.class)
						.putExtra("edit_player", playerId), 1);
				return true;
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 1:
				if (resultCode == 1) {
					loadPlayerStats(databaseAdapter.select(playerId, PlayerEntry.TABLE_NAME));
					setResult(1);
					break;
				}
		}
	}

	private void initSetup() {
		playerId = getIntent().getExtras().getString("player_id");
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		loadPlayerStats(databaseAdapter.select(playerId, PlayerEntry.TABLE_NAME));
	}

	private void loadPlayerStats(Cursor cursor) {
		cursor.moveToFirst();

		final TextView[] playerTextViews = new TextView[]{
				//name
				(TextView) findViewById(R.id.info_player_name),
				//number
				(TextView) findViewById(R.id.stats_player_number),
				//team
				(TextView) findViewById(R.id.stats_player_team),
				//position
				(TextView) findViewById(R.id.stats_player_position),
				//batting average
				(TextView) findViewById(R.id.stats_player_batting_average),
				//runs batted in
				(TextView) findViewById(R.id.stats_player_rbi),
				//number of runs scored
				(TextView) findViewById(R.id.stats_player_runs),
				//number of hits
				(TextView) findViewById(R.id.stats_player_hits),
				//total strikeouts
				(TextView) findViewById(R.id.stats_player_strikeouts),
				//how many times walked
				(TextView) findViewById(R.id.stats_player_walks),
				//how many singles
				(TextView) findViewById(R.id.stats_player_1b),
				//how many doubles
				(TextView) findViewById(R.id.stats_player_2b),
				//how many triples
				(TextView) findViewById(R.id.stats_player_3b),
				//how many home runs
				(TextView) findViewById(R.id.stats_player_home_runs),
				//how many fly balls
				(TextView) findViewById(R.id.stats_player_fly_ball),
				//how many grounders
				(TextView) findViewById(R.id.stats_player_grounders),
				//on base percentage
				(TextView) findViewById(R.id.stats_player_on_base_percentage),
				//how many bases stolen
				(TextView) findViewById(R.id.stats_player_bases_stolen),
				//how many times caught stealing
				(TextView) findViewById(R.id.stats_player_caught_stealing),
				//how many errors
				(TextView) findViewById(R.id.stats_player_errors),
				// field percentage
				(TextView) findViewById(R.id.stats_player_field_percentage),
				//how many outs player made
				(TextView) findViewById(R.id.stats_player_put_out),
				//bats
				(TextView) findViewById(R.id.stats_player_bats),
				//throws
				(TextView) findViewById(R.id.stats_player_throws)
		};

		playerTextViews[0].setText(cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_NAME)));
		playerTextViews[1].setText("#" + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_NUMBER)));
		playerTextViews[2].setText(cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_TEAM_NAME)));
		playerTextViews[3].setText(cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_POSITION)));
		playerTextViews[4].setText("AVG: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_BATTING_AVERAGE)));
		playerTextViews[5].setText("RBI: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_RBI)));
		playerTextViews[6].setText("Runs: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_RUNS)));
		playerTextViews[7].setText("H: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_HITS)));
		playerTextViews[8].setText("SO: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_STRIKE_OUTS)));
		playerTextViews[9].setText("BB: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_WALKS)));
		playerTextViews[10].setText("1B: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_SINGLE)));
		playerTextViews[11].setText("2B: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_DOUBLE)));
		playerTextViews[12].setText("3B: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_TRIPLE)));
		playerTextViews[13].setText("HR: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_HOME_RUNS)));
		playerTextViews[14].setText("FB: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_FLY_BALL)));
		playerTextViews[15].setText("GB: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_GROUND_BALLS)));
		playerTextViews[16].setText("OBP: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_ON_BASE_PERCENTAGE)));
		playerTextViews[17].setText("BS: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_BASES_STOLEN)));
		playerTextViews[18].setText("CS: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_CAUGHT_STEALING)));
		playerTextViews[19].setText("ER: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_ERRORS)));
		playerTextViews[20].setText("FPCT: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_FIELD_PERCENTAGE)));
		playerTextViews[21].setText("PO: " + cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_PUT_OUTS)));
		playerTextViews[22].setText(cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_BATS)));
		playerTextViews[23].setText(cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PLAYER_THROWS)));

		cursor.close();
	}
}
