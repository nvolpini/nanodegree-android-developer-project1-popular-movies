package app.popularmovies.model;

/**
 * Created by neimar on 14/09/16.
 */
public class MoviesSearch {

    public enum Sorting {
        POPULARITY, RATING
    }

    private String language = "en";

    Sorting sortBy = Sorting.POPULARITY;

    public Sorting getSortBy() {
        return sortBy;
    }

    public void setSortBy(Sorting sortBy) {
        this.sortBy = sortBy;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
