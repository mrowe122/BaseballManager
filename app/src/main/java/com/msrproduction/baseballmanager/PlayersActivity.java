package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private ProgressDialog progressDialog = null;
    private Button myButton;
    private int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item_layout);
		/*databaseAdapter = new DatabaseAdapter(this).open();
        databaseAdapter.test();*/


        findViewById(R.id.text_view_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server server = new server(getApplicationContext());
                server.execute();
            }
        });
	}

	@Override
	public void onResume() {
		super.onResume();
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

    private class server extends AsyncTask<Void, Void, Void> {

        Context context;

        public server(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PlayersActivity.this, R.style.StyledDialog);
            progressDialog.setTitle("Storing players");
            progressDialog.setMessage("Please wait...");
            progressDialog.setMax(5);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            Drawable customDrawable = ContextCompat.getDrawable(context, R.drawable.custom_progressbar);
            progressDialog.setProgressDrawable(customDrawable);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(2500);
                    progressDialog.setProgress(i + 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            progressDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
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
					cursor.getInt(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)) + " " +
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME)));
			((TextView) view.findViewById(R.id.list_item_sub_text)).setText(
					"(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME)) + ")");
		}
	}
}
