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
	private String dbpassword = "rootrootroot"; // TODO REPLACE THIS WITH YOUR PASSWORD
	private String dbdriver = "com.mysql.jdbc.Driver";

	/*
	 * Helper method to load the Driver package so it can communicate with the
	 * Driver Manager Package
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
	
	
	/**
	 * Execute a SELECT query with optional bind parameters, and return
	 * all rows as a List of Maps (columnLabel → columnValue).
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object... params) {
	    List<Map<String, Object>> rows = new ArrayList<>();
	    
	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        // bind any provided parameters
	        for (int i = 0; i < params.length; i++) {
	            ps.setObject(i + 1, params[i]);
	        }
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            ResultSetMetaData md = rs.getMetaData();
	            int colCount = md.getColumnCount();
	            
	            while (rs.next()) {
	                Map<String, Object> row = new HashMap<>();
	                for (int col = 1; col <= colCount; col++) {
	                    String label = md.getColumnLabel(col);
	                    Object value = rs.getObject(col);
	                    row.put(label, value);
	                }
	                rows.add(row);
	            }
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return rows;
	}

	/*
	 * This method will validate the user passed in by passing in an SQL statement
	 * to check if a specific user exists
	 */

	public boolean validateUser(User user, String accType) {
		loadDriver(dbdriver);

		String sql = "";
		if (accType.equals("Customer"))
			sql = "SELECT * FROM project.Customer WHERE CustomerID = ? AND Password = ?";
		else
			sql = "SELECT * FROM project.Employee WHERE EmployeeID = ? AND Password = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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

	public boolean addEmployee(String emp_id, String fname, String lname, String email, String password,
			boolean isAdmin, boolean isCustRep) {

		String sql = "INSERT INTO Employee(EmployeeID, FirstName, LastName, Email, Password, isAdmin, "
				+ "isCustomerRepresentative) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, emp_id);
			ps.setString(2, fname);
			ps.setString(3, lname);
			ps.setString(4, email);
			ps.setString(5, password);
			ps.setBoolean(6, isAdmin);
			ps.setBoolean(7, isCustRep);

			try {
				ps.executeUpdate();
			} catch (SQLIntegrityConstraintViolationException e) {
				System.out.println("Duplicate primary key!");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			ps.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean editEmployee(String emp_id, String fname, String lname, String email, String password,
			boolean isAdmin, boolean isCustRep) {
		String sql = "UPDATE Employee" + " SET FirstName = ?," + "     LastName = ?," + "     Email = ?,"
				+ "     Password = ?," + "     isAdmin = ?," + "     isCustomerRepresentative = ?"
				+ " WHERE EmployeeID = ?";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, Object> getEmployee(String emp_id) {
		String sql = "SELECT * FROM Employee WHERE EmployeeID = ?";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
		} catch (SQLException e) {
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
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean addCustomer(String cust_id, String fname, String lname, String email, String password, String phone,
			String address) {

		String sql = "INSERT INTO Customer(CustomerID, FirstName, LastName, Email, Password, Phone, Address) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
	
	public List<Map<String, Object>> getAllCustomers() {
	    String sql = "SELECT * FROM Customer";
	    List<Map<String, Object>> customerList = new ArrayList<>();

	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Map<String, Object> custRow = new HashMap<>();
	            custRow.put("CustomerID",        rs.getString("CustomerID"));
	            custRow.put("FirstName",         rs.getString("FirstName"));
	            custRow.put("LastName",          rs.getString("LastName"));
	            custRow.put("Email",             rs.getString("Email"));
	            custRow.put("Phone",             rs.getString("Phone"));
	            custRow.put("Address",           rs.getString("Address"));
	            customerList.add(custRow);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // depending on your needs, you could return an empty list or null here
	    }

	    return customerList;
	}
	
	/**
	 * Fetch a single customer by ID.
	 */
	public Map<String, Object> getCustomer(String cust_id) {
	    String sql = "SELECT * FROM Customer WHERE CustomerID = ?";
	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        ps.setString(1, cust_id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) return null;
	            
	            Map<String, Object> custMap = new HashMap<>();
	            custMap.put("CustomerID", rs.getString("CustomerID"));
	            custMap.put("FirstName",  rs.getString("FirstName"));
	            custMap.put("LastName",   rs.getString("LastName"));
	            custMap.put("Email",      rs.getString("Email"));
	            custMap.put("Password",   rs.getString("Password"));
	            custMap.put("Phone",      rs.getString("Phone"));
	            custMap.put("Address",    rs.getString("Address"));
	            return custMap;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	/**
	 * Update an existing customer's details.
	 */
	public boolean editCustomer(String cust_id,
	                            String fname,
	                            String lname,
	                            String email,
	                            String password,
	                            String phone,
	                            String address) {
	    String sql = "UPDATE Customer"
	               + " SET FirstName = ?,"
	               + "     LastName = ?,"
	               + "     Email = ?,"
	               + "     Password = ?,"
	               + "     Phone = ?,"
	               + "     Address = ?"
	               + " WHERE CustomerID = ?";
	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        ps.setString(1, fname);
	        ps.setString(2, lname);
	        ps.setString(3, email);
	        ps.setString(4, password);
	        ps.setString(5, phone);
	        ps.setString(6, address);
	        ps.setString(7, cust_id);
	        
	        int rows = ps.executeUpdate();
	        return rows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Delete a customer by ID.
	 */
	public boolean deleteCustomer(String cust_id) {
	    String sql = "DELETE FROM Customer WHERE CustomerID = ?";
	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        ps.setString(1, cust_id);
	        int affected = ps.executeUpdate();
	        return affected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public List<Map<String, Object>> getAllAirports() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM Airport";
		List<Map<String, Object>> airportList = new ArrayList<>();

	    try (Connection con = getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Map<String, Object> airport = new HashMap<>();
	            airport.put("AirportID",        rs.getString("AirportID"));
	            airport.put("Name",         rs.getString("Name"));
	            airport.put("City",          rs.getString("City"));
	            airport.put("Country",             rs.getString("Country"));
	            airportList.add(airport);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // depending on your needs, you could return an empty list or null here
	    }
	    
	    List<Map<String, Object>> countAirlines = executeQuery("SELECT COUNT(*) as count FROM Airport");
	    System.out.println("# of Airlines: " + countAirlines.get(0).get("count"));

	    return airportList;
	}

	public List<Map<String, Object>> getAllAirlines() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM Airline";
		return null;
	}


}