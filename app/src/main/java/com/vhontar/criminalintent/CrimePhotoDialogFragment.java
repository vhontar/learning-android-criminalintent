package com.vhontar.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vhontar.criminalintent.utils.PictureUtils;

public class CrimePhotoDialogFragment extends DialogFragment {

    private static final String ARG_CRIME_PHOTO_PATH = "crime_photo_path";

    private ImageView ivDcpCrimePhoto;

    private String mCrimePhotoPath;

    public static CrimePhotoDialogFragment newInstance(String crimePhotoPath) {
        Bundle args = new Bundle();
        args.putString(ARG_CRIME_PHOTO_PATH, crimePhotoPath);

        CrimePhotoDialogFragment crimePhotoDialogFragment = new CrimePhotoDialogFragment();
        crimePhotoDialogFragment.setArguments(args);
        return crimePhotoDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrimePhotoPath = getArguments().getString(ARG_CRIME_PHOTO_PATH);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_crime_photo, null);

        ivDcpCrimePhoto = v.findViewById(R.id.iv_dcp_crime_photo);
        if (!mCrimePhotoPath.isEmpty()) {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mCrimePhotoPath, getActivity());
            ivDcpCrimePhoto.setImageBitmap(bitmap);
        }

        return v;
    }
}
