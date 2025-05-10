<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>All Aircraft</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
  <style>
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
    th { background: #f4f4f4; }
  </style>
</head>
<body>
  <jsp:include page="header.jsp" />

  <h1>All Aircraft</h1>

  <% 
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> aircraft = 
        (List<Map<String,Object>>) request.getAttribute("ownedAircrafts");
  %>

  <% if (aircraft != null && !aircraft.isEmpty()) { %>
    <table>
      <thead>
        <tr>
          <th>AircraftID</th>
          <th>Model</th>
          <th>Total Seats</th>
          <th>Seat Configuration</th>
        </tr>
      </thead>
      <tbody>
        <% for (Map<String,Object> ac : aircraft) { %>
          <tr>
            <td><%= String.valueOf(ac.get("AircraftID")) %></td>
            <td><%= String.valueOf(ac.get("Model")) %></td>
            <td><%= String.valueOf(ac.get("TotalSeats")) %></td>
            <td><%= String.valueOf(ac.get("ClassConfigurations")) %></td>
          </tr>
        <% } %>
      </tbody>
    </table>
  <% } else { %>
    <p>No aircraft data found.</p>
  <% } %>

</body>
</html>
