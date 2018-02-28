package amandine.project_2018.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Amandine on 16-02-18.
 */

public class Movie extends Media implements Parcelable{
    private int mHours;
    private int mMinutes;

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };
    //-------------------CONSTRUCTOR-------------------
    public Movie(int tmdbId, String name, String releaseDate, List<String> genres, String overview){
        super(tmdbId, name, releaseDate, genres, overview);
    }

    //TODO GENRES
    public Movie(Parcel parcel){
        super(parcel.readInt(), parcel.readString(), parcel.readString(), Arrays.asList("Test"), parcel.readString());
    }

    //-------------------GETTERS & SETTERS-------------------

    //METHODS
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(this.getName());
    }

    public int describeContents() {return 0;}
}
