import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class Customer
 */
@WebServlet({"/addRTFlightToPlan", "/addOWFlightToPlan", "/addLayFlightToPlan", "/reserveFlights",
	"/ViewFlightPlan/*"})
public class Customer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Customer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQL db = new MySQL();
		String pathInfo = request.getPathInfo();
		if ("/reserveFlights".equals(request.getServletPath())) {
		    List<String> custFlightPlan = (List<String>) request.getSession().getAttribute("CustFlightPlan");
		    if (custFlightPlan == null || custFlightPlan.isEmpty()) {
		    	request.setAttribute("EmptyFlightPlan", true);
		    	response.sendRedirect(request.getContextPath() + "/Home");
		    	return;
		    }
		    
		    List<Map<String, Object>> flights = new ArrayList<>();
		    
		    for (String flightID : custFlightPlan) {
		    	flights.add(db.getFlightByFID(flightID));
		    }
		    
		    request.setAttribute("flights", flights);
		    request.getRequestDispatcher("/MakeRes.jsp").forward(request, response);
		}
		else if ("/ViewFlightPlan".equals(request.getServletPath())) {
			// Extract the flight plan ID from the path
			String flightPlanID = pathInfo.substring(1); // Remove the leading slash
			System.out.println("Viewing flight plan ID: " + flightPlanID);
			
			// Get all flights in the flight plan
			List<Map<String, Object>> flights = db.getAllFlightsInFlightPlan(flightPlanID);
			System.out.println("Found " + (flights != null ? flights.size() : 0) + " flights in plan");
			
			// Set the flights as an attribute for the JSP
			request.setAttribute("flights", flights);
			request.setAttribute("flightPlanID", flightPlanID);
			
			// Forward to the view flight plan page
			request.getRequestDispatcher("/ViewFlightPlan.jsp").forward(request, response);
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MySQL db = new MySQL();
		if ("/addFlightToPlan".equals(request.getServletPath())) {
		    String flightID = request.getParameter("flightID");
		    HttpSession session = request.getSession();

		    // 1) Initialize the flight-plan list if needed
		    @SuppressWarnings("unchecked")
		    List<String> custFlightPlan = (List<String>) session.getAttribute("CustFlightPlan");
		    if (custFlightPlan == null) {
		        custFlightPlan = new ArrayList<>();
		        session.setAttribute("CustFlightPlan", custFlightPlan);
		    }

		    // 2) Add the new flight
		    custFlightPlan.add(flightID);
		    // (no need to session.setAttribute again since it's the same list object)

		    // 3) Set a "flash" flag so Home.jsp can show a "Flight added!" message
		    request.setAttribute("FlightAdded", true);

		    // 4) Redirect back to Home
		    response.sendRedirect(request.getContextPath() + "/Home");
		    return;
		}
		else if ("/addRTFlightToPlan".equals(request.getServletPath())) {
			String outgoingFID = request.getParameter("outboundFlight");
			String inboundFID = request.getParameter("inboundFlight");
			
			HttpSession session = request.getSession();

		    // 1) Initialize the flight-plan list if needed
		    @SuppressWarnings("unchecked")
		    List<String> custFlightPlan = (List<String>) session.getAttribute("CustFlightPlan");
		    if (custFlightPlan == null) {
		        custFlightPlan = new ArrayList<>();
		        session.setAttribute("CustFlightPlan", custFlightPlan);
		    }

		    // 2) Add the new flight
		    custFlightPlan.add(outgoingFID);
		    custFlightPlan.add(inboundFID);
		    // (no need to session.setAttribute again since it's the same list object)

		    // 3) Set a "flash" flag so Home.jsp can show a "Flight added!" message
		    request.setAttribute("FlightAdded", true);

		    // 4) Redirect back to Home
		    response.sendRedirect(request.getContextPath() + "/Home");
		    return;
		}
		else if ("/addOWFlightToPlan".equals(request.getServletPath())) {
			String outgoingFID = request.getParameter("leg1ID");
			
			HttpSession session = request.getSession();

		    // 1) Initialize the flight-plan list if needed
		    @SuppressWarnings("unchecked")
		    List<String> custFlightPlan = (List<String>) session.getAttribute("CustFlightPlan");
		    if (custFlightPlan == null) {
		        custFlightPlan = new ArrayList<>();
		        session.setAttribute("CustFlightPlan", custFlightPlan);
		    }

		    // 2) Add the new flight
		    custFlightPlan.add(outgoingFID);
		    // (no need to session.setAttribute again since it's the same list object)

		    // 3) Set a "flash" flag so Home.jsp can show a "Flight added!" message
		    request.setAttribute("FlightAdded", true);

		    // 4) Redirect back to Home
		    response.sendRedirect(request.getContextPath() + "/Home");
		    return;
		}
		else if ("/addLayFlightToPlan".equals(request.getServletPath())) {
			String leg1ID = request.getParameter("leg1ID");
			String leg2ID = request.getParameter("leg2ID");
			
			HttpSession session = request.getSession();

		    // 1) Initialize the flight-plan list if needed
		    @SuppressWarnings("unchecked")
		    List<String> custFlightPlan = (List<String>) session.getAttribute("CustFlightPlan");
		    if (custFlightPlan == null) {
		        custFlightPlan = new ArrayList<>();
		        session.setAttribute("CustFlightPlan", custFlightPlan);
		    }

		    // 2) Add the new flight
		    custFlightPlan.add(leg1ID);
		    custFlightPlan.add(leg2ID);
		    // (no need to session.setAttribute again since it's the same list object)

		    // 3) Set a "flash" flag so Home.jsp can show a "Flight added!" message
		    request.setAttribute("FlightAdded", true);

		    // 4) Redirect back to Home
		    response.sendRedirect(request.getContextPath() + "/Home");
		    return;
		}
		else if ("/reserveFlights".equals(request.getServletPath())) {
		    List<String> custFlightPlan = (List<String>) request.getSession().getAttribute("CustFlightPlan");
		    
		    if (custFlightPlan == null || custFlightPlan.isEmpty()) {
		    	request.setAttribute("PlanCreationFailed", true);
		    	response.sendRedirect(request.getContextPath() + "/Home");
		    	return;
		    }
		    
		    Map<String, String> flightIDClassMapping = new HashMap<>();
		    for (int i = 0; i < custFlightPlan.size(); i++) {
		    	int flightID = Integer.parseInt(request.getParameter("flight"+(i+1)+"Id"));
		    	flightIDClassMapping.put(custFlightPlan.get(i), request.getParameter("class_"+flightID));
		    }
		    
		    int fpID = 0;
		    String cuID = (String) request.getSession().getAttribute("uname");
		    int totalDur = Integer.valueOf(request.getParameter("totalDur"));
		    float totalCost = Float.valueOf(request.getParameter("totalCost"));
		    
		    try {
				fpID = db.createFlightPlan(cuID, totalDur, totalCost);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		   
		    for (int i = 0; i < custFlightPlan.size(); i++) {
				int flightID = Integer.parseInt(custFlightPlan.get(i));
				
				String classString = flightIDClassMapping.get("" + flightID);
				System.out.println(classString);
				db.insertItinerarySegment(fpID, i, classString, flightID);
		    }
		    
		    request.setAttribute("FPSuccess", true);
		    request.getSession().removeAttribute("CustFlightPlan");
		    response.sendRedirect(request.getContextPath() + "/Home");
		    
		}

	}

}
