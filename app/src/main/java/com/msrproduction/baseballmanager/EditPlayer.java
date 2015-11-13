package com.msrproduction.baseballmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.msrproduction.baseballmanager.plugins.PlayerSchema;

public class EditPlayer extends Activity {

	private PlayerSchema playerSchema;
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_player);
		initSetup();
	}

	private void initSetup() {
		setFinishOnTouchOutside(false);
		id = getIntent().getExtras().getString("edit_player");
		playerSchema = new PlayerSchema(this);
		playerSchema.editInitSetup(id);

		//edit player button
		(findViewById(R.id.edit_player)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerSchema.editPlayer()) {
					setResult(1);
					finish();
				}
			}
		});

		//edit player button
		(findViewById(R.id.remove_player)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playerSchema.removePlayer(id);
			}
		});

		//cancel button
		findViewById(R.id.cancel_edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});
	}
}
