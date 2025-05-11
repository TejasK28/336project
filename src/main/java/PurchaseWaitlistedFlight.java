import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/purchaseWaitlistedFlight")
public class PurchaseWaitlistedFlight extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Ensure authenticated account
        if (request.getSession().getAttribute("authenticated") == null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        String username = (String) request.getSession().getAttribute("username");
        int flightId = Integer.parseInt(request.getParameter("flightId"));

        MySQL db = new MySQL();
        
        try {
            // Start transaction
            Connection con = db.getConnection();
            con.setAutoCommit(false);
            
            try {
                // 1. Check if seat is still available
                int availableSeats = db.getAvailableSeats(flightId);
                if (availableSeats <= 0) {
                    request.setAttribute("error", "Sorry, the seat is no longer available.");
                    request.getRequestDispatcher("Home.jsp").forward(request, response);
                    return;
                }

                // 2. Get next seat number
                int seatNumber = db.getNextSeatNumber(flightId);

                // 3. Create ticket with the correct primary key structure
                String sql = "INSERT INTO Ticket (CustomerID, FlightID, SeatNumber, Class, TicketFare, BookingFee, PurchaseDateTime) " +
                           "SELECT ?, ?, ?, Class, StandardFare, " +
                           "CASE WHEN Class = 'First' THEN 15.00 ELSE 0.00 END, " +
                           "NOW() " +
                           "FROM Flight WHERE FlightID = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setInt(2, flightId);
                    ps.setInt(3, seatNumber);
                    ps.setInt(4, flightId);
                    ps.executeUpdate();
                }

                // 4. Remove from waitlist
                sql = "DELETE FROM WaitingList WHERE CustomerID = ? AND FlightID = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setInt(2, flightId);
                    ps.executeUpdate();
                }

                // Commit transaction
                con.commit();
                
                // Redirect back to home page with success message
                request.setAttribute("success", "Successfully purchased ticket for waitlisted flight!");
                response.sendRedirect(request.getContextPath() + "/Home");
                
            } catch (SQLException e) {
                // Rollback transaction on error
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while processing your request.");
            request.getRequestDispatcher("Home.jsp").forward(request, response);
        }
    }
} 