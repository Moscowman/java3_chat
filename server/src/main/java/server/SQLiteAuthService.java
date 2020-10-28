package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteAuthService implements AuthService {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;

    public SQLiteAuthService() {
        try {
            connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            PreparedStatement psSelect = connection.prepareStatement(
                    "SELECT nickname FROM users WHERE login = ? AND password = ?;");
            psSelect.setString(1, login);
            psSelect.setString(2, password);
            ResultSet rows = psSelect.executeQuery();
            if (rows.next()) {
                return rows.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        PreparedStatement psInsert = null;
        try {
            psInsert = connection.prepareStatement(
                    "INSERT INTO users (login, password, nickname ) VALUES (?, ?, ?);");
            psInsert.setString(1, login);
            psInsert.setString(2, password);
            psInsert.setString(3, nickname);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int rows = 0;
        try {
            rows = psInsert.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows == 1;

    }

    @Override
    public boolean changeNick(String login, String nickname) {
        try {
            psInsert = connection.prepareStatement(
                    "UPDATE users SET nickname = ? WHERE login = ?;");
            psInsert.setString(1, nickname);
            psInsert.setString(2, login);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int rows = 0;
        try {
            rows = psInsert.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows == 1;
    }

    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        stmt = connection.createStatement();
    }

}
