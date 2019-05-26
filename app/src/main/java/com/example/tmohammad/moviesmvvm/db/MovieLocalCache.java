package com.example.tmohammad.moviesmvvm.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.util.Log;

import com.example.tmohammad.moviesmvvm.model.Movie;
import com.example.tmohammad.moviesmvvm.model.RecentSearch;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

public class MovieLocalCache {
    //Constant used for Logs
    private static final String LOG_TAG = MovieLocalCache.class.getSimpleName();

    //Dao for movie Entity
    private MovieDAO movieDAO;
    //Single Thread Executor for database operations
    private Executor ioExecutor;

    public MovieLocalCache(MovieDAO movieDAO, Executor ioExecutor) {
        this.movieDAO = movieDAO;
        this.ioExecutor = ioExecutor;
    }

    /**
     * Insert a list of movies in the database, on a background thread.
     */
    public void insertMovies(List<Movie> movies, InsertCallback callback) {
        ioExecutor.execute(() -> {
            Log.d(LOG_TAG, "insert: inserting " + movies.size() + " movies");
            movieDAO.insertMovies(movies);
            callback.insertFinished();
        });
    }

    /**
     * Request a DataSource.Factory<Integer, movie> from the Dao, based on a movie name. If the name contains
     * multiple words separated by spaces, then we're emulating the GitHub API behavior and allow
     * any characters between the words.
     *
     * @param name moviesitory name
     */
    public DataSource.Factory<Integer, Movie> moviesByName(String name) {
        //insert name into recent search
        RecentSearch search = new RecentSearch() ;
        search.setKeyword(name);
        search.setTime(Calendar.getInstance().getTimeInMillis());
        insertResentSearch(search , () -> {});
        // appending '%' so we can allow other characters to be before and after the query string
        return movieDAO.moviesByName("%" + name.replace(' ', '%') + "%");
    }


    /**
     * Request a liveData of recentSearch
     */
    public LiveData<List<RecentSearch>> queryRecentSearch() {
        // appending '%' so we can allow other characters to be before and after the query string
        return movieDAO.getRecentSearch();
    }

    /**
     * Insert a recent search in the database, on a background thread.
     */
    public void insertResentSearch(RecentSearch query, InsertCallback callback) {
        ioExecutor.execute(() -> {
            Log.d(LOG_TAG, "insert: inserting " + query + " recent_search");
            movieDAO.insertRecentSearch(query);
            callback.insertFinished();
        });
    }

    public interface InsertCallback {
        /**
         * Callback method invoked when the insert operation
         * completes.
         */
        void insertFinished();
    }
}
