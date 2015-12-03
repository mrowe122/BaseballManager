package com.msrproduction.baseballmanager.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.msrproduction.baseballmanager.PlayerInformation;
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
import java.util.List;
import java.util.Random;

public class DatabaseAdapter {

    private final String LOG_TAG = DatabaseAdapter.class.getSimpleName();
    private Database DbHelper;
    private SQLiteDatabase db;
    private Activity activity;

    public DatabaseAdapter(Activity act) {
        activity = act;
        DbHelper = Database.getInstance(activity);
    }

    public DatabaseAdapter open() {
        if (db == null) {
            Log.v(LOG_TAG, "Database has been open");
            db = DbHelper.getWritableDatabase();
            return this;
        } else return this;
    }

	/*
    public void insertMyTeamToServer(String value) {
		ContentValues addItem = new ContentValues();
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_NAME, value);
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_COACH, "n/a");
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_WINS, 0);
		addItem.put(Contract.TeamEntry.COLUMN_TEAM_LOSE, 0);
		db.insert(Contract.TeamEntry.TABLE_NAME, null, addItem);
	}
	*/

    public void updatePlayer(String name, String number, String position, String bats, String throws_) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER, number);
        contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_POSITION, position);
        contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_BATS, bats);
        contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_THROWS, throws_);
        db.update(Contract.PlayerEntry.TABLE_NAME, contentValues, Contract.PlayerEntry.COLUMN_PLAYER_NAME + " = '" + name + "'", null);
    }

    public Cursor loadTeams() {
        return db.rawQuery(
                "SELECT " + Contract.TeamEntry._ID + ", " + Contract.TeamEntry.COLUMN_TEAM_NAME +
                        " FROM " + Contract.TeamEntry.TABLE_NAME +
                        " ORDER BY " + Contract.TeamEntry.COLUMN_TEAM_NAME + ";", null
        );
    }

    public Cursor loadPlayers() {
        return db.rawQuery(
                "SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NUMBER + ", " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_POSITION +
                        " FROM " + Contract.PlayerEntry.TABLE_NAME +
                        " ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null
        );
    }

    public Cursor loadPlayersInMyTeam(String teamName) {
        return db.rawQuery(
                "SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NUMBER + ", " + Contract.PlayerEntry.COLUMN_PLAYER_POSITION +
                        " FROM " + Contract.PlayerEntry.TABLE_NAME +
                        " WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME + " = '" + teamName + "'" +
                        " ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null);
    }

    public Cursor loadPlayersInTeam(String teamName) {
        return db.rawQuery(
                "SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NUMBER + ", " + Contract.PlayerEntry.COLUMN_PLAYER_POSITION +
                        " FROM " + Contract.PlayerEntry.TABLE_NAME +
                        " WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME +
                        " IN (SELECT " + Contract.TeamEntry.COLUMN_TEAM_NAME +
                        " FROM " + Contract.TeamEntry.TABLE_NAME +
                        " WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME + " = '" + teamName + "');", null);
    }

    public void removePlayersFromTeam(List<String> removePlayers) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME, "Free Agent");
        for (int i = 0; i < removePlayers.size(); i++) {
            db.update(Contract.PlayerEntry.TABLE_NAME, contentValues, Contract.PlayerEntry._ID + " = " + removePlayers.get(i), null);
        }
    }

    public boolean checkIfTeamExists(String teamName) {
        Cursor cursor = loadTeams();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(Contract.TeamEntry.COLUMN_TEAM_NAME)).equals(teamName)) {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    public Cursor select(String id, String table) {
        return db.rawQuery(
                "SELECT *" +
                        " FROM " + table +
                        " WHERE _id = " + id + ";"
                , null);
    }

    public Cursor searchDatabase(String subString, String filter) {
        switch (filter) {
            case "team":
                return db.rawQuery(
                        "SELECT " + Contract.TeamEntry._ID + ", " + Contract.TeamEntry.COLUMN_TEAM_NAME +
                                " FROM " + Contract.TeamEntry.TABLE_NAME +
                                " WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME +
                                " LIKE '%" + subString + "%'" +
                                " ORDER BY " + Contract.TeamEntry.COLUMN_TEAM_NAME + ";", null
                );
            case "player":
                return db.rawQuery(
                        "SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
                                " FROM " + Contract.PlayerEntry.TABLE_NAME +
                                " WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
                                " LIKE '%" + subString + "%'" +
                                " ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null
                );
            case "free_agents":
                return db.rawQuery(
                        "SELECT " + Contract.PlayerEntry._ID + ", " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
                                " FROM " + Contract.PlayerEntry.TABLE_NAME +
                                " WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME + " = 'Free Agent'" +
                                " AND " + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
                                " LIKE '%" + subString + "%'" +
                                " ORDER BY " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + ";", null
                );
            default:
                return db.rawQuery(
                        "SELECT " + Contract.PlayerEntry._ID + "," + Contract.PlayerEntry.COLUMN_PLAYER_NAME +
                                " FROM " + Contract.PlayerEntry.TABLE_NAME +
                                " WHERE " + Contract.PlayerEntry.COLUMN_PLAYER_NAME + " LIKE '%" + subString + "%'" +
                                " UNION ALL" +
                                " SELECT " + Contract.TeamEntry._ID + "," + Contract.TeamEntry.COLUMN_TEAM_NAME +
                                " FROM " + Contract.TeamEntry.TABLE_NAME +
                                " WHERE " + Contract.TeamEntry.COLUMN_TEAM_NAME + " LIKE '%" + subString + "%'" +
                                " ORDER BY name;", null
                );
        }
    }

    public void savePlayers(List<String> _id, List<String> name, List<String> number, List<String> position, String team, List<String> bats, List<String> throws_) {
        StorePlayers sts = new StorePlayers(activity);
        sts.execute(_id, name, number, position, bats, throws_, team);
    }

    public void readPlayers() {
        ReadPlayersFromServer rps = new ReadPlayersFromServer(activity);
        rps.execute();
    }

    //Store players in the database and server
    public class StorePlayers extends AsyncTask<Object, Void, Void> {

        private ProgressDialog progressDialog;
        private Activity activity;

        public StorePlayers(Activity act) {
            activity = act;
        }

        @Override
        protected void onPreExecute() {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            progressDialog = new ProgressDialog(activity, R.style.StyledDialog);
            progressDialog.setTitle(activity.getResources().getString(R.string.saving_player));
            progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Object... params) {
            List _id = (List) params[0];
            List names = (List) params[1];
            List number = (List) params[2];
            List position = (List) params[3];
            List bats = (List) params[4];
            List throws_ = (List) params[5];
            String team = (String) params[6];
            progressDialog.setMax(names.size());

            //store players locally
            for (int i = 0; i < names.size(); i++) {
                ContentValues addItem = new ContentValues();
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NAME_ID, (String) _id.get(i));
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NAME, (String) names.get(i));
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER, (String) number.get(i));
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME, team);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_POSITION, (String) position.get(i));
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BATS, (String) bats.get(i));
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_THROWS, (String) throws_.get(i));
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BATTING_AVERAGE, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_RBI, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_RUNS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_HITS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_STRIKE_OUTS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_WALKS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_SINGLE, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_DOUBLE, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TRIPLE, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_HOME_RUNS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_FLY_BALL, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_GROUND_BALLS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_ON_BASE_PERCENTAGE, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BASES_STOLEN, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_CAUGHT_STEALING, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_ERRORS, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_FIELD_PERCENTAGE, 0);
                addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_PUT_OUTS, 0);
                db.insert(Contract.PlayerEntry.TABLE_NAME, null, addItem);
            }

            //store players on the server
            BufferedReader reader;
            try {
                URL url = new URL("http://52.25.231.27:8000/api/players");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(3000);

                JSONArray ja = new JSONArray();

                for (int i = 0; i < names.size(); i++) {
                    JSONObject jo = new JSONObject();
                    jo.put("_id", _id.get(i));
                    jo.put("name", names.get(i));
                    jo.put("number", number.get(i));
                    jo.put("position", position.get(i));
                    jo.put("bats", bats.get(i));
                    jo.put("throws_", throws_.get(i));
                    jo.put("team", team);
                    ja.put(jo);
                }

                JSONObject email = new JSONObject();
                String emailCoach = activity.getSharedPreferences("coach_info", 0).getString("coach_email", "");
                email.put(emailCoach, ja);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(ja.toString());
                wr.flush();
                wr.close();

                conn.connect();

                InputStream inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                System.out.println(line);

            } catch (IOException | JSONException e) {
                Log.e(LOG_TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            activity.setResult(1);
            activity.finish();
        }
    }

    public class ReadPlayersFromServer extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private Activity activity;

        public ReadPlayersFromServer(Activity act) {
            activity = act;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(activity.getResources().getString(R.string.fetching_player));
            progressDialog.setMessage(activity.getResources().getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            BufferedReader reader;
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL("http://52.25.231.27:8000/api/players");
                //URL url = new URL("http://192.168.1.219:3000/api/players");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);

                conn.connect();

                InputStream inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null)
                    sb.append(line);

                if (sb.toString().length() > 0 && !sb.toString().equals("null")) {
                    JSONArray jArray = new JSONArray(sb.toString());
                    ContentValues addItem = new ContentValues();

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NAME_ID, jObject.getString("_id"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NAME, jObject.getString("name"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER, jObject.getInt("number"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME, jObject.getString("team_name"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_POSITION, jObject.getString("position"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BATS, jObject.getString("bats"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_THROWS, jObject.getString("throws_"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BATTING_AVERAGE, jObject.getDouble("batting_avg"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_RBI, jObject.getInt("rbi"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_RUNS, jObject.getInt("runs"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_HITS, jObject.getInt("hits"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_STRIKE_OUTS, jObject.getInt("strike_outs"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_WALKS, jObject.getInt("walks"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_SINGLE, jObject.getInt("single"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_DOUBLE, jObject.getInt("double_"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TRIPLE, jObject.getInt("triple"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_HOME_RUNS, jObject.getInt("home_runs"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_FLY_BALL, jObject.getInt("fly_balls"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_GROUND_BALLS, jObject.getInt("ground_balls"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_ON_BASE_PERCENTAGE, jObject.getDouble("on_base_percentage"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BASES_STOLEN, jObject.getInt("bases_stolen"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_CAUGHT_STEALING, jObject.getInt("caught_stealing"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_ERRORS, jObject.getInt("errors_"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_FIELD_PERCENTAGE, jObject.getDouble("field_percentage"));
                        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_PUT_OUTS, jObject.getInt("put_outs"));
                        db.insert(Contract.PlayerEntry.TABLE_NAME, null, addItem);
                    }

                    Thread.sleep(1000);
                    return true;
                } else {
                    Thread.sleep(1000);
                }
            } catch (IOException | JSONException | InterruptedException e) {
                Log.e(LOG_TAG, e.toString());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            progressDialog.dismiss();
            if (response) {
                ListView listView = (ListView) activity.findViewById(R.id.main_list_view);
                activity.registerForContextMenu(listView);
                listView.setAdapter(new PlayerListAdapter(activity, loadPlayers(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        activity.startActivity(new Intent(activity, PlayerInformation.class)
                                .putExtra("player_id", id + ""));
                    }
                });
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("Sorry")
                        .setMessage("There are no players at this time")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.getSharedPreferences("FirstRunPreference", 0).edit().putBoolean("firstTimePlayersLoad", true).apply();
                                activity.finish();
                            }
                        }).setCancelable(false).show();
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
}