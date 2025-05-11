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
		BigDecimal totalRevenue = BigDecimal.ZERO;
		String revenueReportError = null;
		String revenueReportTitle = "Revenue Report";
		boolean reportDataFound = false;

		if (identifier == null || identifier.trim().isEmpty()) {
			revenueReportError = "Identifier cannot be empty.";
		} else {
			identifier = identifier.trim();
			try {
				if ("flight".equals(reportType)) {
					// identifier is FlightID (INT)
					int flightId = Integer.parseInt(identifier);
					totalRevenue = db.getRevenueByFlightID(flightId);
					revenueReportTitle = "Revenue for Flight ID: " + flightId;
					reportDataFound = true; // Assume data found, MySQL method returns ZERO if no records
				} else if ("airline".equals(reportType)) {
					// identifier is AirlineID (INT)
					int airlineId = Integer.parseInt(identifier);
					totalRevenue = db.getRevenueByAirlineID(airlineId);
					// Optionally fetch airline name for the title
					Map<String, Object> airlineInfo = db.getAirlineByID(airlineId); // You'll need to create getAirlineByID
					String airlineName = airlineInfo != null && airlineInfo.get("Name") != null ? airlineInfo.get("Name").toString() : identifier;
					revenueReportTitle = "Revenue for Airline: " + airlineName + " (ID: " + airlineId + ")";
					reportDataFound = true;
				} else if ("customer".equals(reportType)) {
					// Identifier is CustomerID (String)
					totalRevenue = db.getRevenueByCustomerID(identifier);
					// Optionally fetch customer name for the title
					Map<String, Object> customerInfo = db.getCustomer(identifier); // getCustomer exists
					String customerName = customerInfo != null ? customerInfo.get("FirstName") + " " + customerInfo.get("LastName") : identifier;
					revenueReportTitle = "Revenue for Customer: " + customerName + " (ID: " + identifier + ")";
					reportDataFound = true;
				} else {
					revenueReportError = "Invalid report type selected.";
				}
			} catch (NumberFormatException e) {
				revenueReportError = "Invalid ID format. Flight ID and Airline ID must be numeric.";
			} catch (Exception e) {
				revenueReportError = "An error occurred while generating the report: " + e.getMessage();
				e.printStackTrace();
			}
		}

		request.setAttribute("totalRevenueForEntity", totalRevenue);
		request.setAttribute("revenueReportErrorMsg", revenueReportError); // Use a distinct attribute name
		request.setAttribute("revenueReportTitle", revenueReportTitle);
		request.setAttribute("revenueReportTypeInput", reportType);
		request.setAttribute("revenueIdentifierInput", identifier);
		request.setAttribute("revenueByEntityReportGenerated", true);
		request.setAttribute("revenueReportDataFound", reportDataFound && totalRevenue.compareTo(BigDecimal.ZERO) >= 0);


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