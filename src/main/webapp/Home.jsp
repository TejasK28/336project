<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.List,java.util.Map, java.time.LocalDate"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Flight Home</title>

<!-- ===== BASIC STYLES ===== -->
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/styles.css">
</head>
<body>

	<!-- ========== HEADER ========== -->
	<jsp:include page="header.jsp"></jsp:include>

	<!-- ===== HERO / MAIN CONTENT PLACEHOLDER ===== -->

	<%
	String uname = (String) session.getAttribute("uname");
	if (uname == null) {
	%>
	<section class="hero">
		<h1>Book Your Next Flight</h1>
		<p>Fast • Easy • Secure</p>
	</section>
	<%
	} else {
	%>

	<h1>
		Welcome,
		<%=session.getAttribute("uname")%></h1>

	<%-- Flash message for added flight --%>
	<%
	if (request.getAttribute("FlightAdded") != null) {
	%>
	<p>Flight added!</p>
	<%
	}
	%>

	<%
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> flightPlanResults = (List<Map<String, Object>>) request.getAttribute("flightPlanResults");

	if (flightPlanResults != null && !flightPlanResults.isEmpty()) {
	%>
	<h1>Your cart:</h1>
	<ul>
		<%
		for (Map<String, Object> flight : flightPlanResults) {
			String from = (String) flight.get("FromAirportID");
			String to = (String) flight.get("ToAirportID");
			/*             LocalDate departing = (LocalDate) flight.get("DepartTime");
			            LocalDate arriving =  (LocalDate) flight.get("ArrivalTime");
			 */
		%>
		<li><%=from%> &rarr; <%=to%></li>
		<%
		}
		%>
	</ul>
	<form method="get"
		action="<%=request.getContextPath() + "/reserveFlights"%>">
		<%
		for (int i = 0; i < flightPlanResults.size(); i++) {
		%>
		<input type="hidden" name="flight<%=i + 1%>"
			value="<%=flightPlanResults.get(i).get("FlightID")%>">
		<%
		}
		%>
		<button type="submit">Make reservations for all flights in
			cart.</button>
	</form>
	<%
	} else {
	%>
	<p>Your flight plan is empty.</p>
	<%
	}
	%>

	<%
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> userFlightPlans =
        (List<Map<String, Object>>) request.getAttribute("reservedFlightPlans");
    if (userFlightPlans != null && !userFlightPlans.isEmpty()) {
%>
  <h2>Your Flight Plans</h2>
  <table border="1" cellpadding="5" cellspacing="0">
    <tr>
      <th>FlightPlanID</th>
      <th>Total Duration (min)</th>
      <th>Total Fare</th>
    </tr>
    <% for (Map<String, Object> flightPlan : userFlightPlans) {
         Object idObj   = flightPlan.get("FlightPlanID");
         Object durObj  = flightPlan.get("TotalDuration");
         Object fareObj = flightPlan.get("TotalFare");
         String id      = (idObj   != null ? idObj.toString()   : "");
         String dur     = (durObj  != null ? durObj.toString()  : "");
         double fare    = fareObj != null
                          ? ((Number)fareObj).doubleValue()
                          : 0.0;
    %>
    <tr>
      <td><a href="<%= request.getContextPath() + "/ViewFlightPlan/" + id %>"><%= id %></a></td>
      <td><%= dur %></td>
      <td>$<%= String.format("%.2f", fare) %></td>
    </tr>
    <% } %>
  </table>
<% 
    } else {
%>
  <p>No reserved flight plans found.</p>
<% 
    }
%>

<!-- Scheduled Flights Table -->
<h2>Scheduled Flights</h2>
<table border="1" cellpadding="5" cellspacing="0">
  <tr>
    <th>Flight Number</th>
    <th>Airline</th>
    <th>From</th>
    <th>To</th>
    <th>Departure</th>
    <th>Arrival</th>
    <th>Duration</th>
    <th>Class</th>
    <th>Price</th>
  </tr>
  <%
  @SuppressWarnings("unchecked")
  List<Map<String, Object>> scheduledFlights = (List<Map<String, Object>>) request.getAttribute("scheduledFlights");
  if (scheduledFlights != null && !scheduledFlights.isEmpty()) {
    for (Map<String, Object> flight : scheduledFlights) {
  %>
  <tr>
    <td><%= flight.get("FlightNumber") %></td>
    <td><%= flight.get("airline_name") %></td>
    <td><%= flight.get("departure_airport") %> (<%= flight.get("departure_city") %>, <%= flight.get("departure_country") %>)</td>
    <td><%= flight.get("arrival_airport") %> (<%= flight.get("arrival_city") %>, <%= flight.get("arrival_country") %>)</td>
    <td><%= flight.get("DepartTime") %></td>
    <td><%= flight.get("ArrivalTime") %></td>
    <td><%= flight.get("Duration") %> minutes</td>
    <td><%= flight.get("Class") %></td>
    <td>$<%= flight.get("StandardFare") %></td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="9" style="text-align: center;">No scheduled flights found.</td>
  </tr>
  <%
  }
  %>
</table>

<!-- Past Flights Table -->
<h2>Past Flights</h2>
<table border="1" cellpadding="5" cellspacing="0">
  <tr>
    <th>Flight Number</th>
    <th>Airline</th>
    <th>From</th>
    <th>To</th>
    <th>Departure</th>
    <th>Arrival</th>
    <th>Duration</th>
    <th>Class</th>
    <th>Price</th>
  </tr>
  <%
  @SuppressWarnings("unchecked")
  List<Map<String, Object>> pastFlights = (List<Map<String, Object>>) request.getAttribute("pastFlights");
  if (pastFlights != null && !pastFlights.isEmpty()) {
    for (Map<String, Object> flight : pastFlights) {
  %>
  <tr>
    <td><%= flight.get("FlightNumber") %></td>
    <td><%= flight.get("airline_name") %></td>
    <td><%= flight.get("departure_airport") %> (<%= flight.get("departure_city") %>, <%= flight.get("departure_country") %>)</td>
    <td><%= flight.get("arrival_airport") %> (<%= flight.get("arrival_city") %>, <%= flight.get("arrival_country") %>)</td>
    <td><%= flight.get("DepartTime") %></td>
    <td><%= flight.get("ArrivalTime") %></td>
    <td><%= flight.get("Duration") %> minutes</td>
    <td><%= flight.get("Class") %></td>
    <td>$<%= flight.get("StandardFare") %></td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="9" style="text-align: center;">No past flights found.</td>
  </tr>
  <%
  }
  %>
</table>

<h2>Waitlisted Flights</h2>
<table border="1">
    <tr>
        <th>Flight Number</th>
        <th>Airline</th>
        <th>From</th>
        <th>To</th>
        <th>Departure</th>
        <th>Arrival</th>
        <th>Duration</th>
        <th>Class</th>
        <th>Price</th>
    </tr>
    <% 
    List<Map<String, Object>> waitlistedFlights = (List<Map<String, Object>>) request.getAttribute("waitlistedFlights");
    if (waitlistedFlights != null && !waitlistedFlights.isEmpty()) {
        for (Map<String, Object> flight : waitlistedFlights) { %>
            <tr>
                <td><%= flight.get("FlightNumber") %></td>
                <td><%= flight.get("AirlineName") %></td>
                <td><%= flight.get("FromAirportID") %></td>
                <td><%= flight.get("ToAirportID") %></td>
                <td><%= flight.get("DepartureTime") %></td>
                <td><%= flight.get("ArrivalTime") %></td>
                <td><%= flight.get("Duration") %></td>
                <td><%= flight.get("Class") %></td>
                <td><%= flight.get("Price") %></td>
            </tr>
        <% }
    } else { %>
        <tr>
            <td colspan="9">No waitlisted flights found.</td>
        </tr>
    <% } %>
</table>

<div class="search-flights-container" style="text-align: center; margin: 30px 0;">
  <a href="<%=request.getContextPath()%>/SearchFlightsServlet" 
     style="display: inline-block;
            padding: 15px 30px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 1.2em;
            font-weight: bold;
            transition: background-color 0.3s ease;
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);">
    Search Flights
  </a>
</div>

<p>
  <a href="<%=request.getContextPath()%>/MyQuestions">
    📝 My Questions
  </a>
</p>


<!-- This section focuses on the question form -->
<section class="ask-form">
  <h2>Have a question?</h2>
  <form action="${pageContext.request.contextPath}/PostQuestion" method="post">
    <input type="hidden" name="customerId" value="${sessionScope.uname}" />
    <textarea name="message" rows="3" required
              placeholder="Type your question here…"></textarea>
    <button type="submit">Send to support</button>
  </form>
</section>


<section class="search-questions">
  <form action="${pageContext.request.contextPath}/SearchQuestions" method="get">
    <label for="searchQ">Search Q&A:</label>
    <input type="text" id="searchQ" name="q" placeholder="keyword…">
    <button type="submit">🔎</button>
  </form>
</section>




	<%
	}
	%>

	<!-- ===== SCRIPTS ===== -->
</body>
</html>
