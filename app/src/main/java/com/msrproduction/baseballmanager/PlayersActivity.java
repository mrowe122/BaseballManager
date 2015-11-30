package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class PlayersActivity extends AppCompatActivity {

    private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);
        databaseAdapter = new DatabaseAdapter(this).open();
        checkFirstRun();
	}

    private void checkFirstRun() {
        Boolean isFirstRun = getSharedPreferences("FirstRunPreference", MODE_PRIVATE).getBoolean("firstTimePlayersLoad", true);

        if (isFirstRun) {
            getSharedPreferences("FirstRunPreference", MODE_PRIVATE).edit().putBoolean("firstTimePlayersLoad", false).apply();
            databaseAdapter.readPlayers();
        } else {
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