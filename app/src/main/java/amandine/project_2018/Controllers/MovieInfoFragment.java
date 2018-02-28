package amandine.project_2018.Controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import amandine.project_2018.Models.Movie;
import amandine.project_2018.R;

public class MovieInfoFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    public static final String ARG_MOVIE = "movie";
    private int mPosition;
    private Movie mMovie;

    //TODO TextView movie year
    private TextView mMovieReleaseDate;
    private TextView mMovieGenres;
    private TextView mMovieRunningTime;
    private TextView mMovieSynopsis;
    private TextView mMovieCrew;
    private TextView mMovieCast;

    public MovieInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position of the movie in the list.
     * @param movie Movie selected.
     * @return A new instance of fragment MovieInfoFragment.
     */
    public static MovieInfoFragment newInstance(int position, Movie movie) {
        MovieInfoFragment fragment = new MovieInfoFragment();
        Bundle args = new Bundle();
        args.putInt(MovieInfoFragment.ARG_POSITION,position);
        args.putParcelable(MovieInfoFragment.ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_info, container, false);
        mMovieReleaseDate = view.findViewById(R.id.movieInfoReleaseDate);
        mMovieGenres = view.findViewById(R.id.movieInfoGenres);
        mMovieRunningTime = view.findViewById(R.id.movieInfoRunningTime);
        mMovieSynopsis = view.findViewById(R.id.movieInfoSynopsis);
        mMovieCrew = view.findViewById(R.id.movieInfoCrew);
        mMovieCast = view.findViewById(R.id.movieInfoCast);

        //TODO recup real data
        mMovieReleaseDate.setText(mMovie.getReleaseDate());
        mMovieGenres.setText("Action, Drama");
        mMovieRunningTime.setText("2h09");
        mMovieSynopsis.setText(mMovie.getOverview());
        Log.d("CAST","start");
        new MovieResults().execute("https://api.themoviedb.org/3/movie/" + mMovie.getTMDBId() + "/credits?api_key=eee049344d90e9bd7205c38da353d3b1");

        return view;
    }

    public void parseJSON(String jsonString) {
        try {
            Log.d("CAST","json");
            JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            JSONArray jsonArray = jsonObject.getJSONArray("cast");
            for (int i = 0; i < 10; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Log.d("CAST",object.getString("name"));

                String castMember = object.getString("name") + " - " + object.getString("character");
                mMovie.addMemberToCast(castMember);
            }
            jsonArray = jsonObject.getJSONArray("crew");
            for (int i = 0; i < 2; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Log.d("CAST",object.getString("name"));

                String crewMember = object.getString("name") + " - " + object.getString("department");
                mMovie.addMemberToCrew(crewMember);
            }
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    private class MovieResults extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("TMDBRESULTS","doInBackground");
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String results) {
            parseJSON(results);
            String completeCast = "";
            for(String castMember : mMovie.getCast()){
                Log.d("MEMBER", castMember);
                completeCast += castMember + "\n";
            }
            mMovieCast.setText(completeCast);
            String crew = "";

            for(String crewMember : mMovie.getCrew()){
                crew+= crewMember + "\n";
            }
            mMovieCrew.setText(crew);
        }
    }
}
