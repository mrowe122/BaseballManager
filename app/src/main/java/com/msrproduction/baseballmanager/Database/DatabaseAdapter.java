package com.msrproduction.baseballmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

//import com.msrproduction.android.baseballleague.EditActivityTeam;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter extends AsyncTask<String, Void, Cursor> {

	private final String LOG_TAG = DatabaseAdapter.class.getSimpleName();
	private Database DbHelper;
	static SQLiteDatabase db;

	@Override
	protected Cursor doInBackground(String... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Cursor cursor) {
		super.onPostExecute(cursor);
	}

	public DatabaseAdapter(Context ctx) {
		DbHelper = Database.getInstance(ctx);
	}

	public DatabaseAdapter open() {
		if (db == null) {
			Log.v(LOG_TAG, "Database has been open");
			db = DbHelper.getWritableDatabase();
			return this;
		} else return this;
	}

	public void insertTeam(String value) {
		ContentValues addItem = new ContentValues();
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_NAME, value);
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_COACH, "n/a");
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_WINS, 0);
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_LOSE, 0);
		db.insert(Contract.TeamEntry.TABLE_NAME, null, addItem);
	}

	private void insertPlayer(String name, String number, String position, String team) {
		ContentValues addItem = new ContentValues();
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NAME, name);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER, number);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME, team);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_POSITION, position);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BATTING_AVERAGE, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_RBI, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_RUNS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_HITS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_STRIKE_OUTS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_WALKS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_SINGLE, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_DOUBLE, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TRIPLE, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_HOME_RUNS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_FLY_BALL, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_GROUND_BALLS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_ON_BASE_PERCENTAGE, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BASES_STOLEN, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_CAUGHT_STEALING, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_ERRORS, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_FIELD_PERCENTAGE, 0);
		addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_PUT_OUTS, 0);
		db.insert(Contract.PlayerEntry.TABLE_NAME, null, addItem);
	}

	public void updatePlayer(String name, String number, String position) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER, number);
		contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_POSITION, position);
		db.update(Contract.PlayerEntry.TABLE_NAME, contentValues, Contract.PlayerEntry.COLUMN_PLAYER_NAME + " = '" + name + "'", null);
	}

	public Cursor loadTeams() {
		return db.rawQuery(
				"SELECT " + Contract.TeamEntry._ID + ", " + Contract.TeamEntry.COLUMN_TEAM_NAME +
						" FROM " + Contract.TeamEntry.TABLE_NAME +
						" ORDER BY " + Contract.TeamEntry.COLUMN_TEAM_NAME + ";", null
		);
	}

	public Cursor loadPlayers() {
		return db.rawQuery(
				"SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NUMBER + ", " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_POSITION +
						" FROM " + Contract.PlayerEntry.TABLE_NAME +
						" ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null
		);
	}

	public Cursor loadPlayersInTeams(String teamName) {
		return db.rawQuery(
				"SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NUMBER + ", " + Contract.PlayerEntry.COLUMN_PLAYER_POSITION +
						" FROM " + Contract.PlayerEntry.TABLE_NAME +
						" WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME +
						" IN (SELECT " + Contract.TeamEntry.COLUMN_TEAM_NAME +
						" FROM " + Contract.TeamEntry.TABLE_NAME +
						" WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME + " = '" + teamName + "');", null);
	}

	public boolean checkIfTeamExists(String teamName) {
		Cursor cursor = loadTeams();
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_NAME)).equals(teamName)) {
				cursor.close();
				return true;
			}
		}
		return false;
	}

	public boolean checkIfNumberExistsInTeam(String number, String teamName) {
		Cursor cursor = loadPlayersInTeams(teamName);
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)).equals(number)) {
				cursor.close();
				return true;
			}
		}
		return false;
	}

	public boolean editing(String team, String playerNumber) {
		Cursor cursor;
		cursor = loadPlayersInTeams(team);
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)).equals(playerNumber)) {
				return true;
			}
		}
		return false;
	}

	public Cursor select(String id, String table) {
		return db.rawQuery(
				"SELECT *" +
						" FROM " + table +
						" WHERE _id = " + id + ";"
				, null);
	}

	public Cursor searchDatabase(String subString, String filter) {
		switch (filter) {
			case "team":
				return db.rawQuery(
						"SELECT " + Contract.TeamEntry._ID + ", " + Contract.TeamEntry.COLUMN_TEAM_NAME +
								" FROM " + Contract.TeamEntry.TABLE_NAME +
								" WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME +
								" LIKE '%" + subString + "%'" +
								" ORDER BY " + Contract.TeamEntry.COLUMN_TEAM_NAME + ";", null
				);
			case "player":
				return db.rawQuery(
						"SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
								" FROM " + Contract.PlayerEntry.TABLE_NAME +
								" WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
								" LIKE '%" + subString + "%'" +
								" ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null
				);
			case "free_agents":
				return db.rawQuery(
						"SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
								" FROM " + Contract.PlayerEntry.TABLE_NAME +
								" WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME + " = 'Free Agent'" +
								" AND " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
								" LIKE '%" + subString + "%'" +
								" ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null
				);
			default:
				return db.rawQuery(
						"SELECT " + Contract.PlayerEntry._ID + "," + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
								" FROM " + Contract.PlayerEntry.TABLE_NAME +
								" WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + " LIKE '%" + subString + "%'" +
								" UNION ALL" +
								" SELECT " + Contract.TeamEntry._ID + "," + Contract.TeamEntry.COLUMN_TEAM_NAME +
								" FROM " + Contract.TeamEntry.TABLE_NAME +
								" WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME + " LIKE '%" + subString + "%'" +
								" ORDER BY name;", null
				);
		}
	}

	public void bulkInsert(List<String> name, List<String> number, List<String> position, String team) {
		for (int i = 0; i < name.size(); i++) {
			//  insertPlayer params(String name, String number, String team)
			insertPlayer(name.get(i), number.get(i), position.get(i), team);
		}
	}

	public String[] loadTeams(boolean freeAgent) {
		Cursor cursor = loadTeams();
		int size = cursor.getCount();
		List<String> teamList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			cursor.moveToNext();
			teamList.add(cursor.getString(cursor.getColumnIndex(Contract.TeamEntry.COLUMN_TEAM_NAME)));
		}
		cursor.close();
		if(freeAgent) {
			teamList.add("Free Agent");
			return teamList.toArray(new String[size + 1]);
		} else {
			return teamList.toArray(new String[size]);
		}
	}
}