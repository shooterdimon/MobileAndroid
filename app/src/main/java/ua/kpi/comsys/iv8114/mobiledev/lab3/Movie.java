package ua.kpi.comsys.iv8114.mobiledev.lab3;

public class Movie {
    private final String title;
    private final String year;
    private final String imdbID;
    private final String type;
    private final String posterPath;

    public Movie(String title, String year, String imdbID, String type, String posterPath){
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getType() {
        return type;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
