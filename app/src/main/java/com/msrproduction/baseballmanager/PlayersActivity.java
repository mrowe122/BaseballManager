package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayersActivity extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	private String LOG_TAG = PlayersActivity.class.getSimpleName();
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);
		databaseAdapter = new DatabaseAdapter(this).open();
		/*progressDialog = new ProgressDialog(PlayersActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading Players");
		progressDialog.setMessage("Please wait...");
		progressDialog.show();
		new talkToServer().execute("players");*/
	}

	@Override
	public void onResume() {
		super.onResume();
		readPlayers();
	}

	private void readPlayers() {
		ListView listView = (ListView) findViewById(R.id.main_list_view);
		registerForContextMenu(listView);
		listView.setAdapter(new PlayerListAdapter(this, databaseAdapter.loadPlayers(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(PlayersActivity.this, PlayerInformation.class)
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

	public class talkToServer extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... param) {
			BufferedReader reader;
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL("http://amcustomprints.com/android_db/index.php");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
				writer.write("method=" + param[0]);
				writer.flush();
				writer.close();
				os.close();

				conn.connect();

				InputStream inputStream = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				if (sb.length() != 0) {
					return readJsonString(sb.toString());
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, e.toString());
			}
			return "nothing";

		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			progressDialog.dismiss();
			new AlertDialog.Builder(PlayersActivity.this)
					.setTitle("Server Response")
					.setMessage(s)
					.setPositiveButton("OK", null)
					.show();
		}

		public String readJsonString(String s) {
			StringBuilder sb = new StringBuilder();
			try {
				JSONArray databaseJson = new JSONArray(s);
				for (int i = 0; i < databaseJson.length(); i++) {
					JSONObject detail = databaseJson.getJSONObject(i);
					String name = detail.getString("name");
					sb.append(name).append(":").append("\n");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}
	}
}
