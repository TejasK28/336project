import jakarta.servlet.RequestDispatcher;
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
import java.util.List;
import java.util.Map;

@WebServlet({
    "/CustRep",                   // dashboard
    "/CustRep/airport/*",         // view flights at one airport
    "/CustRep/editFlight",
    "/CustRep/editAirport",
    "/CustRep/airline",
    "/CustRep/editAircraft",
    "/CustRep/deleteFlight",
    "/CustRep/deleteAirport",
    "/CustRep/deleteAircraft",
    "/createFlight",
    "/createAircraft",
    "/createAirport",
    "/CustRep/waitlist"
})
public class CustRep extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public CustRep() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Ensure authenticated account
        if (request.getSession().getAttribute("authenticated") == null) {
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        MySQL db = new MySQL();
        String servletPath = request.getServletPath(); // e.g. "/CustRep" or "/CustRep/waitlist"
        String pathInfo    = request.getPathInfo();    // e.g. "/AMS"

        // 1) Dashboard
        if ("/CustRep".equals(servletPath) && pathInfo == null) {
            List<Map<String,Object>> airports = db.getAllAirports();
            for (Map<String,Object> ap : airports) {
                ap.put("numArriving",  db.getNumArrivingFlightsByAirportID((String)ap.get("AirportID")));
                ap.put("numDeparting", db.getNumDepartingFlightByAirportID((String)ap.get("AirportID")));
            }
            request.setAttribute("airports", airports);
            request.setAttribute("airlines", db.getAllAirlines());
            request.setAttribute("flights",  db.getAllFlights());
            RequestDispatcher rd = request.getRequestDispatcher("/CustRepPortal.jsp");
            rd.forward(request, response);
            return;
        }

        // 2) View flights at one airport: /CustRep/airport/{code}
        if ("/CustRep/airport".equals(servletPath) && pathInfo != null) {
            String airportId = pathInfo.substring(1);  // drop leading '/'
            request.setAttribute("airport_flights", db.getFlightsAtAirport(airportId));
            request.getRequestDispatcher("/Airport.jsp").forward(request, response);
            return;
        }

        // 3) Edit Flight form
        if ("/CustRep/editFlight".equals(servletPath)) {
            String fid = request.getParameter("flightId");
            request.setAttribute("flight",     db.getFlightByFID(fid));
            request.setAttribute("airports",   db.getAllAirports());
            request.setAttribute("airlines",   db.getAllAirlines());
            request.setAttribute("aircrafts",  db.getAllAircrafts());
            request.getRequestDispatcher("/EditFlight.jsp").forward(request, response);
            return;
        }

        // 4) Edit Airport form
        if ("/CustRep/editAirport".equals(servletPath)) {
            String aid = request.getParameter("airportID");
            request.setAttribute("airport", db.getAirportByID(aid));
            request.getRequestDispatcher("/EditAirport.jsp").forward(request, response);
            return;
        }

        // 5) View Aircrafts by Airline
        if ("/CustRep/airline".equals(servletPath)) {
            String airlineID = request.getParameter("airlineId");
            request.setAttribute("ownedAircrafts", db.getOwnedAircraftsByAirlineID(airlineID));
            request.getRequestDispatcher("/viewAircrafts.jsp").forward(request, response);
            return;
        }

        // 6) Edit Aircraft form
        if ("/CustRep/editAircraft".equals(servletPath)) {
            String acID = request.getParameter("aircraftID");
            request.setAttribute("aircraft", db.getAircraftByID(acID));
            request.setAttribute("airlines", db.getAllAirlines());
            request.getRequestDispatcher("/EditAircraft.jsp").forward(request, response);
            return;
        }

        // 7) View Waitlist
        if ("/CustRep/waitlist".equals(servletPath)) {
            int fid = Integer.parseInt(request.getParameter("flightId"));
            request.setAttribute("waitlist", db.getWaitingListByFlight(fid));
            request.setAttribute("flightId", fid);
            request.getRequestDispatcher("/Waitlist.jsp").forward(request, response);
            return;
        }

        // If no GET mapping matched
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MySQL db = new MySQL();
        String servletPath = request.getServletPath();

        if ("/createFlight".equals(servletPath)) {
            // --- existing createFlight logic ---
            String flightNumberStr = request.getParameter("FlightNumber");
            String airlineId = request.getParameter("AirlineID");
            String fromAirportId = request.getParameter("FromAirportID");
            String toAirportId = request.getParameter("ToAirportID");
            String departTimeStr = request.getParameter("DepartTime");
            String arrivalTimeStr = request.getParameter("ArrivalTime");
            String operatingDays = request.getParameter("OperatingDays");

            if (flightNumberStr == null || airlineId == null || fromAirportId == null || toAirportId == null
                    || departTimeStr == null || arrivalTimeStr == null || fromAirportId.equals(toAirportId)) {
                request.setAttribute("error", "All fields required, airports must differ.");
                request.getRequestDispatcher("/WEB-INF/jsp/createFlight.jsp").forward(request, response);
                return;
            }

            int flightNumber = Integer.parseInt(flightNumberStr);
            int aId = Integer.parseInt(airlineId);

            java.sql.Timestamp departTimestamp = java.sql.Timestamp.valueOf(departTimeStr.replace('T', ' ') + ":00");
            java.sql.Timestamp arrivalTimestamp = java.sql.Timestamp.valueOf(arrivalTimeStr.replace('T', ' ') + ":00");

            String sql = "INSERT INTO Flight "
                    + "(FlightNumber, AirlineID, FromAirportID, ToAirportID, DepartTime, ArrivalTime, OperatingDays) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, flightNumber);
                ps.setInt(2, aId);
                ps.setString(3, fromAirportId);
                ps.setString(4, toAirportId);
                ps.setTimestamp(5, departTimestamp);
                ps.setTimestamp(6, arrivalTimestamp);
                ps.setString(7, operatingDays);
                int inserted = ps.executeUpdate();
                if (inserted > 0) {
                    response.sendRedirect(request.getContextPath() + "/CustRep");
                    return;
                } else {
                    request.setAttribute("error", "Failed to create flight.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "Database error: " + e.getMessage());
            }
            request.getRequestDispatcher("/CustRep").forward(request, response);
            return;
        }

        if ("/createAircraft".equals(servletPath)) {
            // --- existing createAircraft logic ---
            String airlineId = request.getParameter("AirlineID");
            String model = request.getParameter("Model");
            String totalSeatsStr = request.getParameter("TotalSeats");
            String ecoStr = request.getParameter("EconomySeats");
            String bizStr = request.getParameter("BusinessSeats");
            String firstStr = request.getParameter("FirstClassSeats");
            int total = Integer.parseInt(totalSeatsStr);
            int eco = Integer.parseInt(ecoStr);
            int biz = Integer.parseInt(bizStr);
            int first = Integer.parseInt(firstStr);
            if (total <= 0 || eco <= 0 || biz <= 0 || first <= 0 || total != eco + biz + first) {
                request.setAttribute("createAircraftError", "Error in seat configuration!");
                request.getRequestDispatcher("/CustRep").forward(request, response);
                return;
            }
            String configStr = "E:" + eco + ",B:" + biz + ",F:" + first;
            if (!db.addAircraft(airlineId, model, total, configStr)) {
                request.setAttribute("createAircraftError", "Database error!");
                response.sendRedirect(request.getContextPath() + "/CustRep");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/CustRep");
            return;
        }

        if ("/createAirport".equals(servletPath)) {
            // --- existing createAirport logic ---
            String airportId = request.getParameter("AirportID");
            String name = request.getParameter("Name");
            String city = request.getParameter("City");
            String country = request.getParameter("Country");
            if (!db.addAirport(airportId, name, city, country)) {
                request.setAttribute("createAirportError", "Database error!");
                request.getRequestDispatcher("/CustRep").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/CustRep");
            return;
        }

        if ("/deleteFlight".equals(servletPath)) {
            String flightId = request.getParameter("flightId");
            String referer = request.getHeader("Referer");
            if (!db.deleteFlightByFID(flightId)) {
                request.setAttribute("deleteFlightFailed", "Deleting the flight failed!");
            }
            response.sendRedirect(referer);
            return;
        }

        if ("/editFlight".equals(servletPath)) {
            // --- existing editFlight POST logic ---
            String flightIdStr   = request.getParameter("FlightID");
            String flightNumStr  = request.getParameter("FlightNumber");
            String airlineIdStr  = request.getParameter("AirlineID");
            String fromAirportId = request.getParameter("FromAirportID");
            String toAirportId   = request.getParameter("ToAirportID");
            String departTimeStr = request.getParameter("DepartTime");
            String arrivalTimeStr= request.getParameter("ArrivalTime");
            String operatingDays = request.getParameter("OperatingDays");
            String aircraftIdStr = request.getParameter("AircraftID");

            if (flightIdStr == null || flightNumStr == null || airlineIdStr == null ||
                fromAirportId == null || toAirportId == null ||
                departTimeStr == null || arrivalTimeStr == null ||
                fromAirportId.equals(toAirportId)) {
                request.setAttribute("error", "All fields required and airports must differ.");
                request.getRequestDispatcher("/EditFlight.jsp").forward(request, response);
                return;
            }

            int flightId  = Integer.parseInt(flightIdStr);
            int flightNum = Integer.parseInt(flightNumStr);
            int airlineId = Integer.parseInt(airlineIdStr);
            LocalDateTime depart = LocalDateTime.parse(departTimeStr);
            LocalDateTime arrive = LocalDateTime.parse(arrivalTimeStr);

            try {
                boolean ok = db.updateFlight(
                        flightId, flightNum, airlineId,
                        fromAirportId, toAirportId,
                        depart, arrive,
                        operatingDays,
                        Integer.parseInt(aircraftIdStr)
                );
                if (!ok) {
                    request.setAttribute("error", "Update failed.");
                    request.getRequestDispatcher("/EditFlight.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                throw new ServletException("Error updating flight", e);
            }

            response.sendRedirect(request.getContextPath() + "/CustRep");
            return;
        }

        if ("/editAirport".equals(servletPath)) {
            // --- existing editAirport POST logic ---
            String oldAID  = request.getParameter("originalAID");
            String newAID  = request.getParameter("identifierCode");
            String name    = request.getParameter("Name");
            String city    = request.getParameter("City");
            String country = request.getParameter("Country");

            if (oldAID == null || newAID == null || name == null ||
                city == null || country == null ||
                oldAID.isBlank() || newAID.isBlank() ||
                name.isBlank() || city.isBlank() || country.isBlank()) {
                request.setAttribute("error", "All fields are required.");
                request.setAttribute("airport", db.getAirportByID(oldAID));
                request.getRequestDispatcher("/EditAirport.jsp").forward(request, response);
                return;
            }

            try {
                boolean ok = db.updateAirport(newAID, name, city, country, oldAID);
                if (!ok) {
                    request.setAttribute("airport", db.getAirportByID(oldAID));
                    request.setAttribute("error", "Update failed.");
                    request.getRequestDispatcher("/EditAirport.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                throw new ServletException("Error updating airport", e);
            }

            response.sendRedirect(request.getContextPath() + "/CustRep");
            return;
        }

        if ("/deleteAirport".equals(servletPath)) {
            // --- existing deleteAirport logic ---
            String aID = request.getParameter("airportID");
            if (!db.deleteAirport(aID)) {
                request.setAttribute("airportDeleteError", "Error when deleting airport!");
            }
            response.sendRedirect(request.getContextPath() + "/CustRep");
            return;
        }

        if ("/deleteAircraft".equals(servletPath)) {
            String acID   = request.getParameter("aircraftID");
            String referer = request.getHeader("Referer");
            if (!db.deleteAircraft(acID)) {
                request.setAttribute("aircraftDeleteError", "Error when deleting aircraft!");
            }
            response.sendRedirect(referer);
            return;
        }

        if ("/editAircraft".equals(servletPath)) {
            // --- existing editAircraft POST logic ---
            String acID      = request.getParameter("aircraftID");
            String airlineId = request.getParameter("AirlineID");
            String model     = request.getParameter("Model");
            int total  = Integer.parseInt(request.getParameter("TotalSeats"));
            int eco    = Integer.parseInt(request.getParameter("EconomySeats"));
            int biz    = Integer.parseInt(request.getParameter("BusinessSeats"));
            int first  = Integer.parseInt(request.getParameter("FirstClassSeats"));

            if (eco + biz + first != total) {
                request.setAttribute("error", "Class seats must sum to total seats.");
                request.setAttribute("aircraft", db.getAircraftByID(acID));
                request.setAttribute("airlines", db.getAllAirlines());
                request.getRequestDispatcher("/EditAircraft.jsp").forward(request, response);
                return;
            }

            String config = "E:" + eco + ",B:" + biz + ",F:" + first;
            boolean ok    = db.updateAircraft(acID, airlineId, model, total, config);
            if (!ok) {
                request.setAttribute("error", "Failed to update aircraft.");
                request.setAttribute("aircraft", db.getAircraftByID(acID));
                request.setAttribute("airlines", db.getAllAirlines());
                request.getRequestDispatcher("/EditAircraft.jsp").forward(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/CustRep");
            return;
        }

        // If no POST mapping matched
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
