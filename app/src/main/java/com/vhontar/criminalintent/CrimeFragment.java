package com.vhontar.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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
import com.vhontar.criminalintent.utils.PictureUtils;

import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DATE_DIALOG = "date_dialog";
    private static final String TIME_DIALOG = "time_dialog";
    private static final int REQUEST_DATE_PICKER = 0;
    private static final int REQUEST_TIME_DIALOG = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private EditText mTitleEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mCrimeSuspectButton;
    private Button mSendCrimeResportButton;
    private Button mCallSuspectButton;
    private ImageView mIvCrimePhoto;
    private ImageButton mIbCrimeCamera;

    private File mPhotoFile;
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
        mPhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);
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
        mTimeButton.setOnClickListener(v11 -> openTimeDialog(true));

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setSolved(isChecked));

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mCrimeSuspectButton = v.findViewById(R.id.crime_suspect);
        mCrimeSuspectButton.setOnClickListener(v13 -> startActivityForResult(pickContact, REQUEST_CONTACT));

        if (mCrime.getSuspect() != null) {
            mCrimeSuspectButton.setText(mCrime.getSuspect());
        }

        /**
         * if there are no app contacts at all, without these code, app would crash
         * */
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mCrimeSuspectButton.setEnabled(false);
        }

        mSendCrimeResportButton = v.findViewById(R.id.crime_report);
        mSendCrimeResportButton.setOnClickListener(v14 ->
                ShareCompat.IntentBuilder.from(getActivity())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setText(getCrimeReport())
                    .setType("text/plain")
                    .setChooserTitle(R.string.send_report)
                    .startChooser()
        );

        mCallSuspectButton = v.findViewById(R.id.crime_call_suspect);
        mCallSuspectButton.setOnClickListener(v15 -> {
            Uri uri = Uri.parse("tel:" + mCrime.getNumber());
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(uri);
            startActivity(intent);
        });

        if (mCrime.getNumber() == null) {
            mCallSuspectButton.setEnabled(false);
        }

        mIbCrimeCamera = v.findViewById(R.id.ib_crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mIbCrimeCamera.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mIbCrimeCamera.setOnClickListener(v1 -> startActivityForResult(captureImage, REQUEST_PHOTO));

        mIvCrimePhoto = v.findViewById(R.id.iv_crime_photo);
        updateCrimePhotoView();

        return v;
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

        if (requestCode == REQUEST_PHOTO) {
            updateCrimePhotoView();
        }

        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursorContact = contentResolver.query(
                    contactUri, queryFields, null, null, null
            );
            Cursor cursorContactNumber = null;

            try {
                if (cursorContact.getCount() == 0) return;

                cursorContact.moveToFirst();

                String suspect = cursorContact.getString(1);
                mCrime.setSuspect(suspect);
                mCrimeSuspectButton.setText(suspect);

                cursorContactNumber = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + cursorContact.getString(0),
                        null, null);

                if (cursorContactNumber.getCount() == 0) return;
                cursorContactNumber.moveToFirst();
                String number = cursorContactNumber.getString(cursorContactNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mCrime.setNumber(number);
                mCallSuspectButton.setText(number);

                if (number != null) {
                    mCallSuspectButton.setEnabled(true);
                }


            } finally {
                cursorContact.close();
                if (cursorContactNumber != null) cursorContactNumber.close();
            }
        }
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

    private void updateDate() {
        String dateFormat = "EEE, MMM dd";
        mDateButton.setText(DateFormat.format(dateFormat, mCrime.getDate()));
    }

    private void updateTime() {
        String timeFormat = "HH:mm";
        mTimeButton.setText(DateFormat.format(timeFormat, mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updateCrimePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mIvCrimePhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mIvCrimePhoto.setImageBitmap(bitmap);
        }
    }
}
