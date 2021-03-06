package com.example.android.funkygridlibrary.nineBoxCandidates;

/**
 * Created by Paul Gallini on 3/30/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.funkygridlibrary.databaseOpenHelper.DatabaseOpenHelper;

import java.util.ArrayList;

//import databaseOpenHelper.DatabaseOpenHelper;

public class CandidateOperations {
        // Database fields
        private DatabaseOpenHelper dbHelper;
        private String[] CANDIDATE_TABLE_COLUMNS = {DatabaseOpenHelper.CANDIDATE_ID, DatabaseOpenHelper.CANDIDATE_NAME, DatabaseOpenHelper.CANDIDATE_NICKNAME, DatabaseOpenHelper.CANDIDATE_NOTES, DatabaseOpenHelper.CANDIDATE_COLOR, DatabaseOpenHelper.CANDIDATE_INITIALS };
        private SQLiteDatabase database;

        public CandidateOperations(Context context) {
            dbHelper = DatabaseOpenHelper.getInstance(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public Candidates addCandidate(String name, String nickname, String notes, String CandidateColor, String CandidateInitials) {
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.CANDIDATE_NAME, name);
            values.put(DatabaseOpenHelper.CANDIDATE_NICKNAME, nickname );
            values.put(DatabaseOpenHelper.CANDIDATE_NOTES, notes);
            values.put(DatabaseOpenHelper.CANDIDATE_COLOR, CandidateColor);
            values.put(DatabaseOpenHelper.CANDIDATE_INITIALS,CandidateInitials );
            long candId = database.insert(DatabaseOpenHelper.CANDIDATES, null, values);
            // now that the candidate is created return it ...
            Cursor cursor = database.query(DatabaseOpenHelper.CANDIDATES,
                    CANDIDATE_TABLE_COLUMNS, DatabaseOpenHelper.CANDIDATE_ID + " = "
                            + candId, null, null, null, null, null);

            cursor.moveToFirst();

            Candidates newComment = parseCandidate(cursor);
            cursor.close();

            // update the color table to mark this color as in-use ...
            DatabaseOpenHelper.updateColorsTableToggleInUse(database,  CandidateColor, true);
            return newComment;
        }

    public boolean updateCandidate(long candidateID, String name, String nickname, String notes, String CandidateColor, String CandidateInitials) {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.CANDIDATE_NAME, name);
        values.put(DatabaseOpenHelper.CANDIDATE_NICKNAME, nickname );
        values.put(DatabaseOpenHelper.CANDIDATE_NOTES, notes);
        values.put(DatabaseOpenHelper.CANDIDATE_COLOR, CandidateColor);
        values.put(DatabaseOpenHelper.CANDIDATE_INITIALS,CandidateInitials );

        long candId = database.update(DatabaseOpenHelper.CANDIDATES, values, DatabaseOpenHelper.CANDIDATE_ID + "=" + candidateID ,null);

        return (candId > 0);
    }

        public void deleteCandidate(Candidates candidate) {
            long id = candidate.getCandidateID();
            database.delete(DatabaseOpenHelper.CANDIDATES, DatabaseOpenHelper.CANDIDATE_ID
                    + " = " + id, null);
            // update the color table to mark this color as in-use ...
            DatabaseOpenHelper.updateColorsTableToggleInUse(database, candidate.getCandidateColor(), false);
            // delete all of their responses too
            database.delete(DatabaseOpenHelper.RESPONSES, DatabaseOpenHelper.RESP_CANDIDATE_ID
                    + " = " + id, null);
        }

        public ArrayList<Candidates> getAllCandidates() {
            ArrayList<Candidates> candidates = new ArrayList();
            Cursor cursor = database.query(DatabaseOpenHelper.CANDIDATES,
                    CANDIDATE_TABLE_COLUMNS, null, null, null, null, null, null);

            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Candidates candidate = parseCandidate(cursor);
                    candidates.add(candidate);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }

            return candidates;
        }

        private Candidates parseCandidate(Cursor cursor) {
            Candidates candidate = new Candidates();
            candidate.setCandidateID((cursor.getInt(0)));
            candidate.setCandidateName(cursor.getString(1));
            candidate.setCandidateNickName(cursor.getString(2));
            candidate.setCandidateNotes(cursor.getString(3));
            candidate.setCandidateColor(cursor.getString(4));
            candidate.setCandidateInitials(cursor.getString(5));
            return candidate;
        }
    }
