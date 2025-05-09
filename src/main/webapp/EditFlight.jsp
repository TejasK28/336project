<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Map,java.util.List,java.time.LocalDateTime,java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Flight</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>
    <jsp:include page="header.jsp" />
    <main class="main-content">
        <h1 class="page-title">Edit Flight</h1>

        <%
        // Retrieve data objects
        Map<String,Object> flight = (Map<String,Object>) request.getAttribute("flight");
        List<Map<String,Object>> airlines = (List<Map<String,Object>>) request.getAttribute("airlines");
        List<Map<String,Object>> airports = (List<Map<String,Object>>) request.getAttribute("airports");

        // Prepare datetime-local values using Java Time API
        LocalDateTime depTs = (LocalDateTime) flight.get("DepartTime");
        LocalDateTime arrTs = (LocalDateTime) flight.get("ArrivalTime");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String depVal = depTs.format(fmt);
        String arrVal = arrTs.format(fmt);
        %>

        <% if (flight == null) { %>
    <div class="error-banner">No flight data available.</div>
<% } else { %>
<form class="full-form" action="<%=request.getContextPath()%>/editFlight" method="post">
    <!-- Hidden FlightID -->
    <input type="hidden" name="FlightID" value="<%=flight.get("FlightID")%>" />

    <div class="form-group">
        <label for="airline">Airline</label>
        <select id="airline" name="AirlineID" required size="5">
            <% if (airlines != null) {
                for (Map<String,Object> a : airlines) {
                    String aid = a.get("AirlineID").toString();
                    String fid = flight.get("AirlineID").toString();
            %>
            <option value="<%=aid%>" <%= aid.equals(fid) ? "selected" : "" %>>
                <%= a.get("Name") %>
            </option>
            <%   }
            } else { %>
            <option disabled>No airlines loaded</option>
            <% } %>
        </select>
    </div>

    <div class="form-group">
        <label for="flightNumber">Flight Number</label>
        <input type="number" id="flightNumber" name="FlightNumber" required min="1"
               value="<%=flight.get("FlightNumber")%>" />
    </div>

    <div class="form-group">
        <label for="fromAirport">From Airport</label>
        <select id="fromAirport" name="FromAirportID" required>
            <% if (airports != null) {
                for (Map<String,Object> ap : airports) {
                    String apId = ap.get("AirportID").toString();
                    String fAp = flight.get("FromAirportID").toString();
            %>
            <option value="<%=apId%>" <%= apId.equals(fAp) ? "selected" : "" %>>
                <%=ap.get("AirportID")%> – <%=ap.get("Name")%>
            </option>
            <%   }
            } else { %>
            <option disabled>No airports loaded</option>
            <% } %>
        </select>
    </div>

    <div class="form-group">
        <label for="toAirport">To Airport</label>
        <select id="toAirport" name="ToAirportID" required>
            <% if (airports != null) {
                for (Map<String,Object> ap : airports) {
                    String apId = ap.get("AirportID").toString();
                    String tAp = flight.get("ToAirportID").toString();
            %>
            <option value="<%=apId%>" <%= apId.equals(tAp) ? "selected" : "" %>>
                <%=ap.get("AirportID")%> – <%=ap.get("Name")%>
            </option>
            <%   }
            } else { %>
            <option disabled>No airports loaded</option>
            <% } %>
        </select>
    </div>

    <div class="form-group">
        <label for="departTime">Departure Time</label>
        <input type="datetime-local" id="departTime" name="DepartTime" required
               value="<%=depVal%>" />
    </div>

    <div class="form-group">
        <label for="arrivalTime">Arrival Time</label>
        <input type="datetime-local" id="arrivalTime" name="ArrivalTime" required
               value="<%=arrVal%>" />
    </div>

    <div class="form-group">
        <label for="operatingDays">Operating Days</label>
        <input type="text" id="operatingDays" name="OperatingDays" maxlength="10"
               pattern="[A-Za-z0-9,\- ]{1,10}"
               value="<%=flight.get("OperatingDays")%>" />
    </div>

    <div class="submit-row">
        <button type="submit" class="btn btn-save">Save Changes</button>
        <a href="<%=request.getContextPath()%>/CustRep" class="btn btn-revert">Cancel</a>
    </div>
</form>
<% } %>

    </main>
</body>
</html>
