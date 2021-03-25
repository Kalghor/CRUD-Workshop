package pl.coderslab.utils;

public class Movie {
    private int id;
    private String title;
    private String productionData;
    private int movieLength;

    public int getId() {
        return id;
    }

    public void setId(int movie_id) {
        this.id = movie_id;
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
                "movie_id=" + id +
                ", title='" + title + '\'' +
                ", productionData='" + productionData + '\'' +
                ", movieLength=" + movieLength +
                '}';
    }

    public Movie(String title, String productionData, int movieLength) {
        this.title = title;
        this.productionData = productionData;
        this.movieLength = movieLength;
    }
    public Movie() {
     }

}
