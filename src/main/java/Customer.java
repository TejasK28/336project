

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class Customer
 */
@WebServlet({"/addRTFlightToPlan", "/addOWFlightToPlan", "/addLayFlightToPlan"})
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
			String outgoingFID = request.getParameter("outboundFlight");
			
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
			String outgoingFID = request.getParameter("outboundFlight");
			
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

		doGet(request, response);
	}

}
