package com.vhontar.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "extra_time";

    private static final String ARG_TIME = "arg_time";

    private Calendar mCalendar;
    private TimePicker mTimePicker;
    private Button mOkButton;

    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TIME, date);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_time, null);
        mTimePicker = v.findViewById(R.id.dialog_time_time_picker);
        mOkButton = v.findViewById(R.id.dialog_time_ok);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        int hours = mCalendar.get(Calendar.HOUR);
        int minutes = mCalendar.get(Calendar.MINUTE);

        mTimePicker.setCurrentHour(hours);
        mTimePicker.setCurrentMinute(minutes);

        mOkButton.setOnClickListener(v -> acceptChange());
    }

    public void acceptChange() {
        mCalendar.set(Calendar.HOUR, mTimePicker.getCurrentHour());
        mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
        sendResult(Activity.RESULT_OK, mCalendar.getTime());
    }

    private void sendResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        if (getTargetFragment() == null) {
            getActivity().setResult(resultCode, intent);
            getActivity().finish();
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
            dismiss();
        }
    }
}
