package com.msrproduction.baseballmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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

public class DatabaseAdapter {

    private final String LOG_TAG = DatabaseAdapter.class.getSimpleName();
    private Database DbHelper;
    static SQLiteDatabase db;

    public DatabaseAdapter(Context ctx) {
        DbHelper = Database.getInstance(ctx);
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

    private void insertPlayer(String name, String number, String position, String team, String bats, String throws_) {
        ContentValues addItem = new ContentValues();
        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NAME, name);
        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER, number);
        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME, team);
        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_POSITION, position);
        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_BATS, bats);
        addItem.put(Contract.PlayerEntry.COLUMN_PLAYER_THROWS, throws_);
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

    public void bulkInsert(List<String> name, List<String> number, List<String> position, String team, List<String> bats, List<String> throws_) {
        for (int i = 0; i < name.size(); i++) {
            insertPlayer(name.get(i), number.get(i), position.get(i), team, bats.get(i), throws_.get(i));
        }
        sendToServer sts = new sendToServer();
        sts.execute(name, number, position, bats, throws_, team);
    }

    private class sendToServer extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            List names = (List) params[0];
            List number = (List) params[1];
            List position = (List) params[2];
            List bats = (List) params[3];
            List throws_ = (List) params[4];
            String team = (String) params[5];
            BufferedReader reader;
            try {
                //URL url = new URL("http://52.25.231.27:8000/api/newplayer");
                URL url = new URL("http://192.168.1.219:3000/api/newplayer");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");

                JSONArray ja = new JSONArray();
                JSONObject count = new JSONObject();
                count.put("count", names.size());
                ja.put(count);

                for (int i = 0; i < names.size(); i++) {
                    JSONObject jo = new JSONObject();
                    jo.put("name", names.get(i));
                    jo.put("number", number.get(i));
                    jo.put("position", position.get(i));
                    jo.put("bats", bats.get(i));
                    jo.put("throws_", throws_.get(i));
                    jo.put("team", team);
                    ja.put(jo);
                }

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(ja.toString());
                wr.flush();
                wr.close();

                conn.connect();

                InputStream inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                System.out.println(line);

            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class readFromServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}