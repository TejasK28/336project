import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/PurchaseTicket")
public class PurchaseTicket extends HttpServlet {
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
            request.setAttribute("error", "Please log in to purchase tickets");
            request.getRequestDispatcher("ViewFlightPlan.jsp").forward(request, response);
            return;
        }
        
        // Get parameters from the request
        String flightIDStr = request.getParameter("flightID");
        String className = request.getParameter("className");
        String ticketFareStr = request.getParameter("ticketFare");
        
        if (flightIDStr == null || className == null || ticketFareStr == null) {
            request.setAttribute("error", "Missing required ticket information");
            request.getRequestDispatcher("ViewFlightPlan.jsp").forward(request, response);
            return;
        }
        
        try {
            int flightID = Integer.parseInt(flightIDStr);
            float ticketFare = Float.parseFloat(ticketFareStr);
            
            // Debug: Print initial parameters
            System.out.println("DEBUG: Attempting to purchase ticket:");
            System.out.println("DEBUG: FlightID: " + flightID);
            System.out.println("DEBUG: Class: " + className);
            System.out.println("DEBUG: CustomerID: " + customerID);
            
            // Check if there are available seats
            boolean hasSeats = db.hasAvailableSeats(flightID, className);
            System.out.println("DEBUG: Has available seats: " + hasSeats);
            
            if (!hasSeats) {
                // If no seats available, add to waitlist
                System.out.println("DEBUG: No seats available, attempting to add to waitlist");
                if (db.addToWaitlist(customerID, flightID, className)) {
                    request.setAttribute("message", "No seats available. You have been added to the waitlist.");
                    System.out.println("DEBUG: Successfully added to waitlist");
                } else {
                    request.setAttribute("error", "Failed to add to waitlist. Please try again later.");
                    System.out.println("DEBUG: Failed to add to waitlist");
                }
            } else {
                // Create the ticket (seat number will be automatically assigned)
                System.out.println("DEBUG: Seats available, attempting to create ticket");
                if (db.createTicket(customerID, flightID, className, ticketFare)) {
                    // Remove from waitlist if customer was on it
                    String removeFromWaitlistSQL = "DELETE FROM WaitingList WHERE CustomerID = ? AND FlightID = ?";
                    try {
                        db.executeUpdate(removeFromWaitlistSQL, customerID, flightID);
                        System.out.println("DEBUG: Removed from waitlist if present");
                    } catch (Exception e) {
                        System.out.println("DEBUG: Error removing from waitlist: " + e.getMessage());
                        // Continue anyway since the ticket was created successfully
                    }
                    request.setAttribute("message", "Ticket purchased successfully!");
                    System.out.println("DEBUG: Successfully created ticket");
                } else {
                    request.setAttribute("error", "Failed to purchase ticket. Please try again later.");
                    System.out.println("DEBUG: Failed to create ticket");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("DEBUG: Number format exception: " + e.getMessage());
            request.setAttribute("error", "Invalid flight ID or ticket fare format");
        } catch (Exception e) {
            System.out.println("DEBUG: General exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while processing your request");
        }

        // Forward to the ViewFlightPlan page
        response.sendRedirect(request.getContextPath() + "/ViewFlightPlan/" + request.getParameter("flightPlanID"));
    }
} 