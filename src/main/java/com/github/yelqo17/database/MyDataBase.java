package com.github.yelqo17.database;

import com.github.yelqo17.config.DatabaseProperties;
import com.github.yelqo17.config.PropertiesFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MyDataBase {
    private static MyDataBase instance;
    private final DatabaseProperties properties = PropertiesFactory.getProperties();
    public synchronized static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    private MyDataBase() {
        init();
    }
    private void init() {
        createSchema();
        createTableRole();
    }

    public void deleteDataRoleTable() {
        String sql = """
                truncate mafia.role restart identity;
                """;
        execute(sql);
    }

    private void createSchema() {
        String sql = """
                create schema if not exists mafia;
                """;
        execute(sql);
    }
    public void execute(String sql) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void createTableRole() {
        String sql = """
                create table if not exists mafia.role (
                    id serial primary key,
                    name varchar(50) not null
                )
                """;
        execute(sql);
    }

    public Map<String, String> selectById(int id, String table, String... columnNames) {
        Map<String, String> result = new HashMap<>();
        String sql = """
                select id, %s
                from mafia.%s
                where id = %d
                """;
        String names = Stream.of(columnNames)
                .collect(Collectors.joining(", "));

        String select = String.format(sql, names, table, id);

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery(select);

            while (set.next()) {
                for(String columnName : columnNames) {
                    result.put(columnName, set.getString(columnName));
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return result;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
                properties.getUrl(),
                properties.getLogin(),
                properties.getPassword()
        );
    }
}
