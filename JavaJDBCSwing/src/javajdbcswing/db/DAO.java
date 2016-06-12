/*
 * Copyright (C) 2016 CodeFireUA <edu@codefire.com.ua>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package javajdbcswing.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CodeFireUA <edu@codefire.com.ua>
 */
public class DAO {

    private String connectionString;
    private String username;
    private String password;

    public DAO(String host, String username, String password) {
        this.connectionString = String.format("jdbc:mysql://%s/", host);
        this.username = username;
        this.password = password;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(connectionString, username, password);
    }

    public List<String> getDatabaseList() throws SQLException {
        List<String> databaseList = new ArrayList<>();

        try (Connection conn = connect()) {
            ResultSet rs = conn.createStatement().executeQuery("SHOW DATABASES");

            while (rs.next()) {
                databaseList.add(rs.getString(1));
            }
        }

        return databaseList;
    }

    public List<String> getTableList(String databaseName) throws SQLException {
        List<String> tableList = new ArrayList<>();

        try (Connection conn = connect()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery(String.format("SHOW TABLES FROM `%s`", databaseName));

            while (rs.next()) {
                tableList.add(rs.getString(1));
            }
        }

        return tableList;
    }

    public void printDatabaseTable(String databaseName, String tableName) throws SQLException {
        try (Connection conn = connect()) {
            ResultSet rs = conn.createStatement()
                    .executeQuery(String.format("SELECT * FROM `%s`.`%s`", databaseName, tableName));

            ResultSetMetaData rsmd = rs.getMetaData();

            System.out.printf("# %s ---------------\n", tableName);
            System.out.print("|");
            for (int i = 1; i < rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i);
                int size = rsmd.getColumnDisplaySize(i);
                System.out.printf(" %-" + (size > 255 ? 50 : size) + "s |", columnName);
            }
            System.out.println();

            while (rs.next()) {
                System.out.print("|");
                for (int i = 1; i < rsmd.getColumnCount(); i++) {
                int size = rsmd.getColumnDisplaySize(i);
                    System.out.printf(" %-" + (size > 255 ? 50 : size) + "s |", rs.getString(i));
                }
                System.out.println();
            }
        }
    }
}