package com.vhontar.criminalintent.models.crime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vhontar.criminalintent.helpers.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private int mCrimeToUpdate;

    public static CrimeLab getInstance(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.NUMBER, crime.getNumber());
        return contentValues;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mSQLiteDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }

    public void addCrime(Crime c) {
        ContentValues contentValues = getContentValues(c);
        mSQLiteDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, contentValues);
    }

    public void updateCrime(Crime c) {
        String uuid = c.getId().toString();
        ContentValues contentValues = getContentValues(c);

        mSQLiteDatabase.update(CrimeDbSchema.CrimeTable.NAME, contentValues,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{ uuid });
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursorWrapper = queryCrimes(null, null);

        try {
            cursorWrapper.moveToFirst();
            while(!cursorWrapper.isAfterLast()) {
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID uuid) {
        CrimeCursorWrapper cursor = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[] {uuid.toString()});

        try {
            if (cursor.getCount() == 0) return null;

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void removeCrime(UUID uuid) {
        mSQLiteDatabase.delete(CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { uuid.toString() });
    }

    public int getCrimeToUpdate() {
        return mCrimeToUpdate;
    }

    public void setCrimeToUpdate(int crimeToUpdate) {
        mCrimeToUpdate = crimeToUpdate;
    }

}
