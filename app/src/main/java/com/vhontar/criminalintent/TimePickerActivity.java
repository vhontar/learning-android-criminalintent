package com.vhontar.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class TimePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_TIME = "extra_time";

    private Fragment mFragment;

    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, TimePickerActivity.class);
        intent.putExtra(EXTRA_TIME, date);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_TIME);
        mFragment = TimePickerFragment.newInstance(date);
        return mFragment;
    }
}
