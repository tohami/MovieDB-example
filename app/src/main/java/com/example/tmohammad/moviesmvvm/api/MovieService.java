package com.example.tmohammad.moviesmvvm.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {

    @GET("/3/search/movie")
    Call<MoviesResponce> getMoviesByName(@Query("api_key") String api_key,
                                         @Query("page") int pagee,
                                         @Query("query") String query);


}