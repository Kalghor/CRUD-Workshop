package pl.coderslab.dao;

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

    public void addUserToDB(Movie Movie) {
        if (isExist(user)) {
            try (Connection connection = DBUtils.getConnection("workshop2")) {
                Statement stm = connection.createStatement();
                ResultSet resultSet = stm.executeQuery(SELECT_USER + "\'" + user.getEmail() + "\'");
                resultSet.next();
                user.setId(resultSet.getInt("id"));
                System.out.println(ConsoleColors.PURPLE_BRIGHT + "User with id = " + user.getId() + " already exists in the database.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            try (Connection connection = DBUtils.getConnection("workshop2");
                 PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                System.out.println(ConsoleColors.PURPLE_BRIGHT + "Added user with id = " + user.getId());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public User read(int userID) {
        User user = new User();
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SELECT_USER_BY_ID + "\'" + userID + "\'");
            if (resultSet.next()) {
                user.setId(Integer.parseInt(resultSet.getString("id")));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.toString();
                return user;
            } else {
                System.out.println(ConsoleColors.PURPLE_BRIGHT + "The record does not exist in the database");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public void update(User user) {
        if (!isExist(user)) {
            int id = user.getId();
            String username = user.getUserName();
            String email = user.getEmail();
            String password = user.getPassword();
            try (Connection connection = DBUtils.getConnection("workshop2")) {
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_QUERY);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashPassword(password));
                preparedStatement.setInt(4, id);
                preparedStatement.executeUpdate();
                System.out.println(ConsoleColors.PURPLE_BRIGHT + "Data updated.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            System.out.println(ConsoleColors.PURPLE_BRIGHT + "This user already exists in the database.");
        }
    }

    public void delete(int userId) {
        if (isExist(userId)) {
            try (Connection connection = DBUtils.getConnection("workshop2")) {
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_QUERY);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();
                System.out.println(ConsoleColors.PURPLE_BRIGHT + "Record deleted from database");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            System.out.println(ConsoleColors.PURPLE_BRIGHT + "The record does not exist in the database.");
        }
    }

    public User[] findAll() {
        User user = new User();
        User[] usersArr = new User[0];
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_USERS);
            while (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                usersArr = addToArray(user, usersArr);
                user = new User();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersArr;
    }

    private User[] addToArray(User user, User[] users) {
        User[] usersTmp = Arrays.copyOf(users, users.length + 1);
        usersTmp[usersTmp.length - 1] = user;
        return usersTmp;
    }

    public static void saveDataToFile(User[] userArr) {
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
            for (int i = 0; i < userArr.length; i++) {
                pw.println(userArr[i].getId() + "," + userArr[i].getUserName() + "," + userArr[i].getEmail() + "," + userArr[i].getPassword());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void loadDataToDatabase(Path path) throws FileNotFoundException {
        File file = new File(String.valueOf(path));
        Scanner scan = new Scanner(file);
        UserDao userDao = new UserDao();
        if(Files.exists(path)){
            file = new File(String.valueOf(path));
            while (scan.hasNext()){
                String line = scan.nextLine();
                String[] userArr = line.split(",");
                //userArr[1] - userName
                //userArr[2] - userEmail
                //userArr[3] - userPassword
                User user = new User(userArr[1],userArr[2],userArr[3]);
                userDao.addUserToDB(user);
            }
        } else {
            System.out.println("File does not exist!");
        }
    }

    public Boolean isExist(User user) {
        boolean result = false;
        try (Connection connection = DBUtils.getConnection("workshop2")) {
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SELECT_USER + "\'" + user.getEmail() + "\'");
            if (resultSet.next()) {
                if (user.getEmail().equals(resultSet.getString("email"))) {
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

    public void ShowAllUsers(User[] usersArr) {
        int maxLengthOfUsersId = 0;
        int maxLengthOfUsersName = 0;
        for (int i = 0; i < usersArr.length; i++) {
            String userId = String.valueOf(usersArr[i].getId());
            String userName = usersArr[i].getUserName();
            if (maxLengthOfUsersId < userId.length()) {
                maxLengthOfUsersId = userId.length();
            }
            if (maxLengthOfUsersName < userName.length()) {
                maxLengthOfUsersName = userName.length();
            }
        }
        for (int i = 0; i < usersArr.length; i++) {
            String userId = String.valueOf(usersArr[i].getId());
            if (usersArr[i].getId() < 10) {
                userId = "0" + String.valueOf(usersArr[i].getId());
            }
            String userName = usersArr[i].getUserName();
            int LengthOfUserID = userId.length();
            int LengthOfUserName = userName.length();
            String result = "id = "
                    + userId + "," + "".replace("", " ".repeat(maxLengthOfUsersId - LengthOfUserID + 2))
                    + "userName = " + usersArr[i].getUserName() + "," + "".replace("", " ".repeat(maxLengthOfUsersName - LengthOfUserName + 2))
                    + "email = " + usersArr[i].getPassword();
            if (i % 2 == 0) {
                result = ConsoleColors.BLUE_BRIGHT + result;
            } else if (i % 1 == 0) {
                result = ConsoleColors.YELLOW_BRIGHT + result;
            }
            System.out.println(result);
        }
    }
}