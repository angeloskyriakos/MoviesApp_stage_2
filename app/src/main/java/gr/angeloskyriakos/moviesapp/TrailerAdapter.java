package gr.angeloskyriakos.moviesapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Angel on 15/03/2018.
 */

public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Activity context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Trailer trailer = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer , parent, false);
        }

        TextView trailerTextView = (TextView) convertView.findViewById(R.id.trailer_name_id);
        trailerTextView.setText(trailer.getmName());
        trailerTextView.setTextSize(18);
        ImageButton playButton = (ImageButton) convertView.findViewById(R.id.trailer_button_id);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = trailer.getmKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoId));
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

}
