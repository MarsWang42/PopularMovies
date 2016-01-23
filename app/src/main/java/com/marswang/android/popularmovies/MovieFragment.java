package com.marswang.android.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mars on 1/15/16.
 */

public class MovieFragment extends android.support.v4.app.Fragment {

    public MainListviewAdapter mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle actions bar clicks here. When you click
        //refresh button data will refresh.
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Create an ArrayAdapter to send the raw data to the listView.


        mMovieAdapter = new MainListviewAdapter(
                getActivity(),
                R.layout.list_poster_movie,
                new ArrayList<MovieData>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_movielist);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieData movie = (MovieData) mMovieAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Movie", movie);
                Intent detail = new Intent(getActivity(), DetailActivity.class)
                        .putExtras(bundle);
                startActivity(detail);
            }
        });
        return rootView;
    }

    public String getUrl(){
        final String MOVIE_BASE_URL =
                "https://api.themoviedb.org/3/discover/movie?";
        final String SORT_PARAM = "sort_by";
        final String APIKEY_PARAM = "api_key";

        String apikey = getString(R.string.api_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, sort)
                .appendQueryParameter(APIKEY_PARAM, apikey)
                .build();
        return builtUri.toString();
    }

    public void updateMovie() {
        final String url = getUrl();
        final String INTERNET_CONNECTION_ERROR = "No Internet Connection";
        final String DATA_ERROR = "Data Not Available";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), INTERNET_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), DATA_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                final String responseData = response.body().string();
                final ArrayList<MovieData> movieDataFromJson;
                try {
                    movieDataFromJson = getMovieDataFromJson(responseData);
                    //onResponse is called on Okhttp's thread, so we need to post events on UI thread
                    getActivity().runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                        @Override
                        public void run() {
                            mMovieAdapter.clear();
                            mMovieAdapter.addAll(movieDataFromJson);
                        }
                    });
                } catch (JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), DATA_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });
    }

    public ArrayList<MovieData> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_TITLE = "title";
        final String MDB_DATE = "release_date";
        final String MDB_POSTER = "poster_path";
        final String MDB_VOTE = "vote_average";
        final String MDB_PLOTSYNOPSIS = "overview";

        ArrayList<MovieData> movieDatas = new ArrayList<MovieData>();

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

        for(int i = 0; i < movieArray.length(); i++) {
            String title;
            String date;
            String posterPath;
            String averageVote;
            String plotSynopsis;

            // Get the JSON object representing the title and release date.
            JSONObject movie = movieArray.getJSONObject(i);

            title = movie.getString(MDB_TITLE);
            date = movie.getString(MDB_DATE);
            posterPath = movie.getString(MDB_POSTER);
            averageVote = movie.getString(MDB_VOTE);
            plotSynopsis = movie.getString(MDB_PLOTSYNOPSIS);

            movieDatas.add(new MovieData(IMAGE_BASE_URL+posterPath, title, date, averageVote, plotSynopsis));
        }
        return movieDatas;

    }

    //Define a new Adapter to adapt ImageView.
    private class MainListviewAdapter extends ArrayAdapter{

        ArrayList<MovieData> movieDatas;
        Context context;
        int resource;

        public MainListviewAdapter(Context context, int resource, ArrayList<MovieData> movieDatas) {
            super(context, resource, movieDatas);
            this.resource = resource;
            this.context = context;
            this.movieDatas = movieDatas;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewItemHolder item = null;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(resource, parent, false);

                item = new ListViewItemHolder();
                item.img_iv = (ImageView)convertView.findViewById(R.id.list_item_movie_imageview);

                convertView.setTag(item);
            } else
                item = (ListViewItemHolder) convertView.getTag();

            MovieData movieData = movieDatas.get(position);


            Picasso.with(getActivity()).load(movieData.getPosterUrl())
                    .into(item.img_iv);

            return convertView;
        }

    }

    private class ListViewItemHolder {
        ImageView img_iv;
    }

}


