package com.example.tmohammad.moviesmvvm.ui.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

public class BindingAdapters {
    @BindingAdapter({"imageUrl", "error"})
    public static void loadImage(ImageView view, String url, Drawable error) {
        //w300_and_h450_bestv2
        //w300
        //w600_and_h900_bestv2
        Picasso.get().load("https://image.tmdb.org/t/p/w300" + url).error(error).into(view);
    }

//    @BindingAdapter({"rating"})
//    public static void setRating(RatingBar view, double rate) {
//        view.setRating((float) rate);
//    }
}
