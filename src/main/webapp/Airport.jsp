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

    <% List<Map<String, Object>> flights =
         (List<Map<String, Object>>) request.getAttribute("airport_flights");
       if (flights != null && !flights.isEmpty()) {
         Set<String> columns = flights.get(0).keySet();
    %>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <% for (String col : columns) { %>
              <th><div><%= col %></div></th>
            <% } %>
            <th><div>Aircraft Model</div></th>
            <th><div>Actions</div></th>
          </tr>
        </thead>
        <tbody>
          <% for (Map<String, Object> flight : flights) { %>
            <tr>
              <% for (String col : columns) { %>
                <td><%= flight.get(col) %></td>
              <% } %>
              <td><%= flight.get("AircraftModel") %></td>
              <td class="actions">
                <form action="<%=request.getContextPath()%>/CustRep/editFlight"
                      method="get" style="display:inline-block;margin-right:5px;">
                  <input type="hidden" name="flightId"
                         value="<%=flight.get("FlightID")%>" />
                  <button 
                      type="submit" 
                      style="
                        background-color: #28a745;
                        color: #fff;
                        border: none;
                        border-radius: 4px;
                        padding: 6px 12px;
                        font-size: 0.9rem;
                        cursor: pointer;
                      ">
                    Edit
                  </button>
                </form>
                <form action="<%=request.getContextPath()%>/deleteFlight"
                      method="post" style="display:inline-block;">
                  <input type="hidden" name="flightId"
                         value="<%=flight.get("FlightID")%>" />
                  <button 
                      type="submit" 
                      style="
                        background-color: #dc3545;
                        color: #fff;
                        border: none;
                        border-radius: 4px;
                        padding: 6px 12px;
                        font-size: 0.9rem;
                        cursor: pointer;
                      ">
                    Delete
                  </button>
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
