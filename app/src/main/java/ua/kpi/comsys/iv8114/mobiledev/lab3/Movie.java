package ua.kpi.comsys.iv8114.mobiledev.lab3;

public class Movie {
    private final String title;
    private final String year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String imdbRating;
    private String imdbVotes;
    private String production;
    private final String imdbID;
    private final String type;
    private final String posterPath;


    public Movie(String title, String year, String type) {
        this.title = title;
        this.year = year;
        this.imdbID = "noid";
        this.type = type;
        this.posterPath = "";

    }

    public Movie(String title, String year, String imdbID, String type, String posterPath){
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.posterPath = posterPath;
    }

    public void setRated(String rated) {this.rated = rated;}
    public void setReleased(String released) {this.released = released;}
    public void setRuntime(String runtime) {this.runtime = runtime;}
    public void setGenre(String genre) {this.genre = genre;}
    public void setDirector(String director) {this.director = director;}
    public void setWriter(String writer) {this.writer = writer;}
    public void setActors(String actors) {this.actors = actors;}
    public void setPlot(String plot) {this.plot = plot;}
    public void setLanguage(String language) {this.language = language;}
    public void setCountry(String country) {this.country = country;}
    public void setAwards(String awards) {this.awards = awards;}
    public void setImdbRating(String imdbRating) {this.imdbRating = imdbRating;}
    public void setImdbVotes(String imdbVotes) {this.imdbVotes = imdbVotes;}
    public void setProduction(String production) {this.production = production;}

    public String getRated() {return rated;}
    public String getReleased() {return released;}
    public String getRuntime() {return runtime;}
    public String getGenre() {return genre;}
    public String getDirector() {return director;}
    public String getWriter() {return writer;}
    public String getActors() {return actors;}
    public String getPlot() {return plot;}
    public String getLanguage() {return language;}
    public String getCountry() {return country;}
    public String getAwards() {return awards;}
    public String getImdbRating() {return imdbRating;}
    public String getImdbVotes() {return imdbVotes;}
    public String getProduction() {return production;}

    public String getTitle() {return title;}
    public String getYear() {return year;}
    public String getImdbID() {return imdbID;}
    public String getType() {return type;}
    public String getPosterPath() {return posterPath;}
}
