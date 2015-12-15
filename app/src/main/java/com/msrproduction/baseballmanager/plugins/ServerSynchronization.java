package com.msrproduction.baseballmanager.plugins;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerSynchronization {

	private Activity activity;
	private DatabaseAdapter databaseAdapter;

	public ServerSynchronization(Activity act) {
		activity = act;
	}

	public void syncData(String email) {
		databaseAdapter = new DatabaseAdapter(activity).open();
		new TeamSynchronization().execute(email, new Encrypt().encryptString(email));
	}

	public void syncPlayers(Cursor cursor) {
		new SavePlayers().execute(cursor);
	}

	public class TeamSynchronization extends AsyncTask<String, Void, String[]> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle(activity.getResources().getString(R.string.checking_email));
			progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
			progressDialog.show();
		}

		@Override
		protected String[] doInBackground(String... params) {
			//retrieve coach information in server
			try {
				URL url = new URL("http://192.168.1.219:3000/api/team/" + params[1]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "text/html");
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
				conn.connect();

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuilder sb = new StringBuilder();

				while ((line = reader.readLine()) != null)
					sb.append(line);

				conn.disconnect();

				SharedPreferences coachInfo = activity.getSharedPreferences("team_info", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = coachInfo.edit();
				editor.putString("team_id", params[1]);
				editor.putString("coach_email", params[0]);
				editor.putString("coach_phone", "n/a");
				editor.apply();
				activity.getSharedPreferences("FirstRunPreference", Context.MODE_PRIVATE).edit().putBoolean("isSignedIn", true).apply();

				String[] data = new String[5];
				JSONObject obj = new JSONObject(sb.toString());
				//response code
				data[0] = obj.getString("code");
				//team id (encrypted)
				data[1] = params[1];
				//coach name
				data[2] = coachInfo.getString("coach_name", "");
				//team name
				data[3] = coachInfo.getString("team_name", "");
				//json response
				data[4] = sb.toString();
				return data;

			} catch (IOException | JSONException e) {
				System.out.println(e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(final String[] buffer) {
			progressDialog.dismiss();
			switch (buffer[0]) {
				//if account exists, replace local data with server account
				case "77123":
					System.out.println(buffer[4]);
					break;
				//if account doesn't exist, upload local to server
				case "99000":
					new AlertDialog.Builder(activity)
							.setTitle(R.string.synchronized_data)
							.setMessage(R.string.synchronized_message)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new SaveTeamInfo().execute(buffer[1], buffer[2], buffer[3]);
									new SavePlayers().execute(databaseAdapter.loadPlayersInMyTeam());

									activity.setResult(1);
									activity.finish();
								}
							}).setCancelable(false).show();
					break;
			}
		}
	}

	public class SaveTeamInfo extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			databaseAdapter.addTeamId(params[2]);
			//store team information in server
			try {
				//URL url = new URL("http://52.25.231.27:3000/api/players");
				URL url = new URL("http://192.168.1.219:3000/api/team");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);

				JSONObject teamData = new JSONObject();
				teamData.put("teamId", params[0]);
				teamData.put("coach_name", params[1]);
				teamData.put("team_name", params[2]);

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(teamData.toString());
				wr.flush();
				wr.close();

				conn.connect();

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuilder sb = new StringBuilder();

				while ((line = reader.readLine()) != null)
					sb.append(line);

				System.out.println("Team data: " + sb.toString());
				conn.disconnect();
				return sb.toString();

			} catch (IOException | JSONException e) {
				System.out.println("Team data: " + e.toString());
			}
			return null;
		}
	}

	public class SavePlayers extends AsyncTask<Cursor, Void, Void> {

		@Override
		protected Void doInBackground(Cursor... params) {
			//store players on the server
			try {
				//URL url = new URL("http://52.25.231.27:3000/api/players");
				URL url = new URL("http://192.168.1.219:3000/api/players");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);

				JSONObject teamData = new JSONObject();
				JSONArray jaPlayers = new JSONArray();

				while (params[0].moveToNext()) {
					JSONObject jo = new JSONObject();
					jo.put("teamId", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_TEAM_ID)));
					jo.put("playerId", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_PLAYER_ID)));
					jo.put("name", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_NAME)));
					jo.put("number", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_NUMBER)));
					jo.put("team_name", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_TEAM_NAME)));
					jo.put("bats", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_BATS)));
					jo.put("throws_", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_THROWS)));
					jo.put("position", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_POSITION)));
					jo.put("batting_avg", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_BATTING_AVERAGE)));
					jo.put("rbi", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_RBI)));
					jo.put("runs", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_RUNS)));
					jo.put("hits", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_HITS)));
					jo.put("strike_outs", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_STRIKE_OUTS)));
					jo.put("walks", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_WALKS)));
					jo.put("single", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_SINGLE)));
					jo.put("double_", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_DOUBLE)));
					jo.put("triple", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_TRIPLE)));
					jo.put("home_runs", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_HOME_RUNS)));
					jo.put("fly_balls", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_FLY_BALL)));
					jo.put("ground_balls", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_GROUND_BALLS)));
					jo.put("on_base_percentage", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_ON_BASE_PERCENTAGE)));
					jo.put("bases_stolen", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_BASES_STOLEN)));
					jo.put("caught_stealing", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_CAUGHT_STEALING)));
					jo.put("errors_", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_ERRORS)));
					jo.put("field_percentage", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_FIELD_PERCENTAGE)));
					jo.put("put_outs", params[0].getString(params[0].getColumnIndex(Contract.MyPlayerEntry.COLUMN_PUT_OUTS)));
					jaPlayers.put(jo);
				}

				teamData.put("players", jaPlayers);

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(teamData.toString());
				wr.flush();
				wr.close();

				conn.connect();

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuilder sb = new StringBuilder();

				while ((line = reader.readLine()) != null)
					sb.append(line);

				System.out.println("Player data: " + sb.toString());
				conn.disconnect();

			} catch (IOException | JSONException e) {
				System.out.println("Player data: " + e.toString());
			}
			return null;
		}
	}
}