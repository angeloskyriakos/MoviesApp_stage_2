package gr.angeloskyriakos.moviesapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Angelos on 25/2/2018.
 */

public final class MovieJSONUtils {

    public static ArrayList<Movie> getMoviesFromJSON(Context context, String httpResponse) throws JSONException {

        try{
            JSONObject moviesJSON = new JSONObject(httpResponse);
            int numberOfPages = moviesJSON.optInt("page");
           // Movie[] movies = new Movie[numberOfPages*20];
            ArrayList<Movie> movies = new ArrayList<Movie>();
            JSONArray results = moviesJSON.optJSONArray("results");
            if(results.length() > 0) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movie = results.optJSONObject(i);
                    int movieID = movie.optInt("id");
                    String movieOriginalTitle = movie.optString("original_title");
                    String movieImage = movie.optString("poster_path");
                    String movieSynopsis = movie.optString("overview");
                    int movieUserRating = movie.optInt("vote_average");
                    String movieReleaseDate = movie.optString("release_date");
                    movies.add(new Movie(movieID, movieOriginalTitle, movieImage, movieSynopsis, movieUserRating, movieReleaseDate));
                }
                return movies;
            }
            else{
                return null;
            }
        } catch (JSONException e) {
            Log.e("MovieJSONUtils", "Problem parsing the JSON results", e);
            return null;
        }
    }

    public static ArrayList<Trailer> getTrailersFromJSON(Context context, String httpResponse) throws JSONException {
        try{
            ArrayList<Trailer> youTubeKeys = new ArrayList<Trailer>();
            JSONObject videosJSON = new JSONObject(httpResponse);
            JSONArray results = videosJSON.optJSONArray("results");
            if(results.length() > 0) {
                for(int i = 0; i < results.length(); i++) {
                    if(results.optJSONObject(i).optString("type").equals("Trailer")
                            && results.optJSONObject(i).optString("site").equals("YouTube")){

                        youTubeKeys.add(new Trailer(results.optJSONObject(i).optString("key"),
                                results.optJSONObject(i).optString("name")));
                    }
                }
                return youTubeKeys;
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e("MovieJSONUtils", "Problem parsing the JSON results for trailer", e);
            return null;
        }
    }

    public static ArrayList<String[]> getReviewsFromJSON(Context context, String httpResponse) throws JSONException {
        try{
            ArrayList<String[]> reviews = new ArrayList<String[]>();
            JSONObject reviewsJSON = new JSONObject(httpResponse);
            JSONArray results = reviewsJSON.optJSONArray("results");
            if(results.length() > 0) {
                for(int i = 0; i < results.length(); i++) {
                    String[] element = new String[2];
                    element[0] = results.optJSONObject(i).optString("author");
                    element[1] = results.optJSONObject(i).optString("content");
                    reviews.add(element);
                }
                return reviews;
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e("MovieJSONUtils", "Problem parsing the JSON results for reviews", e);
            return null;
        }
    }
}
