package com.peruzal.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peruzal.popularmovies.BuildConfig;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by joseph on 5/10/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private List<Movie> mMovies;
    private Context mContext;
    private IMovieClickListener mMovieClickLister;

    public MovieAdapter(Context mContext, IMovieClickListener clickListener) {
        this.mContext = mContext;
        this.mMovieClickLister = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_movie,parent,false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        String movieUrl = NetworkUtils.buildPostImageUrl(mContext,movie.posterPath);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, movieUrl);
            Log.d(TAG, movie.toString());
        }

        //ViewCompat.setTransitionName(holder.mPosterImageView, movie.posterPath);
        Picasso.with(mContext)
                .load(movieUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) return 0;
        return mMovies.size();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPosterImageView;
        public MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieClickLister.onItemClick(getAdapterPosition());
                }
            });
            mPosterImageView = (ImageView) itemView.findViewById(R.id.imgPosterImage);
        }
    }

    public void setMovieData(List<Movie> movies){
        mMovies = movies;
        notifyDataSetChanged();
    }

    public interface IMovieClickListener{
      void onItemClick(int position);
    }
}
