package com.example.tmohammad.moviesmvvm.di;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.tmohammad.moviesmvvm.api.MoviesServiceClient;
import com.example.tmohammad.moviesmvvm.data.MoviesRepository;
import com.example.tmohammad.moviesmvvm.db.MovieDatabase;
import com.example.tmohammad.moviesmvvm.db.MovieLocalCache;

import java.util.concurrent.Executors;


public class Injection {

    /**
     * Creates an instance of {@link MovieLocalCache} based on the database DAO.
     */
    @NonNull
    private static MovieLocalCache provideCache(Context context) {
        MovieDatabase movieDatabase = MovieDatabase.getInstance(context);
        return new MovieLocalCache(movieDatabase.movieDao(), Executors.newSingleThreadExecutor());
    }

    /**
     * Creates an instance of {@link com.example.tmohammad.moviesmvvm.data.MoviesRepository} based on the
     * {@link .api.MovieService} and a
     * {@link MovieLocalCache}
     */
    @NonNull
    private static MoviesRepository provideMoviesRepository(Context context) {
        return new MoviesRepository(MoviesServiceClient.create(), provideCache(context));
    }


    @NonNull
    public static ViewModelFactory provideViewModelFactory(Context context) {
        return new ViewModelFactory(provideMoviesRepository(context));
    }
}
