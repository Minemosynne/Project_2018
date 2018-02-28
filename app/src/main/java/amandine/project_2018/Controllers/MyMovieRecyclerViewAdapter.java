package amandine.project_2018.Controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import amandine.project_2018.Controllers.MoviesFragment.OnMovieSelectedListener;
import amandine.project_2018.Models.Movie;
import amandine.project_2018.R;

import java.util.ArrayList;

public class MyMovieRecyclerViewAdapter extends ArrayAdapter<Movie> {

    //View lookup cache
    private static class ViewHolder{
        TextView movieName;
        TextView movieYear;
        TextView movieGenres;
    }

    public MyMovieRecyclerViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Movie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        MyMovieRecyclerViewAdapter.ViewHolder viewHolder; //view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new MyMovieRecyclerViewAdapter.ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_movie, parent, false);
            viewHolder.movieName = convertView.findViewById(R.id.movieName);
            viewHolder.movieYear = convertView.findViewById(R.id.movieYear);
            viewHolder.movieGenres = convertView.findViewById(R.id.movieGenres);
            //TODO afficher score
            //Cache the viewHolder object inside the view
            convertView.setTag(R.id.VIEWHOLDER_TAG,viewHolder);
        }else{
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (MyMovieRecyclerViewAdapter.ViewHolder)convertView.getTag(R.id.VIEWHOLDER_TAG);
        }
        convertView.setTag(R.id.MOVIE_TAG,movie);
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.movieName.setText(movie.getName());
        viewHolder.movieYear.setText(" " + movie.getReleaseDate()+"");
        String genres = " ";
        for(String genre : movie.getGenres()){
            genres += genre + ", ";
        }
        viewHolder.movieGenres.setText(genres);
        // Return the completed view to render on screen
        return convertView;
    }
}
