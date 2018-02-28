package amandine.project_2018.Models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Amandine on 16-02-18.
 */

public class Media {
    private int mMediaId;
    private int mTMDBId;
    private String mName;
    private String mReleaseDate;
    private List<String> mGenres;
    private String mOverview;
    private List<String> mCast;
    private List<String> mCrew;

    private float mAverageScore;
    private LinkedList<Review> mReviews;

    //-------------------CONSTRUCTOR-------------------
    public Media(int tmdbId, String name, String releaseDate, List<String> genres, String overview){
        this.mTMDBId = tmdbId;
        this.mName = name;
        this.mReleaseDate = releaseDate;
        this.mGenres = genres;
        this.mOverview = overview;
        this.mCast = new ArrayList<>();
        this.mCrew = new ArrayList<>();
    }
    //TODO constructor with JSONObject

    //-------------------GETTERS & SETTERS-------------------
    public  int getTMDBId(){
        return mTMDBId;
    }

    public void setTMDBId(int tmdbId){
        this.mTMDBId = tmdbId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public void setGenres(List<String> genres) {
        mGenres = genres;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public List<String> getCast() {
        return mCast;
    }

    public void setCast(List<String> cast) {
        mCast = cast;
    }

    public List<String> getCrew() {
        return mCrew;
    }

    public void setCrew(List<String> crew) {
        mCrew = crew;
    }

    //-------------------FUNCTIONS-------------------
    public boolean addMemberToCast(String member){
        return mCast.add(member);
    }

    public boolean addMemberToCrew(String member){
        return mCrew.add(member);
    }

}
