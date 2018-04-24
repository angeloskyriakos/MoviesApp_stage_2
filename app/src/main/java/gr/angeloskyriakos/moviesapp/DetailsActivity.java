package gr.angeloskyriakos.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import gr.angeloskyriakos.moviesapp.data.MovieContract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, View.OnClickListener{

    public static final String EXTRA_MOVIE = "movie";

    private static final int TRAILER_SEARCH_LOADER = 20;
    private static final int REVIEWS_SEARCH_LOADER = 30;
    private static final String QUERY_URL_EXTRA = "query_extra";
    private static final String MOVIE_ID = "movie_id";
    private ArrayList<Trailer> trailers = new ArrayList<Trailer>();
    private ListAdapter mListAdapter = null;
    private ArrayList<String[]> reviews = new ArrayList<String[]>();
    private ListAdapter mReviewAdapter = null;
    private Movie movie = null;
    private Bitmap mMovieBitmap;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mMovieBitmap = bitmap;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mMovieBitmap = null;

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        Parcelable myParcelableObject = intent.getParcelableExtra(EXTRA_MOVIE);
        if (myParcelableObject == null) {
            closeOnError();
            return;
        }
        movie = (Movie) myParcelableObject;
        TextView originalTV = (TextView) findViewById(R.id.originalID);
        TextView dateTV = (TextView) findViewById(R.id.dateID);
        TextView ratingTV = (TextView) findViewById(R.id.ratingID);
        TextView synopsisTV = (TextView) findViewById(R.id.synopsisID);
        originalTV.setText(movie.getmOriginalTitle());
        String date = movie.getmReleaseDate();
        String[] parts = date.split("-");
        dateTV.setText(parts[0]);
        ratingTV.setText(String.valueOf(movie.getmUserRating())+"/10");
        synopsisTV.setText(movie.getmSynopsis());

        ImageView imageView = (ImageView) findViewById(R.id.imageID);
        Picasso.with(this)
                .load(NetworkUtils.getImageUrl(movie.getmImage()).toString())
                .into(imageView);

        System.out.println("The movie id is " + movie.getmMovieID());

        mListAdapter = new TrailerAdapter(DetailsActivity.this, trailers);
        LinearLayout listView = (LinearLayout) findViewById(R.id.trailer_listView_id);
        int adapterCount = mListAdapter.getCount();
//        for (int i = 0; i < adapterCount; i++) {
//            View item = mListAdapter.getView(i, null, null);
//            listView.addView(item);
//        }

        Bundle trailerQueryBundle = new Bundle();
        trailerQueryBundle.putString(QUERY_URL_EXTRA, "trailers");
        trailerQueryBundle.putInt(MOVIE_ID, movie.getmMovieID());
        getSupportLoaderManager().initLoader(TRAILER_SEARCH_LOADER, trailerQueryBundle, this);
        Bundle reviewsQueryBundle = new Bundle();
        reviewsQueryBundle.putString(QUERY_URL_EXTRA, "reviews");
        reviewsQueryBundle.putInt(MOVIE_ID, movie.getmMovieID());
        getSupportLoaderManager().initLoader(REVIEWS_SEARCH_LOADER, reviewsQueryBundle, this);

        Picasso.with(this).load(NetworkUtils.getImageUrl(movie.getmImage()).toString()).into(target);

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "Details Activity Error Parcelable", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            public String loadInBackground() {
                try {
                    if(id == 20) {
                        URL trailerUrl = NetworkUtils.getUrl(DetailsActivity.this,
                                args.getString(QUERY_URL_EXTRA), args.getInt(MOVIE_ID));
                        String httpResponseJSON = NetworkUtils.getResponseFromHttpUrl(trailerUrl);
                        return httpResponseJSON;
                    } else if(id == 30) {
                        URL reviewsUrl = NetworkUtils.getUrl(DetailsActivity.this,
                                args.getString(QUERY_URL_EXTRA), args.getInt(MOVIE_ID));
                        String httpResponseJSON = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);
                        return httpResponseJSON;
                    } else {
                        return null;
                    }
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
        int id = loader.getId();
        if(id == 20) {
            extractTrailersJSON(data);
            LinearLayout listView = (LinearLayout) findViewById(R.id.trailer_listView_id);
            if (trailers != null) {
                mListAdapter = new TrailerAdapter(DetailsActivity.this, trailers);
                int adapterCount = mListAdapter.getCount();
                for (int i = 0; i < adapterCount; i++) {
                    View item = mListAdapter.getView(i, null, null);
                    listView.addView(item);
                }
            }
        } else if (id == 30){
            extractReviewsJSON(data);
            LinearLayout listView = (LinearLayout) findViewById(R.id.reviews_listView_id);
            if (reviews != null) {
                mReviewAdapter = new ReviewAdapter(DetailsActivity.this, reviews);
                int adapterCount = mReviewAdapter.getCount();
                for (int i = 0; i < adapterCount; i++) {
                    View item = mReviewAdapter.getView(i, null, null);
                    listView.addView(item);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void extractTrailersJSON(String queryResponse) {
        try {
            trailers = MovieJSONUtils.getTrailersFromJSON(DetailsActivity.this, queryResponse);
            if (trailers != null) {
                Trailer testTrailer = trailers.get(1);
                System.out.println(testTrailer.getmKey() +" " + testTrailer.getmName());
            }
        } catch(Exception e) {
            e.printStackTrace();
            trailers = null;
        }
    }

    private void extractReviewsJSON(String queryResponse) {
        try {
            reviews = MovieJSONUtils.getReviewsFromJSON(DetailsActivity.this, queryResponse);
        } catch(Exception e) {
            e.printStackTrace();
            trailers = null;
        }
    }

    @Override
    public void onClick(View view) {
        // Write this movie's content to the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getmMovieID());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getmOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getmReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getmUserRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getmSynopsis());

        //Picasso.with(this).load("url").into(target);

        String picturePath = "";
        File internalStorage = getApplicationContext().getDir("MoviePictures", Context.MODE_PRIVATE);
        File pictureFilePath = new File(internalStorage, movie.getmMovieID() + ".png");
        picturePath = pictureFilePath.toString();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFilePath);
            mMovieBitmap.compress(Bitmap.CompressFormat.PNG, 100 /*quality*/, fos);
            fos.close();
        }
        catch (Exception ex) {
            Log.i("DATABASE", "Problem updating picture", ex);
            picturePath = "";
        }
        contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, picturePath);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if(uri != null){
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
