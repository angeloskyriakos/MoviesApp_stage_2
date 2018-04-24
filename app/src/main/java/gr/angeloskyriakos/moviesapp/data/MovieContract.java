package gr.angeloskyriakos.moviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Angel on 20/03/2018.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "gr.angeloskyriakos.moviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
    }
}
