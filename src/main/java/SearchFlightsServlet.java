
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/SearchFlightsServlet")
public class SearchFlightsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MySQL db;

	@Override
	public void init() throws ServletException {
		super.init();
		db = new MySQL();
		db.loadDriver("com.mysql.jdbc.Driver");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// 1) airports
		String sqlAirports = "SELECT DISTINCT FromAirportID AS AirportID FROM Flight " + " UNION "
				+ "SELECT DISTINCT ToAirportID      FROM Flight";
		List<Map<String, Object>> airports = db.executeQuery(sqlAirports);

		// 2) departure dates
		String sqlDates = "SELECT DISTINCT DATE(DepartTime) AS departDate " + "  FROM Flight " + " ORDER BY departDate";
		List<Map<String, Object>> dates = db.executeQuery(sqlDates);

		req.setAttribute("airports", airports);
		req.setAttribute("dates", dates);
		req.getRequestDispatcher("/search.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    // 1) Pull parameters from the form
	    String from       = request.getParameter("fromAirport");
	    String to         = request.getParameter("toAirport");
	    LocalDate depart  = LocalDate.parse(request.getParameter("departDate"));
	    String tripType   = request.getParameter("tripType");     // "oneway" or "roundtrip"
	    LocalDate retDate = null;
	    boolean flexible = "true".equals(request.getParameter("flexible"));
	    if ("roundtrip".equals(tripType)) {
	        retDate = LocalDate.parse(request.getParameter("returnDate"));
	    }
	    
	    System.out.printf(
	    	    "params → from=%s to=%s depart=%s tripType=%s retDate=%s%n",
	    	    from, to, depart, tripType, retDate
	    	);
	    
	    List<Map<String, Object>> allDeparted = null;
	    List<Map<String, Object>> allStops = null;
	    
	    List<Map<String, Object>> allDepartedRet = null;
	    List<Map<String, Object>> allStopsRet = null;
	    
	    if ("roundtrip".equals(tripType)) {
	    	
	    	try {
				allDeparted = flexible ? 
						db.getDirectFlightsFlexible(from, to, depart, depart) 
						: db.getDirectFlights(from, to, depart, depart);
				allStops = flexible ? 
						db.getOneStopFlightsFlexible(from, to, depart, depart) 
						: db.getOneStopFlights(from, to, depart, depart);
				allDepartedRet = flexible ? 
						db.getDirectFlightsFlexible(to, from, retDate, retDate) 
						: db.getDirectFlights(from, to, retDate, retDate);
				allStopsRet = flexible ? 
						db.getOneStopFlightsFlexible(to, from, retDate, retDate) 
						: db.getOneStopFlights(from, to, retDate, retDate);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    else {
	    	try {
				allDeparted = flexible ? 
						db.getDirectFlightsFlexible(from, to, depart, depart) 
						: db.getDirectFlights(from, to, depart, depart);
				allStops = flexible ? 
						db.getOneStopFlightsFlexible(from, to, depart, depart) 
						: db.getOneStopFlights(from, to, depart, depart);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    List<Map<String, Object>> outbound = new ArrayList();
	    if (allDeparted != null)
			outbound.addAll(allDeparted);
	    if (allStops != null)
			outbound.addAll(allStops);
	    
	    List<Map<String, Object>> inbound = new ArrayList();
	    if (allDepartedRet != null) 
			inbound.addAll(allDepartedRet);
	    if (allStopsRet != null)
			inbound.addAll(allStopsRet);
	    
	    request.setAttribute("outbound", outbound);
	    request.setAttribute("inbound", inbound);

	    request.getRequestDispatcher("/results.jsp").forward(request, response);
	}
}