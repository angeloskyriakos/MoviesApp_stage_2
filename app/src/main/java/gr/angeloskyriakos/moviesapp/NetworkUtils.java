package gr.angeloskyriakos.moviesapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Angelos on 24/2/2018.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = BuildConfig.MY_MOVIE_DB_API_KEY;
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";

    public static URL getImageUrl(String imagePath) {
        Uri imageUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE)
                .build();
        try {
            Log.v(TAG, "IMAGEPATH: " + imagePath);
            URL imagePathUrl = new URL(imageUri.toString() + imagePath);
            Log.v(TAG, "URL: " + imagePathUrl);
            return imagePathUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getUrl(Context context, String selection) {
        if (selection.equals("top_rated")) {
            return buildUrlForTopRatedQuery();
        } else if(selection.equals("popular")) {
            return buildUrlForPopularQuery();
        } else {
            return null;
        }
    }

    public static URL getUrl(Context context, String selection, int id) {
        if(selection.equals("trailers")) {
            return buildUrlForTrailersQuery(id);
        } else if(selection.equals("reviews")) {
            return buildUrlForReviewsQuery(id);
        } else {
            return null;
        }
    }

    private static URL buildUrlForReviewsQuery(int id) {
        Uri reviewsQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Integer.toString(id))
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY)
                .build();
        try {
            URL reviewsUrl = new URL(reviewsQueryUri.toString());
            Log.v(TAG, "URL: " + reviewsUrl);
            return reviewsUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static URL buildUrlForTrailersQuery(int id) {
        Uri trailersQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Integer.toString(id))
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY)
                .build();

        try {
            URL trailersUrl = new URL(trailersQueryUri.toString());
            Log.v(TAG, "URL: " + trailersUrl);
            return trailersUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static URL buildUrlForPopularQuery() {
        Uri popularMoviesQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("popular")
                .appendQueryParameter("api_key", API_KEY)
                .build();

        try {
            URL popularMoviesUrl = new URL(popularMoviesQueryUri.toString());
            Log.v(TAG, "URL: " + popularMoviesUrl);
            return popularMoviesUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static URL buildUrlForTopRatedQuery() {
        Uri topRatedMoviesQueryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("top_rated")
                .appendQueryParameter("api_key", API_KEY)
                .build();

        try {
            URL topRatedMoviesUrl = new URL(topRatedMoviesQueryUri.toString());
            Log.v(TAG, "URL: " + topRatedMoviesUrl);
            return topRatedMoviesUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
