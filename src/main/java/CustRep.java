
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class CustRep
 */
@WebServlet({ "/CustRep/*", "/createFlight", "/createAircraft", 
	"/createAirport", "/deleteFlight", "/deleteAirport",
		"/editFlight", "/editAirport", "/deleteAircraft"})
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Ensure authenticated account
		if (request.getSession().getAttribute("authenticated") == null) {
			response.sendRedirect(request.getContextPath() + "/Home");
			return;
		}

		MySQL r = new MySQL();
		String pathInfo = request.getPathInfo();
		if ("/CustRep".equals(request.getServletPath()) && pathInfo == null) {
			// Grab Airport data from database
			List<Map<String, Object>> airports = r.getAllAirports();
			request.setAttribute("airports", airports);
			
			for (Map<String, Object> airport : airports) {
				airport.put("numArriving", r.getNumArrivingFlightsByAirportID((String) airport.get("AirportID")));
				airport.put("numDeparting", r.getNumDepartingFlightByAirportID((String) airport.get("AirportID")));
			}

			// Grab Airline data from database
			List<Map<String, Object>> airlines = r.getAllAirlines();
			request.setAttribute("airlines", airlines);

			// Grab Flight data from database
			List<Map<String, Object>> flights = r.getAllFlights();
			request.setAttribute("flights", flights);

			RequestDispatcher dispatcher = request.getRequestDispatcher("CustRepPortal.jsp");
			dispatcher.forward(request, response);
		} else if ("/CustRep".equals(request.getServletPath()) && pathInfo.contains("/airport")) {
			if (pathInfo != null) {
				System.out.println("It got here!");
				String airport_id = pathInfo.split("/")[2];
				request.setAttribute("airport_flights", r.getFlightsAtAirport(airport_id));
//				response.sendRedirect("Airport.jsp");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/Airport.jsp");
				dispatcher.forward(request, response);
				return;
			}
		} else if ("/CustRep".equals(request.getServletPath()) && pathInfo.contains("/editFlight")) {
			String fid = request.getParameter("flightId");
			request.setAttribute("flight", r.getFlightByFID(fid));

			// Grab Airport data from database
			List<Map<String, Object>> airports = r.getAllAirports();
			request.setAttribute("airports", airports);

			// Grab Airline data from database
			List<Map<String, Object>> airlines = r.getAllAirlines();
			request.setAttribute("airlines", airlines);

			request.getRequestDispatcher("/EditFlight.jsp").forward(request, response);
			return;
		} else if ("/CustRep".equals(request.getServletPath()) && pathInfo.contains("/editAirport")) {
			String aid = request.getParameter("airportID");
			request.setAttribute("airport", r.getAirportByID(aid));

			request.getRequestDispatcher("/EditAirport.jsp").forward(request, response);
			return;
		} else if ("/CustRep".equals(request.getServletPath()) && pathInfo.contains("/airline")) {
			String airlineID = request.getParameter("airlineId");
			request.setAttribute("ownedAircrafts", r.getOwnedAircraftsByAirlineID(airlineID));

			request.getRequestDispatcher("/viewAircrafts.jsp").forward(request, response);
			return;
		
		} else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed on this route.");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MySQL db = new MySQL();
		if ("/createFlight".equals(request.getServletPath())) {
			// 1. Read form parameters
			String flightNumberStr = request.getParameter("FlightNumber");
			String airlineId = request.getParameter("AirlineID");
			String fromAirportId = request.getParameter("FromAirportID");
			String toAirportId = request.getParameter("ToAirportID");
			String departTimeStr = request.getParameter("DepartTime");
			String arrivalTimeStr = request.getParameter("ArrivalTime");
			String operatingDays = request.getParameter("OperatingDays");

			// 2. Basic validation
			if (flightNumberStr == null || airlineId == null || fromAirportId == null || toAirportId == null
					|| departTimeStr == null || arrivalTimeStr == null || fromAirportId.equals(toAirportId)) {
				request.setAttribute("error", "All fields required, airports must differ.");
				request.getRequestDispatcher("/WEB-INF/jsp/createFlight.jsp").forward(request, response);
				return;
			}

			int flightNumber = Integer.parseInt(flightNumberStr);
			int aId = Integer.parseInt(airlineId);

			// 3. Convert datetime-local to Timestamp
			java.sql.Timestamp departTimestamp = java.sql.Timestamp.valueOf(departTimeStr.replace('T', ' ') + ":00");
			java.sql.Timestamp arrivalTimestamp = java.sql.Timestamp.valueOf(arrivalTimeStr.replace('T', ' ') + ":00");

			// 4. Insert into database (now including AirlineID)
			String sql = "INSERT INTO Flight "
					+ "(FlightNumber, AirlineID, FromAirportID, ToAirportID, DepartTime, ArrivalTime, OperatingDays) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

			try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
		} else if ("/createAircraft".equals(request.getServletPath())) {
			String airlineId = request.getParameter("AirlineID");
			String model = request.getParameter("Model");
			String totalSeatsStr = request.getParameter("TotalSeats");
			String ecoStr = request.getParameter("EconomySeats");
			String bizStr = request.getParameter("BusinessSeats");
			String firstStr = request.getParameter("FirstClassSeats");

			String configStr = "";

			// parse & validate
			int total = Integer.parseInt(totalSeatsStr);
			int eco = Integer.parseInt(ecoStr);
			int biz = Integer.parseInt(bizStr);
			int first = Integer.parseInt(firstStr);

			if (total <= 0 || eco <= 0 || biz <= 0 || first <= 0 || total != eco + biz + first) {
				request.setAttribute("createAircraftError", "Error in seat configuration!");
				request.getRequestDispatcher("/CustRep").forward(request, response);
				return;
			} else {
				configStr = "E:" + eco + "," + "B:" + biz + "," + "F:" + first;
			}

			System.out.println(configStr);

			if (!db.addAircraft(airlineId, model, total, configStr)) {
				request.setAttribute("createAircraftError", "Database error!");
				request.getRequestDispatcher("/CustRep").forward(request, response);
				return;
			}

			response.sendRedirect(request.getContextPath() + "/CustRep");
			return;
		} else if ("/createAirport".equals(request.getServletPath())) {
			String airportId = request.getParameter("AirportID");
			String name = request.getParameter("Name");
			String city = request.getParameter("City");
			String country = request.getParameter("Country");

			if (!db.addAirport(airportId, name, city, country)) {
				System.out.println("Database error in Airport insertion!");
				request.setAttribute("createAirportError", "Database error!");
				request.getRequestDispatcher("/CustRep").forward(request, response);
				return;
			}
			response.sendRedirect(request.getContextPath() + "/CustRep");
			return;
		} else if ("/deleteFlight".equals(request.getServletPath())) {
			String flightId = request.getParameter("flightId");
			String referer = request.getHeader("Referer");

			if (!db.deleteFlightByFID(flightId)) {
				request.setAttribute("deleteFlightFailed", "Deleting the flight failed!");
			}
			response.sendRedirect(referer);
			return;
		} else if ("/editFlight".equals(request.getServletPath())) {
			String flightIdStr = request.getParameter("FlightID");
			String flightNumStr = request.getParameter("FlightNumber");
			String airlineIdStr = request.getParameter("AirlineID");
			String fromAirportId = request.getParameter("FromAirportID");
			String toAirportId = request.getParameter("ToAirportID");
			String departTimeStr = request.getParameter("DepartTime"); // "2025-05-10T14:30"
			String arrivalTimeStr = request.getParameter("ArrivalTime");
			String operatingDays = request.getParameter("OperatingDays");

			// 2. Basic validation (you can expand)
			if (flightIdStr == null || flightNumStr == null || airlineIdStr == null || fromAirportId == null
					|| toAirportId == null || departTimeStr == null || arrivalTimeStr == null
					|| fromAirportId.equals(toAirportId)) {
				request.setAttribute("error", "All fields required and airports must differ.");
				// re‐forward back to edit form with the same FlightID
				request.getRequestDispatcher("/EditFlight.jsp").forward(request, response);
				return;
			}

			// 3. Parse values
			int flightId = Integer.parseInt(flightIdStr);
			int flightNum = Integer.parseInt(flightNumStr);
			int airlineId = Integer.parseInt(airlineIdStr);

			// Convert to java.time.LocalDateTime or java.sql.Timestamp depending on your
			// DAO
			LocalDateTime depart = LocalDateTime.parse(departTimeStr);
			LocalDateTime arrive = LocalDateTime.parse(arrivalTimeStr);

			// 4. Update database
			try {
				// Assume you add this method to your MySQL helper:
				// public boolean updateFlight(int flightId, int flightNum, int airlineId,
				// String fromAirport, String toAirport,
				// LocalDateTime depart, LocalDateTime arrive, String operatingDays)
				boolean ok = db.updateFlight(flightId, flightNum, airlineId, fromAirportId, toAirportId, depart, arrive,
						operatingDays);

				if (!ok) {
					request.setAttribute("error", "Update failed.");
					request.getRequestDispatcher("/EditFlight.jsp").forward(request, response);
					return;
				}
			} catch (Exception e) {
				throw new ServletException("Error updating flight", e);
			}

			// 5. Redirect back to wherever they came from
			String referer = request.getHeader("Referer");
			if (referer != null && !referer.isEmpty()) {
				response.sendRedirect(referer);
			} else {
				response.sendRedirect(request.getContextPath() + "/CustRep");
			}
			return;
		}
		else if ("/editAirport".equals(request.getServletPath())) {
		    // 1. Read parameters
			String oldAID = request.getParameter("originalAID");
		    String newAID   = request.getParameter("identifierCode");
		    String name         = request.getParameter("Name");
		    String city         = request.getParameter("City");
		    String country      = request.getParameter("Country");

		    // 2. Basic validation
		    if (oldAID == null || newAID == null || name == null || city == null || country == null
		        || oldAID.isBlank() || newAID.isBlank() || name.isBlank() || city.isBlank() || country.isBlank()) {
		        request.setAttribute("error", "All fields are required.");
		        // re‐forward back to edit form; ensure the airport map is still set
		        // (you may need to re-fetch it from DB by originalId)
		        request.setAttribute("airport", db.getAirportByID(oldAID));
		        request.getRequestDispatcher("/EditAirport.jsp").forward(request, response);
		        return;
		    }

		    // 3. Perform update
		    try {
		        // signature: boolean updateAirport(String oldId, String newId, String name, String city, String country)
		        // If you don't allow changing the PK, pass originalId twice
		        boolean ok = db.updateAirport(newAID, name, city, country, oldAID);

		        if (!ok) {
					request.setAttribute("airport", db.getAirportByID(oldAID));
		            request.setAttribute("error", "Update failed.");
		            request.getRequestDispatcher("/EditAirport.jsp").forward(request, response);
		            return;
		        }

		    } catch (Exception e) {
		        throw new ServletException("Error updating airport", e);
		    }

		    // 4. Redirect back
			response.sendRedirect(request.getContextPath() + "/CustRep");
		    return;
		}
		else if ("/deleteAirport".equals(request.getServletPath())) {
			String aID = request.getParameter("airportID");
			if (!db.deleteAirport(aID)) {
				request.setAttribute("airportDeleteError", "Error when deleting airport!");
				response.sendRedirect(request.getContextPath() + "/CustRep");
				return;
			}
			else {
				response.sendRedirect(request.getContextPath() + "/CustRep");
			}
		}
		else if ("/deleteAircraft".equals(request.getServletPath())) {
			String acID = request.getParameter("aircraftID");
			String referer = request.getHeader("Referer");
			if (!db.deleteAircraft(acID)) {
				request.setAttribute("aircraftDeleteError", "Error when deleting aircraft!");
				response.sendRedirect(referer);
				return;
			}
			else {
				response.sendRedirect(referer);
			}
		}

	}

}
