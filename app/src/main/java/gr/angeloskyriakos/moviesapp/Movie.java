package gr.angeloskyriakos.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Angelos on 24/2/2018.
 */

public class Movie implements Parcelable{

    private int mMovieID;
    private String mOriginalTitle;
    private String mImage;
    private String mSynopsis;
    private int mUserRating;
    private String mReleaseDate;

    public Movie(int movieID, String originalTitle, String image, String synopsis, int userRating, String releaseDate) {
        mMovieID = movieID;
        mOriginalTitle = originalTitle;
        mImage = image;
        mSynopsis = synopsis;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public void setmSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public void setmUserRating(int mUserRating) {
        this.mUserRating = mUserRating;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public String getmImage() {
        return mImage;
    }

    public String getmSynopsis() {
        return mSynopsis;
    }

    public int getmUserRating() {
        return mUserRating;
    }

    public int getmMovieID() {
        return mMovieID;
    }

    public void setmMovieID(int mMovieID) {

        this.mMovieID = mMovieID;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mMovieID);
        parcel.writeString(mImage);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mSynopsis);
        parcel.writeInt(mUserRating);
    }

    protected Movie(Parcel in) {
        mMovieID = in.readInt();
        mImage = in.readString();
        mOriginalTitle = in.readString();
        mReleaseDate = in.readString();
        mSynopsis = in.readString();
        mUserRating= in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
