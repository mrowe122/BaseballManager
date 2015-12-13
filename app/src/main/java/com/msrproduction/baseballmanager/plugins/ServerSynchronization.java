package com.msrproduction.baseballmanager.plugins;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.msrproduction.baseballmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerSynchronization {

	private Activity activity;
	private ProgressDialog progressDialog;

	public ServerSynchronization(Activity act) {
		activity = act;
	}

	public void players() {
		new SyncPlayers().execute();
	}

	public void syncAllData(String email) {
		SharedPreferences coachInfo = activity.getSharedPreferences("coach_info", Context.MODE_PRIVATE);
		String coach_name = coachInfo.getString("coach_name", "");
		String team_name = coachInfo.getString("team_name", "");
		new SaveTeamInfo().execute(coach_name, team_name, email);
		//new SyncPlayers().execute();
	}

	public class SaveTeamInfo extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle(activity.getResources().getString(R.string.synchronizing));
			progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			//store coach information in server
			BufferedReader reader;
			try {
				//URL url = new URL("http://52.25.231.27:3000/api/players");
				URL url = new URL("http://192.168.1.219:3000/api/team");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);

				JSONObject teamData = new JSONObject();

				teamData.put("coach_name", params[0]);
				teamData.put("team_name", params[1]);
				teamData.put("coach_email", params[2]);

				JSONArray jaPlayers = new JSONArray();
				teamData.put("players", "");

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(teamData.toString());
				wr.flush();
				wr.close();

				conn.connect();

				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = reader.readLine();
				System.out.println(line);
				return line;

			} catch (IOException | JSONException e) {
				System.out.print(e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String aVoid) {
			progressDialog.dismiss();
			activity.finish();
		}
	}

	public class RetrieveTeamInfo extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle(activity.getResources().getString(R.string.synchronizing));
			progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			//retrieve coach information in server
			try {
				//URL url = new URL("http://52.25.231.27:3000/api/players");
				URL url = new URL("http://192.168.1.219:3000/api/team/" + params[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);

				conn.connect();

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				System.out.println(sb.toString());
				return sb.toString();

			} catch (IOException e) {
				System.out.print(e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String aVoid) {
			progressDialog.dismiss();
			activity.finish();
		}
	}

	public class SyncPlayers extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle(activity.getResources().getString(R.string.synchronizing));
			progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//store players on the server
			BufferedReader reader;
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

				JSONObject jo = new JSONObject();
				jo.put("playerNum", "6df5fsdgfd");
				jo.put("name", "Rick");
				jo.put("number", "2");
				jo.put("position", "P");
				jo.put("bats", "Left");
				jo.put("throws_", "Left");
				jo.put("team_name", "Yankees");
				jaPlayers.put(jo);

				JSONObject jo2 = new JSONObject();
				jo2.put("playerNum", "12345678");
				jo2.put("name", "Steve");
				jo2.put("number", "2");
				jo2.put("position", "P");
				jo2.put("bats", "Left");
				jo2.put("throws_", "Left");
				jo2.put("team_name", "Yankees");
				jaPlayers.put(jo2);

				teamData.put("email", "mrowe122@gmail.com");
				teamData.put("players", jaPlayers);

				System.out.println(teamData);

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(teamData.toString());
				wr.flush();
				wr.close();

				conn.connect();

				InputStream inputStream = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
				String line = reader.readLine();
				System.out.println(line);

			} catch (IOException | JSONException e) {
				System.out.println(e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			progressDialog.dismiss();
			activity.finish();
		}
	}
}