package com.marswang.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mars on 1/18/16.
 */
public class MovieData implements Parcelable {

    private String posterUrl;
    private String title;
    private String release_date;
    private String averageVote;
    private String plotSynopsis;

    public MovieData(String posterUrl, String title, String release_date, String averageVote, String plotSynopsis){
        this.posterUrl = posterUrl;
        this.title = title;
        this.release_date = release_date;
        this.averageVote = averageVote;
        this.plotSynopsis = plotSynopsis;
    }

    public String getAverageVote() {
        return averageVote;
    }

    public void setAverageVote(String averageVote) {
        this.averageVote = averageVote;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    protected MovieData(Parcel in) {
        posterUrl = in.readString();
        title = in.readString();
        release_date = in.readString();
        averageVote = in.readString();
        plotSynopsis = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterUrl);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeString(averageVote);
        dest.writeString(plotSynopsis);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}