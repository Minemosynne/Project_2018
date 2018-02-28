package amandine.project_2018.Controllers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import amandine.project_2018.Models.Movie;
import amandine.project_2018.R;

public class MainActivity extends AppCompatActivity
        implements MoviesFragment.OnMovieSelectedListener, ProfileFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener, ProfileMoviesFragment.OnFragmentInteractionListener,
        ProfileTVShowsFragment.OnFragmentInteractionListener{

    private MoviesFragment.OnMovieSelectedListener mListener;
    private BottomNavigationView mBottomNavView;
    private DatabaseReference mUserRef;

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.actionProfile :
                    //TODO profil user connecté
                    if(findViewById(R.id.fragmentContainer) != null){
                        ProfileFragment fragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                    }
                    return true;
                case R.id.actionToWatchList :
                    //TODO recup bonne liste
                    if(findViewById(R.id.fragmentContainer) != null){
                        MoviesFragment fragment = MoviesFragment.newInstance(null, true, true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                    }
                    return true;
                case R.id.actionWatchedList :
                    //TODO recup bonne liste
                    if(findViewById(R.id.fragmentContainer) != null){
                        MoviesFragment fragment = MoviesFragment.newInstance(null, true, false);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                    }
                    return true;
                case R.id.actionCalendar :
                    //TODO recup bonne liste
                    if(findViewById(R.id.fragmentContainer) != null){
                        CalendarFragment fragment = new CalendarFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                    }
                    return true;
                default :
                    //TODO
                    return true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST","onCreate");
        setContentView(R.layout.activity_main);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        mBottomNavView = findViewById(R.id.navigation);
        mBottomNavView.setOnNavigationItemSelectedListener(mNavigationListener);
        //Sets the first fragment (MoviesFragment)
        mBottomNavView.setSelectedItemId(R.id.actionToWatchList);
        handleIntent(getIntent());
    }

    //MoviesFragment
    public void onMovieSelected(int position, Movie movie){
        //Récupère movieInfoFragment
        MovieInfoFragment movieInfoFrag = (MovieInfoFragment) getSupportFragmentManager().findFragmentById(R.id.movieInfoFragment);
        if(movieInfoFrag != null){
            //two-pane layout
            //Update fragment
        }else{
            //Create a new fragment
            MovieInfoFragment newFragment = MovieInfoFragment.newInstance(position, movie);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    //ProfileFragment
    public void onFragmentInteraction(){
        //TODO
    }

    //CalendarFragment
    public void onFragmentInteraction(Uri uri){
        //TODO
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d("HANDLEINTENT","wait");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
            Log.d("HANDLEINTENT","yes");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void doMySearch(String query){
        Log.d("DOMYRESEARCH","yes");
        new TmdbResults().execute("https://api.themoviedb.org/3/search/movie?api_key=eee049344d90e9bd7205c38da353d3b1&query="+query);
    }

    //Pour faire recherche sur tmdb en background
    private class TmdbResults extends AsyncTask<String, Void, String>{
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
            Log.d("TMDBRESULTS","OnPostExecute");
            MoviesFragment fragment = MoviesFragment.newInstance(results, false, false);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
        }
    }
}
