package org.csd211.cdApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fcarella
 */
public class App {

    private String USERNAME = "root";
    private String IP = "localhost";
    private String PASSWORD = "itstudies12345";
    private String dbNameArg = "csd211testdb";
    private Connection con = null;
    private String TABLE_NAME = "CD";

    /**
     * @param args the command line arguments
     */
    public void run() {
        try {
            // get connection.  This connection may or may not be connected to a database
            // Depends on if one exists or not
            // If it doesn't exist we get a connection with which we can create a database.
            try {
                con = getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }

            // create the database if it doesn't exist
            Statement s = con.createStatement();
            String newDatabaseString = "CREATE DATABASE IF NOT EXISTS " + dbNameArg;
            // String newDatabaseString = "CREATE DATABASE " + dbName;
            try {
                s.executeUpdate(newDatabaseString);
                s.executeUpdate("use "+dbNameArg);
            } catch (Exception e) {
                System.out.println("Error Creating database " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Created database " + dbNameArg);
            
            dropTable();

            createTable();

            populateTable();

            viewTable();

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void viewTable() throws SQLException {
        Statement stmt = null;
        String query
                = "select ID, CD_NAME, BAND_NAME from " + TABLE_NAME;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int supplierID = rs.getInt("ID");
                String cdName = rs.getString("CD_NAME");
                String bandName = rs.getString("BAND_NAME");
                System.out.println(cdName + "(" + supplierID + "): " + cdName + bandName);
            }
        } finally {
            stmt.close();
        }
    }

    public void dropTable() throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            System.out.println("Dropping table " + TABLE_NAME + " from " + dbNameArg);
            stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
        } finally {
            stmt.close();
        }

    }

    public void populateTable() throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();

            stmt.executeUpdate("insert into " + TABLE_NAME + " "
                    + "values(1, '5150_1', 'Van Halen')");
            stmt.executeUpdate("insert into " + TABLE_NAME + " "
                    + "values(2, '1984_1', 'Van Halen')");
            stmt.executeUpdate("insert into " + TABLE_NAME + " "
                    + "values(3, 'Back in Black_1', 'AC/DC')");
            stmt.executeUpdate("insert into " + TABLE_NAME + " "
                    + "values(4, 'Full Bluntal Nugity_1', 'Ted Nugent')");

        } finally {
            stmt.close();
        }
    }

    public void createTable() throws SQLException {
        String createString
                = "create table " + TABLE_NAME + " " + "(ID integer NOT NULL AUTO_INCREMENT, "
                + "CD_NAME varchar(40) NOT NULL, "
                + "BAND_NAME varchar(40) NOT NULL, "
                + "PRIMARY KEY (ID))";

        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createString);
        } finally {
            stmt.close();
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", USERNAME);
        connectionProps.put("password", PASSWORD);

        try {
            conn = DriverManager.getConnection(""
                    + "jdbc:mysql://" + // protocol for mysql
                    "" + IP + // ip of database server
                    ":3306" + // mysql port number
                    "/" + dbNameArg
                    + "", connectionProps);
        } catch (SQLException e) {
            if (e.getMessage().contains("Unknown database")) {
                conn = DriverManager.getConnection(""
                        + "jdbc:mysql://" + // protocol for mysql
                        "" + IP + // ip of database server
                        ":3306" + // mysql port number
                        //                        "/" + dbNameArg +// we could specify an  existing database here but we dont need to because our app creates a database
                        "", connectionProps);
            } else {
                System.out.println("ERROR-------------------------------------------------");
                System.out.println(e.getMessage());
                System.out.println("------------------------------------------------------");
                System.exit(0);
            }
        }
        System.out.println("Connected to database");
        return conn;
    }
}
