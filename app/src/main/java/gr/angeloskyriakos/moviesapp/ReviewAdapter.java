package gr.angeloskyriakos.moviesapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Angel on 16/03/2018.
 */

public class ReviewAdapter extends ArrayAdapter<String[]> {

    public ReviewAdapter(Activity context, List<String[]> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String[] review = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review , parent, false);
        }

        TextView reviewTextView = (TextView) convertView.findViewById(R.id.review_id);
        reviewTextView.setText(review[1]);
        reviewTextView.setTextSize(16);
        TextView reviewAuthorTextView = (TextView) convertView.findViewById(R.id.review_author_id);
        reviewAuthorTextView.setText(review[0] + ":");
        reviewAuthorTextView.setTextSize(18);
        return convertView;
    }
}
