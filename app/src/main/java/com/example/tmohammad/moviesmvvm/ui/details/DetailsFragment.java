package com.example.tmohammad.moviesmvvm.ui.details;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tmohammad.moviesmvvm.R;
import com.example.tmohammad.moviesmvvm.databinding.DetailsFragmentBinding;
import com.example.tmohammad.moviesmvvm.ui.main.MainViewModel;

public class DetailsFragment extends Fragment {

    private MainViewModel mViewModel;
    private DetailsFragmentBinding binding;

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.details_fragment, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get the view model
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

}
