<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Airport Flights</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>
    <jsp:include page="header.jsp" />
    <main class="main-content">
        <h1 class="page-title">Flights at Airport</h1>

        <%@ page import="java.util.List,java.util.Map,java.util.Set" %>
        <%
            List<Map<String,Object>> flights =
                (List<Map<String,Object>>) request.getAttribute("airport_flights");
        %>
        <% if (flights != null && !flights.isEmpty()) {
            Set<String> columns = flights.get(0).keySet();
        %>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <% for (String col : columns) { %>
                        <th><div><%= col %></div></th>
                        <% } %>
                        <th><div>Actions</div></th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Map<String,Object> flight : flights) { %>
                    <tr>
                        <% for (String col : columns) { %>
                        <td><%= flight.get(col) %></td>
                        <% } %>
                        <td class="actions">
                            <a href="<%=request.getContextPath()%>/editFlight?flightId=<%=flight.get("FlightID")%>" class="btn btn-query">
                                <span>Edit</span>
                            </a>
                            <a href="<%=request.getContextPath()%>/deleteFlight?flightId=<%=flight.get("FlightID")%>" class="btn btn-revert">
                                <span>Delete</span>
                            </a>
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
