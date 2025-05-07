import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class MySQL {

    private String dburl = "jdbc:mysql://localhost:3306/project";
    private String dbuname = "root";
    private String dbpassword = "Kan29442"; // TODO REPLACE THIS WITH YOUR PASSWORD
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
    
    public boolean addEmployee(String emp_id, String fname, String lname, 
    		String email, String password, boolean isAdmin, boolean isCustRep) {	
    	
    	String sql = "INSERT INTO Employee(EmployeeID, FirstName, LastName, Email, Password, isAdmin, "
    			+ "isCustomerRepresentative) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    	try (Connection con = getConnection();
    		 PreparedStatement ps = con.prepareStatement(sql)) {	
    		ps.setString(1, emp_id);
    		ps.setString(2, fname);
    		ps.setString(3, lname);
    		ps.setString(4, email);
    		ps.setString(5, password);
    		ps.setBoolean(6, isAdmin);
    		ps.setBoolean(7, isCustRep);
    	
    		
    		try {
				ps.executeUpdate();
    		}
    		catch (SQLIntegrityConstraintViolationException e) {
    		    System.out.println("Duplicate primary key!");
    		} 
    		catch (SQLException e) {
    		    e.printStackTrace();
    		}

    		ps.close();
    		con.close();
    	}
    	catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    	
    	return true;
    }
}