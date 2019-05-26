package com.example.tmohammad.moviesmvvm.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.tmohammad.moviesmvvm.model.Movie;
import com.example.tmohammad.moviesmvvm.model.RecentSearch;

import java.util.List;

@Dao
public interface MovieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    // Do a similar query as the search API:
    // Look for movies that contain the query string in the name or in the description
    // and order those results descending, by the number of stars and then by name ascending
    @Query("SELECT * FROM movies WHERE (title LIKE :queryString) ORDER BY voteCount DESC, title ASC")
    DataSource.Factory<Integer, Movie> moviesByName(String queryString);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecentSearch(RecentSearch recentSearch);

    // Do a similar query as the search API:
    // Look for movies that contain the query string in the name or in the description
    // and order those results descending, by the number of stars and then by name ascending
    @Query("SELECT * FROM recent_search")
    LiveData<List<RecentSearch>> getRecentSearch();
}
