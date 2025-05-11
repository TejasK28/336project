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

        // 1) List customers & their reservations
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

        // 2) “New reservation” form
        if ("/CustRep/reservation/new".equals(path)) {
            String custId = req.getParameter("customerId");
            if (custId == null || custId.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/CustRep/reservations");
                return;
            }
            req.setAttribute("customerId", custId);
            // <-- load *all* flights here
            req.setAttribute("flights", db.getAllFlightOptions());
            req.getRequestDispatcher("/ReservationForm.jsp")
               .forward(req, resp);
            return;
        }

        // 3) “Edit reservation” form
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
            // <-- and here as well
            req.setAttribute("flights", db.getAllFlightOptions());
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

        // ─── Create new reservation ─────────────────────────────────────────────
        if ("/CustRep/reservation/create".equals(path)) {
            String     custId      = req.getParameter("customerId");
            int        flightId    = Integer.parseInt(req.getParameter("flightId"));
            String     travelClass = req.getParameter("travelClass");
            BigDecimal fare        = new BigDecimal(req.getParameter("fare"));

            // 1) insert ticket
            boolean created = db.createReservation(custId, flightId, travelClass, fare);
            if (!created) {
                req.setAttribute("error", "Could not create reservation");
                doGet(req, resp);
                return;
            }

            try {
                // 2) build a flight plan & segment
                Map<String,Object> flight = db.getFlightByFID(String.valueOf(flightId));
                int duration = ((Number)flight.get("Duration")).intValue();

                int planId = db.createFlightPlan(custId, duration, fare.floatValue());
                db.insertItinerarySegment(planId, /*segmentNum=*/1, travelClass, flightId);

            } catch (SQLException e) {
                throw new ServletException("Failed to create flight plan", e);
            }

            resp.sendRedirect(req.getContextPath()
                + "/CustRep/reservations?customerId=" + custId);
            return;
        }

        // ─── Update existing reservation ────────────────────────────────────────
        if ("/CustRep/reservation/update".equals(path)) {
            String     custId      = req.getParameter("customerId");
            int        oldFlight   = Integer.parseInt(req.getParameter("oldFlightId"));
            int        oldSeat     = Integer.parseInt(req.getParameter("oldSeatNo"));
            int        newFlight   = Integer.parseInt(req.getParameter("flightId"));
            String     newClass    = req.getParameter("travelClass");
            BigDecimal newFare     = new BigDecimal(req.getParameter("fare"));

            // 1) update the ticket (delete + reinsert)
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
                // 2) find their latest flight plan
                List<Map<String,Object>> plans = db.getUserFlightPlans(custId);
                if (plans.isEmpty()) {
                    // no existing plan → create new one
                    Map<String,Object> fl = db.getFlightByFID(String.valueOf(newFlight));
                    int dur = ((Number)fl.get("Duration")).intValue();
                    int planId = db.createFlightPlan(custId, dur, newFare.floatValue());
                    db.insertItinerarySegment(planId, 1, newClass, newFlight);
                } else {
                    // update the most recent plan
                    int planId = plans.stream()
                                      .mapToInt(m -> ((Number)m.get("FlightPlanID")).intValue())
                                      .max()
                                      .orElseThrow(() -> new SQLException("No flight plan found"));

                    // 3) update the existing itinerary segment
                    String updSegSql =
                      "UPDATE ItinerarySegment " +
                      "   SET FlightID = ?, Class = ? " +
                      " WHERE FlightPlanID = ? AND SegmentNum = ?";
                    db.executeUpdate(updSegSql,
                                     newFlight, newClass,
                                     planId, 1);

                    // 4) recompute totals on the flight plan
                    Map<String,Object> fl = db.getFlightByFID(String.valueOf(newFlight));
                    int dur = ((Number)fl.get("Duration")).intValue();

                    String updPlanSql =
                      "UPDATE FlightPlan " +
                      "   SET TotalDuration = ?, TotalFare = ? " +
                      " WHERE FlightPlanID  = ?";
                    db.executeUpdate(updPlanSql,
                                     dur, newFare.floatValue(),
                                     planId);
                }

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
