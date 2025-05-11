import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.text.NumberFormat;
import java.util.List; 
import java.util.Locale;


@WebServlet("/SalesReportServlet")
public class SalesReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SalesReportServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
            response.sendRedirect(request.getContextPath() + "/Home.jsp");
            return;
        }

        // we need to ensure employee and customer data is also loaded.
        MySQL db = new MySQL();

        String empSql = "SELECT EmployeeID, FirstName, LastName, Email, isAdmin, isCustomerRepresentative FROM Employee";
        List<Map<String, Object>> employeeList = db.executeQuery(empSql);
        request.setAttribute("employees", employeeList);

        List<Map<String, Object>> customerList = db.getAllCustomers();
        request.setAttribute("customers", customerList);

        request.setAttribute("reportGenerated", false); // Default to false

        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
            response.sendRedirect(request.getContextPath() + "/Home.jsp");
        }

        String reportMonthYear = request.getParameter("reportMonthYear");
        MySQL db = new MySQL(); // This instance can be used for all DB operations in this method

        if (reportMonthYear == null || !reportMonthYear.matches("\\d{4}-\\d{2}")) {
            request.setAttribute("reportError", "Invalid month/year format. Please use YYYY-MM.");
        } else {
            String[] parts = reportMonthYear.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            Map<String, Object> salesData = db.getMonthlySalesReport(year, month);
            request.setAttribute("salesData", salesData);
            request.setAttribute("reportGenerated", true);
        }

        request.setAttribute("reportForMonthYearInput", reportMonthYear);
        // request.setAttribute("showSalesReportSection", true); 

        String empSql = "SELECT EmployeeID, FirstName, LastName, Email, isAdmin, isCustomerRepresentative FROM Employee";
        List<Map<String, Object>> employeeList = db.executeQuery(empSql);
        request.setAttribute("employees", employeeList);

        List<Map<String, Object>> customerList = db.getAllCustomers(); 
        request.setAttribute("customers", customerList);

        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }
}