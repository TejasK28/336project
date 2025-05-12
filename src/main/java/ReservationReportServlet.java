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
import java.util.ArrayList;
import java.math.BigDecimal; // For JspFormatHelper consistency
import java.text.NumberFormat; // For JspFormatHelper consistency
import java.util.Locale; // For JspFormatHelper consistency


@WebServlet("/ReservationReportServlet")
public class ReservationReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ReservationReportServlet() {
        super();
    }

    private void loadAdminPortalEssentialData(HttpServletRequest request, MySQL db) {
        // This method ensures that data needed by AdminPortal.jsp's other sections is loaded.
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
            response.sendRedirect(request.getContextPath() + "/Home.jsp");
            return;
        }

        MySQL db = new MySQL();
        loadAdminPortalEssentialData(request, db); // Load data for other portal sections

        // Set default attributes for the reservation report section for the initial GET load
        request.setAttribute("reservationReportGenerated", false);
        request.setAttribute("showReservationReportSection", true); // To ensure the section is visible

        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Authentication and authorization check
        if (session == null || session.getAttribute("authenticated") == null ||
            !"Admin".equals(session.getAttribute("accType"))) { // Assuming "accType" stores role
            response.sendRedirect(request.getContextPath() + "/Home.jsp"); // Or your designated login page
            return;
        }

        // Retrieve search parameters from the request
        String searchBy = request.getParameter("searchBy");
        String searchValue = request.getParameter("searchValue");

        MySQL db = new MySQL(); // Database access object
        List<Map<String, Object>> reservationsList = new ArrayList<>(); // Initialize to avoid null pointer issues
        String reservationReportError = null; // For storing error messages
        String reservationReportTitle = "Reservation Search Results"; // Default title for the report

        // Validate search value
        if (searchValue == null || searchValue.trim().isEmpty()) {
            reservationReportError = "Search value cannot be empty.";
        } else {
            searchValue = searchValue.trim(); // Use trimmed search value
            // Perform search based on the selected criteria
            if ("flightNumber".equals(searchBy)) {
                // The MySQL.java method getReservationsByFlightNumber handles parsing
                reservationsList = db.getReservationsByFlightNumber(searchValue);
                reservationReportTitle = "Reservations for Flight Number: " + searchValue;
                // No explicit error message if list is empty; JSP will handle display
            } else if ("customerName".equals(searchBy)) {
                reservationsList = db.getReservationsByCustomerName(searchValue);
                reservationReportTitle = "Reservations for Customer name containing: '" + searchValue + "'";
            } else if ("customerID".equals(searchBy)){
                reservationsList = db.getReservationsByCustomerID(searchValue);
                reservationReportTitle = "Reservations for Customer ID: " + searchValue;
            } else {
                reservationReportError = "Invalid search type selected.";
            }
        }

        // Set attributes for the JSP
        request.setAttribute("reservationsList", reservationsList);
        request.setAttribute("reservationReportError", reservationReportError);
        request.setAttribute("reservationReportTitle", reservationReportTitle);
        request.setAttribute("searchByInput", searchBy); // For repopulating the form
        request.setAttribute("searchValueInput", searchValue); // For repopulating the form
        request.setAttribute("reservationReportGenerated", true); // Indicate that a search was attempted

        // Load other essential data needed by AdminPortal.jsp
        // Assuming loadAdminPortalEssentialData is defined in your servlet:
        loadAdminPortalEssentialData(request, db);

        // Forward to the JSP for display
        RequestDispatcher dispatcher = request.getRequestDispatcher("/AdminPortal.jsp");
        dispatcher.forward(request, response);
    }
}