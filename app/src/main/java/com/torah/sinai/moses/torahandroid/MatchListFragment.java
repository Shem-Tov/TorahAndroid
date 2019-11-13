package com.torah.sinai.moses.torahandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class MatchListFragment extends Fragment {
    private RecyclerView mmatchRecyclerView;
    private MyRecyclerViewAdapter adapter = null;
    private MyDilugRecyclerViewAdapter adapter_dilug = null;
    private MyReportRecyclerViewAdapter adapter_report = null;
    private SearchFragment.SearchModeEnum mSearchModeEnum;
    private static MatchListFragment instance = null;


    public static synchronized MatchListFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_match_list,
                container, false);
        Bundle bundle = getArguments();
        instance = this;
        try {
            mSearchModeEnum = SearchFragment.getSearchModeEnum(bundle.getInt(MainActivity.ARGS_ADAPTER));
        } catch (NullPointerException e) {
            mSearchModeEnum = SearchFragment.SearchModeEnum.Search;
        }

        mmatchRecyclerView = view
                .findViewById(R.id.match_recycler_view);
        //match_recycler_view in fragment_match_list.xml
        mmatchRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity()));
        //mmatchRecyclerView.setItemAnimator(null);
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private void updateUI() {
        // data to populate the RecyclerView with
        if (mSearchModeEnum == SearchFragment.SearchModeEnum.Dilugim) {
            if (adapter_dilug == null) {
                ArrayList<ArrayList<Match>> dilugSets = DilugMatchLab.getDilugMatches();
                ArrayList<String> lineDilugSet = DilugMatchLab.getLineDilugSets();
                adapter_dilug = new MyDilugRecyclerViewAdapter(getActivity(), dilugSets, lineDilugSet);
                mmatchRecyclerView.setAdapter(adapter_dilug);
            } else {
                adapter_dilug.notifyDataSetChanged();
            }
        } else if (mSearchModeEnum == SearchFragment.SearchModeEnum.Report_Count) {
            if (adapter_report == null) {
                ArrayList<Match> matches = MatchLab.getMatches();
                adapter_report = new MyReportRecyclerViewAdapter(getActivity(), matches);
                mmatchRecyclerView.setAdapter(adapter_report);
            } else {
                adapter_report.notifyDataSetChanged();
            }
        } else {
            if (adapter == null) {
                ArrayList<Match> matches = MatchLab.getMatches();
                adapter = new MyRecyclerViewAdapter(getActivity(), matches);
                mmatchRecyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void update() {
        if (mSearchModeEnum == SearchFragment.SearchModeEnum.Dilugim) {
            ArrayList<ArrayList<Match>> dilugSets = DilugMatchLab.getDilugMatches();
            ArrayList<String> lineDilugSet = DilugMatchLab.getLineDilugSets();
            adapter_dilug = new MyDilugRecyclerViewAdapter(getActivity(), dilugSets, lineDilugSet);
            mmatchRecyclerView.setAdapter(adapter_dilug);
        } else if (mSearchModeEnum == SearchFragment.SearchModeEnum.Report_Count) {
            ArrayList<Match> matches = MatchLab.getMatches();
            adapter_report = new MyReportRecyclerViewAdapter(getActivity(), matches);
            mmatchRecyclerView.setAdapter(adapter_report);
        } else {
            ArrayList<Match> matches = MatchLab.getMatches();
            adapter = new MyRecyclerViewAdapter(getActivity(), matches);
            mmatchRecyclerView.setAdapter(adapter);
        }
    }
}
