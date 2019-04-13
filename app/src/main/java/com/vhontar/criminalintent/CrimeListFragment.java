package com.vhontar.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vhontar.criminalintent.model.Crime;
import com.vhontar.criminalintent.model.CrimeLab;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle_visible";

    private RecyclerView mRvCrimeList;
    private TextView mTvNoCrimes;
    private Button mBtnNewCrime;
    private CrimeAdapter mCrimeAdapter;
    private CrimeLab mCrimeLab;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * It is required to tell FragmentManager that we receive a call from OS to onCreateOptionsMenu method
         * */
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRvCrimeList = view.findViewById(R.id.rv_crime_list);
        mRvCrimeList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTvNoCrimes = view.findViewById(R.id.tv_no_crime);
        mBtnNewCrime = view.findViewById(R.id.btn_new_crime);

        mBtnNewCrime.setOnClickListener(v -> createCrimeAndOpenEditor());

        updateUI();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) subtitleItem.setTitle(R.string.hide_subtitle);
        else subtitleItem.setTitle(R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fcl_menu_item_new_crime: {
                createCrimeAndOpenEditor();
                return true;
            }
            case R.id.menu_item_show_subtitle: {
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createCrimeAndOpenEditor() {
        Crime crime = new Crime();
        mCrimeLab.addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivity(intent);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int size = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, size);

        if (!mSubtitleVisible) subtitle = null;

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        mCrimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = mCrimeLab.getCrimes();

        if (crimes.size() == 0) {
            mTvNoCrimes.setVisibility(View.VISIBLE);
            mBtnNewCrime.setVisibility(View.VISIBLE);
        }

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mRvCrimeList.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyDataSetChanged();
//            mCrimeAdapter.notifyItemChanged(mCrimeLab.getCrimeToUpdate());
        }

        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;
        private int mCrimePosition;

        private TextView mTvCrimeTitle;
        private TextView mTvCrimeDate;
        private CheckBox mCbCrimeSolved;

        private CrimeHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvCrimeTitle = itemView.findViewById(R.id.tv_list_item_crime_title);
            mTvCrimeDate = itemView.findViewById(R.id.tv_list_item_crime_date);
            mCbCrimeSolved = itemView.findViewById(R.id.cb_list_item_crime_solved);
        }

        private void bindCrime(Crime crime, int position) {
            mCrime = crime;
            mCrimePosition = position;
            mTvCrimeTitle.setText(crime.getTitle());
            mTvCrimeDate.setText(crime.getDate().toString());
            mCbCrimeSolved.setChecked(crime.isSolved());
        }

        @Override
        public void onClick(View v) {
            mCrimeLab.setCrimeToUpdate(mCrimePosition);
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter {

        private List<Crime> mCrimes;

        private CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, viewGroup, false);
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Crime crime = mCrimes.get(i);
            ((CrimeHolder) viewHolder).bindCrime(crime, i);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
