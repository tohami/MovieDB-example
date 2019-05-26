package com.example.tmohammad.moviesmvvm.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.example.tmohammad.moviesmvvm.api.MovieService;
import com.example.tmohammad.moviesmvvm.db.MovieLocalCache;
import com.example.tmohammad.moviesmvvm.model.Movie;
import com.example.tmohammad.moviesmvvm.model.MoviesSearchResult;
import com.example.tmohammad.moviesmvvm.model.RecentSearch;

import java.util.List;

public class MoviesRepository {
    //Constant used for logs
    private static final String LOG_TAG = MoviesRepository.class.getSimpleName();
    //Constant for the number of items to be loaded at once from the DataSource by the PagedList
    private static final int DATABASE_PAGE_SIZE = 20;
    private MovieService movieService;
    private MovieLocalCache localCache;

    public MoviesRepository(MovieService movieService, MovieLocalCache localCache) {
        this.movieService = movieService;
        this.localCache = localCache;
    }

    /**
     * Search movies whose names match the query.
     */
    public MoviesSearchResult search(String query) {
        Log.d(LOG_TAG, "search: New query: " + query);

        // Get data source factory from the local cache
        DataSource.Factory<Integer, Movie> moviesByName = localCache.moviesByName(query);

        // Construct the boundary callback
        MovieBoundaryCallback boundaryCallback = new MovieBoundaryCallback(query, movieService, localCache);
        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();

        // Set the Page size for the Paged list
        PagedList.Config pagedConfig = new PagedList.Config.Builder()
                .setPageSize(DATABASE_PAGE_SIZE)
                .build();

        // Get the Live Paged list
        LiveData<PagedList<Movie>> data = new LivePagedListBuilder<>(moviesByName, pagedConfig)
                .setBoundaryCallback(boundaryCallback)
                .build();

        // Get the Search result with the network errors exposed by the boundary callback
        return new MoviesSearchResult(data, networkErrors);
    }

    /**
     * get all recent search.
     */
    public LiveData<List<RecentSearch>> getRecentSearch() {
        return localCache.queryRecentSearch() ;
    }
}
