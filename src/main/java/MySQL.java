import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

    private String dburl = "jdbc:mysql://localhost:3306/project";
    private String dbuname = "root";
    private String dbpassword = "Proj3ct!23"; // TODO REPLACE THIS WITH YOUR PASSWORD
    private String dbdriver = "com.mysql.jdbc.Driver";

    /*
     * Helper method to load the Driver package so it can communicate with the Driver Manager Package
     */
    public void loadDriver(String db) {
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * This method will create a connection using the DriverManager package
     */
    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(dburl, dbuname, dbpassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;

    }
    /*
     * This method will validate the user passed in by passing in an SQL statement to check if a specific user exists
     */

    public boolean validateUser(User user, String accType) {
        loadDriver(dbdriver);
       
        String sql = "";
        if (accType.equals("Customer"))
        	sql = "SELECT * FROM project.Customer WHERE CustomerID = ? AND Password = ?";
        else
        	sql = "SELECT * FROM project.Employee WHERE EmployeeID = ? AND Password = ?";
        try (Connection con = getConnection(); 
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user.getUname());
            ps.setString(2, user.getPassword());
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // returns true if a record exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}