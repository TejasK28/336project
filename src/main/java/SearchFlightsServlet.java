

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/SearchFlightsServlet")
public class SearchFlightsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MySQL db;

    @Override
    public void init() throws ServletException {
        super.init();
        db = new MySQL();
        db.loadDriver("com.mysql.jdbc.Driver");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

      // 1) airports
      String sqlAirports =
        "SELECT DISTINCT FromAirportID AS AirportID FROM Flight " +
        " UNION " +
        "SELECT DISTINCT ToAirportID      FROM Flight";
      List<Map<String,Object>> airports = db.executeQuery(sqlAirports);

      // 2) departure dates
      String sqlDates =
        "SELECT DISTINCT DATE(DepartTime) AS departDate " +
        "  FROM Flight " +
        " ORDER BY departDate";
      List<Map<String,Object>> dates = db.executeQuery(sqlDates);

      req.setAttribute("airports", airports);
      req.setAttribute("dates",    dates);
      req.getRequestDispatcher("/search.jsp").forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // 1) Pull parameters from the form
        String from       = request.getParameter("fromAirport");
        String to         = request.getParameter("toAirport");
        LocalDate depart  = LocalDate.parse(request.getParameter("departDate"));
        String tripType   = request.getParameter("tripType");        // "oneway" or "roundtrip"
        boolean flexible  = "true".equals(request.getParameter("flexible"));
        String sortBy     = request.getParameter("sortBy");          // e.g. "price", "duration", "f.DepartTime"

        // 2) Perform outbound search
        List<Map<String,Object>> outbound = flexible
            ? db.searchFlexible(from, to, depart, sortBy)
            : db.searchOneWay(from, to, depart, sortBy);

        // 3) If round-trip, perform inbound search
        List<Map<String,Object>> inbound = List.of();
        if ("roundtrip".equals(tripType)) {
            LocalDate ret = LocalDate.parse(request.getParameter("returnDate"));
            inbound = flexible
                ? db.searchFlexible(to, from, ret, sortBy)
                : db.searchOneWay(to, from, ret, sortBy);
        }

        // 4) Attach to request and forward to JSP
        request.setAttribute("outbound", outbound);
        request.setAttribute("inbound",  inbound);
        request.getRequestDispatcher("/results.jsp")
               .forward(request, response);
    }
}