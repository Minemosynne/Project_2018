package amandine.project_2018.Controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import amandine.project_2018.Models.Movie;
import amandine.project_2018.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMovieSelectedListener}
 * interface.
 */
public class MoviesFragment extends ListFragment {

    public static final String ARG_LIST = "moviesList";
    public static final String ARG_LIST_TOWATCH = "listFirebase";
    private ArrayList<Movie> mMovies;
    private DatabaseReference mUserRef;
    private DatabaseReference listRef;

    public interface OnMovieSelectedListener {
        void onMovieSelected(int position, Movie movie);
    }

    private OnMovieSelectedListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragment() {

    }

    public static MoviesFragment newInstance(String listJson, boolean fromDatabase, boolean toWatch){
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        if(!fromDatabase)
            args.putString(MoviesFragment.ARG_LIST, listJson);
        else
            args.putBoolean(ARG_LIST_TOWATCH, toWatch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String listJSON;
        if (getArguments() != null) {
            listJSON = getArguments().getString(ARG_LIST);
            if(listJSON != null){
                parseJSON(listJSON);
            }else{
                getListFromFirebase(getArguments().getBoolean(ARG_LIST_TOWATCH));
            }
        }
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.d("USER_REF",mUserRef.getKey());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        if(mMovies == null){
            mMovies = new ArrayList<>();
            fillInMovies();
        }
        // Set the adapter
        Context context = view.getContext();
        ListView listView = (ListView) view;
        listView.setAdapter(new MyMovieRecyclerViewAdapter(context, mMovies));

        registerForContextMenu(listView);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onMovieSelected(position, (Movie)v.getTag(R.id.MOVIE_TAG));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d("MENU","onCreateContextMenu");
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d("MENU","onContextItemSelected");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Movie movie = mMovies.get(info.position);
        final String tmdbId = movie.getTMDBId() + "";
        Log.d("TMDB",tmdbId);
        switch (item.getItemId()) {
            case R.id.actionAddToWatchedList:
                //Vérifie que le film n'est pas déjà dans la liste "vus"
                mUserRef.child("watched_movies").child(tmdbId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Context context = getContext();
                            CharSequence text = "Déjà dans la liste \"vus\"";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else{
                            mUserRef.child("watched_movies").child(tmdbId).child("name").setValue(movie.getName());
                            mUserRef.child("watched_movies").child(tmdbId).child("releaseDate").setValue(movie.getReleaseDate());
                            //Supprime le film de la liste "à voir" s'il y est déjà
                            mUserRef.child("to_watch_movies").child(tmdbId).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            case R.id.actionAddToToWatchList:
                //Vérifie que le film n'est pas déjà dans la liste "à voir"
                mUserRef.child("to_watch_movies").child(tmdbId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Context context = getContext();
                            CharSequence text = "Déjà dans la liste \"à voir\"";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else{
                            mUserRef.child("to_watch_movies").child(tmdbId).child("name").setValue(movie.getName());
                            mUserRef.child("to_watch_movies").child(tmdbId).child("releaseDate").setValue(movie.getReleaseDate());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void parseJSON(String jsonString) {
        try {
            if(mMovies != null){
                mMovies.clear();
            }else{
                mMovies = new ArrayList<>();
            }
            JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                int id = object.getInt("id");
                Log.d("MOVIE_ID",id+"");
                String name = object.getString("title");
                String releaseDate = object.getString("release_date");
                ArrayList<String> genres = new ArrayList<>();
                JSONArray arrayGenres = object.getJSONArray("genre_ids");
                for(int j = 0; j < arrayGenres.length(); j++){
                    genres.add(arrayGenres.getString(j));
                }
                String overview = object.getString("overview");
                mMovies.add(new Movie(id, name, releaseDate, genres, overview));
            }
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    public void getListFromFirebase(boolean toWatch){
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference listRef = null;
        if(mMovies != null){
            //mMovies.clear();
        }else{
            //mMovies = new ArrayList<>();
        }
        if(toWatch){
            listRef = mUserRef.child("to_watch_movies");
        }else{
            listRef = mUserRef.child("watched_movies");
        }
        Log.d("FIREBASE","method");
        listRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("FIREBASE","onDataChange");
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Log.d("FIREBASE","child");
                            String name = child.child("name").getValue().toString();
                            String releaseDate = child.child("releaseDate").getValue().toString();
                            Movie movie = new Movie(2222, name, releaseDate, Arrays.asList("test"), "");
                            mMovies.add(movie);
                            Log.d("FIREBASE","movie - "+ movie.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void fillInMovies(){
        Movie movie = new Movie(24428, "The assassination of Jesse James", "2006", Arrays.asList("Western","Biographie"), "blablabla");
        mMovies.add(movie);
        Movie movie02 = new Movie(24428, "Alice in Wonderland", "2010", Arrays.asList("Fantasy"), "blablabla");
        mMovies.add(movie02);
        Movie movie03 = new Movie(24428, "Dunkirk", "2017", Arrays.asList("Drama","Action","Historical"), "blablabla");
        mMovies.add(movie03);
        Movie movie04 = new Movie(24428, "Harry Potter", "2017", Arrays.asList("Fantasy","Children"), "blablabla");
        mMovies.add(movie04);
        Movie movie05 = new Movie(24428, "Pirates of the Caribbean", "2017", Arrays.asList("Fantasy","Pirates","Action"), "blablabla");
        mMovies.add(movie05);
        Movie movie06 = new Movie(24428, "Anna Karenina","2017", Arrays.asList("Romance","Drama"), "blablabla");
        mMovies.add(movie06);
        Movie movie07 = new Movie(24428, "The Duchess", "2017", Arrays.asList("Romance","Drama"), "blablabla");
        mMovies.add(movie07);
        Movie movie08 = new Movie(24428, "Fantastic Beasts and Where to Find Them", "2017", Arrays.asList("Fantasy","Action"), "blablabla");
        mMovies.add(movie08);
        Movie movie09 = new Movie(24428, "The Shape of Water", "2017", Arrays.asList("Monster","Fantasy","Romance"), "blablabla");
        mMovies.add(movie09);
        Movie movie10 = new Movie(24428, "Black Panther", "2017", Arrays.asList("Action","Super-heroes"), "blablabla");
        mMovies.add(movie10);
        Movie movie11 = new Movie(24428, "Mad Max", "2017", Arrays.asList("Action","Post-Apocalyptic"), "blablabla");
        mMovies.add(movie11);
    }
}
