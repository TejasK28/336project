<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List,java.util.Map" %>
<%
    // tripType was submitted in the search form
    String tripType = request.getParameter("tripType");

    @SuppressWarnings("unchecked")
    List<Map<String,Object>> outbound =
        (List<Map<String,Object>>) request.getAttribute("outbound");
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> inbound =
        (List<Map<String,Object>>) request.getAttribute("inbound");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>Flight Results</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"/>
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

  <% if ("roundtrip".equals(tripType)) { %>
    <%-- Round-trip: checkbox version --%>
    <form method="post" action="<%= request.getContextPath() %>/addRTFlightToPlan">
      <h2>Outbound Flights</h2>
      <%
        if (outbound != null && !outbound.isEmpty()) {
      %>
        <table border="1" cellpadding="5">
          <tr>
            <th>Select</th><th>Depart</th><th>Arrive</th><th>Duration</th><th>Price</th><th>Airline</th>
          </tr>
          <%
            for (Map<String,Object> f : outbound) {
              String id    = f.get("FlightID").toString();
              String dep   = f.get("DepartTime").toString();
              String arr   = f.get("ArrivalTime").toString();
              String dur   = f.get("duration").toString();
              String price = f.get("price").toString();
              String air   = f.get("AirlineName").toString();
          %>
          <tr>
            <td>
              <input type="checkbox" name="selectedFlights" value="<%= id %>" />
            </td>
            <td><%= dep %></td>
            <td><%= arr %></td>
            <td><%= dur %> min</td>
            <td>$<%= price %></td>
            <td><%= air %></td>
          </tr>
          <%
            }
          %>
        </table>
      <%
        } else {
      %>
        <p>No outbound flights found.</p>
      <%
        }
      %>

      <h2>Return Flights</h2>
      <%
        if (inbound != null && !inbound.isEmpty()) {
      %>
        <table border="1" cellpadding="5">
          <tr>
            <th>Select</th><th>Depart</th><th>Arrive</th><th>Duration</th><th>Price</th><th>Airline</th>
          </tr>
          <%
            for (Map<String,Object> f : inbound) {
              String id    = f.get("FlightID").toString();
              String dep   = f.get("DepartTime").toString();
              String arr   = f.get("ArrivalTime").toString();
              String dur   = f.get("duration").toString();
              String price = f.get("price").toString();
              String air   = f.get("AirlineName").toString();
          %>
          <tr>
            <td>
              <input type="checkbox" name="selectedFlights" value="<%= id %>" />
            </td>
            <td><%= dep %></td>
            <td><%= arr %></td>
            <td><%= dur %> min</td>
            <td>$<%= price %></td>
            <td><%= air %></td>
          </tr>
          <%
            }
          %>
        </table>
      <%
        } else {
      %>
        <p>No return flights found.</p>
      <%
        }
      %>

      <button type="submit">Add Selected Flights to Plan</button>
    </form>

  <% } else { %>
    <%-- One-way or layover: buttons per row with dynamic action --%>
    <h2>Available Flights</h2>
    <%
      if (outbound != null && !outbound.isEmpty()) {
    %>
      <table border="1" cellpadding="5">
        <tr>
          <th>Depart</th><th>Arrive</th><th>Duration</th><th>Price</th><th>Airline</th><th>Action</th>
        </tr>
        <%
          for (Map<String,Object> f : outbound) {
            String id        = f.get("FlightID").toString();
            String dep       = f.get("DepartTime").toString();
            String arr       = f.get("ArrivalTime").toString();
            String dur       = f.get("duration").toString();
            String price     = f.get("price").toString();
            String air       = f.get("AirlineName").toString();
            Object layoverId = f.get("LayoverFlightID");
            String action    = (layoverId != null)
                                ? "/addLayFlightToPlan"
                                : "/addOWFlightToPlan";
        %>
        <tr>
          <td><%= dep %></td>
          <td><%= arr %></td>
          <td><%= dur %> min</td>
          <td>$<%= price %></td>
          <td><%= air %></td>
          <td>
            <form method="post"
                  action="<%= request.getContextPath() + action %>">
              <input type="hidden" name="flightID" value="<%= id %>"/>
              <button type="submit">Add to Plan</button>
            </form>
          </td>
        </tr>
        <%
          }
        %>
      </table>
    <%
      } else {
    %>
      <p>No flights found.</p>
    <%
      }
    %>
  <% } %>
</body>
</html>
