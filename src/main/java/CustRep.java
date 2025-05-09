
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class CustRep
 */
@WebServlet({"/CustRep/*", "/createFlight"})
public class CustRep extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustRep() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MySQL r = new MySQL();
		String pathInfo = request.getPathInfo();
		if ("/CustRep".equals(request.getServletPath()) && pathInfo == null) {
			// Grab Airport data from database
			List<Map<String, Object>> airports = r.getAllAirports();
			request.setAttribute("airports", airports);
			
			// Grab Airline data from database
			List<Map<String, Object>> airlines = r.getAllAirlines();
			request.setAttribute("airlines", airlines);
			
			
			// Grab Flight data from database
			List<Map<String, Object>> flights = r.getAllFlights();
			request.setAttribute("flights", flights);
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("CustRepPortal.jsp");
    		dispatcher.forward(request, response);
		}
		else if ("/CustRep".equals(request.getServletPath()) && pathInfo.contains("/airport")) {
			if (pathInfo != null) {
				System.out.println("It got here!");
				String airport_id = pathInfo.split("/")[2];
				request.setAttribute("airport_flights", r.getFlightsAtAirport(airport_id));
//				response.sendRedirect("Airport.jsp");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/Airport.jsp");
				dispatcher.forward(request, response);
				return;
			}
		}
		else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
    		return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    MySQL db = new MySQL();
	    if ("/createFlight".equals(request.getServletPath())) {
	        // 1. Read form parameters
	        String flightNumberStr = request.getParameter("FlightNumber");
	        String airlineId       = request.getParameter("AirlineID");
	        String fromAirportId   = request.getParameter("FromAirportID");
	        String toAirportId     = request.getParameter("ToAirportID");
	        String departTimeStr   = request.getParameter("DepartTime");   // e.g. "2025-05-10T14:30"
	        String arrivalTimeStr  = request.getParameter("ArrivalTime");
	        String operatingDays   = request.getParameter("OperatingDays");

	        // 2. Basic validation
	        if (flightNumberStr == null || airlineId == null
	                || fromAirportId == null || toAirportId == null
	                || departTimeStr == null || arrivalTimeStr == null
	                || fromAirportId.equals(toAirportId)) {
	            request.setAttribute("error", "All fields required, airports must differ.");
	            request.getRequestDispatcher("/WEB-INF/jsp/createFlight.jsp")
	                   .forward(request, response);
	            return;
	        }

	        int flightNumber = Integer.parseInt(flightNumberStr);
	        int aId          = Integer.parseInt(airlineId);

	        // 3. Convert datetime-local to Timestamp
	        java.sql.Timestamp departTimestamp = java.sql.Timestamp.valueOf(
	            departTimeStr.replace('T', ' ') + ":00"
	        );
	        java.sql.Timestamp arrivalTimestamp = java.sql.Timestamp.valueOf(
	            arrivalTimeStr.replace('T', ' ') + ":00"
	        );

	        // 4. Insert into database (now including AirlineID)
	        String sql = "INSERT INTO Flight "
	                   + "(FlightNumber, AirlineID, FromAirportID, ToAirportID, DepartTime, ArrivalTime, OperatingDays) "
	                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

	        try (Connection con = db.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setInt(1, flightNumber);
	            ps.setInt(2, aId);
	            ps.setString(3, fromAirportId);
	            ps.setString(4, toAirportId);
	            ps.setTimestamp(5, departTimestamp);
	            ps.setTimestamp(6, arrivalTimestamp);
	            ps.setString(7, operatingDays);

	            int inserted = ps.executeUpdate();
	            if (inserted > 0) {
	                // success
	                response.sendRedirect(request.getContextPath() + "/CustRep");
	                return;
	            } else {
	                request.setAttribute("error", "Failed to create flight.");
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	            request.setAttribute("error", "Database error: " + e.getMessage());
	        }

	        // failure: back to form
	        request.getRequestDispatcher("/CustRep").forward(request, response);
	    }
	}

}
