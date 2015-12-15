package com.msrproduction.baseballmanager.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.msrproduction.baseballmanager.PlayerInformation;
import com.msrproduction.baseballmanager.R;
import com.msrproduction.baseballmanager.plugins.ServerSynchronization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DatabaseAdapter {

	private final String LOG_TAG = DatabaseAdapter.class.getSimpleName();
	private Database DbHelper;
	private SQLiteDatabase db;
	private Activity activity;

	public DatabaseAdapter(Activity act) {
		activity = act;
		DbHelper = Database.getInstance(activity);
	}

	public DatabaseAdapter open() {
		if (db == null) {
			db = DbHelper.getWritableDatabase();
			return this;
		} else return this;
	}

	public void updatePlayer(String name, String number, String position, String bats, String throws_) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contract.MyPlayerEntry.COLUMN_NUMBER, number);
		contentValues.put(Contract.MyPlayerEntry.COLUMN_POSITION, position);
		contentValues.put(Contract.MyPlayerEntry.COLUMN_BATS, bats);
		contentValues.put(Contract.MyPlayerEntry.COLUMN_THROWS, throws_);
		db.update(Contract.MyPlayerEntry.TABLE_NAME, contentValues, Contract.MyPlayerEntry.COLUMN_NAME + " = '" + name + "'", null);
	}

	public void addTeamId(String teamId) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contract.MyPlayerEntry.COLUMN_TEAM_ID, teamId);
		db.update(Contract.MyPlayerEntry.TABLE_NAME, contentValues, null, null);
	}

	public Cursor loadPlayers() {
		return db.rawQuery(
				"SELECT " + Contract.MyPlayerEntry._ID + ", " + Contract.MyPlayerEntry.COLUMN_NAME + ", " + Contract.MyPlayerEntry.COLUMN_NUMBER + ", " + Contract.MyPlayerEntry.COLUMN_TEAM_NAME + ", " + Contract.MyPlayerEntry.COLUMN_POSITION +
						" FROM " + Contract.MyPlayerEntry.TABLE_NAME +
						" ORDER BY " + Contract.MyPlayerEntry.COLUMN_NAME + ";", null
		);
	}

	public Cursor loadPlayersInMyTeam() {
		return db.rawQuery(
				"SELECT *" +
						" FROM " + Contract.MyPlayerEntry.TABLE_NAME +
						" ORDER BY " + Contract.MyPlayerEntry.COLUMN_NAME + ";", null);
	}

	public void removePlayersFromTeam(List<String> removePlayers) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contract.MyPlayerEntry.COLUMN_TEAM_NAME, "Free Agent");
		for (int i = 0; i < removePlayers.size(); i++) {
			db.update(Contract.MyPlayerEntry.TABLE_NAME, contentValues, Contract.MyPlayerEntry._ID + " = " + removePlayers.get(i), null);
		}
	}

	public Cursor select(String id, String table) {
		return db.rawQuery(
				"SELECT *" +
						" FROM " + table +
						" WHERE _id = " + id + ";"
				, null);
	}

	public void loadPlayersFromServer(String[] playerInfo) {
		ContentValues addItem = new ContentValues();
		addItem.put(Contract.MyPlayerEntry.COLUMN_TEAM_ID, playerInfo[0]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_PLAYER_ID, playerInfo[1]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_NAME, playerInfo[2]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_NUMBER, playerInfo[3]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_TEAM_NAME, playerInfo[4]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_POSITION, playerInfo[5]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_BATS, playerInfo[6]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_THROWS, playerInfo[7]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_BATTING_AVERAGE, playerInfo[8]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_RBI, playerInfo[9]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_RUNS, playerInfo[10]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_HITS, playerInfo[11]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_STRIKE_OUTS, playerInfo[12]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_WALKS, playerInfo[13]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_SINGLE, playerInfo[14]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_DOUBLE, playerInfo[15]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_TRIPLE, playerInfo[16]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_HOME_RUNS, playerInfo[17]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_FLY_BALL, playerInfo[18]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_GROUND_BALLS, playerInfo[19]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_ON_BASE_PERCENTAGE, playerInfo[20]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_BASES_STOLEN, playerInfo[21]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_CAUGHT_STEALING, playerInfo[22]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_ERRORS, playerInfo[23]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_FIELD_PERCENTAGE, playerInfo[24]);
		addItem.put(Contract.MyPlayerEntry.COLUMN_PUT_OUTS, playerInfo[25]);
		db.insert(Contract.MyPlayerEntry.TABLE_NAME, null, addItem);
	}

	/*
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
						"SELECT " + Contract.MyPlayerEntry._ID + ", " + Contract.MyPlayerEntry.COLUMN_NAME +
								" FROM " + Contract.MyPlayerEntry.TABLE_NAME +
								" WHERE " + Contract.MyPlayerEntry.COLUMN_NAME +
								" LIKE '%" + subString + "%'" +
								" ORDER BY " + Contract.MyPlayerEntry.COLUMN_NAME + ";", null
				);
			case "free_agents":
				return db.rawQuery(
						"SELECT " + Contract.MyPlayerEntry._ID + ", " + Contract.MyPlayerEntry.COLUMN_NAME +
								" FROM " + Contract.MyPlayerEntry.TABLE_NAME +
								" WHERE " + Contract.MyPlayerEntry.COLUMN_TEAM_NAME + " = 'Free Agent'" +
								" AND " + Contract.MyPlayerEntry.COLUMN_NAME +
								" LIKE '%" + subString + "%'" +
								" ORDER BY " + Contract.MyPlayerEntry.COLUMN_NAME + ";", null
				);
			default:
				return db.rawQuery(
						"SELECT " + Contract.MyPlayerEntry._ID + "," + Contract.MyPlayerEntry.COLUMN_NAME +
								" FROM " + Contract.MyPlayerEntry.TABLE_NAME +
								" WHERE " + Contract.MyPlayerEntry.COLUMN_NAME + " LIKE '%" + subString + "%'" +
								" UNION ALL" +
								" SELECT " + Contract.TeamEntry._ID + "," + Contract.TeamEntry.COLUMN_TEAM_NAME +
								" FROM " + Contract.TeamEntry.TABLE_NAME +
								" WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME + " LIKE '%" + subString + "%'" +
								" ORDER BY name;", null
				);
		}
	}
	*/

	public void saveMyPlayers(List<String> _id, List<String> name, List<String> number, List<String> position, String team, List<String> bats, List<String> throws_) {
		StoreMyPlayers sts = new StoreMyPlayers(activity);
		sts.execute(_id, name, number, position, bats, throws_, team);
	}

	/*public void readPlayers() {
		ReadPlayersFromServer rps = new ReadPlayersFromServer(activity);
		rps.execute();
	}*/

	//Store players in the local database
	private class StoreMyPlayers extends AsyncTask<Object, Void, Void> {

		private Activity activity;
		private ProgressDialog progressDialog;

		public StoreMyPlayers(Activity act) {
			activity = act;
		}

		@Override
		protected void onPreExecute() {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle(activity.getResources().getString(R.string.saving_player));
			progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Object... params) {
			List _id = (List) params[0];
			List names = (List) params[1];
			List number = (List) params[2];
			List position = (List) params[3];
			List bats = (List) params[4];
			List throws_ = (List) params[5];
			String team = (String) params[6];
			String teamId = activity.getSharedPreferences("team_info", Context.MODE_PRIVATE).getString("team_id", "");

			//store players locally
			for (int i = 0; i < names.size(); i++) {
				ContentValues addItem = new ContentValues();
				addItem.put(Contract.MyPlayerEntry.COLUMN_TEAM_ID, teamId);
				addItem.put(Contract.MyPlayerEntry.COLUMN_PLAYER_ID, (String) _id.get(i));
				addItem.put(Contract.MyPlayerEntry.COLUMN_NAME, (String) names.get(i));
				addItem.put(Contract.MyPlayerEntry.COLUMN_NUMBER, (String) number.get(i));
				addItem.put(Contract.MyPlayerEntry.COLUMN_TEAM_NAME, team);
				addItem.put(Contract.MyPlayerEntry.COLUMN_POSITION, (String) position.get(i));
				addItem.put(Contract.MyPlayerEntry.COLUMN_BATS, (String) bats.get(i));
				addItem.put(Contract.MyPlayerEntry.COLUMN_THROWS, (String) throws_.get(i));
				addItem.put(Contract.MyPlayerEntry.COLUMN_BATTING_AVERAGE, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_RBI, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_RUNS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_HITS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_STRIKE_OUTS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_WALKS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_SINGLE, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_DOUBLE, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_TRIPLE, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_HOME_RUNS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_FLY_BALL, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_GROUND_BALLS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_ON_BASE_PERCENTAGE, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_BASES_STOLEN, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_CAUGHT_STEALING, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_ERRORS, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_FIELD_PERCENTAGE, 0);
				addItem.put(Contract.MyPlayerEntry.COLUMN_PUT_OUTS, 0);
				db.insert(Contract.MyPlayerEntry.TABLE_NAME, null, addItem);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			boolean signedIn = activity.getSharedPreferences("FirstRunPreference", Context.MODE_PRIVATE).getBoolean("isSignedIn", false);
			if(signedIn) {
				ServerSynchronization serverSynchronization = new ServerSynchronization(activity);
				serverSynchronization.syncPlayers(loadPlayersInMyTeam());
			}
			progressDialog.dismiss();
			activity.setResult(1);
			activity.finish();
		}
	}
	/*
	private class ReadPlayersFromServer extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog progressDialog;
		private Activity activity;

		public ReadPlayersFromServer(Activity act) {
			activity = act;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setCancelable(false);
			progressDialog.setTitle(activity.getResources().getString(R.string.fetching_player));
			progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			BufferedReader reader;
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL("http://52.25.231.27:8000/api/players");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(2000);
				conn.setReadTimeout(2000);

				conn.connect();

				InputStream inputStream = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;

				while ((line = reader.readLine()) != null)
					sb.append(line);

				if (sb.toString().length() > 0 && !sb.toString().equals("null")) {
					JSONArray jArray = new JSONArray(sb.toString());
					ContentValues addItem = new ContentValues();

					for (int i = 0; i < jArray.length(); i++) {
						JSONObject jObject = jArray.getJSONObject(i);
						addItem.put(Contract.MyPlayerEntry.COLUMN_PLAYER_ID, jObject.getString("_id"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_NAME, jObject.getString("name"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_NUMBER, jObject.getInt("number"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_TEAM_NAME, jObject.getString("team_name"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_POSITION, jObject.getString("position"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_BATS, jObject.getString("bats"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_THROWS, jObject.getString("throws_"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_BATTING_AVERAGE, jObject.getDouble("batting_avg"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_RBI, jObject.getInt("rbi"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_RUNS, jObject.getInt("runs"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_HITS, jObject.getInt("hits"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_STRIKE_OUTS, jObject.getInt("strike_outs"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_WALKS, jObject.getInt("walks"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_SINGLE, jObject.getInt("single"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_DOUBLE, jObject.getInt("double_"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_TRIPLE, jObject.getInt("triple"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_HOME_RUNS, jObject.getInt("home_runs"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_FLY_BALL, jObject.getInt("fly_balls"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_GROUND_BALLS, jObject.getInt("ground_balls"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_ON_BASE_PERCENTAGE, jObject.getDouble("on_base_percentage"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_BASES_STOLEN, jObject.getInt("bases_stolen"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_CAUGHT_STEALING, jObject.getInt("caught_stealing"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_ERRORS, jObject.getInt("errors_"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_FIELD_PERCENTAGE, jObject.getDouble("field_percentage"));
						addItem.put(Contract.MyPlayerEntry.COLUMN_PUT_OUTS, jObject.getInt("put_outs"));
						db.insert(Contract.MyPlayerEntry.TABLE_NAME, null, addItem);
					}

					Thread.sleep(1000);
					return true;
				} else {
					Thread.sleep(1000);
				}
			} catch (IOException | JSONException | InterruptedException e) {
				Log.e(LOG_TAG, e.toString());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean response) {
			progressDialog.dismiss();
			if (response) {
				ListView listView = (ListView) activity.findViewById(R.id.main_list_view);
				activity.registerForContextMenu(listView);
				listView.setAdapter(new PlayerListAdapter(activity, loadPlayers(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						activity.startActivity(new Intent(activity, PlayerInformation.class)
								.putExtra("player_id", id + ""));
					}
				});
			} else {
				new AlertDialog.Builder(activity)
						.setTitle("Sorry")
						.setMessage("There are no players at this time")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								activity.getSharedPreferences("FirstRunPreference", 0).edit().putBoolean("firstTimePlayersLoad", true).apply();
								activity.finish();
							}
						}).setCancelable(false).show();
			}
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
						cursor.getInt(cursor.getColumnIndexOrThrow(Contract.MyPlayerEntry.COLUMN_NUMBER)) + " " +
						cursor.getString(cursor.getColumnIndexOrThrow(Contract.MyPlayerEntry.COLUMN_NAME)));
				((TextView) view.findViewById(R.id.list_item_sub_text)).setText(
						"(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.MyPlayerEntry.COLUMN_TEAM_NAME)) + ")");
			}
		}
	}
	*/
}