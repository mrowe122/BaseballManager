package com.msrproduction.baseballmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class PlayersActivity extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);
		databaseAdapter = new DatabaseAdapter(this).open();
		//checkFirstRun();
	}

	/*private void checkFirstRun() {
		Boolean isFirstRun = getSharedPreferences("FirstRunPreference", MODE_PRIVATE).getBoolean("firstTimePlayersLoad", true);

		if (isFirstRun) {
			getSharedPreferences("FirstRunPreference", MODE_PRIVATE).edit().putBoolean("firstTimePlayersLoad", false).apply();
			databaseAdapter.readPlayers();
		} else {
			list players
		}
	}*/
}