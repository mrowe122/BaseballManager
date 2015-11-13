package com.msrproduction.baseballmanager.plugins;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class PlayerSchema {

	final private String positionList[] = {"P", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF"};
	private Activity activity;
	private DatabaseAdapter databaseAdapter;
	private String name, number, position, bats, throws_;
	private CheckBox batsLeft, batsRight, throwsLeft, throwsRight;

	private ArrayList<EditText> nameArray = new ArrayList<>();
	private ArrayList<EditText> numberArray = new ArrayList<>();
	private ArrayList<Button> positionArray = new ArrayList<>();
	private ArrayList<CheckBox> batsLeftArray = new ArrayList<>();
	private ArrayList<CheckBox> batsRightArray = new ArrayList<>();
	private ArrayList<CheckBox> throwsLeftArray = new ArrayList<>();
	private ArrayList<CheckBox> throwsRightArray = new ArrayList<>();
	private LinkedList<View> listView = new LinkedList<>();
	private int numPlayers = 0;

	public PlayerSchema(Activity act) {
		activity = act;
		databaseAdapter = new DatabaseAdapter(activity).open();
	}

	private void configureAlertDialog(String position, final Button spinnerButton) {
		final ArrayList<Integer> selList = new ArrayList<>();

		final AlertDialog popupPosition = new AlertDialog.Builder(activity).setMultiChoiceItems(positionList, setSelectedPositions(position), new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
					//add position of which position was chosen
					selList.add(which);
				} else if (selList.contains(which)) {
					//if unchecked, remove it from the selectedList array
					selList.remove((Integer.valueOf(which)));
				}
			}
		}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getSelectedPositions(spinnerButton, selList);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		spinnerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupPosition.show();
			}
		});
	}

	///////////////////////////////////////////////////////
	//               NEW PLAYER SECTION                  //
	///////////////////////////////////////////////////////

	public void addField() {
		final LinearLayout containerLayout = (LinearLayout) activity.findViewById(R.id.new_fields_created);
		final View layout = activity.getLayoutInflater().inflate(R.layout.layout_new_player_field, null);
		final EditText name = (EditText) layout.findViewById(R.id.form_player_name);
		final EditText number = (EditText) layout.findViewById(R.id.form_player_number);
		final Button spinnerPosition = (Button) layout.findViewById(R.id.spinner_pos);
		final CheckBox batsLeft = (CheckBox) layout.findViewById(R.id.bats_left);
		final CheckBox batsRight = (CheckBox) layout.findViewById(R.id.bats_right);
		final CheckBox throwsLeft = (CheckBox) layout.findViewById(R.id.throws_left);
		final CheckBox throwsRight = (CheckBox) layout.findViewById(R.id.throws_right);

		listView.add(layout);
		nameArray.add(name);
		numberArray.add(number);
		positionArray.add(spinnerPosition);
		batsLeftArray.add(batsLeft);
		batsRightArray.add(batsRight);
		throwsLeftArray.add(throwsLeft);
		throwsRightArray.add(throwsRight);
		((TextView) activity.findViewById(R.id.number_players)).setText(String.valueOf(++numPlayers));
		((TextView) layout.findViewById(R.id.field_number)).setText(String.valueOf(numPlayers));

		//delete button
		layout.findViewById(R.id.remove_field).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TextView) activity.findViewById(R.id.number_players)).setText(String.valueOf(--numPlayers));
				listView.remove(layout);
				nameArray.remove(name);
				numberArray.remove(number);
				positionArray.remove(spinnerPosition);
				batsLeftArray.remove(batsLeft);
				batsRightArray.remove(batsRight);
				throwsLeftArray.remove(throwsLeft);
				throwsRightArray.remove(throwsRight);
				containerLayout.removeView(layout);
				for (int i = 0; i < numPlayers; ) {
					((TextView) listView.get(i).findViewById(R.id.field_number)).setText(String.valueOf(++i));
				}
			}
		});

		configureAlertDialog(activity.getString(R.string.no_position_selected), spinnerPosition);
		containerLayout.addView(layout);
	}

	private void getSelectedPositions(Button positionButton, ArrayList<Integer> selList) {
		StringBuilder position = new StringBuilder();
		for (int i = 0; i < selList.size(); i++) {
			position.append(positionList[selList.get(i)]);
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
		List<String> nameList = new ArrayList<>(numPlayers);
		List<String> numberList = new ArrayList<>(numPlayers);
		List<String> positionList = new ArrayList<>(numPlayers);
		List<String> batsList = new ArrayList<>(numPlayers);
		List<String> throwsList = new ArrayList<>(numPlayers);
		for (int i = 0; i < numPlayers; i++) {
			if (!nameArray.get(i).getText().toString().equals("") && !numberArray.get(i).getText().toString().equals("")) {
				nameList.add(nameArray.get(i).getText().toString());
				numberList.add(numberArray.get(i).getText().toString());
				if (positionArray.get(i).getText().toString().equals(activity.getString(R.string.spinner_position_title))) {
					positionList.add(activity.getString(R.string.no_position_selected));
				} else {
					positionList.add(positionArray.get(i).getText().toString());
				}
				//grab players batting direction
				if (batsLeftArray.get(i).isChecked() && batsRightArray.get(i).isChecked()) {
					batsList.add(activity.getString(R.string.both));
				} else if (batsLeftArray.get(i).isChecked()) {
					batsList.add(activity.getString(R.string.left));
				} else if (batsRightArray.get(i).isChecked()) {
					batsList.add(activity.getString(R.string.right));
				} else {
					batsList.add(activity.getString(R.string.n_a));
				}
				//grab players throwing direction
				if (throwsLeftArray.get(i).isChecked() && throwsRightArray.get(i).isChecked()) {
					throwsList.add(activity.getString(R.string.both));
				} else if (throwsLeftArray.get(i).isChecked()) {
					throwsList.add(activity.getString(R.string.left));
				} else if (throwsRightArray.get(i).isChecked()) {
					throwsList.add(activity.getString(R.string.right));
				} else {
					throwsList.add(activity.getString(R.string.n_a));
				}
			} else {
				String message = "Error: Field " + ++i + " is empty";
				//toast message to a field is empty or team wasn't selected
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
				nameList.clear();
				numberList.clear();
				positionList.clear();
				return false;
			}
		}
		databaseAdapter.bulkInsert(nameList, numberList, positionList, activity.getSharedPreferences("coach_info", Context.MODE_PRIVATE).getString("team_name", ""), batsList, throwsList);
		return true;
	}

	///////////////////////////////////////////////////////
	//               EDIT PLAYER SECTION                 //
	///////////////////////////////////////////////////////

	public void editInitSetup(String id) {
		Cursor cursor = databaseAdapter.select(id, Contract.PlayerEntry.TABLE_NAME);
		cursor.moveToNext();
		name = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME));
		number = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER));
		position = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_POSITION));
		bats = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_BATS));
		throws_ = cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_THROWS));
		cursor.close();

		((TextView) activity.findViewById(R.id.player_name)).setText(name);
		((EditText) activity.findViewById(R.id.form_player_number)).setText(number);

		setPlayersDirections(bats, throws_);
		configureAlertDialog(position, ((Button) activity.findViewById(R.id.spinner_pos)));
	}

	private boolean[] setSelectedPositions(String position) {
		int size = positionList.length;
		boolean checkedPositions[] = new boolean[size];
		if (!position.equals(activity.getString(R.string.no_position_selected))) {
			StringTokenizer st = new StringTokenizer(position, ", ");
			while (st.hasMoreTokens()) {
				String temp = st.nextToken();
				for (int i = 0; i < size; i++) {
					if (positionList[i].equals(temp)) {
						checkedPositions[i] = true;
					}
				}
			}
			((Button) activity.findViewById(R.id.spinner_pos)).setText(position);
		}
		return checkedPositions;
	}

	private void setPlayersDirections(String batDirection, String throwsDirection) {
		batsLeft = (CheckBox) activity.findViewById(R.id.bats_left);
		batsRight = (CheckBox) activity.findViewById(R.id.bats_right);
		throwsLeft = (CheckBox) activity.findViewById(R.id.throws_left);
		throwsRight = (CheckBox) activity.findViewById(R.id.throws_right);

		if (batDirection.equals(activity.getString(R.string.both))) {
			batsLeft.setChecked(true);
			batsRight.setChecked(true);
		} else if (batDirection.equals(activity.getString(R.string.left))) {
			batsLeft.setChecked(true);
		} else if (batDirection.equals(activity.getString(R.string.right))) {
			batsRight.setChecked(true);
		}
		if (throwsDirection.equals(activity.getString(R.string.both))) {
			throwsLeft.setChecked(true);
			throwsRight.setChecked(true);
		} else if (throwsDirection.equals(activity.getString(R.string.left))) {
			throwsLeft.setChecked(true);
		} else if (throwsDirection.equals(activity.getString(R.string.right))) {
			throwsRight.setChecked(true);
		}
	}

	public boolean editPlayer() {
		//get number of player
		number = ((EditText) activity.findViewById(R.id.form_player_number)).getText().toString();
		position = ((Button) activity.findViewById(R.id.spinner_pos)).getText().toString();
		if (!number.equals("")) {
			//get which way the player bats
			if (batsLeft.isChecked() && batsRight.isChecked()) {
				bats = activity.getString(R.string.both);
			} else if (batsRight.isChecked()) {
				bats = activity.getString(R.string.right);
			} else if (batsLeft.isChecked()) {
				bats = activity.getString(R.string.left);
			} else {
				bats = activity.getString(R.string.n_a);
			}
			//get which way the player throws
			if (throwsLeft.isChecked() && throwsRight.isChecked()) {
				throws_ = activity.getString(R.string.both);
			} else if (throwsRight.isChecked()) {
				throws_ = activity.getString(R.string.right);
			} else if (throwsLeft.isChecked()) {
				throws_ = activity.getString(R.string.left);
			} else {
				throws_ = activity.getString(R.string.n_a);
			}
		} else {
			Toast.makeText(activity, activity.getString(R.string.error_player_number_empty), Toast.LENGTH_SHORT).show();
			return false;
		}
		databaseAdapter.updatePlayer(name, number, position, bats, throws_);
		return true;
	}

	public void removePlayer(String id) {
		final List<String> temp = new ArrayList<>();
		temp.add(id);
		new AlertDialog.Builder(activity)
				.setTitle(R.string.removing_players_title)
				.setMessage(R.string.removing_players_message)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						databaseAdapter.removePlayersFromTeam(temp);
						activity.setResult(1);
						activity.finish();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.setResult(0);
						//do nothing
					}
				}).show();
	}
}
