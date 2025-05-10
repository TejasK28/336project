import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQL {

	private String dburl = "jdbc:mysql://localhost:3306/project";
	private String dbuname = "root";
	private String dbdriver = "com.mysql.cj.jdbc.Driver";

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
	 * Execute a SELECT query with optional bind parameters, and return all rows as
	 * a List of Maps (columnLabel → columnValue).
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object... params) {
		List<Map<String, Object>> rows = new ArrayList<>();

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
				employeeMap.put("Password",   rs.getString("Password"));
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
				custRow.put("CustomerID", rs.getString("CustomerID"));
				custRow.put("FirstName", rs.getString("FirstName"));
				custRow.put("LastName", rs.getString("LastName"));
				custRow.put("Email", rs.getString("Email"));
				custRow.put("Phone", rs.getString("Phone"));
				custRow.put("Address", rs.getString("Address"));
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
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, cust_id);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next())
					return null;

				Map<String, Object> custMap = new HashMap<>();
				custMap.put("CustomerID", rs.getString("CustomerID"));
				custMap.put("FirstName", rs.getString("FirstName"));
				custMap.put("LastName", rs.getString("LastName"));
				custMap.put("Email", rs.getString("Email"));
				custMap.put("Password", rs.getString("Password"));
				custMap.put("Phone", rs.getString("Phone"));
				custMap.put("Address", rs.getString("Address"));
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
	public boolean editCustomer(String cust_id, String fname, String lname, String email, String password, String phone,
			String address) {
		String sql = "UPDATE Customer" + " SET FirstName = ?," + "     LastName = ?," + "     Email = ?,"
				+ "     Password = ?," + "     Phone = ?," + "     Address = ?" + " WHERE CustomerID = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
				airport.put("AirportID", rs.getString("AirportID"));
				airport.put("Name", rs.getString("Name"));
				airport.put("City", rs.getString("City"));
				airport.put("Country", rs.getString("Country"));

				airportList.add(airport);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// depending on your needs, you could return an empty list or null here
		}

		return airportList;
	}

	public List<Map<String, Object>> getAllAirlines() {
//		String sql = "SELECT * FROM Airline";
		String sql =
			    "SELECT a.Name, " +
			    "a.AirlineID," + 
			    "(SELECT COUNT(*) FROM Aircraft WHERE AirlineID = a.AirlineID) AS numOwnedAircrafts, " +
			    "(SELECT COUNT(*) FROM Flight   WHERE AirlineID = a.AirlineID) AS numSchedFlights " +
			    "FROM Airline a " +
			    "ORDER BY a.Name;";

		return executeQuery(sql);
	}

	public List<Map<String, Object>> getAllFlights() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM Flight";
		return executeQuery(sql);
	}

	public Map<String, Object> getFlightByFID(String flightID) {
		String sql = "SELECT * FROM Flight WHERE FlightID = ?";
		return executeQuery(sql, flightID).get(0);
	}

	public boolean deleteFlightByFID(String flightID) {
		String sql = "DELETE FROM Flight WHERE FlightID = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, flightID);
			int affected = ps.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Map<String, Object>> getFlightsAtAirport(String airport_id) {
		String sql = "SELECT * FROM Flight WHERE ToAirportID = ? OR FromAirportID = ?";
		return executeQuery(sql, airport_id, airport_id);
	}

	public boolean addAircraft(String airlineId, String model, int totalSeats, String configStr) {
		String sql = "INSERT INTO Aircraft(AirlineID, Model, TotalSeats, ClassConfigurations)" + "VALUES (?,?,?,?)";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, airlineId);
			ps.setString(2, model);
			ps.setInt(3, totalSeats);
			ps.setString(4, configStr);

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

	public boolean addAirport(String airportID, String name, String city, String country) {
		String sql = "INSERT INTO Airport(AirportID, Name, City, Country)" + "VALUES (?,?,?,?)";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, airportID);
			ps.setString(2, name);
			ps.setString(3, city);
			ps.setString(4, country);

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
	
	public boolean deleteAirport(String airportID) {
		String sql = "DELETE FROM Airport WHERE AirportID = ?";
		try (Connection con = getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, airportID);
		        int rowsDeleted = ps.executeUpdate();
		        return rowsDeleted > 0;

		    } catch (SQLException e) {
		        // You might use a logger instead of printStackTrace in real code
		        e.printStackTrace();
		        return false;
		    }
		
	}
	
	public boolean updateAirport(String airportID, String name, String city, String country, String originalAID) {
		String sql = "UPDATE Airport " +
		        "SET AirportID = ?, " +
		        "    City      = ?, " +
		        "    Country   = ?, " +
		        "    Name      = ?  " +
		        "WHERE AirportID = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, airportID);
			ps.setString(2, city);
			ps.setString(3, country);
			ps.setString(4, name);
			ps.setString(5, originalAID);

			int rows = ps.executeUpdate();
			return rows > 0;
		} 
		catch (SQLIntegrityConstraintViolationException ex) {
	        System.err.println("Duplicate AirportID: " + airportID);
	        return false;

	    }
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateFlight(int flightId, int flightNumber, int airlineId, String fromAirport, String toAirport,
			LocalDateTime depart, LocalDateTime arrive, String operatingDays) {
		String sql = "UPDATE Flight" + " SET FlightNumber = ?," + "     AirlineID    = ?," + "     FromAirportID= ?,"
				+ "     ToAirportID  = ?," + "     DepartTime   = ?," + "     ArrivalTime  = ?,"
				+ "     OperatingDays= ?" + " WHERE FlightID = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, flightNumber);
			ps.setInt(2, airlineId);
			ps.setString(3, fromAirport);
			ps.setString(4, toAirport);
// JDBC 4+ can handle LocalDateTime via setObject
			ps.setObject(5, depart);
			ps.setObject(6, arrive);
			ps.setString(7, operatingDays);
			ps.setInt(8, flightId);

			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getNumArrivingFlightsByAirportID(String airportID) {
		String sql = "SELECT COUNT(*) as numArriving FROM Flight WHERE ToAirportID = ?";
		Map<String, Object> count = executeQuery(sql, airportID).get(0);
		
		return (String) count.get("numArriving").toString();
	}

	public Object getNumDepartingFlightByAirportID(String airportID) {
		String sql = "SELECT COUNT(*) as numDeparting FROM Flight WHERE FromAirportID = ?";
		Map<String, Object> count = executeQuery(sql, airportID).get(0);
		return (String) count.get("numDeparting").toString();
	}

	public Map<String, Object> getAirportByID(String aid) {
		String sql = "SELECT * FROM Airport WHERE AirportID = ?";
		return executeQuery(sql, aid).get(0);
	}

	public List<Map<String, Object>> getOwnedAircraftsByAirlineID(String airlineID) {
		String sql = "SELECT * FROM Aircraft WHERE AirlineID = ?";
		return executeQuery(sql, airlineID);
	}

	public boolean deleteAircraft(String acID) {
		String sql = "DELETE FROM Aircraft WHERE AircraftID = ?";
		try (Connection con = getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setString(1, acID);
		        int rowsDeleted = ps.executeUpdate();
		        return rowsDeleted > 0;

		    } catch (SQLException e) {
		        // You might use a logger instead of printStackTrace in real code
		        e.printStackTrace();
		        return false;
		    }
	}
	
	
	/**
	 * One-way search on an exact date, now including flight-plan flags.
	 */
	public List<Map<String,Object>> searchOneWay(
		    String fromAirport,
		    String toAirport,
		    LocalDate departDate,
		    String sortBy)
		{
		  // whitelist to prevent SQL-injection
		  var allowed = Set.of("f.DepartTime","f.ArrivalTime","duration","price");
		  if (!allowed.contains(sortBy)) sortBy = "f.DepartTime";

		  String sql =
		    "SELECT f.FlightID,\n" +
		    "       f.FromAirportID,\n" +
		    "       f.ToAirportID,\n" +
		    "       f.DepartTime,\n" +
		    "       f.ArrivalTime,\n" +
		    "       TIMESTAMPDIFF(MINUTE, f.DepartTime, f.ArrivalTime) AS duration,\n" +
		    "       f.StandardFare AS price,\n" +
		    "       a.Name          AS AirlineName\n" +
		    "  FROM Flight f\n" +
		    "  JOIN Airline a ON a.AirlineID = f.AirlineID\n" +
		    " WHERE f.FromAirportID    = ?\n" +
		    "   AND f.ToAirportID      = ?\n" +
		    "   AND DATE(f.DepartTime) = ?\n" +
		    " ORDER BY " + sortBy;

		  return executeQuery(sql,
		                      fromAirport,
		                      toAirport,
		                      Date.valueOf(departDate));
		}


	/**
	 * ±3-day flexible search, now including flight-plan flags.
	 */
	public List<Map<String,Object>> searchFlexible(
		    String fromAirport,
		    String toAirport,
		    LocalDate departDate,
		    String sortBy)
		{
		  var allowed = Set.of("f.DepartTime","f.ArrivalTime","duration","price");
		  if (!allowed.contains(sortBy)) sortBy = "f.DepartTime";

		  String sql =
		    "SELECT f.FlightID,\n" +
		    "       f.FromAirportID,\n" +
		    "       f.ToAirportID,\n" +
		    "       f.DepartTime,\n" +
		    "       f.ArrivalTime,\n" +
		    "       TIMESTAMPDIFF(MINUTE, f.DepartTime, f.ArrivalTime) AS duration,\n" +
		    "       f.StandardFare AS price,\n" +
		    "       a.Name          AS AirlineName\n" +
		    "  FROM Flight f\n" +
		    "  JOIN Airline a ON a.AirlineID = f.AirlineID\n" +
		    " WHERE f.FromAirportID    = ?\n" +
		    "   AND f.ToAirportID      = ?\n" +
		    "   AND DATE(f.DepartTime)\n" +
		    "       BETWEEN DATE_SUB(?, INTERVAL 3 DAY)\n" +
		    "           AND DATE_ADD(?, INTERVAL 3 DAY)\n" +
		    " ORDER BY " + sortBy;

		  return executeQuery(sql,
		                      fromAirport,
		                      toAirport,
		                      Date.valueOf(departDate),
		                      Date.valueOf(departDate));
		}
	
	public Map<String, Object> getMonthlySalesReport(int year, int month) {
	    String sql = "SELECT " +
	                 "COUNT(*) as numberOfTickets, " +
	                 "COALESCE(SUM(TicketFare), 0) as totalFare, " +
	                 "COALESCE(SUM(BookingFee), 0) as totalBookingFee, " +
	                 "COALESCE(SUM(TicketFare + BookingFee), 0) as totalRevenue " +
	                 "FROM Ticket " + // Ensure your table is named 'Ticket'
	                 "WHERE YEAR(PurchaseDateTime) = ? AND MONTH(PurchaseDateTime) = ?";

	    // 'executeQuery' is your existing method in MySQL.java
	    List<Map<String, Object>> results = executeQuery(sql, year, month);

	    if (!results.isEmpty() && results.get(0) != null) {
	        Map<String, Object> reportData = results.get(0);
	        // Ensure all expected keys have non-null values, defaulting to 0 or BigDecimal.ZERO
	        // COUNT(*) in MySQL returns BIGINT (Long in Java)
	        reportData.putIfAbsent("numberOfTickets", 0L);
	        reportData.putIfAbsent("totalFare", java.math.BigDecimal.ZERO);
	        reportData.putIfAbsent("totalBookingFee", java.math.BigDecimal.ZERO);
	        reportData.putIfAbsent("totalRevenue", java.math.BigDecimal.ZERO);
	        return reportData;
	    } else {
	        // This case handles if executeQuery returns an empty list (e.g., on SQL error)
	        // or if the query itself correctly returns no rows (which COALESCE should handle by returning 0s)
	        Map<String, Object> emptyResult = new HashMap<>();
	        emptyResult.put("numberOfTickets", 0L);
	        emptyResult.put("totalFare", java.math.BigDecimal.ZERO);
	        emptyResult.put("totalBookingFee", java.math.BigDecimal.ZERO);
	        emptyResult.put("totalRevenue", java.math.BigDecimal.ZERO);
	        return emptyResult;
	    }
	}

}