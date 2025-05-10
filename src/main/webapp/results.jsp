<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List,java.util.Map,java.util.Set,java.util.TreeSet" %>
<%
    // tripType was submitted in the search form
    String tripType = request.getParameter("tripType");

    @SuppressWarnings("unchecked")
    List<Map<String,Object>> outbound =
        (List<Map<String,Object>>) request.getAttribute("outbound");
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> inbound =
        (List<Map<String,Object>>) request.getAttribute("inbound");

    // Build filter sets
    Set<String> airlines     = new TreeSet<>();
    Set<String> takeoffTimes = new TreeSet<>();
    Set<String> landingTimes = new TreeSet<>();

    if (outbound != null) {
      for (Map<String,Object> f : outbound) {
        String a   = f.get("AirlineName").toString();
        String dep = f.get("DepartTime").toString().substring(11,16);
        String arr = f.get("ArrivalTime").toString().substring(11,16);
        airlines.add(a);
        takeoffTimes.add(dep);
        landingTimes.add(arr);
      }
    }
    if (inbound != null) {
      for (Map<String,Object> f : inbound) {
        String a   = f.get("AirlineName").toString();
        String dep = f.get("DepartTime").toString().substring(11,16);
        String arr = f.get("ArrivalTime").toString().substring(11,16);
        airlines.add(a);
        takeoffTimes.add(dep);
        landingTimes.add(arr);
      }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>Flight Results</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"/>

  <script>
    function filterFlights() {
      const priceFilter   = parseFloat(document.getElementById('priceFilter').value) || Infinity;
      const airlineFilter = document.getElementById('airlineFilter').value.toLowerCase();
      const takeoffFilter = document.getElementById('takeoffFilter').value;
      const landingFilter = document.getElementById('landingFilter').value;

      document.querySelectorAll('.flight-row').forEach(row => {
        const price   = parseFloat(row.dataset.price) || 0;
        const airline = row.dataset.airline.toLowerCase();
        const takeoff = row.dataset.takeoff;
        const landing = row.dataset.landing;

        const okPrice   = price <= priceFilter;
        const okAirline = !airlineFilter || airline === airlineFilter;
        const okTakeoff = !takeoffFilter   || takeoff >= takeoffFilter;
        const okLanding = !landingFilter   || landing <= landingFilter;

        row.style.display = (okPrice && okAirline && okTakeoff && okLanding)
          ? '' : 'none';
      });
    }

    window.addEventListener('DOMContentLoaded', () => {
      ['priceFilter','airlineFilter','takeoffFilter','landingFilter']
        .forEach(id => document.getElementById(id)
          .addEventListener('change', filterFlights));
    });
  </script>
</head>
<body>
  <jsp:include page="header.jsp"/>

  <%-- Flash message --%>
  <%
    Boolean added = (Boolean) session.getAttribute("FlightAdded");
    if (added != null && added) {
  %>
    <p class="success">Flight(s) added to your plan!</p>
  <%
      session.removeAttribute("FlightAdded");
    }
  %>

  <h2>Filter Flights</h2>
  <form onsubmit="return false;">
    <label for="priceFilter">Max Price:</label>
    <input type="number" id="priceFilter" placeholder="Enter max price">

    <label for="airlineFilter">Airline:</label>
    <select id="airlineFilter">
      <option value="">-- any --</option>
      <% for (String a : airlines) { %>
        <option value="<%= a.toLowerCase() %>"><%= a %></option>
      <% } %>
    </select>

    <label for="takeoffFilter">Earliest Takeoff:</label>
    <select id="takeoffFilter">
      <option value="">-- any --</option>
      <% for (String t : takeoffTimes) { %>
        <option value="<%= t %>"><%= t %></option>
      <% } %>
    </select>

    <label for="landingFilter">Latest Landing:</label>
    <select id="landingFilter">
      <option value="">-- any --</option>
      <% for (String t : landingTimes) { %>
        <option value="<%= t %>"><%= t %></option>
      <% } %>
    </select>
  </form>

  <% if ("roundtrip".equals(tripType)) { %>
    <%-- Round-trip: select outbound & return via radio --%>
    <form method="post" action="<%= request.getContextPath() %>/addRTFlightToPlan">
      <h2>Select Outbound Flight</h2>
      <table border="1" cellpadding="5">
        <tr>
          <th>Select</th><th>Depart</th><th>Arrive</th><th>Duration</th><th>Price</th><th>Airline</th>
        </tr>
        <% if (outbound != null && !outbound.isEmpty()) {
             for (Map<String,Object> f : outbound) {
               String id    = f.get("FlightID").toString();
               String dep   = f.get("DepartTime").toString().substring(11,16);
               String arr   = f.get("ArrivalTime").toString().substring(11,16);
        %>
        <tr class="flight-row"
            data-price="<%= f.get("price") %>"
            data-airline="<%= f.get("AirlineName").toString().toLowerCase() %>"
            data-takeoff="<%= dep %>"
            data-landing="<%= arr %>">
          <td>
            <input type="radio" name="outboundFlight" value="<%= id %>" required/>
          </td>
          <td><%= f.get("DepartTime") %></td>
          <td><%= f.get("ArrivalTime") %></td>
          <td><%= f.get("duration") %> min</td>
          <td>$<%= f.get("price") %></td>
          <td><%= f.get("AirlineName") %></td>
        </tr>
        <%   }
           } else { %>
        <tr><td colspan="6">No outbound flights found.</td></tr>
        <% } %>
      </table>

      <h2>Select Return Flight</h2>
      <table border="1" cellpadding="5">
        <tr>
          <th>Select</th><th>Depart</th><th>Arrive</th><th>Duration</th><th>Price</th><th>Airline</th>
        </tr>
        <% if (inbound != null && !inbound.isEmpty()) {
             for (Map<String,Object> f : inbound) {
               String id    = f.get("FlightID").toString();
               String dep   = f.get("DepartTime").toString().substring(11,16);
               String arr   = f.get("ArrivalTime").toString().substring(11,16);
        %>
        <tr class="flight-row"
            data-price="<%= f.get("price") %>"
            data-airline="<%= f.get("AirlineName").toString().toLowerCase() %>"
            data-takeoff="<%= dep %>"
            data-landing="<%= arr %>">
          <td>
            <input type="radio" name="inboundFlight" value="<%= id %>" required/>
          </td>
          <td><%= f.get("DepartTime") %></td>
          <td><%= f.get("ArrivalTime") %></td>
          <td><%= f.get("duration") %> min</td>
          <td>$<%= f.get("price") %></td>
          <td><%= f.get("AirlineName") %></td>
        </tr>
        <%   }
           } else { %>
        <tr><td colspan="6">No return flights found.</td></tr>
        <% } %>
      </table>

      <button type="submit">Add Round-Trip to Plan</button>
    </form>

  <% } else { %>
    <%-- One-way or layover: per-row buttons with dynamic action --%>
    <h2>Available Flights</h2>
    <table border="1" cellpadding="5">
      <tr>
        <th>Depart</th><th>Arrive</th><th>Duration</th><th>Price</th><th>Airline</th><th>Action</th>
      </tr>
      <% if (outbound != null && !outbound.isEmpty()) {
           for (Map<String,Object> f : outbound) {
             String id        = f.get("FlightID").toString();
             String dep       = f.get("DepartTime").toString().substring(11,16);
             String arr       = f.get("ArrivalTime").toString().substring(11,16);
             Object layoverId = f.get("LayoverFlightID");
             String action    = (layoverId != null)
                                 ? "/addLayFlightToPlan"
                                 : "/addOWFlightToPlan";
      %>
      <tr class="flight-row"
          data-price="<%= f.get("price") %>"
          data-airline="<%= f.get("AirlineName").toString().toLowerCase() %>"
          data-takeoff="<%= dep %>"
          data-landing="<%= arr %>">
        <td><%= f.get("DepartTime") %></td>
        <td><%= f.get("ArrivalTime") %></td>
        <td><%= f.get("duration") %> min</td>
        <td>$<%= f.get("price") %></td>
        <td><%= f.get("AirlineName") %></td>
        <td>
          <form method="post"
                action="<%= request.getContextPath() + action %>">
            <input type="hidden" name="flightID" value="<%= id %>"/>
            <button type="submit">Add to Plan</button>
          </form>
        </td>
      </tr>
      <%   }
         } else { %>
      <tr><td colspan="6">No flights found.</td></tr>
      <% } %>
    </table>
  <% } %>
</body>
</html>
