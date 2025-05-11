
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet({
    "/CustRep/reservations",
    "/CustRep/reservation/new",
    "/CustRep/reservation/create",
    "/CustRep/reservation/edit",
    "/CustRep/reservation/update"
})
public class ReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MySQL db = new MySQL();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        // ─── 1) List customers & their reservations ─────────────────────────────
        if ("/CustRep/reservations".equals(path)) {
            List<Map<String,Object>> customers = db.getAllCustomers();
            req.setAttribute("customers", customers);

            String custId = req.getParameter("customerId");
            if (custId != null && !custId.isEmpty()) {
                req.setAttribute("selectedCustomerId", custId);
                req.setAttribute("reservations", db.getReservationsForCustomer(custId));
            }

            req.getRequestDispatcher("/ListReservations.jsp")
               .forward(req, resp);
            return;
        }

        // ─── 2) Show “new reservation” form ──────────────────────────────────────
        if ("/CustRep/reservation/new".equals(path)) {
            String custId = req.getParameter("customerId");
            if (custId == null || custId.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/CustRep/reservations");
                return;
            }
            req.setAttribute("customerId", custId);
            req.setAttribute("flights", db.getAllFlights());
            req.getRequestDispatcher("/ReservationForm.jsp")
               .forward(req, resp);
            return;
        }

        // ─── 3) Show “edit reservation” form ─────────────────────────────────────
        if ("/CustRep/reservation/edit".equals(path)) {
            String custId   = req.getParameter("customerId");
            int    flightId = Integer.parseInt(req.getParameter("flightId"));
            int    seatNo   = Integer.parseInt(req.getParameter("seatNo"));

            Map<String,Object> ticket = null;
            for (Map<String,Object> t : db.getReservationsForCustomer(custId)) {
                int f = ((Number)t.get("FlightID")).intValue();
                int s = ((Number)t.get("SeatNumber")).intValue();
                if (f == flightId && s == seatNo) {
                    ticket = t;
                    break;
                }
            }

            req.setAttribute("ticket", ticket);
            req.setAttribute("customerId", custId);
            req.setAttribute("flights", db.getAllFlights());
            req.getRequestDispatcher("/ReservationForm.jsp")
               .forward(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        // ─── 4) Handle “create” ──────────────────────────────────────────────────
        if ("/CustRep/reservation/create".equals(path)) {
            String     custId      = req.getParameter("customerId");
            int        flightId    = Integer.parseInt(req.getParameter("flightId"));
            String     travelClass = req.getParameter("travelClass");
            BigDecimal fare        = new BigDecimal(req.getParameter("fare"));

            // 1) create the ticket
            boolean created = db.createReservation(custId, flightId, travelClass, fare);
            if (!created) {
                req.setAttribute("error", "Could not create reservation");
                doGet(req, resp);
                return;
            }

            try {
                // 2) fetch that flight’s duration
                Map<String,Object> flight = db.getFlightByFID(String.valueOf(flightId));
                int duration = ((Number)flight.get("Duration")).intValue();

                // 3) create a one‐segment FlightPlan
                int planId = db.createFlightPlan(custId, duration, fare.floatValue());

                // 4) insert the single ItinerarySegment
                db.insertItinerarySegment(planId, /*segmentNum=*/1, travelClass, flightId);

            } catch (SQLException e) {
                throw new ServletException("Failed to create flight plan", e);
            }

            resp.sendRedirect(req.getContextPath()
                + "/CustRep/reservations?customerId=" + custId);
            return;
        }

        // ─── 5) Handle “update” ──────────────────────────────────────────────────
        if ("/CustRep/reservation/update".equals(path)) {
            String     custId      = req.getParameter("customerId");
            int        oldFlight   = Integer.parseInt(req.getParameter("oldFlightId"));
            int        oldSeat     = Integer.parseInt(req.getParameter("oldSeatNo"));
            int        newFlight   = Integer.parseInt(req.getParameter("flightId"));
            String     newClass    = req.getParameter("travelClass");
            BigDecimal newFare     = new BigDecimal(req.getParameter("fare"));

            // 1) update the ticket (delete + reinsert internally)
            boolean updated = db.updateReservation(
                custId,
                oldFlight, oldSeat,
                newFlight, newClass, newFare
            );
            if (!updated) {
                req.setAttribute("error", "Could not update reservation");
                doGet(req, resp);
                return;
            }

            try {
                // 2) find the user’s most recent FlightPlanID
                int planId = db.getUserFlightPlans(custId).stream()
                              .mapToInt(m -> ((Number)m.get("FlightPlanID")).intValue())
                              .max()
                              .orElseThrow(() -> new SQLException("No flight plan found"));

                // 3) re‐insert (or you could delete & insert) the single segment
                db.insertItinerarySegment(planId, /*segmentNum=*/1, newClass, newFlight);

                // 4) recompute totals and update FlightPlan
                Map<String,Object> flight = db.getFlightByFID(String.valueOf(newFlight));
                int duration = ((Number)flight.get("Duration")).intValue();

                String updSql = """
                    UPDATE FlightPlan
                       SET TotalDuration = ?,
                           TotalFare     = ?
                     WHERE FlightPlanID  = ?
                """;
                db.executeUpdate(updSql, duration, newFare.floatValue(), planId);

            } catch (SQLException e) {
                throw new ServletException("Failed to update flight plan", e);
            }

            resp.sendRedirect(req.getContextPath()
                + "/CustRep/reservations?customerId=" + custId);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
