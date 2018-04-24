package gr.angeloskyriakos.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gr.angeloskyriakos.moviesapp.data.MovieContract;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, AdapterView.OnItemClickListener{

    private String mSearchUrlExtra = "top_rated";
    private static final int MOVIE_SEARCH_LOADER = 10;
    private static final int MOVIE_FAVORITES_LOADER = 20;
    private static final String QUERY_URL_EXTRA = "query_extra";

    private List<Movie> movies = new ArrayList<Movie>();
    //String moviesQueryResponse = null;
    private GridAdapter mGridAdapter = null;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private boolean mIsConnected;
    private FavoritesAdapter mFavoritesAdapter = null;
    private List<Movie> favoriteMovies = new ArrayList<Movie>();

    private LoaderManager.LoaderCallbacks<String> webDataLoaderListener = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String>(getApplicationContext()) {
                @Override
                public String loadInBackground() {
                    try {
                        URL testUrl = NetworkUtils.getUrl(MainActivity.this, args.getString(QUERY_URL_EXTRA));
                        String httpResponseJSON = NetworkUtils.getResponseFromHttpUrl(testUrl);
                        //System.out.println(httpResponseJSON);
                        return httpResponseJSON;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                @Override
                protected void onStartLoading() {
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            extractJSON(data);
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.GONE);
            GridView gridView = (GridView) findViewById(R.id.grid_view);
            if(movies != null) {
                mGridAdapter = new GridAdapter(MainActivity.this, movies);
                gridView.setAdapter(mGridAdapter);
            }
            else {
                mEmptyView = (TextView) findViewById(R.id.emptyView);
                mEmptyView.setVisibility(View.VISIBLE);
                gridView.setEmptyView(mEmptyView);
            }
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> dbLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {
                Cursor data = null;
                @Override
                protected void onStartLoading() {
                    if(data != null) {
                        deliverResult(data);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);

                    } catch (Exception e) {
                        Log.e("DB Loader", "Failed to asynchronously load data for the database.");
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(Cursor data) {
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            extractDataFromCursor(data);
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.GONE);
            GridView gridView = (GridView) findViewById(R.id.grid_view);
            gridView.setAdapter(null);
            if(favoriteMovies != null) {
                mFavoritesAdapter = new FavoritesAdapter(MainActivity.this, favoriteMovies);
                gridView.setAdapter(mFavoritesAdapter);
            }
            else {
                mEmptyView = (TextView) findViewById(R.id.emptyView);
                mEmptyView.setVisibility(View.VISIBLE);
                gridView.setEmptyView(mEmptyView);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();
        setTitle();
        mGridAdapter = new GridAdapter(MainActivity.this, movies);
        mFavoritesAdapter = new FavoritesAdapter(MainActivity.this, favoriteMovies);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(mGridAdapter);
        gridView.setOnItemClickListener(MainActivity.this);

        //Check internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        mIsConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (mIsConnected) {
            if(!(mSearchUrlExtra.equals("favorites"))) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString(QUERY_URL_EXTRA, mSearchUrlExtra);
                getSupportLoaderManager().initLoader(MOVIE_SEARCH_LOADER, queryBundle, webDataLoaderListener);
            } else {
                getSupportLoaderManager().initLoader(MOVIE_FAVORITES_LOADER, null, dbLoaderListener);
            }
        }
        else {
            System.out.println("NOT CONNECTED");
            getSupportLoaderManager().initLoader(MOVIE_FAVORITES_LOADER, null, dbLoaderListener);
//            mEmptyView = (TextView) findViewById(R.id.emptyView);
//            mEmptyView.setText("There is no internet connection");
//            mEmptyView.setVisibility(View.VISIBLE);
//            gridView.setEmptyView(mEmptyView);
//            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//            mProgressBar.setVisibility(View.GONE);
        }

    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSearchUrlExtra = sharedPreferences.getString(getString(R.string.pref_query_key),
                getResources().getString(R.string.pref_query_default));
        loadFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void loadFromPreferences(SharedPreferences sharedPreferences) {
        mSearchUrlExtra = sharedPreferences.getString(getString(R.string.pref_query_key),
                getString(R.string.pref_query_default));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_query_key))){
            loadFromPreferences(sharedPreferences);
            setTitle();
            LoaderManager loaderManager = getSupportLoaderManager();
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            mIsConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            GridView gridView = (GridView) findViewById(R.id.grid_view);
            if (mIsConnected) {
                if(mSearchUrlExtra.equals("favorites")) {
                    Loader<Cursor> DbLoader = loaderManager.getLoader(MOVIE_FAVORITES_LOADER);
                    mGridAdapter.clear();
                    gridView.setAdapter(null);
                    if (DbLoader == null) {
                        System.out.println("DB NULL");
                        loaderManager.initLoader(MOVIE_FAVORITES_LOADER, null, dbLoaderListener);
                    } else {
                        System.out.println("DBLOADER NOT NULL");
                        loaderManager.destroyLoader(MOVIE_FAVORITES_LOADER);
                        loaderManager.initLoader(MOVIE_FAVORITES_LOADER, null, dbLoaderListener);
                    }
                } else {
                    gridView.setAdapter(null);
                    Loader<String> queryLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);
                    if (queryLoader == null) {
                        Bundle queryBundle = new Bundle();
                        queryBundle.putString(QUERY_URL_EXTRA, mSearchUrlExtra);
                        loaderManager.initLoader(MOVIE_SEARCH_LOADER, queryBundle, webDataLoaderListener);
                    } else {
                        Bundle queryBundle = new Bundle();
                        queryBundle.putString(QUERY_URL_EXTRA, mSearchUrlExtra);
                        loaderManager.restartLoader(MOVIE_SEARCH_LOADER, queryBundle, webDataLoaderListener);
                    }
                }
            } else {
                mEmptyView = (TextView) findViewById(R.id.emptyView);
                Loader<Cursor> DbLoader = loaderManager.getLoader(MOVIE_FAVORITES_LOADER);
                mGridAdapter.clear();
                gridView.setAdapter(null);
                if (DbLoader == null) {
                    System.out.println("DB NULL");
                    loaderManager.initLoader(MOVIE_FAVORITES_LOADER, null, dbLoaderListener);
                } else {
                    System.out.println("DBLOADER NOT NULL");
                    loaderManager.destroyLoader(MOVIE_FAVORITES_LOADER);
                    loaderManager.initLoader(MOVIE_FAVORITES_LOADER, null, dbLoaderListener);
                }
//                mEmptyView.setText("There is no internet connection");
//                mEmptyView.setVisibility(View.VISIBLE);
//                gridView.setEmptyView(mEmptyView);
//                mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void extractJSON(String moviesQueryResponse) {
        try {
            movies = MovieJSONUtils.getMoviesFromJSON(MainActivity.this, moviesQueryResponse);
            if (movies != null) {
                Movie testMovie = movies.get(1);
                System.out.println(testMovie.getmMovieID() + " " + testMovie.getmOriginalTitle() + "\n" +
                        testMovie.getmUserRating() + " " + testMovie.getmImage() + "\n" +
                        testMovie.getmReleaseDate() + " " + testMovie.getmSynopsis());
            }
        } catch(Exception e) {
            e.printStackTrace();
            movies = null;
        }
    }

    private void extractDataFromCursor(Cursor cursor){
        System.out.println("extractDataFromCursor");
        try {
            favoriteMovies.clear();
            if (cursor.moveToFirst()){
                do{
                    int movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
                    String imagePath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE));
                    String synopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS));
                    int userRating = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING));
                    String date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                    favoriteMovies.add(new Movie(movieId, title, imagePath, synopsis, userRating, date));
                }while(cursor.moveToNext());
            }
            mFavoritesAdapter.notifyDataSetChanged();
            //cursor.close();
            for(int i =0; i< favoriteMovies.size(); i++){
                System.out.println("Movie in db: " + favoriteMovies.get(i).getmOriginalTitle());
            }
        } catch(Exception e) {
            e.printStackTrace();
            movies = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.visualize_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle() {
        TextView queryTV = (TextView) findViewById(R.id.query_textview);
        if (mSearchUrlExtra.equals("top_rated")) {
            queryTV.setText("Top Rated");
        } else if (mSearchUrlExtra.equals("popular")) {
            queryTV.setText("Popular");
        } else if (mSearchUrlExtra.equals("favorites")) {
            queryTV.setText("Favorites");
        }else {
            queryTV.setText("Error");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(DetailsActivity.EXTRA_MOVIE, (Parcelable) movies.get(i));
        startActivity(detailsIntent);
    }
}
