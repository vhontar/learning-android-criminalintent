package com.vhontar.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vhontar.criminalintent.model.Crime;
import com.vhontar.criminalintent.model.CrimeLab;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mRvCrimeList;
    private CrimeAdapter mCrimeAdapter;
    private CrimeLab mCrimeLab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRvCrimeList = view.findViewById(R.id.rv_crime_list);
        mRvCrimeList.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        mCrimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = mCrimeLab.getCrimes();

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mRvCrimeList.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyDataSetChanged();
//            mCrimeAdapter.notifyItemChanged(mCrimeLab.getCrimeToUpdate());
        }
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
