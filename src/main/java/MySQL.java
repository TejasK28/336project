import java.math.BigDecimal; 
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
import java.math.BigDecimal;
import java.sql.Timestamp;
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
        String sql = "SELECT a.*, " +
                    "COALESCE(d.numDeparting, 0) as numDeparting, " +
                    "COALESCE(r.numArriving, 0) as numArriving " +
                    "FROM Airport a " +
                    "LEFT JOIN (SELECT FromAirportID as AirportID, COUNT(*) as numDeparting " +
                    "          FROM Flight GROUP BY FromAirportID) d " +
                    "ON a.AirportID = d.AirportID " +
                    "LEFT JOIN (SELECT ToAirportID as AirportID, COUNT(*) as numArriving " +
                    "          FROM Flight GROUP BY ToAirportID) r " +
                    "ON a.AirportID = r.AirportID " +
                    "ORDER BY a.AirportID";
        
        return executeQuery(sql);
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
        String sql = "SELECT f.FlightID, f.FlightNumber, f.DepartTime, f.ArrivalTime, f.Duration, f.StandardFare, " +
                    "a.Name as airline_name, " +
                    "dep.Name as departure_airport, dep.City as departure_city, dep.Country as departure_country, " +
                    "arr.Name as arrival_airport, arr.City as arrival_city, arr.Country as arrival_country, " +
                    "t.Class " +
                    "FROM Ticket t " +
                    "JOIN Flight f ON t.FlightID = f.FlightID " +
                    "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                    "JOIN Airport dep ON f.FromAirportID = dep.AirportID " +
                    "JOIN Airport arr ON f.ToAirportID = arr.AirportID " +
                    "WHERE f.DepartTime >= NOW() " +
                    "ORDER BY f.DepartTime ASC";
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
        String sql = """
          SELECT 
            f.FlightID,
            f.AirlineID,
            f.FlightNumber,
            f.FromAirportID,
            f.ToAirportID,
            f.DepartTime,
            f.ArrivalTime,
            f.OperatingDays,
            f.AircraftID,
            a.Model AS AircraftModel
          FROM Flight f
          JOIN Aircraft a USING (AircraftID)
          WHERE f.ToAirportID   = ?
             OR f.FromAirportID = ?
        """;
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
                // if it's a LocalDateTime, convert to Timestamp
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

    /**
     * Retrieves monthly sales report data.
     * TicketFare and BookingFee are INT in the database. SUM will produce BIGINT or DECIMAL.
     * We cast to DECIMAL in SQL or handle as BigDecimal in Java.
     */
    public Map<String, Object> getMonthlySalesReport(int year, int month) {
        // SQL query to count tickets and sum fares/fees, casting to DECIMAL for sums
        String sql = "SELECT " +
                     "COUNT(*) as numberOfTickets, " +
                     "COALESCE(SUM(CAST(TicketFare AS DECIMAL(10,2))), 0.00) as totalFare, " +
                     "COALESCE(SUM(CAST(BookingFee AS DECIMAL(10,2))), 0.00) as totalBookingFee, " +
                     "COALESCE(SUM(CAST(TicketFare AS DECIMAL(10,2)) + CAST(BookingFee AS DECIMAL(10,2))), 0.00) as totalRevenue " +
                     "FROM Ticket " + // Table name is Ticket
                     "WHERE YEAR(PurchaseDateTime) = ? AND MONTH(PurchaseDateTime) = ?";

        // Execute the query
        List<Map<String, Object>> results = executeQuery(sql, year, month);

        // Process results
        if (!results.isEmpty() && results.get(0) != null) {
            Map<String, Object> reportData = results.get(0);
            // Ensure all expected keys have non-null values, defaulting if necessary
            reportData.putIfAbsent("numberOfTickets", 0L); // COUNT(*) is typically Long
            reportData.putIfAbsent("totalFare", BigDecimal.ZERO);
            reportData.putIfAbsent("totalBookingFee", BigDecimal.ZERO);
            reportData.putIfAbsent("totalRevenue", BigDecimal.ZERO);
            return reportData;
        } else {
            // Return an empty/default map if no results
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("numberOfTickets", 0L);
            emptyResult.put("totalFare", BigDecimal.ZERO);
            emptyResult.put("totalBookingFee", BigDecimal.ZERO);
            emptyResult.put("totalRevenue", BigDecimal.ZERO);
            return emptyResult;
        }
    }

/**
 * Retrieves reservations based on flight number.
 * Constructs a DisplayTicketID from FlightID and SeatNumber.
 * TicketFare and BookingFee are retrieved as Integers.
 */
public List<Map<String, Object>> getReservationsByFlightNumber(String flightNumberValue) {
    // SQL query to get reservation details, constructing a display ticket ID
    String sql = "SELECT CONCAT(t.FlightID, '-', t.SeatNumber) AS DisplayTicketID, " +
                 "t.PurchaseDateTime, t.TicketFare, t.BookingFee, t.Class, t.SeatNumber, " +
                 "c.CustomerID, c.FirstName AS CustomerFirstName, c.LastName AS CustomerLastName, " +
                 "f.FlightID, f.FlightNumber, f.FromAirportID, f.ToAirportID, f.DepartTime, f.ArrivalTime, " +
                 "al.Name as AirlineName " +
                 "FROM Ticket t " +
                 "JOIN Customer c ON t.CustomerID = c.CustomerID " +
                 "JOIN Flight f ON t.FlightID = f.FlightID " +
                 "JOIN Airline al ON f.AirlineID = al.AirlineID " +
                 "WHERE f.FlightNumber = ?";
    try {
        // FlightNumber is an INT in the Flight table
        int fn = Integer.parseInt(flightNumberValue);
        return executeQuery(sql, fn);
    } catch (NumberFormatException e) {
        // Log error: Invalid flight number format
        System.err.println("Flight number search requires an integer. Value received: " + flightNumberValue + ". Error: " + e.getMessage());
        return new ArrayList<>(); // Return empty list for invalid format
    }
}

/**
 * Retrieves reservations based on partial customer name.
 * Constructs a DisplayTicketID. TicketFare and BookingFee are retrieved as Integers.
 */
public List<Map<String, Object>> getReservationsByCustomerName(String customerNamePart) {
    // SQL query to get reservation details by customer name
    String sql = "SELECT CONCAT(t.FlightID, '-', t.SeatNumber) AS DisplayTicketID, " +
                 "t.PurchaseDateTime, t.TicketFare, t.BookingFee, t.Class, t.SeatNumber, " +
                 "c.CustomerID, c.FirstName AS CustomerFirstName, c.LastName AS CustomerLastName, " +
                 "f.FlightID, f.FlightNumber, f.FromAirportID, f.ToAirportID, f.DepartTime, f.ArrivalTime, " +
                 "al.Name as AirlineName " +
                 "FROM Ticket t " +
                 "JOIN Customer c ON t.CustomerID = c.CustomerID " +
                 "JOIN Flight f ON t.FlightID = f.FlightID " +
                 "JOIN Airline al ON f.AirlineID = al.AirlineID " +
                 "WHERE c.FirstName LIKE ? OR c.LastName LIKE ?";
    String searchPattern = "%" + customerNamePart + "%"; // Prepare search pattern for LIKE
    return executeQuery(sql, searchPattern, searchPattern);
}

/**
 * Retrieves reservations based on CustomerID.
 * Constructs a DisplayTicketID. TicketFare and BookingFee are retrieved as Integers.
 */
public List<Map<String, Object>> getReservationsByCustomerID(String customerID) {
    // SQL query to get reservation details by customer ID
    String sql = "SELECT CONCAT(t.FlightID, '-', t.SeatNumber) AS DisplayTicketID, " +
                 "t.PurchaseDateTime, t.TicketFare, t.BookingFee, t.Class, t.SeatNumber, " +
                 "c.CustomerID, c.FirstName AS CustomerFirstName, c.LastName AS CustomerLastName, " +
                 "f.FlightID, f.FlightNumber, f.FromAirportID, f.ToAirportID, f.DepartTime, f.ArrivalTime, " +
                 "al.Name as AirlineName " +
                 "FROM Ticket t " +
                 "JOIN Customer c ON t.CustomerID = c.CustomerID " +
                 "JOIN Flight f ON t.FlightID = f.FlightID " +
                 "JOIN Airline al ON f.AirlineID = al.AirlineID " +
                 "WHERE t.CustomerID = ?";
    return executeQuery(sql, customerID);
}

/**
 * Finds the customer who has generated the most total revenue.
 * Revenue is SUM(TicketFare + BookingFee). These are INTs, sum can be BIGINT/DECIMAL.
 */
public Map<String, Object> getTopRevenueCustomer() {
    // SQL query to find the top revenue-generating customer
    String sql = "SELECT c.CustomerID, c.FirstName, c.LastName, " +
                 "SUM(CAST(t.TicketFare AS DECIMAL(10,2)) + CAST(t.BookingFee AS DECIMAL(10,2))) AS TotalRevenue " +
                 "FROM Ticket t " +
                 "JOIN Customer c ON t.CustomerID = c.CustomerID " +
                 "GROUP BY c.CustomerID, c.FirstName, c.LastName " +
                 "ORDER BY TotalRevenue DESC " +
                 "LIMIT 1";
    List<Map<String, Object>> results = executeQuery(sql);
    if (!results.isEmpty()) {
         Map<String, Object> data = results.get(0);
         data.putIfAbsent("TotalRevenue", BigDecimal.ZERO); // Ensure TotalRevenue is present, even if null from DB (COALESCE handles this in SQL now)
         return data;
    }
    // Return a default map if no data is found
    Map<String, Object> emptyResult = new HashMap<>();
    emptyResult.put("CustomerID", "N/A");
    emptyResult.put("FirstName", "N/A");
    emptyResult.put("LastName", "N/A");
    emptyResult.put("TotalRevenue", BigDecimal.ZERO);
    return emptyResult;
}

/**
 * Produces a list of most active flights based on the number of tickets sold.
 * @param limit The number of top active flights to return.
 * @return A List of Maps, each containing FlightID, FlightNumber, FromAirportID, ToAirportID, AirlineName and TicketCount.
 */
public List<Map<String, Object>> getMostActiveFlights(int limit) {
    // SQL query to find most active flights by ticket count
    String sql = "SELECT f.FlightID, f.FlightNumber, f.FromAirportID, f.ToAirportID, al.Name AS AirlineName, " +
                 "COUNT(*) AS TicketCount " + // Counting rows (tickets) for the flight
                 "FROM Ticket t " +
                 "JOIN Flight f ON t.FlightID = f.FlightID " +
                 "JOIN Airline al ON f.AirlineID = al.AirlineID " +
                 "GROUP BY f.FlightID, f.FlightNumber, f.FromAirportID, f.ToAirportID, al.Name " +
                 "ORDER BY TicketCount DESC " +
                 "LIMIT ?";
    return executeQuery(sql, limit);
}
    
/**
 * Calculates detailed revenue generated by a specific FlightID.
 * Includes number of tickets, total fare, total booking fee, and total revenue.
 * @param flightID The ID of the flight.
 * @return A Map containing revenue details, or a default map if no tickets/flight.
 */
public Map<String, Object> getRevenueByFlightID(int flightID) {
    String sql = "SELECT " +
                 "COUNT(*) as numberOfTickets, " +
                 "COALESCE(SUM(CAST(t.TicketFare AS DECIMAL(10,2))), 0.00) as totalFare, " +
                 "COALESCE(SUM(CAST(t.BookingFee AS DECIMAL(10,2))), 0.00) as totalBookingFee, " +
                 "COALESCE(SUM(CAST(t.TicketFare AS DECIMAL(10,2)) + CAST(t.BookingFee AS DECIMAL(10,2))), 0.00) as totalRevenue " +
                 "FROM Ticket t " +
                 "WHERE t.FlightID = ?";
    List<Map<String, Object>> results = executeQuery(sql, flightID);

    if (!results.isEmpty() && results.get(0) != null) {
        Map<String, Object> reportData = results.get(0);
        reportData.putIfAbsent("numberOfTickets", 0L);
        reportData.putIfAbsent("totalFare", BigDecimal.ZERO);
        reportData.putIfAbsent("totalBookingFee", BigDecimal.ZERO);
        reportData.putIfAbsent("totalRevenue", BigDecimal.ZERO);
        return reportData;
    } else {
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("numberOfTickets", 0L);
        emptyResult.put("totalFare", BigDecimal.ZERO);
        emptyResult.put("totalBookingFee", BigDecimal.ZERO);
        emptyResult.put("totalRevenue", BigDecimal.ZERO);
        return emptyResult;
    }
}

/**
 * Calculates detailed revenue generated by a specific AirlineID.
 * Includes number of tickets, total fare, total booking fee, and total revenue.
 * @param airlineID The ID of the airline.
 * @return A Map containing revenue details, or a default map if no tickets/airline.
 */
public Map<String, Object> getRevenueByAirlineID(int airlineID) {
    String sql = "SELECT " +
                 "COUNT(*) as numberOfTickets, " +
                 "COALESCE(SUM(CAST(t.TicketFare AS DECIMAL(10,2))), 0.00) as totalFare, " +
                 "COALESCE(SUM(CAST(t.BookingFee AS DECIMAL(10,2))), 0.00) as totalBookingFee, " +
                 "COALESCE(SUM(CAST(t.TicketFare AS DECIMAL(10,2)) + CAST(t.BookingFee AS DECIMAL(10,2))), 0.00) as totalRevenue " +
                 "FROM Ticket t " +
                 "JOIN Flight f ON t.FlightID = f.FlightID " +
                 "WHERE f.AirlineID = ?";
    List<Map<String, Object>> results = executeQuery(sql, airlineID);

    if (!results.isEmpty() && results.get(0) != null) {
        Map<String, Object> reportData = results.get(0);
        reportData.putIfAbsent("numberOfTickets", 0L);
        reportData.putIfAbsent("totalFare", BigDecimal.ZERO);
        reportData.putIfAbsent("totalBookingFee", BigDecimal.ZERO);
        reportData.putIfAbsent("totalRevenue", BigDecimal.ZERO);
        return reportData;
    } else {
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("numberOfTickets", 0L);
        emptyResult.put("totalFare", BigDecimal.ZERO);
        emptyResult.put("totalBookingFee", BigDecimal.ZERO);
        emptyResult.put("totalRevenue", BigDecimal.ZERO);
        return emptyResult;
    }
}

/**
 * Calculates detailed revenue generated by a specific CustomerID.
 * Includes number of tickets, total fare, total booking fee, and total revenue.
 * @param customerID The ID of the customer.
 * @return A Map containing revenue details, or a default map if no tickets for the customer.
 */
public Map<String, Object> getRevenueByCustomerID(String customerID) {
    String sql = "SELECT " +
                 "COUNT(*) as numberOfTickets, " +
                 "COALESCE(SUM(CAST(t.TicketFare AS DECIMAL(10,2))), 0.00) as totalFare, " +
                 "COALESCE(SUM(CAST(t.BookingFee AS DECIMAL(10,2))), 0.00) as totalBookingFee, " +
                 "COALESCE(SUM(CAST(t.TicketFare AS DECIMAL(10,2)) + CAST(t.BookingFee AS DECIMAL(10,2))), 0.00) as totalRevenue " +
                 "FROM Ticket t " +
                 "WHERE t.CustomerID = ?";
    List<Map<String, Object>> results = executeQuery(sql, customerID);

    if (!results.isEmpty() && results.get(0) != null) {
        Map<String, Object> reportData = results.get(0);
        reportData.putIfAbsent("numberOfTickets", 0L);
        reportData.putIfAbsent("totalFare", BigDecimal.ZERO);
        reportData.putIfAbsent("totalBookingFee", BigDecimal.ZERO);
        reportData.putIfAbsent("totalRevenue", BigDecimal.ZERO);
        return reportData;
    } else {
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("numberOfTickets", 0L);
        emptyResult.put("totalFare", BigDecimal.ZERO);
        emptyResult.put("totalBookingFee", BigDecimal.ZERO);
        emptyResult.put("totalRevenue", BigDecimal.ZERO);
        return emptyResult;
    }
}

    /**
     * Helper method to get Airline details by ID (e.g., to fetch Name).
     * @param airlineID The ID of the airline.
     * @return A Map containing airline details, or null if not found.
     */
    public Map<String, Object> getAirlineByID(int airlineID) {
        String sql = "SELECT AirlineID, Name FROM Airline WHERE AirlineID = ?";
        List<Map<String, Object>> results = executeQuery(sql, airlineID);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

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
    
    /**
     * Gets the next available seat number for a flight
     */
    public int getNextSeatNumber(int flightID) {
        String sql = "SELECT COALESCE(MAX(SeatNumber), 0) + 1 as nextSeat " +
                    "FROM Ticket " +
                    "WHERE FlightID = ?";
        List<Map<String, Object>> results = executeQuery(sql, flightID);
        return ((Number) results.get(0).get("nextSeat")).intValue();
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

    public List<Map<String, Object>> getAllFlightsInFlightPlan(String flightPlanID) {
        System.out.println("DEBUG: Getting flights for FlightPlanID: " + flightPlanID);
        
        // First get the customer ID for this flight plan
        String customerSql = "SELECT CustomerID FROM FlightPlan WHERE FlightPlanID = ?";
        List<Map<String, Object>> customerResult = executeQuery(customerSql, flightPlanID);
        if (customerResult.isEmpty()) {
            System.out.println("DEBUG: No flight plan found with ID: " + flightPlanID);
            return new ArrayList<>();
        }
        String customerID = (String) customerResult.get(0).get("CustomerID");
        System.out.println("DEBUG: Found customer ID: " + customerID);
        
        String sql = "SELECT f.FlightID, f.FlightNumber, f.DepartTime, f.ArrivalTime, f.Duration, f.StandardFare, " +
                    "a.Name as airline_name, " +
                    "dep.Name as departure_airport, dep.City as departure_city, dep.Country as departure_country, " +
                    "arr.Name as arrival_airport, arr.City as arrival_city, arr.Country as arrival_country, " +
                    "its.Class, " +
                    "wl.RequestDateTime as waitlist_date, " +
                    "CASE " +
                    "    WHEN EXISTS (SELECT 1 FROM Ticket t WHERE t.FlightID = f.FlightID AND t.Class = its.Class AND t.CustomerID = ?) THEN 'Confirmed' " +
                    "    WHEN EXISTS (SELECT 1 FROM WaitingList wl2 WHERE wl2.FlightID = f.FlightID AND wl2.Class = its.Class AND wl2.CustomerID = ?) THEN 'Waitlisted' " +
                    "    ELSE 'Available' " +
                    "END as status " +
                    "FROM ItinerarySegment its " +
                    "JOIN Flight f ON its.FlightID = f.FlightID " +
                    "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                    "JOIN Airport dep ON f.FromAirportID = dep.AirportID " +
                    "JOIN Airport arr ON f.ToAirportID = arr.AirportID " +
                    "LEFT JOIN WaitingList wl ON wl.FlightID = f.FlightID AND wl.Class = its.Class " +
                    "WHERE its.FlightPlanID = ? " +
                    "ORDER BY its.SegmentNum";
        
        List<Map<String, Object>> results = executeQuery(sql, customerID, customerID, flightPlanID);
        System.out.println("DEBUG: Found " + results.size() + " flights in flight plan");
        return results;
    }
    
    /**
     * Checks if a flight has available seats in a specific class
     * @param flightID The flight to check
     * @param className The class to check (E, B, or F)
     * @return true if seats are available, false if full
     */
    public boolean hasAvailableSeats(int flightID, String className) {
        // First get the aircraft configuration for this flight
        String sql = "SELECT a.ClassConfigurations " +
                    "FROM Flight f " +
                    "JOIN Aircraft a ON f.AircraftID = a.AircraftID " +
                    "WHERE f.FlightID = ?";
        
        System.out.println("DEBUG: Checking seats for FlightID: " + flightID + ", Class: " + className);
        
        List<Map<String, Object>> results = executeQuery(sql, flightID);
        if (results.isEmpty()) {
            System.out.println("DEBUG: No aircraft configuration found for flight");
            return false;
        }
        String config = (String) results.get(0).get("ClassConfigurations");
        System.out.println("DEBUG: Aircraft configuration: " + config);
        
        // Map full class names to their single-letter codes
        Map<String, String> classNameToClassChar = new HashMap<>();
        classNameToClassChar.put("Economy", "E");
        classNameToClassChar.put("Business", "B");
        classNameToClassChar.put("First", "F");
        
        // Get the single-letter code for the class
        String classCode = classNameToClassChar.get(className);
        System.out.println("DEBUG: Converting class name '" + className + "' to code '" + classCode + "'");
        
        if (classCode == null) {
            System.out.println("DEBUG: Invalid class name: " + className);
            return false;
        }
        
        // Parse config string like "E:100,B:50,F:20"
        int maxSeats = 0;
        for (String part : config.split(",")) {
            String[] keyValue = part.split(":");
            if (keyValue[0].equals(classCode)) {
                maxSeats = Integer.parseInt(keyValue[1]);
                break;
            }
        }
        System.out.println("DEBUG: Maximum seats for class " + className + ": " + maxSeats);
        
        // Now count how many actual tickets are sold in this class
        sql = "SELECT COUNT(*) as taken " +
              "FROM Ticket " +
              "WHERE FlightID = ? AND Class = ?";
        
        results = executeQuery(sql, flightID, className);
        int takenSeats = ((Number) results.get(0).get("taken")).intValue();
        System.out.println("DEBUG: Currently taken seats: " + takenSeats);
        
        boolean hasSeats = takenSeats < maxSeats;
        System.out.println("DEBUG: Has available seats: " + hasSeats + " (" + takenSeats + " < " + maxSeats + ")");
        return hasSeats;
    }
    
    /**
     * Adds a customer to the waiting list for a flight
     * @param customerID The customer to add
     * @param flightID The flight to wait for
     * @param className The class they want
     * @return true if successfully added to waitlist
     */
    public boolean addToWaitlist(String customerID, int flightID, String className) {
        String sql = "INSERT INTO WaitingList (CustomerID, FlightID, Class, RequestDateTime) " +
                    "VALUES (?, ?, ?, NOW())";
        return executeUpdate(sql, customerID, flightID, className) > 0;
    }
    
    /**
     * Creates a ticket for a flight
     * @param customerID The customer buying the ticket
     * @param flightID The flight to book
     * @param className The class to book
     * @param ticketFare The fare for the ticket
     * @return true if ticket was created successfully
     */
    public boolean createTicket(String customerID, int flightID, String className, float ticketFare) {
        String sql = "INSERT INTO Ticket (CustomerID, FlightID, SeatNumber, Class, TicketFare, BookingFee, PurchaseDateTime) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        // Calculate booking fee based on class
        float bookingFee = ("F".equals(className)) ? 15.00f : 0.00f;
        
        // Get next available seat number
        int seatNumber = getNextSeatNumber(flightID);
        
        return executeUpdate(sql, customerID, flightID, seatNumber, className, ticketFare, bookingFee) > 0;
    }
    
    public boolean updateAircraft(String aircraftID,
            String airlineID,
            String model,
            int totalSeats,
            String config) {
String sql = """
UPDATE Aircraft
SET AirlineID           = ?,
Model               = ?,
TotalSeats          = ?,
ClassConfigurations = ?
WHERE AircraftID         = ?
""";

try (Connection c = getConnection();
PreparedStatement ps = c.prepareStatement(sql)) {

ps.setInt(1, Integer.parseInt(airlineID));
ps.setString(2, model);
ps.setInt(3, totalSeats);
ps.setString(4, config);
ps.setInt(5, Integer.parseInt(aircraftID));

return ps.executeUpdate() > 0;
} catch (SQLException e) {
e.printStackTrace();
return false;
}
}

	
	
	
	
	



	/**
	 * Cancels a ticket for a customer
	 * @param customerID The ID of the customer
	 * @param flightId The ID of the flight
	 * @param className The class of the ticket (B or F)
	 * @return true if the ticket was cancelled successfully, false otherwise
	 */
// 	public boolean cancelTicket(String customerID, int flightId, String className) {
// 		try {
// 			// First check if the ticket exists
// 			String checkQuery = "SELECT COUNT(*) FROM Ticket WHERE CustomerID = ? AND FlightID = ? AND Class = ?";
// 			Connection conn = getConnection();
// 			PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
// 			checkStmt.setString(1, customerID);
// 			checkStmt.setInt(2, flightId);
// 			checkStmt.setString(3, className);
// 			ResultSet rs = checkStmt.executeQuery();
			
// 			if (rs.next() && rs.getInt(1) > 0) {
// 				// Delete the ticket
// 				String deleteQuery = "DELETE FROM Ticket WHERE CustomerID = ? AND FlightID = ? AND Class = ?";
// 				PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
// 				deleteStmt.setString(1, customerID);
// 				deleteStmt.setInt(2, flightId);
// 				deleteStmt.setString(3, className);
				
// 				int rowsAffected = deleteStmt.executeUpdate();
// 				return rowsAffected > 0;
// 			}
// 			return false;
// 		} catch (SQLException e) {
// 			e.printStackTrace();
// 			return false;
// 		}
// 	}

	
	public List<Map<String,Object>> getWaitingListByFlight(int flightId) {
	    String sql = 
	      "SELECT wl.CustomerID, c.FirstName, c.LastName, " +
	      "       wl.Class, wl.RequestDateTime " +
	      "  FROM WaitingList wl " +
	      "  JOIN Customer c ON wl.CustomerID = c.CustomerID " +
	      " WHERE wl.FlightID = ?";
	    return executeQuery(sql, flightId);
	}
	
	
	/**
	 * Make a reservation (ticket) for a customer.
	 */
	public boolean createReservation(
	    String     customerID,
	    int        flightID,
	    String     travelClass,
	    BigDecimal fare
	) {
	    String sql = """
	      INSERT INTO Ticket
	        (CustomerID, FlightID, SeatNumber, Class, TicketFare, BookingFee, PurchaseDateTime)
	      VALUES (?,?,?,?,?,?,NOW())
	    """;
	    // booking fee only for First
	    BigDecimal bookingFee = "First".equals(travelClass)
	        ? new BigDecimal("15.00")
	        : BigDecimal.ZERO;

	    return executeUpdate(sql,
	        customerID,
	        flightID,
	        getNextSeatNumber(flightID),
	        travelClass,
	        fare,
	        bookingFee
	    ) > 0;
	}

	/**
	 * List all reservations for a given customer.
	 */
	public List<Map<String,Object>> getReservationsForCustomer(String custId) {
	    String sql = """
	      SELECT
	        t.FlightID,
	        t.SeatNumber,
	        t.Class,
	        t.TicketFare,
	        t.BookingFee,
	        t.PurchaseDateTime,
	        f.FlightNumber,
	        f.FromAirportID,
	        f.ToAirportID,
	        a.Name AS AirlineName
	      FROM Ticket t
	      JOIN Flight  f ON t.FlightID = f.FlightID
	      JOIN Airline a ON f.AirlineID = a.AirlineID
	      WHERE t.CustomerID = ?
	      ORDER BY t.PurchaseDateTime DESC
	    """;
	    return executeQuery(sql, custId);
	}

	/**
	 * Update an existing reservation.  For simplicity we delete+re-insert
	 * (so seat numbers get re-assigned), but you could do a full UPDATE.
	 */
	public boolean updateReservation(
	    String     customerID,
	    int        oldFlightID,
	    int        oldSeatNumber,
	    int        newFlightID,
	    String     newClass,
	    BigDecimal newFare
	) {
	    // delete old
	    String del = "DELETE FROM Ticket WHERE CustomerID=? AND FlightID=? AND SeatNumber=?";
	    if (executeUpdate(del, customerID, oldFlightID, oldSeatNumber) == 0) return false;
	    // insert new
	    return createReservation(customerID, newFlightID, newClass, newFare);
	}



    
    
    /**
     * Fetch one aircraft by its primary key.
     */
    public Map<String,Object> getAircraftByID(String aircraftID) {
        String sql =
                  "SELECT AircraftID, AirlineID, TotalSeats, Model, " +
                  "       ClassConfigurations AS Config " +
                  "  FROM Aircraft " +
                  " WHERE AircraftID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aircraftID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("AircraftID", rs.getString("AircraftID"));
                    m.put("AirlineID",  rs.getInt   ("AirlineID"));
                    m.put("Model",      rs.getString("Model"));
                    m.put("TotalSeats", rs.getInt   ("TotalSeats"));
                    m.put("Config",     rs.getString("Config"));
                    return m;
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace();    
        }
        return null;
    }
    
    
    // Change signature to accept a new int aircraftId
    public boolean updateFlight(int flightId,
                                int flightNumber,
                                int airlineId,
                                String fromAirport,
                                String toAirport,
                                LocalDateTime depart,
                                LocalDateTime arrive,
                                String operatingDays,
                                int aircraftId)               // NEW
    {
        String sql =
            "UPDATE Flight SET "
          + " FlightNumber  = ?,"
          + " AirlineID     = ?,"
          + " FromAirportID = ?,"
          + " ToAirportID   = ?,"
          + " DepartTime    = ?,"
          + " ArrivalTime   = ?,"
          + " OperatingDays = ?,"
          + " AircraftID    = ? "                  // NEW
          + "WHERE FlightID = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, flightNumber);
            ps.setInt(2, airlineId);
            ps.setString(3, fromAirport);
            ps.setString(4, toAirport);
            ps.setObject(5, depart);
            ps.setObject(6, arrive);
            ps.setString(7, operatingDays);
            ps.setInt(8, aircraftId);            // NEW
            ps.setInt(9, flightId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public List<Map<String, Object>> getAllAircrafts() {
        String sql = "SELECT AircraftID, AirlineID, Model, TotalSeats, ClassConfigurations FROM Aircraft";
        return executeQuery(sql);
    }

    /**
     * Cancels a ticket for a customer
     * @param customerID The ID of the customer
     * @param flightId The ID of the flight
     * @param className The class of the ticket (B or F)
     * @return true if the ticket was cancelled successfully, false otherwise
     */
    public boolean cancelTicket(String customerID, int flightId, String className) {
        try {
            // First check if the ticket exists
            String checkQuery = "SELECT COUNT(*) FROM Ticket WHERE CustomerID = ? AND FlightID = ? AND Class = ?";
            Connection conn = getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, customerID);
            checkStmt.setInt(2, flightId);
            checkStmt.setString(3, className);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Delete the ticket
                String deleteQuery = "DELETE FROM Ticket WHERE CustomerID = ? AND FlightID = ? AND Class = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setString(1, customerID);
                deleteStmt.setInt(2, flightId);
                deleteStmt.setString(3, className);
                
                int rowsAffected = deleteStmt.executeUpdate();
                return rowsAffected > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the status of an itinerary segment
     * @param flightPlanID The ID of the flight plan
     * @param flightId The ID of the flight
     * @param className The class of the ticket
     * @param status The new status (e.g., "Confirmed", "Waitlisted")
     * @return true if the update was successful
     */
    public boolean updateItinerarySegmentStatus(String flightPlanID, int flightId, String className, String status) {
        String sql = "UPDATE ItinerarySegment SET Status = ? WHERE FlightPlanID = ? AND FlightID = ? AND Class = ?";
        try {
            return executeUpdate(sql, status, flightPlanID, flightId, className) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getPastFlights(String customerID) {
        String sql = "SELECT f.FlightID, f.FlightNumber, f.DepartTime, f.ArrivalTime, f.Duration, f.StandardFare, " +
                    "a.Name as airline_name, " +
                    "dep.Name as departure_airport, dep.City as departure_city, dep.Country as departure_country, " +
                    "arr.Name as arrival_airport, arr.City as arrival_city, arr.Country as arrival_country, " +
                    "t.Class " +
                    "FROM Ticket t " +
                    "JOIN Flight f ON t.FlightID = f.FlightID " +
                    "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                    "JOIN Airport dep ON f.FromAirportID = dep.AirportID " +
                    "JOIN Airport arr ON f.ToAirportID = arr.AirportID " +
                    "WHERE f.DepartTime < NOW() " + 
                    "AND t.CustomerID = ? " + 
                    "ORDER BY f.DepartTime DESC";
        return executeQuery(sql, customerID);
    }

    public List<Map<String, Object>> getAllFlights(String customerID) {
        String sql = "SELECT f.FlightID, f.FlightNumber, f.DepartTime, f.ArrivalTime, f.Duration, f.StandardFare, " +
                    "a.Name as airline_name, " +
                    "dep.Name as departure_airport, dep.City as departure_city, dep.Country as departure_country, " +
                    "arr.Name as arrival_airport, arr.City as arrival_city, arr.Country as arrival_country, " +
                    "t.Class " +
                    "FROM Ticket t " +
                    "JOIN Flight f ON t.FlightID = f.FlightID " +
                    "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                    "JOIN Airport dep ON f.FromAirportID = dep.AirportID " +
                    "JOIN Airport arr ON f.ToAirportID = arr.AirportID " +
                    "WHERE f.DepartTime >= NOW() " +
                    "AND t.CustomerID = ? " +
                    "ORDER BY f.DepartTime ASC";
        return executeQuery(sql, customerID);
    }

    public List<Map<String, Object>> getWaitlistedFlights(String customerID) {
        String sql = "SELECT w.CustomerID, w.FlightID, w.Class, w.RequestDateTime, " +
                    "f.FlightNumber, f.DepartTime, f.ArrivalTime, f.Duration, f.StandardFare, " +
                    "a.Name as airline_name, " +
                    "dep.Name as departure_airport, dep.City as departure_city, dep.Country as departure_country, " +
                    "arr.Name as arrival_airport, arr.City as arrival_city, arr.Country as arrival_country " +
                    "FROM WaitingList w " +
                    "JOIN Flight f ON w.FlightID = f.FlightID " +
                    "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                    "JOIN Airport dep ON f.FromAirportID = dep.AirportID " +
                    "JOIN Airport arr ON f.ToAirportID = arr.AirportID " +
                    "WHERE w.CustomerID = ? " +
                    "ORDER BY w.RequestDateTime ASC";
        return executeQuery(sql, customerID);
    }
    
    /**
     * Return every upcoming flight (no JOIN on Ticket).
     */
    public List<Map<String,Object>> getAllFlightOptions() {
        String sql =
          "SELECT FlightID, FlightNumber, FromAirportID, ToAirportID, DepartTime "
        + "FROM Flight "
        + "WHERE DepartTime >= NOW() "
        + "ORDER BY DepartTime";
        return executeQuery(sql);
    }


    public int getAvailableSeats(int flightId) {
        String sql = "SELECT " +
                     "  a.TotalSeats - COUNT(t.SeatNumber) as available_seats " +
                     "FROM Flight f " +
                     "JOIN Aircraft a ON f.AircraftID = a.AircraftID " +
                     "LEFT JOIN Ticket t ON f.FlightID = t.FlightID " +
                     "WHERE f.FlightID = ? " +
                     "GROUP BY a.TotalSeats";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, flightId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("available_seats");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}