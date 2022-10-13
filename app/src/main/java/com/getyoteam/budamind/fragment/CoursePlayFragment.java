package com.getyoteam.budamind.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getyoteam.budamind.R;
import com.getyoteam.budamind.activity.IMainActivity;
import com.getyoteam.budamind.adapter.HomeRecyclerAdapter;
import java.util.ArrayList;


public class CoursePlayFragment extends Fragment implements HomeRecyclerAdapter.IHomeSelector
{

    private static final String TAG = "HomeFragment";


    // UI Components
    private RecyclerView mRecyclerView;

    // Vars
    private HomeRecyclerAdapter mAdapter;
    private ArrayList<String> mCategories = new ArrayList<>();
    private IMainActivity mIMainActivity;

    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            mIMainActivity.setActionBarTitle("Categories");
        }
    }
	
	@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initRecyclerView(view);
        mIMainActivity.setActionBarTitle("Categories");
    }

    private void retrieveCategories(){
        mIMainActivity.showPrgressBar();
    }


    private void initRecyclerView(View view){
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new HomeRecyclerAdapter(mCategories, getActivity(),  this);
        mRecyclerView.setAdapter(mAdapter);

        if(mCategories.size() == 0){
            retrieveCategories();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onCategorySelected(int postion) {
        Log.d(TAG, "onCategorySelected: list item is clicked!");
        mIMainActivity.onCategorySelected(mCategories.get(postion));
    }


}















