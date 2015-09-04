package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.ListView;
import com.msrproduction.baseballmanager.plugins.OnDetectScrollListener;

public class PlayersActivity extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);
		databaseAdapter = new DatabaseAdapter(this).open();
	}

	@Override
	public void onResume() {
		super.onResume();
		readPlayers();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.main_list_view) {
			getMenuInflater().inflate(R.menu.context_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.edit_selected:
				/*startActivity(new Intent(this, EditActivityPlayer.class)
						.putExtra("edit_player", info.id + ""));*/
				return true;

			default:
				return super.onContextItemSelected(item);
		}
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
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		listView.setOnDetectScrollListener(new OnDetectScrollListener() {
			@Override
			public void onUpScrolling() {
				fab.show();
			}

			@Override
			public void onDownScrolling() {
				fab.hide();
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
