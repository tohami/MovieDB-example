package com.example.tmohammad.moviesmvvm.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

public class MoviesSearchResult {
    //LiveData for Search Results
    private final LiveData<PagedList<Movie>> data;
    //LiveData for the Network Errors
    private final LiveData<String> networkErrors;

    public MoviesSearchResult(LiveData<PagedList<Movie>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<Movie>> getData() {
        return data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

}
