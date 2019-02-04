package com.example.tmohammad.moviesmvvm.ui.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class BindingAdapters {
    @BindingAdapter({"imageUrl", "error"})
    public static void loadImage(ImageView view, String url, Drawable error) {
        Picasso.get().load("https://image.tmdb.org/t/p/w300" + url).error(error).into(view);
    }
}
