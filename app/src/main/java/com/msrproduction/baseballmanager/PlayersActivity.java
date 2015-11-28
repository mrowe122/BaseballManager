package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
		setContentView(R.layout.list_item_layout);
		databaseAdapter = new DatabaseAdapter(this).open();
        databaseAdapter.test();
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
		//readPlayers();
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
}
