package gr.angeloskyriakos.moviesapp;

/**
 * Created by Angel on 15/03/2018.
 */

public class Trailer {
    private String mKey;
    private String mName;

    public Trailer(String key, String name) {
        mKey = key;
        mName = name;
    }

    public String getmKey() {
        return mKey;
    }

    public String getmName() {
        return mName;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
