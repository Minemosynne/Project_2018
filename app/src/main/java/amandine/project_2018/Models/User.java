package amandine.project_2018.Models;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Amandine on 16-02-18.
 */

public class User {
    private int mUserId;
    private String mUsername;
    private String mEmail;
    private int mHoursSpentWatchingM;
    private int mHoursSpentWatchingS;
    private List<Integer> mFollow;

    private LinkedList<Media> mMoviesWatched;
    private LinkedList<Media> mTVShowsWatched;
    private List<Media> mMoviesToWatch;
    private List<Media> mTVShowsToWatch;

    private LinkedList<Review> mReviews;
}
