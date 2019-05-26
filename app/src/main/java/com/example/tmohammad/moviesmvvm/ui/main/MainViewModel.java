package com.example.tmohammad.moviesmvvm.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.example.tmohammad.moviesmvvm.data.MoviesRepository;
import com.example.tmohammad.moviesmvvm.model.Movie;
import com.example.tmohammad.moviesmvvm.model.MoviesSearchResult;
import com.example.tmohammad.moviesmvvm.model.RecentSearch;

import java.util.List;

public class MainViewModel extends ViewModel {
    private MoviesRepository moviemoviesitory;
    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    //Applying transformation to get movieSearchResult for the given Search Query
    private LiveData<MoviesSearchResult> movieResult = Transformations.map(queryLiveData,
            inputQuery -> moviemoviesitory.search(inputQuery)
    );
    //Applying transformation to get Live PagedList<movie> from the movieSearchResult
    private LiveData<PagedList<Movie>> movies = Transformations.switchMap(movieResult, input -> {
        return input.getData();
    });
    //Applying transformation to get Live Network Errors from the movieSearchResult
    private LiveData<String> networkErrors = Transformations.switchMap(movieResult,
            MoviesSearchResult::getNetworkErrors
    );

    public MainViewModel(MoviesRepository moviemoviesitory) {
        this.moviemoviesitory = moviemoviesitory;
    }

    public LiveData<PagedList<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<RecentSearch>> getRecentSearch() {
        return this.moviemoviesitory.getRecentSearch();
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    /**
     * Search a moviesitory based on a query string.
     */
    public void searchMovies(String queryString) {
        queryLiveData.postValue(queryString);
    }



}
