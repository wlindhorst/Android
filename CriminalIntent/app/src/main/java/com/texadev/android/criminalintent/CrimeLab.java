package com.texadev.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.texadev.android.criminalintent.database.CrimeCursorWrapper;
import com.texadev.android.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.texadev.android.criminalintent.database.CrimeBaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Bill on 2/27/2018.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
//    private List<Crime> mCrimes;  // USING SQL
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
//        mCrimes = new ArrayList<>();  // USING SQL
//        for(int i = 0; i < 100; i++) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" +i);
//            crime.setSolved(i % 2 == 0); // every other one
//            mCrimes.add(crime);
    }

    public void addCrime(Crime c){
//        mCrimes.add(c);// USING SQL
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
//        return mCrimes; // USING SQL
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
//        for (Crime crime : mCrimes){ // USING SQL
//            if(crime.getId().equals(id)){
//                return crime;
//            }
//        }
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();return cursor.getCrime();

        } finally {
            cursor.close();
        }
//        return null;
    }
    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFileName());
    }
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[] { uuidString});
    }
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupby
                null, // having
                null // orderby
        );
        return new CrimeCursorWrapper(cursor);
    }
}
