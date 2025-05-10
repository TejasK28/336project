<%@ page import="java.util.*" %>
<%
  @SuppressWarnings("unchecked")
  List<Map<String,Object>> outbound =
    (List<Map<String,Object>>)request.getAttribute("outbound");
  @SuppressWarnings("unchecked")
  List<Map<String,Object>> inbound =
    (List<Map<String,Object>>)request.getAttribute("inbound");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Flight Results</title>
</head>
<body>
  <h2>Outbound Flights</h2>
  <table border="1" cellpadding="5">
    <tr>
      <th>Depart</th>
      <th>Arrive</th>
      <th>Duration (min)</th>
      <th>Price</th>
      <th>Airline</th>
    </tr>
    <% for (Map<String,Object> f : outbound) { %>
      <tr>
        <td><%= f.get("DepartTime") %></td>
        <td><%= f.get("ArrivalTime") %></td>
        <td><%= f.get("duration") %></td>
        <td>$<%= f.get("price") %></td>
        <td><%= f.get("AirlineName") %></td>        
      </tr>
    <% } %>
  </table>

  <%
    if (inbound != null && !inbound.isEmpty()) {
  %>
    <h2>Return Flights</h2>
    <table border="1" cellpadding="5">
      <tr>
        <th>Depart</th>
        <th>Arrive</th>
        <th>Duration (min)</th>
        <th>Price</th>
        <th>Airline</th>
      </tr>
      <% for (Map<String,Object> f : inbound) { %>
        <tr>
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
