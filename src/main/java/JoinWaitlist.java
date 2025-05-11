import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/JoinWaitlist")
public class JoinWaitlist extends HttpServlet {
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
            request.setAttribute("error", "Please log in to join the waiting list");
            request.getRequestDispatcher("ViewFlightPlan.jsp").forward(request, response);
            return;
        }

        try {
            int flightId = Integer.parseInt(request.getParameter("flightId"));
            String className = request.getParameter("className");
            // Add to waitlist
            boolean success = db.addToWaitlist(customerID, flightId, className);
            if (success) {
                request.setAttribute("success", "Successfully added to waiting list");
            } else {
                request.setAttribute("error", "Failed to join waiting list");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error processing request: " + e.getMessage());
        }

        // Redirect back to the flight plan page with the flight plan ID as a path parameter
        response.sendRedirect(request.getContextPath() + "/ViewFlightPlan/" + request.getParameter("flightPlanID"));
    }
} 