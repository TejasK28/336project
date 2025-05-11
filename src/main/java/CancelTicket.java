import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/CancelTicket")
public class CancelTicket extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MySQL db;

    public void init() {
        db = new MySQL();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String customerID = (String) session.getAttribute("uname");
        
        if (customerID == null) {
            request.setAttribute("error", "Please log in to cancel tickets");
            request.getRequestDispatcher("ViewFlightPlan.jsp").forward(request, response);
            return;
        }

        try {
            int flightId = Integer.parseInt(request.getParameter("flightId"));
            String className = request.getParameter("className");

            // Check if the ticket is Business or First class
            if (!"B".equals(className) && !"F".equals(className)) {
                request.setAttribute("error", "Only Business and First class tickets can be cancelled");
                response.sendRedirect(request.getContextPath() + "/ViewFlightPlan/" + request.getParameter("flightPlanID"));
                return;
            }
            // Cancel the ticket
            boolean success = db.cancelTicket(customerID, flightId, className);
            if (success) {
                request.setAttribute("success", "Ticket cancelled successfully");
            } else {
                request.setAttribute("error", "Failed to cancel ticket");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error processing request: " + e.getMessage());
        }

        // Redirect back to the flight plan page
        response.sendRedirect(request.getContextPath() + "/ViewFlightPlan/" + request.getParameter("flightPlanID"));
    }
} 