<%@ page import="java.util.*" %>
<%
  @SuppressWarnings("unchecked")
  List<Map<String,Object>> outbound =
    (List<Map<String,Object>>)request.getAttribute("outbound");
  @SuppressWarnings("unchecked")
  List<Map<String,Object>> inbound =
    (List<Map<String,Object>>)request.getAttribute("inbound");

  // Collect unique filter values
  Set<String> airlines    = new TreeSet<>();
  Set<String> takeoffTimes = new TreeSet<>();
  Set<String> landingTimes = new TreeSet<>();
  for (Map<String,Object> f : outbound) {
    String a = f.get("AirlineName").toString();
    String dep = f.get("DepartTime").toString().substring(11,16);
    String arr = f.get("ArrivalTime").toString().substring(11,16);
    airlines.add(a);
    takeoffTimes.add(dep);
    landingTimes.add(arr);
  }
  if (inbound != null) {
    for (Map<String,Object> f : inbound) {
      String a = f.get("AirlineName").toString();
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
  <meta charset="UTF-8">
  <title>Flight Results</title>
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
      // attach change listener to all filters
      ['priceFilter','airlineFilter','takeoffFilter','landingFilter']
        .forEach(id => document.getElementById(id)
          .addEventListener('change', filterFlights));
    });
  </script>
</head>
<body>
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

  <h2>Outbound Flights</h2>
  <table border="1" cellpadding="5">
    <tr>
      <th>Depart</th>
      <th>Arrive</th>
      <th>Duration (min)</th>
      <th>Price</th>
      <th>Airline</th>
    </tr>
    <% for (Map<String,Object> f : outbound) {
         String dep = f.get("DepartTime").toString().substring(11,16);
         String arr = f.get("ArrivalTime").toString().substring(11,16);
    %>
      <tr class="flight-row"
          data-price="<%= f.get("price") %>"
          data-airline="<%= f.get("AirlineName").toString().toLowerCase() %>"
          data-takeoff="<%= dep %>"
          data-landing="<%= arr %>">
        <td><%= f.get("DepartTime") %></td>
        <td><%= f.get("ArrivalTime") %></td>
        <td><%= f.get("duration") %></td>
        <td>$<%= f.get("price") %></td>
        <td><%= f.get("AirlineName") %></td>
      </tr>
    <% } %>
  </table>

  <% if (inbound != null && !inbound.isEmpty()) { %>
    <h2>Return Flights</h2>
    <table border="1" cellpadding="5">
      <tr>
        <th>Depart</th>
        <th>Arrive</th>
        <th>Duration (min)</th>
        <th>Price</th>
        <th>Airline</th>
      </tr>
      <% for (Map<String,Object> f : inbound) {
           String dep = f.get("DepartTime").toString().substring(11,16);
           String arr = f.get("ArrivalTime").toString().substring(11,16);
      %>
        <tr class="flight-row"
            data-price="<%= f.get("price") %>"
            data-airline="<%= f.get("AirlineName").toString().toLowerCase() %>"
            data-takeoff="<%= dep %>"
            data-landing="<%= arr %>">
          <td><%= f.get("DepartTime") %></td>
          <td><%= f.get("ArrivalTime") %></td>
          <td><%= f.get("duration") %></td>
          <td>$<%= f.get("price") %></td>
          <td><%= f.get("AirlineName") %></td>
        </tr>
      <% } %>
    </table>
  <% } %>
</body>
</html>
