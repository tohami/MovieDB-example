package com.example.tmohammad.moviesmvvm.ui.main;

import android.arch.lifecycle.Transformations;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tmohammad.moviesmvvm.R;
import com.example.tmohammad.moviesmvvm.databinding.MainFragmentBinding;
import com.example.tmohammad.moviesmvvm.di.Injection;
import com.example.tmohammad.moviesmvvm.model.RecentSearch;
import com.example.tmohammad.moviesmvvm.ui.adapter.MoviesAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;

public class MainFragment extends Fragment {

    //Constant used for Logs
    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    //Bundle constant to save the last searched query
    private static final String LAST_SEARCH_QUERY = "last_search_query";
    //The default query to load
    private static final String DEFAULT_QUERY = "now you see me";
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

        //Post the query to be searched if it not empty
        if(!query.isEmpty())
            mViewModel.searchMovies(query);

        //Initialize the EditText for Search Actions
        initAutoCompleteTextView(query);
    }

    /**
     * Initializes the EditText for handling the Search actions
     *
     * @param query The query to be searched for in the moviesitories
     */
    private void initAutoCompleteTextView(String query) {
        binding.searchMovie.setText(query);

        binding.searchMovie.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String inputText = binding.searchMovie.getText().toString().trim();
                updatemovieListFromInput(inputText);
                return true;
            } else {
                return false;
            }
        });

        binding.searchMovie.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String inputText = binding.searchMovie.getText().toString().trim();
                updatemovieListFromInput(inputText);
                return true;
            } else {
                return false;
            }
        });

        initAutoCompleteAdapter();
    }

    private void initAutoCompleteAdapter() {
        //init autocomplete Text adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (binding.searchMovie.getContext(), android.R.layout.simple_spinner_dropdown_item){
            //limit the suggistion count to 10 items
            @Override
            public int getCount() {
                return super.getCount() > 10 ? 10 : super.getCount() ;
            }
        };

        binding.searchMovie.setThreshold(1); //will start working from first character
        binding.searchMovie.setAdapter(adapter);

        binding.searchMovie.setOnItemClickListener((adapterView, view, position, id) -> {
            mViewModel.searchMovies(adapter.getItem(position));
        });

        mViewModel.getRecentSearch().observe(this , recentSearches -> {
            List<String> recentSearch = new ArrayList<>() ;
            if (recentSearches != null) {
                for(RecentSearch search : recentSearches){
                    recentSearch.add(search.getKeyword()) ;
                }
            }
            adapter.clear();
            adapter.addAll(recentSearch);
//            adapter.notifyDataSetChanged();
        });
        binding.searchMovie.dismissDropDown();
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
        mMoviesAdapter = new MoviesAdapter(movie -> {
            mViewModel.setSelectedMovie(movie);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_mainFragment_to_detailsFragment);
        });
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
     * Updates the list with the new data when the User entered the query and hit 'enter'
     * or corresponding action to trigger the Search.
     */
    private void updatemovieListFromInput(String inputText) {
        if (!TextUtils.isEmpty(inputText)) {
            binding.list.scrollToPosition(0);
            //Posts the query to be searched
            mViewModel.searchMovies(inputText);
            //Resets the old list
            mMoviesAdapter.submitList(null);
        }
    }

}
