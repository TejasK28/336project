import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public boolean editEmployee(String emp_id,
				String fname,
				String lname,
				String email,
				String password,
				boolean isAdmin,
				boolean isCustRep) {
			String sql = "UPDATE Employee"
			+ " SET FirstName = ?,"
			+ "     LastName = ?,"
			+ "     Email = ?,"
			+ "     Password = ?,"
			+ "     isAdmin = ?,"
			+ "     isCustomerRepresentative = ?"
			+ " WHERE EmployeeID = ?";

		try (Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(sql)) {

			// Bind form values to the UPDATE statement
			ps.setString(1, fname);
			ps.setString(2, lname);
			ps.setString(3, email);
			ps.setString(4, password);
			ps.setBoolean(5, isAdmin);
			ps.setBoolean(6, isCustRep);
			ps.setString(7, emp_id);

			int rows = ps.executeUpdate();
			return rows > 0;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
    
    public Map<String, Object> getEmployee(String emp_id) {
			String sql = "SELECT * FROM Employee WHERE EmployeeID = ?";

		try (Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(sql)) {

			// Bind form values to the UPDATE statement
			ps.setString(1, emp_id);

			Map<String, Object> employeeMap = new HashMap<>();

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				employeeMap.put("EmployeeID", rs.getString("EmployeeID"));
				employeeMap.put("FirstName", rs.getString("FirstName"));
				employeeMap.put("LastName", rs.getString("LastName"));
				employeeMap.put("Email", rs.getString("Email"));
				employeeMap.put("isAdmin", rs.getBoolean("isAdmin"));
				employeeMap.put("isCustomerRepresentative", rs.getBoolean("isCustomerRepresentative"));
			}

			return employeeMap;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	
    }
    
    public boolean deleteEmployee(String emp_id) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            // Step 2: Prepare the DELETE statement
            String sql = "DELETE FROM Employee WHERE EmployeeID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emp_id);

            // Step 3: Execute the statement
            int affectedRows = stmt.executeUpdate();

            // Step 4: Return true if at least one row was deleted
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Step 5: Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean addCustomer(String cust_id, String fname, String lname, 
            String email, String password, String phone, String address) {

			String sql = "INSERT INTO Customer(CustomerID, FirstName, LastName, Email, Password, Phone, Address) " +
			  "VALUES (?, ?, ?, ?, ?, ?)";

			try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, cust_id);
			ps.setString(2, fname);
			ps.setString(3, lname);
			ps.setString(4, email);
			ps.setString(5, password);
			ps.setString(6, phone);
			ps.setString(7, address);
			
			System.out.println("Does it work?");
			try {
				ps.executeUpdate();
				} catch (SQLIntegrityConstraintViolationException e) {
				System.out.println("Duplicate primary key!");
				return false;
				} catch (SQLException e) {
				e.printStackTrace();
				return false;
				}

				} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

			return true;
    }
 

}