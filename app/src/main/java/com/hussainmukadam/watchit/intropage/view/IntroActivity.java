package com.hussainmukadam.watchit.intropage.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.hussainmukadam.watchit.BuildConfig;
import com.hussainmukadam.watchit.R;
import com.hussainmukadam.watchit.intropage.IntroMVPContract;
import com.hussainmukadam.watchit.intropage.adapter.MoviesGenreAdapter;
import com.hussainmukadam.watchit.intropage.adapter.TvGenreAdapter;
import com.hussainmukadam.watchit.intropage.model.Genre;
import com.hussainmukadam.watchit.intropage.presenter.IntroPresenter;
import com.hussainmukadam.watchit.mainpage.view.MainActivity;
import com.hussainmukadam.watchit.util.CustomSharedPreference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hussain on 7/21/17.
 */

public class IntroActivity extends AppCompatActivity implements IntroMVPContract.View, MoviesGenreAdapter.MoviesListItemClickListener, TvGenreAdapter.TvListItemClickListener{
    private static final String TAG = "IntroActivity";
    IntroMVPContract.Presenter presenter;
    IntroPresenter introPresenter;
    MoviesGenreAdapter moviesGenreAdapter;
    TvGenreAdapter tvGenreAdapter;
    ProgressDialog progressDialog;
    List<Genre> selectedTvGenres = new ArrayList<>();
    List<Genre> selectedMoviesGenres = new ArrayList<>();
    CustomSharedPreference customSharedPreference;

    @BindView(R.id.rv_genre_movies) RecyclerView rvGenreMovies;
    @BindView(R.id.rv_genre_tv) RecyclerView rvGenreTVShows;
    @BindView(R.id.btn_get_started) Button btnGetStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        customSharedPreference = new CustomSharedPreference(this);

        if(customSharedPreference.isIntroDisplayed()){
            Intent startMainActivity = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(startMainActivity);
        } else {
            setupRecyclerView();
            introPresenter = new IntroPresenter(this);
            setupProgressDialog();
            introPresenter.fetchGenresByMovies();
            introPresenter.fetchGenresByTV();
        }

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTvGenres.size()!=0 || selectedMoviesGenres.size()!=0){
                    if(selectedTvGenres.size()>0 && selectedMoviesGenres.size()>0){
                        customSharedPreference.setTvGenrePreference(selectedTvGenres);
                        customSharedPreference.setMoviesGenrePreference(selectedMoviesGenres);
                    } else if(selectedTvGenres.size()>0){
                        customSharedPreference.setTvGenrePreference(selectedTvGenres);
                    } else{
                        customSharedPreference.setMoviesGenrePreference(selectedMoviesGenres);
                    }

                    customSharedPreference.setIntroDisplayed(true);
                    Intent startMainActivity = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(startMainActivity);

                } else {
                    Toast.makeText(getApplicationContext(), "Please select at least one genre to move forward", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void setPresenter(IntroMVPContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayGenresByTV(List<Genre> genreTVList) {
        tvGenreAdapter = new TvGenreAdapter(genreTVList, this);
        rvGenreTVShows.setAdapter(tvGenreAdapter);
        tvGenreAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "TVs Fetched", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayGenresByMovies(List<Genre> genreMoviesList) {
        moviesGenreAdapter = new MoviesGenreAdapter(genreMoviesList, this);
        rvGenreMovies.setAdapter(moviesGenreAdapter);
        moviesGenreAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Movies Fetched", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView(){
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        rvGenreMovies.setLayoutManager(layoutManager);

        FlexboxLayoutManager tvLayoutManager= new FlexboxLayoutManager(this);
        tvLayoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        rvGenreTVShows.setLayoutManager(tvLayoutManager);
    }

    private void setupProgressDialog(){
        progressDialog = new ProgressDialog(IntroActivity.this);
        progressDialog.setCancelable(false);
    }



    @Override
    public void onTvListItemSelected(Genre genre) {
        selectedTvGenres.add(genre);
        Log.d(TAG, "onTvListItemSelected: Selected Genre Size "+selectedTvGenres.size());
    }

    @Override
    public void onTvListItemUnSelected(Genre genre) {
        selectedTvGenres.remove(genre);
        Log.d(TAG, "onTvListItemUnSelected: Selected Genre Size "+selectedTvGenres.size());
    }

    @Override
    public void onMovieListItemSelect(Genre genre) {
        selectedMoviesGenres.add(genre);
        Log.d(TAG, "onMovieListItemUnSelected: Selected Genre Size "+selectedMoviesGenres.size());
    }

    @Override
    public void onMovieListItemUnselect(Genre genre) {
        selectedMoviesGenres.remove(genre);
        Log.d(TAG, "onMovieListItemUnSelected: Selected Genre Size "+selectedMoviesGenres.size());
    }
}
