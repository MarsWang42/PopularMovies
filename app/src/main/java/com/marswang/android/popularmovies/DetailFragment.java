package com.marswang.android.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

/**
 * A placeholder fragment containing a simple view.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Extract the data from the intent.
        Intent intent = getActivity().getIntent();
        MovieData movie = (MovieData) intent.getParcelableExtra("Movie");
        String title = movie.getTitle();
        String date = movie.getRelease_date();
        String posterUrl = movie.getPosterUrl();
        String averageVote = movie.getAverageVote();
        String plotSynopsis = movie.getPlotSynopsis();

        //Put detail data in to the imageView and textView.
        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_detail_imageview);
        Picasso.with(getActivity()).load(posterUrl)
                .into(imageView);
        TextView title_textView = (TextView) rootView.findViewById(R.id.title_textview);
        TextView releasedate_textView = (TextView) rootView.findViewById(R.id.releasedate_textview);
        TextView averagevote_textView = (TextView) rootView.findViewById(R.id.averagevote_textview);
        TextView plotsynopsis_textView = (TextView) rootView.findViewById(R.id.plotsynopsis_textview);
        title_textView.setText(title);
        releasedate_textView.setText(date);
        averagevote_textView.setText("Average Vote: " + averageVote + "/10");
        plotsynopsis_textView.setText("Plot Synopsis:  " + plotSynopsis);


        return rootView;
    }
}
