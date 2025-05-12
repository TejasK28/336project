<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.List, java.util.Map" %>
<%
  @SuppressWarnings("unchecked")
  List<Map<String,Object>> flights =
      (List<Map<String,Object>>) request.getAttribute("airport_flights");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Flights at <%= request.getParameter("airportId") %></title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>
  <jsp:include page="header.jsp"/>
  <main class="main-content">
    <h1 class="page-title">Flights at Airport</h1>

    <% if (flights != null && !flights.isEmpty()) { %>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Flight ID</th>
              <th>Flight Number</th>
              <th>From</th>
              <th>To</th>
              <th>Departure</th>
              <th>Arrival</th>
              <th>Days</th>
              <th>Aircraft</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <% for (Map<String,Object> f : flights) {
                 Object model = f.get("AircraftModel");
            %>
            <tr>
              <td><%= f.get("FlightID") %></td>
              <td><%= f.get("FlightNumber") %></td>
              <td><%= f.get("FromAirportID") %></td>
              <td><%= f.get("ToAirportID") %></td>
              <td><%= f.get("DepartTime") %></td>
              <td><%= f.get("ArrivalTime") %></td>
              <td><%= f.get("OperatingDays") %></td>
              <td><%= (model != null ? model : "Unassigned") %></td>
              <td class="actions">
                <form action="<%=request.getContextPath()%>/CustRep/editFlight"
                      method="get" style="display:inline-block">
                  <input type="hidden" name="flightId" value="<%=f.get("FlightID")%>"/>
                  <button type="submit" class="btn btn-green">Edit</button>
                </form>
                <form action="<%=request.getContextPath()%>/CustRep/deleteFlight"
                      method="post" style="display:inline-block">
                  <input type="hidden" name="flightId" value="<%=f.get("FlightID")%>"/>
                  <button type="submit" class="btn btn-red">Delete</button>
                </form>
              </td>
            </tr>
            <% } %>
          </tbody>
        </table>
      </div>
    <% } else { %>
      <p>No flights found for this airport.</p>
    <% } %>
  </main>
</body>
</html>
