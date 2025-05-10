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
            response.sendRedirect(request.getContextPath() + "/Home.jsp"); // Or your login page
            return;
        }
        // Forward to AdminPortal, which contains the form
        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !"Admin".equals(session.getAttribute("accType"))) {
            response.sendRedirect(request.getContextPath() + "/Home.jsp"); // Or your login page
            return;
        }

        String reportMonthYear = request.getParameter("reportMonthYear"); // Expecting YYYY-MM format

        if (reportMonthYear == null || !reportMonthYear.matches("\\d{4}-\\d{2}")) {
            request.setAttribute("reportError", "Invalid month/year format. Please use YYYY-MM.");
            request.setAttribute("showSalesReportSection", true); // To keep the section visible on error
        } else {
            String[] parts = reportMonthYear.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            MySQL db = new MySQL(); // Assuming MySQL.java is in the default package or imported correctly
            Map<String, Object> salesData = db.getMonthlySalesReport(year, month);

            request.setAttribute("salesData", salesData);
            request.setAttribute("reportGenerated", true);
            // We can pre-format currency here if desired to simplify the JSP scriptlets
            // NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
            // if (salesData != null) {
            //     if (salesData.get("totalFare") != null) request.setAttribute("formattedTotalFare", currencyFormatter.format(salesData.get("totalFare")));
            //     if (salesData.get("totalBookingFee") != null) request.setAttribute("formattedTotalBookingFee", currencyFormatter.format(salesData.get("totalBookingFee")));
            //     if (salesData.get("totalRevenue") != null) request.setAttribute("formattedTotalRevenue", currencyFormatter.format(salesData.get("totalRevenue")));
            // }
        }
        
        request.setAttribute("reportForMonthYearInput", reportMonthYear); // To repopulate the input field
        request.setAttribute("showSalesReportSection", true); // Ensure section is shown

        RequestDispatcher dispatcher = request.getRequestDispatcher("AdminPortal.jsp");
        dispatcher.forward(request, response);
    }
}