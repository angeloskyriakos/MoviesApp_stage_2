package gr.angeloskyriakos.moviesapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Angel on 26/02/2018.
 */

public class GridAdapter extends ArrayAdapter<Movie> {
    public GridAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);
        if(convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item , parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_image);

        Picasso.with(getContext())
                .load(NetworkUtils.getImageUrl(movie.getmImage()).toString())
                .into(imageView);
        return convertView;
    }
}
