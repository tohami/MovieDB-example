package com.example.tmohammad.moviesmvvm.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tmohammad.moviesmvvm.api.MovieService;
import com.example.tmohammad.moviesmvvm.api.MoviesServiceClient;
import com.example.tmohammad.moviesmvvm.db.MovieLocalCache;
import com.example.tmohammad.moviesmvvm.model.Movie;

import java.util.List;


public class MovieBoundaryCallback extends PagedList.BoundaryCallback<Movie> implements MoviesServiceClient.ApiCallback {
    //Constant used for logs
    private static final String LOG_TAG = MovieBoundaryCallback.class.getSimpleName();
    // Constant for the Number of items in a page to be requested from the Github API
    private static final int NETWORK_PAGE_SIZE = 50;
    private String query;
    private MovieService githubService;
    private MovieLocalCache localCache;
    // Keep the last requested page. When the request is successful, increment the page number.
    private int lastRequestedPage = 1;
    // Avoid triggering multiple requests in the same time
    private boolean isRequestInProgress = false;
    // LiveData of network errors.
    private MutableLiveData<String> networkErrors = new MutableLiveData<>();

    public MovieBoundaryCallback(String query, MovieService githubService, MovieLocalCache localCache) {
        this.query = query;
        this.githubService = githubService;
        this.localCache = localCache;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    /**
     * Method to request data from Github API for the given search query
     * and save the results.
     *
     * @param query The query to use for retrieving the moviesitories from API
     */
    private void requestAndSaveData(String query) {
        //Exiting if the request is in progress
        if (isRequestInProgress) return;

        //Set to true as we are starting the network request
        isRequestInProgress = true;

        //Calling the client API to retrieve the movies for the given search query
        MoviesServiceClient.getMoviesByname(githubService, query, lastRequestedPage, NETWORK_PAGE_SIZE, this);
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    @Override
    public void onZeroItemsLoaded() {
        Log.d(LOG_TAG, "onZeroItemsLoaded: Started");
        requestAndSaveData(query);
    }

    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within {@link PagedList.Config#prefetchDistance} of it.
     * <p>
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    @Override
    public void onItemAtEndLoaded(@NonNull Movie itemAtEnd) {
        Log.d(LOG_TAG, "onItemAtEndLoaded: Started");
        requestAndSaveData(query);
    }

    /**
     * Callback invoked when the Search movie API Call
     * completed successfully
     *
     * @param items The List of movies retrieved for the Search done
     */
    @Override
    public void onSuccess(List<Movie> items) {
        //Inserting records in the database thread
        localCache.insert(items, () -> {
            //Updating the last requested page number when the request was successful
            //and the results were inserted successfully
            lastRequestedPage++;
            //Marking the request progress as completed
            isRequestInProgress = false;
        });
    }

    /**
     * Callback invoked when the Search movie API Call failed
     *
     * @param errorMessage The Error message captured for the API Call failed
     */
    @Override
    public void onError(String errorMessage) {
        //Update the Network error to be shown
        networkErrors.postValue(errorMessage);
        //Mark the request progress as completed
        isRequestInProgress = false;
    }
}
