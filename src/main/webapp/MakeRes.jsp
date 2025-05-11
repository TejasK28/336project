<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List,java.util.Map" %>
<%
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> flights =
        (List<Map<String,Object>>) request.getAttribute("flights");

    double totalCost = 0;
    int totalDur = 0;
    for (Map<String,Object> f : flights) {
        totalCost += ((Number)f.get("StandardFare")).doubleValue();
        totalDur += (int) f.get("Duration");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>Your Selected Flights</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css"/>
</head>
<body>
  <jsp:include page="header.jsp"/>

  <h1>Review &amp; Reserve Flights</h1>
  <form method="post" action="<%= request.getContextPath() %>/reserveFlights">
    <table border="1" cellpadding="5" cellspacing="0">
      <tr>
        <th>Route</th>
        <th>Departure</th>
        <th>Arrival</th>
        <th>Cost</th>
        <th>Class</th>
      </tr>
      <% for (Map<String,Object> f : flights) {
    	  String route = f.get("FromAirportID") + "->" + f.get("ToAirportID");
    	  String depDT = String.valueOf(f.get("DepartTime"));
    	  String arrDT = String.valueOf(f.get("ArrivalTime"));
    	  Number cost = (Number) f.get("StandardFare");
    	  int i = 1;
      %>
      <tr>
        <td><%= route %></td>
        <td><%= depDT %></td>
        <td><%= arrDT %></td>
        <td>$<%= String.format("%.2f", cost.doubleValue()) %></td>
        <td>
          <select name="class_<%= f.get("FlightID") %>" required>
            <option value="">Select class</option>
            <option value="Economy">Economy</option>
            <option value="Business">Business</option>
            <option value="First">First</option>
          </select>
        </td>

        <!-- hidden flight IDs for server -->
        <input type="hidden" name="flight<%= i %>Id" value="<%= f.get("FlightID") %>"/>
        <input type="hidden" name="flight<%= i %>Fare" value="<%= f.get("StandardFare") %>"/>
      </tr>
      <% 
     	i++; 
      } %>
    </table>

    <h2>Total Cost: $<%= String.format("%.2f", totalCost) %></h2>
    <h2>Total Duration: <%= totalDur %> min</h2>
	<input type="hidden" name="totalDur" value="<%= totalDur %>"/>
	<input type="hidden" name="totalCost" value="<%= totalCost %>"/>

    <button type="submit"
            style="background-color: green;
                   color: white;
                   padding: 10px 20px;
                   font-size: 1.1em;">
      Reserve
    </button>
  </form>
</body>
</html>
