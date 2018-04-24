package gr.angeloskyriakos.moviesapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

public class FavoritesAdapter extends ArrayAdapter<Movie>  {
    public FavoritesAdapter(Activity context, List<Movie> movies) {
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
        Bitmap picture = BitmapFactory.decodeFile(movie.getmImage());
        imageView.setImageBitmap(picture);
//        Picasso.with(getContext())
//                .load(NetworkUtils.getImageUrl(movie.getmImage()).toString())
//                .into(imageView);
        return convertView;
    }
}
