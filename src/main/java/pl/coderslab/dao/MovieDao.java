package pl.coderslab.dao;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.Movie;
import pl.coderslab.utils.DBUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class MovieDao extends Movie {

    private static final String CREATE_USER_QUERY = "INSERT INTO movies(movie_name, movie_date, movie_length) VALUES (?,?,?)";
    private static final String SELECT_ALL_FROM_USERS = "SELECT * FROM movies";
    private static final String UPDATE_USER_QUERY = "UPDATE movies SET movie_name = ?, movie_date = ?, movie_length = ? WHERE movie_name = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM movies WHERE movie_name = ?";
    public static final String SELECT_USER = "SELECT * FROM movies WHERE movie_name=";
    public static final String SELECT_USER_BY_ID = "SELECT * FROM movies WHERE movie_id =";
    public static final String PATH_TO_SAVE_AND_LOAD_DATA = "Data.csv";

    public MovieDao() {
    }

    public void addMovieToDB(Movie movie) {
        if (isExist(movie)) {
            try (Connection connection = DBUtils.getConnection("workshop2")) {
                Statement stm = connection.createStatement();
                ResultSet resultSet = stm.executeQuery(SELECT_USER + "\'" + movie.getTitle() + "\'");
                resultSet.next();
                movie.setId(resultSet.getInt("id"));
                System.out.println(movie.getId() + " already exists in the database.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            try (Connection connection = DBUtils.getConnection("workshop2");
                 PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, movie.getTitle());
                preparedStatement.setString(2, movie.getProductionData());
                preparedStatement.setString(3, String.valueOf(movie.getMovieLength()));
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    movie.setId(rs.getInt(1));
                }
                System.out.println("Added user with id = " + movie.getId());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public Movie read(int userID) {
        Movie movie = new Movie();
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SELECT_USER_BY_ID + "\'" + userID + "\'");
            if (resultSet.next()) {
                movie.setId(Integer.parseInt(resultSet.getString("id")));
                movie.setTitle(resultSet.getString("title"));
                movie.setProductionData(resultSet.getString("movie_date"));
                movie.setMovieLength(Integer.parseInt(resultSet.getString("movie_length")));
                movie.toString();
                return movie;
            } else {
//                System.out.println(ConsoleColors.PURPLE_BRIGHT + "The record does not exist in the database");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return movie;
    }

    public void update(Movie movie) {
        if (!isExist(movie)) {
            int id = movie.getId();
            String username = movie.getTitle();
            String email = movie.getProductionData();
            int password = movie.getMovieLength();
            try (Connection connection = DBUtils.getConnection("workshop2")) {
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_QUERY);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, String.valueOf(password));
                preparedStatement.setInt(4, id);
                preparedStatement.executeUpdate();
                System.out.println("Data updated.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
//            System.out.println(ConsoleColors.PURPLE_BRIGHT + "This user already exists in the database.");
        }
    }

    public void delete(int userId) {
        if (isExist(userId)) {
            try (Connection connection = DBUtils.getConnection("workshop2")) {
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_QUERY);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();
//                System.out.println(ConsoleColors.PURPLE_BRIGHT + "Record deleted from database");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
//            System.out.println(ConsoleColors.PURPLE_BRIGHT + "The record does not exist in the database.");
        }
    }

    public Movie[] findAll() {
        Movie movie = new Movie();
        Movie[] moviesArr = new Movie[0];
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_USERS);
            while (resultSet.next()) {
                movie.setId(resultSet.getInt("id"));
                movie.setTitle(resultSet.getString("username"));
                movie.setProductionData(resultSet.getString("email"));
                movie.setMovieLength(Integer.parseInt(resultSet.getString("password")));
                moviesArr = addToArray(movie, moviesArr);
                movie = new Movie();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moviesArr;
    }

    private Movie[] addToArray(Movie movie, Movie[] movies) {
        Movie[] moviesTmp = Arrays.copyOf(movies, movies.length + 1);
        moviesTmp[moviesTmp.length - 1] = movie;
        return moviesTmp;
    }

    public static void saveDataToFile(Movie[] moviesArr) {
        File file = new File(PATH_TO_SAVE_AND_LOAD_DATA);
        Path path = Paths.get(PATH_TO_SAVE_AND_LOAD_DATA);
        if (!file.exists()) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (PrintWriter pw = new PrintWriter(file);) {
            for (int i = 0; i < moviesArr.length; i++) {
                pw.println(moviesArr[i].getId() + "," + moviesArr[i].getTitle() + "," + moviesArr[i].getProductionData() + "," + moviesArr[i].getMovieLength());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void loadDataToDatabase(Path path) throws FileNotFoundException {
        File file = new File(String.valueOf(path));
        Scanner scan = new Scanner(file);
        MovieDao movieDao = new MovieDao();
        if(Files.exists(path)){
            file = new File(String.valueOf(path));
            while (scan.hasNext()){
                String line = scan.nextLine();
                String[] userArr = line.split(",");
                //userArr[1] - userName
                //userArr[2] - userEmail
                //userArr[3] - userPassword
                Movie movie = new Movie(userArr[1],userArr[2],Integer.parseInt(userArr[3]));
                movieDao.addMovieToDB(movie);
            }
        } else {
            System.out.println("File does not exist!");
        }
    }

    public Boolean isExist(Movie movie) {
        boolean result = false;
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SELECT_USER + "\'" + movie.getTitle() + "\'");
            if (resultSet.next()) {
                if (movie.getTitle().equals(resultSet.getString("title"))) {
                    result = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public Boolean isExist(int id) {
        boolean result = false;
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SELECT_ALL_FROM_USERS);
            while (resultSet.next()) {
                if (id == (resultSet.getInt("id"))) {
                    result = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void ShowAllUsers(Movie[] moviesArr) {
        int maxLengthOfUsersId = 0;
        int maxLengthOfUsersName = 0;
        for (int i = 0; i < moviesArr.length; i++) {
            String movieId = String.valueOf(moviesArr[i].getId());
            String title = moviesArr[i].getTitle();
            if (maxLengthOfUsersId < movieId.length()) {
                maxLengthOfUsersId = movieId.length();
            }
            if (maxLengthOfUsersName < title.length()) {
                maxLengthOfUsersName = title.length();
            }
        }
        for (int i = 0; i < moviesArr.length; i++) {
            String userId = String.valueOf(moviesArr[i].getId());
            if (moviesArr[i].getId() < 10) {
                userId = "0" + String.valueOf(moviesArr[i].getId());
            }
            String userName = moviesArr[i].getTitle();
            int LengthOfUserID = userId.length();
            int LengthOfUserName = userName.length();
            String result = "id = "
                    + userId + "," + "".replace("", " ".repeat(maxLengthOfUsersId - LengthOfUserID + 2))
                    + "userName = " + moviesArr[i].getTitle() + "," + "".replace("", " ".repeat(maxLengthOfUsersName - LengthOfUserName + 2))
                    + "email = " + moviesArr[i].getMovieLength();
            if (i % 2 == 0) {
//                result = ConsoleColors.BLUE_BRIGHT + result;
            } else if (i % 1 == 0) {
//                result = ConsoleColors.YELLOW_BRIGHT + result;
            }
            System.out.println(result);
        }
    }
}