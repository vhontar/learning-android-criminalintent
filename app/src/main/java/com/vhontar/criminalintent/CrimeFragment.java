package com.vhontar.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.vhontar.criminalintent.models.crime.Crime;
import com.vhontar.criminalintent.models.crime.CrimeLab;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DATE_DIALOG = "date_dialog";
    private static final String TIME_DIALOG = "time_dialog";
    private static final int REQUEST_DATE_PICKER = 0;
    private static final int REQUEST_TIME_DIALOG = 1;

    private EditText mTitleEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    private Crime mCrime;

    public static Fragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID mCrimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(mCrimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.getInstance(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_crime_delete: {
                CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
                crimeLab.removeCrime(mCrime.getId());
                getActivity().finish();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleEditText = v.findViewById(R.id.et_crime_title);
        mTitleEditText.setText(mCrime.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(v12 -> openDateDialog(true));

        mTimeButton = v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(v1 -> openTimeDialog(true));

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setSolved(isChecked));

        return v;
    }

    private void openDateDialog(boolean isDialogOpen) {

        if (isDialogOpen) {
            FragmentManager fm = getFragmentManager();
            DatePickerFragment dpf = DatePickerFragment.newInstance(mCrime.getDate());
            dpf.setTargetFragment(CrimeFragment.this, REQUEST_DATE_PICKER);
            dpf.show(fm, DATE_DIALOG);
        } else {
            Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
            startActivityForResult(intent, REQUEST_DATE_PICKER);
        }
    }

    private void openTimeDialog(boolean isDialogOpen) {
        if (isDialogOpen) {
            FragmentManager fm = getFragmentManager();
            TimePickerFragment tpf = TimePickerFragment.newInstance(mCrime.getDate());
            tpf.setTargetFragment(CrimeFragment.this, REQUEST_TIME_DIALOG);
            tpf.show(fm, TIME_DIALOG);
        } else {
            Intent intent = TimePickerActivity.newIntent(getActivity(), mCrime.getDate());
            startActivityForResult(intent, REQUEST_TIME_DIALOG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE_PICKER) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
            updateTime();
        }

        if (requestCode == REQUEST_TIME_DIALOG) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateTime();
            updateDate();
        }
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(mCrime.getDate()));
    }

    private void updateTime() {
        mTimeButton.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL).format(mCrime.getDate()));
    }
}
