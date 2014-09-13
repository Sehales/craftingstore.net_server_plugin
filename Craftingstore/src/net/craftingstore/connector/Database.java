
package net.craftingstore.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    
    private Connection con;
    
    Database(String relativePath, String databaseName) {
        // sqlite driver
        try {
            Class.forName("org.sqlite.JDBCd");
            con = DriverManager.getConnection("jdbc:sqlite:" + relativePath + databaseName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public Connection getConnection() {
        return this.con;
    }
}
