import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
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
				employeeMap.put("Password", rs.getString("Password"));
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
		String sql = "SELECT a.Name, " + "a.AirlineID,"
				+ "(SELECT COUNT(*) FROM Aircraft WHERE AirlineID = a.AirlineID) AS numOwnedAircrafts, "
				+ "(SELECT COUNT(*) FROM Flight   WHERE AirlineID = a.AirlineID) AS numSchedFlights "
				+ "FROM Airline a " + "ORDER BY a.Name;";

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
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
		String sql = "UPDATE Airport " + "SET AirportID = ?, " + "    City      = ?, " + "    Country   = ?, "
				+ "    Name      = ?  " + "WHERE AirportID = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, airportID);
			ps.setString(2, city);
			ps.setString(3, country);
			ps.setString(4, name);
			ps.setString(5, originalAID);

			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLIntegrityConstraintViolationException ex) {
			System.err.println("Duplicate AirportID: " + airportID);
			return false;

		} catch (SQLException e) {
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
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, acID);
			int rowsDeleted = ps.executeUpdate();
			return rowsDeleted > 0;

		} catch (SQLException e) {
			// You might use a logger instead of printStackTrace in real code
			e.printStackTrace();
			return false;
		}
	}

	public List<Map<String, Object>> getDirectFlights(String fromAirport, String toAirport, LocalDate start,
			LocalDate end) throws SQLException {

		// if end is null, just search on the single start‐date
		LocalDate effectiveEnd = (end == null) ? start : end;

		String sql = "SELECT " + "  f.FlightID             AS first_leg_id, "
				+ "  NULL                   AS second_leg_id, " + "  f.FromAirportID        AS origin, "
				+ "  NULL                   AS stopover, " + "  f.ToAirportID          AS destination, "
				+ "  f.DepartTime           AS depart_time, " + "  f.ArrivalTime          AS arrival_time, "
				+ "  f.StandardFare         AS total_fare, " + "  f.Duration             AS total_duration, "
				+ "  a.Name                 AS airlineName " + "FROM Flight f "
				+ "JOIN Airline a ON f.AirlineID = a.AirlineID " + "WHERE f.LayoverFlightID IS NULL "
				+ "  AND f.FromAirportID = ? " + "  AND f.ToAirportID   = ? "
				+ "  AND DATE(f.DepartTime) BETWEEN ? AND ? " + // now uses effectiveEnd
				"  AND NOT EXISTS ( " + "      SELECT 1 FROM Flight x " + "      WHERE x.LayoverFlightID = f.FlightID "
				+ "  )";

		try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, fromAirport);
			ps.setString(2, toAirport);
			ps.setDate(3, java.sql.Date.valueOf(start)); // start
			ps.setDate(4, java.sql.Date.valueOf(effectiveEnd)); // end or start if null

			try (ResultSet rs = ps.executeQuery()) {
				List<Map<String, Object>> result = new ArrayList<>();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("first_leg_id", rs.getInt("first_leg_id"));
					row.put("second_leg_id", rs.getObject("second_leg_id"));
					row.put("origin", rs.getString("origin"));
					row.put("stopover", rs.getString("stopover"));
					row.put("destination", rs.getString("destination"));
					row.put("depart_time", rs.getTimestamp("depart_time").toLocalDateTime());
					row.put("arrival_time", rs.getTimestamp("arrival_time").toLocalDateTime());
					row.put("total_fare", rs.getBigDecimal("total_fare"));
					row.put("total_duration", rs.getLong("total_duration"));
					row.put("airlineName", rs.getString("airlineName"));
					result.add(row);
				}
				return result;
			}
		}
	}

	/**
	 * 2-Leg (one-stop) itineraries: join Flight→Flight via LayoverFlightID.
	 */
	public List<Map<String, Object>> getOneStopFlights(String fromAirport, String toAirport, LocalDate start,
			LocalDate end) throws SQLException {
		String sql = "SELECT " + "  f1.FlightID                            AS first_leg_id, "
				+ "  f2.FlightID                            AS second_leg_id, "
				+ "  f1.FromAirportID                       AS origin, "
				+ "  f1.ToAirportID                         AS stopover, "
				+ "  f2.ToAirportID                         AS destination, "
				+ "  f1.DepartTime                          AS depart_time, "
				+ "  f2.ArrivalTime                         AS arrival_time, "
				+ "  (f1.StandardFare + f2.StandardFare)    AS total_fare, " + "  (f1.Duration + f2.Duration + "
				+ "   TIMESTAMPDIFF(MINUTE, f1.ArrivalTime, f2.DepartTime)"
				+ "  )                                      AS total_duration, "
				+ "  a1.Name                                AS airlineName " + "FROM Flight f1 "
				+ "JOIN Flight f2 ON f1.LayoverFlightID = f2.FlightID "
				+ "JOIN Airline a1 ON f1.AirlineID = a1.AirlineID " + "WHERE f1.FromAirportID = ? "
				+ "  AND f2.ToAirportID   = ? " + "  AND DATE(f1.DepartTime) BETWEEN ? AND ? "
				+ "  AND DATE(f2.DepartTime) BETWEEN ? AND ? "
				+ "  AND TIMESTAMPDIFF(MINUTE, f1.ArrivalTime, f2.DepartTime) BETWEEN 60 AND 360";

		try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, fromAirport);
			ps.setString(2, toAirport);
			ps.setDate(3, java.sql.Date.valueOf(start));
			ps.setDate(4, java.sql.Date.valueOf(end));
			ps.setDate(5, java.sql.Date.valueOf(start));
			ps.setDate(6, java.sql.Date.valueOf(end));
			try (ResultSet rs = ps.executeQuery()) {
				List<Map<String, Object>> result = new ArrayList<>();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("first_leg_id", rs.getInt("first_leg_id"));
					row.put("second_leg_id", rs.getInt("second_leg_id"));
					row.put("origin", rs.getString("origin"));
					row.put("stopover", rs.getString("stopover"));
					row.put("destination", rs.getString("destination"));
					row.put("depart_time", rs.getTimestamp("depart_time").toLocalDateTime());
					row.put("arrival_time", rs.getTimestamp("arrival_time").toLocalDateTime());
					row.put("total_fare", rs.getBigDecimal("total_fare"));
					row.put("total_duration", rs.getLong("total_duration"));
					row.put("airlineName", rs.getString("airlineName"));
					result.add(row);
				}
				return result;
			}
		}
	}

	/**
	 * Flexible direct flights: no stop, date between (start–3d) and (end+3d).
	 */
	public List<Map<String, Object>> getDirectFlightsFlexible(String fromAirport, String toAirport, LocalDate start,
			LocalDate end) throws SQLException {
		// compute ±3-day window
		LocalDate flexStart = start.minusDays(3);
		LocalDate flexEnd = (end == null ? start : end).plusDays(3);

		String sql = "SELECT " + "  f.FlightID             AS first_leg_id, "
				+ "  NULL                   AS second_leg_id, " + "  f.FromAirportID        AS origin, "
				+ "  NULL                   AS stopover, " + "  f.ToAirportID          AS destination, "
				+ "  f.DepartTime           AS depart_time, " + "  f.ArrivalTime          AS arrival_time, "
				+ "  f.StandardFare         AS total_fare, " + "  f.Duration             AS total_duration, "
				+ "  a.Name                 AS airlineName " + "FROM Flight f "
				+ "JOIN Airline a ON f.AirlineID = a.AirlineID " + "WHERE f.LayoverFlightID IS NULL "
				+ "  AND f.FromAirportID = ? " + "  AND f.ToAirportID   = ? "
				+ "  AND DATE(f.DepartTime) BETWEEN ? AND ? " + // uses flexStart/flexEnd
				"  AND NOT EXISTS ( " + "      SELECT 1 FROM Flight x " + "      WHERE x.LayoverFlightID = f.FlightID "
				+ "  )";

		try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, fromAirport);
			ps.setString(2, toAirport);
			ps.setDate(3, java.sql.Date.valueOf(flexStart));
			ps.setDate(4, java.sql.Date.valueOf(flexEnd));

			try (ResultSet rs = ps.executeQuery()) {
				List<Map<String, Object>> result = new ArrayList<>();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("first_leg_id", rs.getInt("first_leg_id"));
					row.put("second_leg_id", rs.getObject("second_leg_id"));
					row.put("origin", rs.getString("origin"));
					row.put("stopover", rs.getString("stopover"));
					row.put("destination", rs.getString("destination"));
					row.put("depart_time", rs.getTimestamp("depart_time").toLocalDateTime());
					row.put("arrival_time", rs.getTimestamp("arrival_time").toLocalDateTime());
					row.put("total_fare", rs.getBigDecimal("total_fare"));
					row.put("total_duration", rs.getLong("total_duration"));
					row.put("airlineName", rs.getString("airlineName"));
					result.add(row);
				}
				return result;
			}
		}
		
	}
	
	
	
	/**
     * Execute an INSERT/UPDATE/DELETE, binding any params, and return
     * the number of rows affected.
     */
    public int executeUpdate(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // bind parameters
            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                // if it’s a LocalDateTime, convert to Timestamp
                if (p instanceof LocalDateTime) {
                    ps.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime)p));
                } else {
                    ps.setObject(i+1, p);
                }
            }
            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
	
	
	/*
	 * Method to add a new question
	 */	
	
	// 1) Post a new question
	public boolean addQuestion(String customerId, LocalDateTime when, String message) {
	  String sql = """
	    INSERT INTO Question(CustomerID, SubmitDateTime, Message)
	    VALUES (?, ?, ?)
	  """;
	  return executeUpdate(sql, customerId, when, message) > 0;
	}

	// 2) Browse all questions (with optional paging)
	public List<Map<String,Object>> getAllQuestions() {
	  String sql = """
	    SELECT q.QuestionID, q.CustomerID, q.SubmitDateTime, q.Message,
	           c.FirstName, c.LastName
	      FROM Question q
	      JOIN Customer c ON c.CustomerID = q.CustomerID
	     ORDER BY q.SubmitDateTime DESC
	  """;
	  return executeQuery(sql);
	}

	public List<Map<String,Object>> searchQuestions(String keyword) {
		  String sql = """
		    SELECT q.QuestionID,
		           q.CustomerID,
		           q.SubmitDateTime,
		           q.Message,
		           c.FirstName,
		           c.LastName
		      FROM Question q
		      JOIN Customer c ON c.CustomerID = q.CustomerID
		     WHERE q.Message LIKE ?
		     ORDER BY q.SubmitDateTime DESC
		  """;
		  return executeQuery(sql, "%" + keyword + "%");
		}


	// 4) Post an answer
	public boolean addAnswer(int questionId, String employeeId, LocalDateTime when, String message) {
	  String sql = """
	    INSERT INTO Answer(QuestionID, EmployeeID, ResponseDateTime, Message)
	    VALUES (?, ?, ?, ?)
	  """;
	  return executeUpdate(sql, questionId, employeeId, when, message) > 0;
	}

	// 5) Browse answers for a question
	public List<Map<String,Object>> getAnswersForQuestion(int questionId) {
	  String sql = """
	    SELECT a.AnswerID, a.EmployeeID, a.ResponseDateTime, a.Message,
	           e.FirstName, e.LastName
	      FROM Answer a
	      JOIN Employee e ON e.EmployeeID = a.EmployeeID
	     WHERE a.QuestionID = ?
	     ORDER BY a.ResponseDateTime
	  """;
	  return executeQuery(sql, questionId);
	}
	
	
	public Map<String,Object> getQuestionById(int id) {
		  String sql = """
		    SELECT q.QuestionID, q.CustomerID, q.SubmitDateTime, q.Message,
		           c.FirstName, c.LastName
		      FROM Question q
		      JOIN Customer c ON c.CustomerID = q.CustomerID
		     WHERE q.QuestionID = ?""";
		  return executeQuery(sql, id).get(0);
		}
	
	
	public List<Map<String,Object>> getQuestionsByCustomer(String customerId) {
	    String sql = """
	      SELECT q.QuestionID,
	             q.SubmitDateTime,
	             q.Message
	        FROM Question q
	       WHERE q.CustomerID = ?
	       ORDER BY q.SubmitDateTime DESC
	    """;
	    return executeQuery(sql, customerId);
	}
    
	/**
	 * Flexible one-stop itineraries: both legs depart between (start–3d) and
	 * (end+3d).
	 */
	public List<Map<String, Object>> getOneStopFlightsFlexible(String fromAirport, String toAirport, LocalDate start,
			LocalDate end) throws SQLException {
		LocalDate flexStart = start.minusDays(3);
		LocalDate flexEnd = (end == null ? start : end).plusDays(3);

		String sql = "SELECT " + "  f1.FlightID                            AS first_leg_id, "
				+ "  f2.FlightID                            AS second_leg_id, "
				+ "  f1.FromAirportID                       AS origin, "
				+ "  f1.ToAirportID                         AS stopover, "
				+ "  f2.ToAirportID                         AS destination, "
				+ "  f1.DepartTime                          AS depart_time, "
				+ "  f2.ArrivalTime                         AS arrival_time, "
				+ "  (f1.StandardFare + f2.StandardFare)    AS total_fare, " + "  (f1.Duration + f2.Duration + "
				+ "   TIMESTAMPDIFF(MINUTE, f1.ArrivalTime, f2.DepartTime)"
				+ "  )                                      AS total_duration, "
				+ "  a1.Name                                AS airlineName " + "FROM Flight f1 "
				+ "JOIN Flight f2 ON f1.LayoverFlightID = f2.FlightID "
				+ "JOIN Airline a1 ON f1.AirlineID = a1.AirlineID " + "WHERE f1.FromAirportID = ? "
				+ "  AND f2.ToAirportID   = ? " + "  AND DATE(f1.DepartTime) BETWEEN ? AND ? " + // flexStart/flexEnd
				"  AND DATE(f2.DepartTime) BETWEEN ? AND ? " + // flexStart/flexEnd
				"  AND TIMESTAMPDIFF(MINUTE, f1.ArrivalTime, f2.DepartTime) BETWEEN 60 AND 360";

		try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, fromAirport);
			ps.setString(2, toAirport);
			ps.setDate(3, java.sql.Date.valueOf(flexStart));
			ps.setDate(4, java.sql.Date.valueOf(flexEnd));
			ps.setDate(5, java.sql.Date.valueOf(flexStart));
			ps.setDate(6, java.sql.Date.valueOf(flexEnd));

			try (ResultSet rs = ps.executeQuery()) {
				List<Map<String, Object>> result = new ArrayList<>();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("first_leg_id", rs.getInt("first_leg_id"));
					row.put("second_leg_id", rs.getInt("second_leg_id"));
					row.put("origin", rs.getString("origin"));
					row.put("stopover", rs.getString("stopover"));
					row.put("destination", rs.getString("destination"));
					row.put("depart_time", rs.getTimestamp("depart_time").toLocalDateTime());
					row.put("arrival_time", rs.getTimestamp("arrival_time").toLocalDateTime());
					row.put("total_fare", rs.getBigDecimal("total_fare"));
					row.put("total_duration", rs.getLong("total_duration"));
					row.put("airlineName", rs.getString("airlineName"));
					result.add(row);
				}
				return result;
			}
		}
	}
	
	public int createFlightPlan(String cuID, int totalDur, float totalCost) throws SQLException {
	    String sql = "INSERT INTO FlightPlan (CustomerID, TotalDuration, TotalFare) VALUES (?, ?, ?)";

	    // This will hold the generated key we fetch below
	    int newFlightPlanId = -1;

	    try (Connection connection = getConnection();
	         PreparedStatement ps = connection.prepareStatement(
	           sql,
	           Statement.RETURN_GENERATED_KEYS   // ask for auto‐generated keys
	         )) {

	        // bind your parameters…
	        ps.setString(1, cuID);
	        ps.setInt(2, totalDur);
	        ps.setFloat(3, totalCost);

	        int rows = ps.executeUpdate();
	        if (rows == 0) {
	            throw new SQLException("Inserting FlightPlan failed, no rows affected.");
	        }

	        // now pull the generated key
	        try (ResultSet keys = ps.getGeneratedKeys()) {
	            if (keys.next()) {
	                newFlightPlanId = keys.getInt(1);
	                System.out.println("Inserted FlightPlanID = " + newFlightPlanId);
	            } else {
	                throw new SQLException("Inserting FlightPlan failed, no ID obtained.");
	            }
	        }
	    }

	    return newFlightPlanId;
	}
	
	public boolean insertItinerarySegment(int flightPlanId, int segmentNum, String travelClass, int flightID) {
	    String sql = 
	      "INSERT INTO ItinerarySegment (FlightPlanID, SegmentNum, Class, FlightID) " +
	      "VALUES (?, ?, ?, ?)";
	    try (Connection conn = getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, flightPlanId);
	        ps.setInt(2, segmentNum);
	        ps.setString(3, travelClass);
	        ps.setInt(4, flightID);

	        int affected = ps.executeUpdate();
	        return (affected == 1);

	    } catch (SQLException e) {
	        // log the exception and return false to indicate failure
	        e.printStackTrace();
	        return false;
	    }
	}
	
	private int getNextSeatNumber(int flightID) {
		String sql = "SELECT COUNT(*)+1 as seatNum FROM Ticket WHERE FlightID = ?";
		return Integer.valueOf((String) executeQuery(sql, flightID).get(0).get("seatNum"));
	}
	
	public boolean createTicket(int flightID, float ticketFare, String className) {
	    String sql = 
	      "INSERT INTO Ticket (FlightID, SeatNumber, Class, TicketFare, BookingFee) " +
	      "VALUES (?, ?, ?, ?, ?)";
	    int seatNumber = getNextSeatNumber(flightID);
	    float bookingFee = (float) (("First".equals(className)) ? 15.00 : 0.00);
	    try (Connection conn = getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, flightID);
	        ps.setInt(2, seatNumber);
	        ps.setString(3, className);
	        ps.setFloat(4, ticketFare);
	        ps.setFloat(5,  bookingFee);

	        int affected = ps.executeUpdate();
	        return (affected == 1);

	    } catch (SQLException e) {
	        // log the exception and return false to indicate failure
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public List<Map<String, Object>> getUserFlightPlans(String cust_id) {
		String sql = "SELECT * FROM FlightPlan WHERE CustomerID = ?";
		return executeQuery(sql, cust_id);
	}
	
	

}