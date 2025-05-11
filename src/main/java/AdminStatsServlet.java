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

@WebServlet("/AdminStatsServlet")
public class AdminStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AdminStatsServlet() {
        super();
    }

    private void loadAdminPortalEssentialData(HttpServletRequest request, MySQL db) {
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

        String action = request.getParameter("action");
        MySQL db = new MySQL();

        if ("topCustomer".equals(action)) {
            Map<String, Object> topCustomerData = db.getTopRevenueCustomer();
            request.setAttribute("topCustomerData", topCustomerData);
            request.setAttribute("topCustomerReportGenerated", true);
        } else if ("activeFlights".equals(action)) {
            List<Map<String, Object>> activeFlightsData = db.getMostActiveFlights(5); // Get top 5 active flights
            request.setAttribute("activeFlightsData", activeFlightsData);
            request.setAttribute("activeFlightsReportGenerated", true);
        }

        loadAdminPortalEssentialData(request, db);
        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }

    // doPost can simply call doGet if parameters are passed via URL for simplicity with button links
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
