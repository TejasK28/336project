import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal; // For BigDecimal results from SUM

@WebServlet("/RevenueReportServlet")
public class RevenueReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RevenueReportServlet() {
		super();
	}

	private void loadAdminPortalEssentialData(HttpServletRequest request, MySQL db) {
		// Helper to load data needed by AdminPortal.jsp's other sections
		if (request.getAttribute("employees") == null) {
			String empSql = "SELECT EmployeeID, FirstName, LastName, Email, isAdmin, isCustomerRepresentative FROM Employee";
			List<Map<String, Object>> employeeList = db.executeQuery(empSql);
			request.setAttribute("employees", employeeList);
		}
		if (request.getAttribute("customers") == null) {
			List<Map<String, Object>> customerList = db.getAllCustomers();
			request.setAttribute("customers", customerList);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
            response.sendRedirect(request.getContextPath() + "/Home.jsp");
            return;
        }

        String reportType = request.getParameter("revenueReportType");
        String identifier = request.getParameter("revenueIdentifier");

        MySQL db = new MySQL();
        Map<String, Object> revenueDetails = null;
        String revenueReportError = null;
        String revenueReportTitle = "Revenue Report";
        boolean reportDataFound = false; // This will indicate if the query ran and returned data

        if (identifier == null || identifier.trim().isEmpty()) {
            revenueReportError = "Identifier cannot be empty.";
        } else {
            identifier = identifier.trim();
            try {
                if ("flight".equals(reportType)) {
                    int flightId = Integer.parseInt(identifier);
                    revenueDetails = db.getRevenueByFlightID(flightId);
                    revenueReportTitle = "Revenue Details for Flight ID: " + flightId;
                    reportDataFound = (revenueDetails != null);
                } else if ("airline".equals(reportType)) {
                    int airlineId = Integer.parseInt(identifier);
                    revenueDetails = db.getRevenueByAirlineID(airlineId); 
                    Map<String, Object> airlineInfo = db.getAirlineByID(airlineId);
                    String airlineName = airlineInfo != null && airlineInfo.get("Name") != null ? airlineInfo.get("Name").toString() : identifier;
                    revenueReportTitle = "Revenue Details for Airline: " + airlineName + " (ID: " + airlineId + ")";
                    reportDataFound = (revenueDetails != null);
                } else if ("customer".equals(reportType)) {
                    revenueDetails = db.getRevenueByCustomerID(identifier); 
                    Map<String, Object> customerInfo = db.getCustomer(identifier);
                    String customerName = customerInfo != null ? customerInfo.get("FirstName") + " " + customerInfo.get("LastName") : identifier;
                    revenueReportTitle = "Revenue Details for Customer: " + customerName + " (ID: " + identifier + ")";
                    reportDataFound = (revenueDetails != null);
                } else {
                    revenueReportError = "Invalid report type selected.";
                }
            } catch (NumberFormatException e) {
                revenueReportError = "Invalid ID format. Flight ID and Airline ID must be numeric.";
            } catch (Exception e) {
                revenueReportError = "An error occurred while generating the report: " + e.getMessage();
                e.printStackTrace(); // for debugging
            }
        }

        request.setAttribute("revenueDetailsForEntity", revenueDetails);
        request.setAttribute("revenueReportErrorMsg", revenueReportError);
        request.setAttribute("revenueReportTitle", revenueReportTitle);
        request.setAttribute("revenueReportTypeInput", reportType);
        request.setAttribute("revenueIdentifierInput", identifier);
        request.setAttribute("revenueByEntityReportGenerated", true);

        // Updated logic for revenueReportDataFound: check if data was found and if there are tickets
        boolean actualDataExists = false;
        if (reportDataFound && revenueDetails != null) {
            Object ticketsObj = revenueDetails.get("numberOfTickets");
            if (ticketsObj instanceof Number && ((Number) ticketsObj).longValue() > 0) {
                actualDataExists = true;
            }
        }
        request.setAttribute("revenueReportDataFound", actualDataExists);

        loadAdminPortalEssentialData(request, db);
        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }

	// If we want to allow GET requests to pre-fill the form or show an empty state:
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
			response.sendRedirect(request.getContextPath() + "/Home.jsp");
			return;
		}
		MySQL db = new MySQL();
		loadAdminPortalEssentialData(request, db);
		request.setAttribute("revenueByEntityReportGenerated", false); // Ensure it's not showing old results
		RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
		dispatcher.forward(request, response);
	}
}