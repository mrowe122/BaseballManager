package com.msrproduction.baseballmanager.plugins;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.R;

import java.util.ArrayList;
import java.util.List;

public class NewPlayerModel {

	final private CharSequence positions[] = {"P", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF"};
	private Activity activity;
	private DatabaseAdapter databaseAdapter;

	public ArrayList<EditText> playerName = new ArrayList<>();
	public ArrayList<EditText> playerNumber = new ArrayList<>();
	public ArrayList<Button> playerPositions = new ArrayList<>();
	public int numPlayers = 0;

	public NewPlayerModel(Activity act) {
		activity = act;
		databaseAdapter = new DatabaseAdapter(activity).open();
	}

	public void addField() {
		final LinearLayout containerLayout = (LinearLayout) activity.findViewById(R.id.new_fields_created);
		final View layout = activity.getLayoutInflater().inflate(R.layout.layout_new_player_field, null);
		final EditText name = (EditText) layout.findViewById(R.id.form_player_name);
		final EditText number = (EditText) layout.findViewById(R.id.form_player_number);
		final Button spinnerPosition = (Button) layout.findViewById(R.id.spinner_pos);
		final ArrayList<Integer> selList = new ArrayList<>();
		playerName.add(name);
		playerNumber.add(number);
		playerPositions.add(spinnerPosition);
		((TextView) activity.findViewById(R.id.number_players)).setText(String.valueOf(++numPlayers));
		//delete button
		layout.findViewById(R.id.remove_field).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TextView) activity.findViewById(R.id.number_players)).setText(String.valueOf(--numPlayers));
				playerName.remove(name);
				playerNumber.remove(number);
				playerPositions.remove(spinnerPosition);
				containerLayout.removeView(layout);
			}
		});
		//configure Alert Dialog for players position
		final AlertDialog popupPosition = new AlertDialog.Builder(activity).setMultiChoiceItems(positions, new boolean[positions.length], new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
					//add position of which position was chosen
					selList.add(which);
				} else if (selList.contains(which)) {
					//if unchecked, remove the selectedList array
					selList.remove((Integer.valueOf(which)));
				}
			}
		}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				configurePosButton(spinnerPosition, selList);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		spinnerPosition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupPosition.show();
			}
		});
		containerLayout.addView(layout);
	}

	public void configurePosButton(Button positionButton, ArrayList<Integer> selList) {
		StringBuilder position = new StringBuilder();
		for (int i = 0; i < selList.size(); i++) {
			position.append(positions[selList.get(i)]);
			position.append(", ");
		}
		if (position.length() > 0) {
			positionButton.setText(position.substring(0, position.length() - 2));
			position.setLength(0);
		} else {
			positionButton.setText(R.string.spinner_position_title);
			selList.clear();
		}
	}

	public boolean addPlayers() {
		List<String> nameList = new ArrayList<>(playerName.size());
		List<String> numberList = new ArrayList<>(playerNumber.size());
		List<String> positionList = new ArrayList<>(playerPositions.size());
		for (int i = 0; i < numPlayers; i++) {
			if (!playerName.get(i).getText().toString().equals("") && !playerNumber.get(i).getText().toString().equals("")) {
				nameList.add(playerName.get(i).getText().toString());
				numberList.add(playerNumber.get(i).getText().toString());
				if (playerPositions.get(i).getText().toString().equals("")) {
					positionList.add("n/a");
				} else {
					positionList.add(playerPositions.get(i).getText().toString());
				}
			} else {
				//toast message to a field is empty or team wasn't selected
				Toast.makeText(activity, R.string.error_player_form_empty, Toast.LENGTH_SHORT).show();
				nameList.clear();
				numberList.clear();
				positionList.clear();
				return false;
			}
		}
		databaseAdapter.bulkInsert(nameList, numberList, positionList, activity.getSharedPreferences("coach_info", Context.MODE_PRIVATE).getString("team_name", ""));
		return true;
	}
}
