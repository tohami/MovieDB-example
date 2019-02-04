package com.example.tmohammad.moviesmvvm.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.tmohammad.moviesmvvm.R;
import com.example.tmohammad.moviesmvvm.databinding.MainFragmentBinding;
import com.example.tmohammad.moviesmvvm.di.Injection;
import com.example.tmohammad.moviesmvvm.ui.adapter.MoviesAdapter;

public class MainFragment extends Fragment {

    //Constant used for Logs
    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    //Bundle constant to save the last searched query
    private static final String LAST_SEARCH_QUERY = "last_search_query";
    //The default query to load
    private static final String DEFAULT_QUERY = "The Terminator";
    private MainViewModel mViewModel;
    private MainFragmentBinding binding;
    private MoviesAdapter mMoviesAdapter;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.main_fragment, container, false);
        View view = binding.getRoot();

        //Set the Empty text with emoji unicode
        binding.emptyList.setText(getString(R.string.no_results, "\uD83D\uDE13"));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Get the view model
        mViewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(getContext()))
                .get(MainViewModel.class);

        //Initialize RecyclerView
        initRecyclerView();

        //Get the query to search
        String query = DEFAULT_QUERY;
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY, DEFAULT_QUERY);
        }

        //Post the query to be searched
        mViewModel.searchMovies(query);

        //Initialize the EditText for Search Actions
        initSearch(query);
    }

    /**
     * Initializes the RecyclerView that loads the list of movies
     */
    private void initRecyclerView() {
        //Add dividers between RecyclerView's row items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.list.addItemDecoration(dividerItemDecoration);

        //Initializing Adapter
        initAdapter();
    }

    /**
     * Initializes the Adapter of RecyclerView which is {@link MoviesAdapter}
     */
    private void initAdapter() {
        mMoviesAdapter = new MoviesAdapter();
        binding.list.setAdapter(mMoviesAdapter);

        //Subscribing to receive the new PagedList movies
        mViewModel.getMovies().observe(this, movies -> {
            if (movies != null) {
                Log.d(LOG_TAG, "initAdapter: movie List size: " + movies.size());
                showEmptyList(movies.size() == 0);
                mMoviesAdapter.submitList(movies);
            }
        });

        //Subscribing to receive the recent Network Errors if any
        mViewModel.getNetworkErrors().observe(this, errorMsg -> {
            Toast.makeText(getContext(), "\uD83D\uDE28 Wooops " + errorMsg, Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Shows the Empty view when the list is empty
     *
     * @param show Displays the empty view and hides the list when the boolean is <b>True</b>
     */
    private void showEmptyList(boolean show) {
        if (show) {
            binding.list.setVisibility(View.GONE);
            binding.emptyList.setVisibility(View.VISIBLE);
        } else {
            binding.list.setVisibility(View.VISIBLE);
            binding.emptyList.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes the EditText for handling the Search actions
     *
     * @param query The query to be searched for in the moviesitories
     */
    private void initSearch(String query) {
        binding.searchMovie.setText(query);

        binding.searchMovie.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatemovieListFromInput();
                return true;
            } else {
                return false;
            }
        });

        binding.searchMovie.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatemovieListFromInput();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Updates the list with the new data when the User entered the query and hit 'enter'
     * or corresponding action to trigger the Search.
     */
    private void updatemovieListFromInput() {
        String queryEntered = binding.searchMovie.getText().toString().trim();
        if (!TextUtils.isEmpty(queryEntered)) {
            binding.list.scrollToPosition(0);
            //Posts the query to be searched
            mViewModel.searchMovies(queryEntered);
            //Resets the old list
            mMoviesAdapter.submitList(null);
        }
    }

}
