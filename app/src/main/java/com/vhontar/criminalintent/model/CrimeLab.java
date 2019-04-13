package com.vhontar.criminalintent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;
    private int mCrimeToUpdate;

    public static CrimeLab getInstance(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        // Temporary
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0); // Every other one
            mCrimes.add(crime);
        }
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID uuid) {
        for(Crime crime: mCrimes) {
            if (uuid.equals(crime.getId())) {
                return crime;
            }
        }
        return null;
    }


    public int getCrimeToUpdate() {
        return mCrimeToUpdate;
    }

    public void setCrimeToUpdate(int crimeToUpdate) {
        mCrimeToUpdate = crimeToUpdate;
    }

}
