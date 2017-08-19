package com.hussainmukadam.watchit.mainpage.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.hussainmukadam.watchit.R;
import com.hussainmukadam.watchit.intropage.model.Genre;
import com.hussainmukadam.watchit.mainpage.MainMVPContract;
import com.hussainmukadam.watchit.mainpage.adapter.MovieAdapter;
import com.hussainmukadam.watchit.mainpage.adapter.TvSeriesAdapter;
import com.hussainmukadam.watchit.mainpage.model.Movie;
import com.hussainmukadam.watchit.mainpage.model.TvSeries;
import com.hussainmukadam.watchit.mainpage.presenter.MainPresenter;
import com.hussainmukadam.watchit.util.CustomSharedPreference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hussain on 7/23/17.
 */

public class MainFragment extends Fragment implements MainMVPContract.View, View.OnClickListener {
    private static final String TAG = "MainFragment";
    private static final int PAGE_START = 1;
    @BindView(R.id.swiping_layout)
    SwipeFlingAdapterView swipeFlingAdapterView;
    @BindView(R.id.animated_progress_bar)
    LottieAnimationView progressBar;
    @BindView(R.id.ib_favorite)
    ImageButton imageButtonFavorite;
    @BindView(R.id.ib_cancel)
    ImageButton imageButtonCancel;

    private MovieAdapter movieAdapter;
    private TvSeriesAdapter tvSeriesAdapter;
    private MainMVPContract.Presenter presenter;
    private MainPresenter mainPresenter;
    private CustomSharedPreference prefs;
    private String genresList;
    private Boolean isMovies;
    private List<Movie> nextPageArrayList = new ArrayList<>();
    private List<TvSeries> nextPageTvArrayList = new ArrayList<>();
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        isMovies = getArguments().getBoolean("IS_MOVIES");
        Log.d(TAG, "onCreateView: Boolean " + isMovies);

        prefs = new CustomSharedPreference(getContext());
        mainPresenter = new MainPresenter(this);
        imageButtonFavorite.setOnClickListener(this);
        imageButtonCancel.setOnClickListener(this);
        genresList = getGenres();

        if (isMovies) {
            mainPresenter.fetchFirstPageMoviesByGenres(genresList, currentPage);
        } else {
            mainPresenter.fetchFirstPageTvSeriesByGenres(genresList, currentPage);
        }

        return view;
    }

    static void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(MainMVPContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayFirstPageMovies(List<Movie> movieList, int totalPages) {
        setupSwipeFlingAdapterViewForMovies(movieList);
        this.TOTAL_PAGES = totalPages;
    }

    @Override
    public void displayNextPageMovies(List<Movie> movieList) {
        this.nextPageArrayList = movieList;
    }

    @Override
    public void displayFirstPageTvSeries(List<TvSeries> tvSeriesList, int totalPages) {
        setupSwipeFlingAdapterViewForTv(tvSeriesList);
        this.TOTAL_PAGES = totalPages;
    }

    @Override
    public void displayNextPageTvSeries(List<TvSeries> tvSeriesList) {
        this.nextPageTvArrayList = tvSeriesList;
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getGenres() {

        if (isMovies) {
            List<Genre> tempList = prefs.getMoviesGenrePreference();
            if (tempList.size() != 0) {

                List<Genre> moviesList = new ArrayList<>();
                Random r = new Random();
                for (int i = 0; i < 3; i++) {
                    moviesList.add(tempList.get(r.nextInt(prefs.getMoviesGenrePreference().size())));
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < moviesList.size(); i++) {
                    builder.append(moviesList.get(i).getGenreId());

                    if (i < moviesList.size() - 1) {
                        builder.append(",");
                    }

                    Log.d(TAG, "onCreateView: Movie String " + builder.toString());
                }
                return builder.toString();
            } else {
                return null;
            }
        } else {
            List<Genre> tempList = prefs.getTvGenrePreference();
            if (tempList.size() != 0) {

                List<Genre> tvList = new ArrayList<>();
                Random r = new Random();
                for (int i = 0; i < 3; i++) {
                    tvList.add(tempList.get(r.nextInt(prefs.getTvGenrePreference().size())));
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < tvList.size(); i++) {
                    builder.append(tvList.get(i).getGenreId());

                    if (i < tvList.size() - 1) {
                        builder.append(",");
                    }

                    Log.d(TAG, "onCreateView: TV String " + builder.toString());
                }
                return builder.toString();
            } else {
                return null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_favorite:
                if (isMovies) {
                    if (!movieAdapter.isEmpty()) {
                        swipeFlingAdapterView.getTopCardListener().selectRight();
                    }
                } else {
                    if (!tvSeriesAdapter.isEmpty()) {
                        swipeFlingAdapterView.getTopCardListener().selectRight();
                    }
                }
                break;
            case R.id.ib_cancel:
                if(isMovies) {
                    if (!movieAdapter.isEmpty()) {
                        swipeFlingAdapterView.getTopCardListener().selectLeft();
                    }
                } else {
                    if (!tvSeriesAdapter.isEmpty()) {
                        swipeFlingAdapterView.getTopCardListener().selectLeft();
                    }
                }
                break;
        }
    }


    private void setupSwipeFlingAdapterViewForMovies(final List<Movie> movieList) {
        Log.d(TAG, "displayMoviesCards: Movies List " + movieList.size());

        movieAdapter = new MovieAdapter(getContext(), R.layout.main_item, movieList);

        swipeFlingAdapterView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                if (movieList.size() != 0) {
                    movieList.remove(0);
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Log.d(TAG, "onLeftCardExit: dataObject " + dataObject.getClass().getSimpleName());
                Movie movie = (Movie) dataObject;
                mainPresenter.storeMovieData(false, movie);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Log.d(TAG, "onRightCardExit: dataObject " + dataObject.getClass().getSimpleName());
                Movie movie = (Movie) dataObject;
                mainPresenter.storeMovieData(true, movie);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                int i = 0;
                Log.d(TAG, "onAdapterAboutToEmpty: itemsInAdapter " + itemsInAdapter + " & currentPage " + currentPage + " & total Pages " + TOTAL_PAGES);

                if (itemsInAdapter == 0 && currentPage == 1 && TOTAL_PAGES == 0) {
                    Toast.makeText(getContext(), "Refreshing..", Toast.LENGTH_SHORT).show();
                    mainPresenter.fetchFirstPageMoviesByGenres(getGenres(), currentPage);
                } else {
                    if (itemsInAdapter == 6 && currentPage <= 5 && TOTAL_PAGES > 1) {
                        i++;

                        if (i == 1) {
                            currentPage++;
                            Log.d(TAG, "onAdapterAboutToEmpty: Current Page is " + currentPage);
                            mainPresenter.fetchNextPageMoviesByGenres(genresList, currentPage);
                        }
                    } else if (currentPage == TOTAL_PAGES || currentPage > 5) {
                        Log.d(TAG, "onAdapterAboutToEmpty: Fetching First Page " + currentPage + " Total Pages " + TOTAL_PAGES + " itemsInAdapter " + itemsInAdapter);
                        currentPage = PAGE_START;
                        mainPresenter.fetchFirstPageMoviesByGenres(getGenres(), currentPage);
                    }
                }

                if (itemsInAdapter <= 3 && nextPageArrayList.size() != 0) {
                    Log.d(TAG, "onAdapterAboutToEmpty: Adapter Changed");
                    movieList.addAll(nextPageArrayList);
                    movieAdapter.notifyDataSetChanged();
                }

                Log.d("LIST", "notified");
            }


            @Override
            public void onScroll(float scrollProgressPercent) {
//                View view = swipeFlingAdapterView.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(getContext(), "Clicked!");
            }
        });
    }

    private void setupSwipeFlingAdapterViewForTv(final List<TvSeries> tvSeriesList) {
        Log.d(TAG, "displayMoviesCards: Tv Series List " + tvSeriesList.size());

        tvSeriesAdapter = new TvSeriesAdapter(getContext(), R.layout.main_item, tvSeriesList);

        swipeFlingAdapterView.setAdapter(tvSeriesAdapter);
        tvSeriesAdapter.notifyDataSetChanged();
        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                if (tvSeriesList.size() != 0) {
                    tvSeriesList.remove(0);
                    tvSeriesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Log.d(TAG, "onLeftCardExit: dataObject " + dataObject.getClass().getSimpleName());
                TvSeries tvSeries = (TvSeries) dataObject;
//                mainPresenter.storeMovieData(false, movie);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Log.d(TAG, "onRightCardExit: dataObject " + dataObject.getClass().getSimpleName());
                TvSeries tvSeries = (TvSeries) dataObject;
//                mainPresenter.storeMovieData(true, movie);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                int i = 0;
                Log.d(TAG, "onAdapterAboutToEmpty: itemsInAdapter " + itemsInAdapter + " & currentPage " + currentPage + " & total Pages " + TOTAL_PAGES);

                if (itemsInAdapter == 0 && currentPage == 1 && TOTAL_PAGES == 0) {
                    Toast.makeText(getContext(), "Refreshing..", Toast.LENGTH_SHORT).show();
                    mainPresenter.fetchFirstPageTvSeriesByGenres(getGenres(), currentPage);
                } else {
                    if (itemsInAdapter == 6 && currentPage <= 5 && TOTAL_PAGES > 1) {
                        i++;

                        if (i == 1) {
                            currentPage++;
                            Log.d(TAG, "onAdapterAboutToEmpty: Current Page is " + currentPage);
                            mainPresenter.fetchNextPageTvSeriesByGenres(genresList, currentPage);
                        }
                    } else if (currentPage == TOTAL_PAGES || currentPage > 5) {
                        Log.d(TAG, "onAdapterAboutToEmpty: Fetching First Page " + currentPage + " Total Pages " + TOTAL_PAGES + " itemsInAdapter " + itemsInAdapter);
                        currentPage = PAGE_START;
                        mainPresenter.fetchFirstPageTvSeriesByGenres(getGenres(), currentPage);
                    }
                }

                if (itemsInAdapter <= 3 && nextPageTvArrayList.size() != 0) {
                    Log.d(TAG, "onAdapterAboutToEmpty: Adapter Changed");
                    tvSeriesList.addAll(nextPageTvArrayList);
                    tvSeriesAdapter.notifyDataSetChanged();
                }

                Log.d("LIST", "notified");
            }


            @Override
            public void onScroll(float scrollProgressPercent) {
//                View view = swipeFlingAdapterView.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(getContext(), "Clicked!");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
