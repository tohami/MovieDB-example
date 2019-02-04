package com.example.tmohammad.moviesmvvm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.tmohammad.moviesmvvm.data.MoviesRepository;
import com.example.tmohammad.moviesmvvm.ui.main.MainViewModel;

import java.lang.reflect.InvocationTargetException;


public class ViewModelFactory implements ViewModelProvider.Factory {

    private MoviesRepository moviesmoviesitory;

    public ViewModelFactory(MoviesRepository moviesmoviesitory) {
        this.moviesmoviesitory = moviesmoviesitory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (MainViewModel.class.isAssignableFrom(modelClass)) {
            try {
                return modelClass.getConstructor(MoviesRepository.class).newInstance(moviesmoviesitory);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class " + modelClass);
    }
}
