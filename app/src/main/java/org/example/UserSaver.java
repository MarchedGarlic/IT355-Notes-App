package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserSaver {
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:data/sample.db");
    }

    private static void initTables(){
        try(Connection conn = getConnection(); Statement stat = conn.createStatement()) {
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS users (id STRING PRIMARY KEY, username STRING, password STRING)");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }   
    }

    public static void saveUser(User user){
        initTables();

        try (Connection conn = getConnection(); Statement stat = conn.createStatement()) {
            stat.executeUpdate("INSERT INTO users VALUES('"+ user.getId() +"', '"+ user.getUsername() +"', '"+ user.getPassword() +"')");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static ArrayList<User> loadUsers(){
        ArrayList<User> users = new ArrayList<>();
        initTables();
        
        try (Connection conn = getConnection(); Statement stat = conn.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("password")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users;
    }

    public static void main(String[] args) {
        User user = new User("Test", "Password");
        saveUser(user);

        System.out.println(loadUsers());
    }
}
