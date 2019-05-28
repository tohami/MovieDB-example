package com.example.tmohammad.moviesmvvm.ui.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tmohammad.moviesmvvm.R;
import com.example.tmohammad.moviesmvvm.databinding.MovieViewItemBinding;
import com.example.tmohammad.moviesmvvm.model.Movie;

import androidx.navigation.fragment.FragmentNavigator;

/*
 * Adapter for the list of moviesitories.
 */
public class MoviesAdapter extends PagedListAdapter<Movie, MoviesAdapter.MovieViewHolder> {

    /**
     * DiffUtil to compare the movie data (old and new)
     * for issuing notify commands suitably to update the list
     */
    private static DiffUtil.ItemCallback<Movie> MOVIE_COMPARATOR
            = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final OnListItemSelectedListener onItemSelectedListener;

    public MoviesAdapter(OnListItemSelectedListener onItemSelectedListener) {
        super(MOVIE_COMPARATOR);
        this.onItemSelectedListener = onItemSelectedListener ;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Uses DataBinding to inflate the Item View
        MovieViewItemBinding itemBinding = MovieViewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(itemBinding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * View Holder for a {@link Movie} RecyclerView list item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MovieViewItemBinding mDataBinding;

        MovieViewHolder(MovieViewItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mDataBinding = itemBinding;

            View itemView = itemBinding.getRoot();
            itemView.setOnClickListener(this);
        }

        private void bind(Movie movie) {
            if (movie == null) {
                //Binding the elements in the code when the movie is null
                Resources resources = mDataBinding.getRoot().getContext().getResources();
                mDataBinding.movieName.setText(resources.getString(R.string.loading));
                mDataBinding.movieDescription.setVisibility(View.GONE);
            } else {
                //When movie is not null, data binding will be automatically done in the layout
                mDataBinding.setMovie(movie);
                //For Immediate Binding
                mDataBinding.executePendingBindings();
            }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            if (getAdapterPosition() > RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                Movie movie = getItem(getAdapterPosition());
                if (movie != null) {

                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                            .addSharedElement(mDataBinding.ivPoster, "poster")
                            .build();
                    onItemSelectedListener.onListItemSelected(movie , extras);
                }
            }
        }
    }

    public interface OnListItemSelectedListener {
        void onListItemSelected(Movie movie, FragmentNavigator.Extras extras) ;
    }
}
