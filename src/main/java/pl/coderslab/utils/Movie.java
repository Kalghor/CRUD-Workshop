package pl.coderslab.utils;

public class Movie {
    private int movie_id;
    private String title;
    private String productionData;
    private int movieLength;

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductionData() {
        return productionData;
    }

    public void setProductionData(String productionData) {
        this.productionData = productionData;
    }

    public int getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(int movieLength) {
        this.movieLength = movieLength;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", title='" + title + '\'' +
                ", productionData='" + productionData + '\'' +
                ", movieLength=" + movieLength +
                '}';
    }
}
