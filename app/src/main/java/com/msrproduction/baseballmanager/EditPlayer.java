package com.msrproduction.baseballmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;
import com.msrproduction.baseballmanager.plugins.PlayerSchema;
import com.nirhart.parallaxscroll.views.ParallaxListView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class EditPlayer extends Activity {

	private PlayerSchema playerSchema;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_player);
		initSetup();
	}

	private void initSetup() {
		playerSchema = new PlayerSchema(this);
		playerSchema.editInitSetup(getIntent().getExtras().getString("edit_player"));

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
