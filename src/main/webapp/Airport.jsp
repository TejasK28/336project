<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,java.util.Map,java.util.Set"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Airport Flights</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>
  <jsp:include page="header.jsp" />
  <main class="main-content">
    <h1 class="page-title">Flights at Airport</h1>

    <%
      List<Map<String, Object>> flights =
        (List<Map<String, Object>>) request.getAttribute("airport_flights");
    %>
    <%
      if (flights != null && !flights.isEmpty()) {
        Set<String> columns = flights.get(0).keySet();
    %>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <%-- existing dynamic columns --%>
            <%
              for (String col : columns) {
            %>
              <th><div><%= col %></div></th>
            <%
              }
            %>
            <%-- add AircraftID header --%>
            <th><div>AircraftID</div></th>
            <th><div>Actions</div></th>
          </tr>
        </thead>
        <tbody>
          <%
            for (Map<String, Object> flight : flights) {
          %>
          <tr>
            <%-- existing dynamic cells --%>
            <%
              for (String col : columns) {
            %>
              <td><%= flight.get(col) %></td>
            <%
              }
            %>
            <%-- new AircraftID cell --%>
            <td><%= flight.get("AircraftID") %></td>
            <td class="actions">
              <form action="<%=request.getContextPath()%>/CustRep/editFlight"
                    method="get">
                <input type="hidden" name="flightId"
                       value="<%=flight.get("FlightID")%>" />
                <button type="submit" style="background-color: green;">Edit</button>
              </form>
              <form action="<%=request.getContextPath()%>/deleteFlight"
                    method="post">
                <input type="hidden" name="flightId"
                       value="<%=flight.get("FlightID")%>" />
                <button type="submit" style="background-color: red;">Delete</button>
              </form>
            </td>
          </tr>
          <%
            }
          %>
        </tbody>
      </table>
    </div>
    <%
      } else {
    %>
    <p>No flights found for this airport.</p>
    <%
      }
    %>

  </main>
</body>
</html>