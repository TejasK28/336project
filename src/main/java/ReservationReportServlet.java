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
        // It's similar to what might be in the main /Admin GET handler or other report servlets.
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
            response.sendRedirect(request.getContextPath() + "/Home.jsp");
            return;
        }

        String searchBy = request.getParameter("searchBy");
        String searchValue = request.getParameter("searchValue");

        MySQL db = new MySQL();
        List<Map<String, Object>> reservations = new ArrayList<>();
        String reportError = null;
        String reportTitle = "Reservations";

        if (searchValue == null || searchValue.trim().isEmpty()) {
            reportError = "Search value cannot be empty.";
        } else {
            if ("flightNumber".equals(searchBy)) {
                try {
                    // Flight.FlightNumber is INT in your schema
                    reservations = db.getReservationsByFlightNumber(searchValue.trim());
                    reportTitle = "Reservations for Flight Number: " + searchValue;
                    if (reservations.isEmpty()) {
                        reportError = "No reservations found for flight number: " + searchValue;
                    }
                } catch (NumberFormatException e) {
                    reportError = "Invalid Flight Number format. Please enter a numeric flight number.";
                }
            } else if ("customerName".equals(searchBy)) {
                reservations = db.getReservationsByCustomerName(searchValue.trim());
                reportTitle = "Reservations for Customer name containing: '" + searchValue + "'";
                 if (reservations.isEmpty()) {
                    reportError = "No reservations found for customer name containing: '" + searchValue + "'";
                }
            } else if ("customerID".equals(searchBy)){
                reservations = db.getReservationsByCustomerID(searchValue.trim());
                reportTitle = "Reservations for Customer ID: " + searchValue;
                if (reservations.isEmpty()) {
                    reportError = "No reservations found for Customer ID: " + searchValue;
                }
            }
             else {
                reportError = "Invalid search type selected.";
            }
        }

        request.setAttribute("reservationsList", reservations);
        request.setAttribute("reservationReportError", reportError);
        request.setAttribute("reservationReportTitle", reportTitle);
        request.setAttribute("searchByInput", searchBy);
        request.setAttribute("searchValueInput", searchValue);
        request.setAttribute("reservationReportGenerated", true);
        request.setAttribute("showReservationReportSection", true); // Keep section visible

        loadAdminPortalEssentialData(request, db); // Reload other essential data for AdminPortal.jsp

        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }
}