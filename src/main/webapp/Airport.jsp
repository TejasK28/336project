<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List,java.util.Map,java.util.Set" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Airport Flights</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<%
    // Retrieve the list of flights for this airport
    List<Map<String,Object>> flights =
        (List<Map<String,Object>>) request.getAttribute("airport_flights");
    if (flights != null && !flights.isEmpty()) {
        // Use the first map to determine the column headers
        Set<String> columns = flights.get(0).keySet();
%>

<table>
    <thead>
        <tr>
            <% for (String col : columns) { %>
                <th><%= col %></th>
            <% } %>
        </tr>
    </thead>
    <tbody>
        <% for (Map<String,Object> flight : flights) { %>
        <tr>
            <% for (String col : columns) { %>
                <td><%= flight.get(col) %></td>
            <% } %>
        </tr>
        <% } %>
    </tbody>
</table>

<%  } else { %>
    <p>No flights found for this airport.</p>
<%  } %>

</body>
</html>
